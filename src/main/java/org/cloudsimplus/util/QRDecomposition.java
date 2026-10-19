/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2021 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cloudsimplus.util;

import java.util.Arrays;

/// Computes the [QR decomposition](https://en.wikipedia.org/wiki/QR_decomposition) of a matrix
/// and uses it to solve [linear least squares](https://en.wikipedia.org/wiki/Linear_least_squares) problems.
///
/// ## What it is
///
/// The QR decomposition splits an `m × n` matrix `A` (with `m >= n`) into `A = Q·R`, where:
///
/// - `Q` is an [orthogonal matrix](https://en.wikipedia.org/wiki/Orthogonal_matrix) (`Qᵀ·Q = I`).
///   Multiplying by `Q` only rotates or reflects a vector, never stretches it,
///   so it doesn't make rounding errors bigger.
/// - `R` is an upper triangular matrix: all values below its diagonal are zero.
///
/// ## Why it's used for least squares
///
/// A least squares problem looks for the vector `β` that makes `A·β` as close as possible to a vector `b`.
/// The simplest way to solve it is using the *normal equations* `(Aᵀ·A)·β = Aᵀ·b`.
/// However, computing `Aᵀ·A` squares the [condition number](https://en.wikipedia.org/wiki/Condition_number)
/// of the problem, which measures how much rounding errors may grow in the result.
/// When the columns of `A` are nearly linearly dependent (for instance, two highly correlated variables),
/// that may lose about half of the significant digits of a `double`.
///
/// Using `A = Q·R`, the problem becomes `R·β = Qᵀ·b`, which is solved by back substitution
/// (since `R` is triangular), without ever computing `Aᵀ·A`.
/// This way, the condition number is not squared.
///
/// ## How it's computed
///
/// It uses [Householder reflections](https://en.wikipedia.org/wiki/Householder_transformation).
/// For each column `k`, from left to right, a reflection (a mirror operation, which is orthogonal)
/// is chosen to zero all values below the diagonal of that column.
/// After all columns are processed, what is left is `R`.
/// `Q` is the product of all reflections, but it's never built:
/// the reflections are stored and applied directly to `b` when solving a system.
///
/// This is the same algorithm used by the `QRDecomposition` class from Apache Commons Math 3
/// (which CloudSim Plus used before) and by the [JAMA](https://math.nist.gov/javanumerics/jama/) library.
///
/// @author Manoel Campos da Silva Filho
/// @since CloudSim Plus 9.0.0
/// @see LinearRegression
final class QRDecomposition {
    /// Stores the Householder reflection vectors (on and below the diagonal)
    /// and the values of `R` above the diagonal.
    private final double[][] qr;

    /// The diagonal values of `R`.
    private final double[] rDiag;

    private final int rows;
    private final int cols;

    /// Computes the QR decomposition of a matrix.
    ///
    /// @param matrix the matrix to decompose, where each row is a line of the matrix.
    ///               It must have at least as many rows as columns, and it's not changed.
    /// @throws IllegalArgumentException when the matrix is empty, not rectangular
    ///         or has fewer rows than columns
    QRDecomposition(final double[][] matrix) {
        validate(matrix);
        this.rows = matrix.length;
        this.cols = matrix[0].length;
        this.qr = copy(matrix);
        this.rDiag = new double[cols];
        for (int k = 0; k < cols; k++) {
            rDiag[k] = computeHouseholderReflection(k);
        }
    }

    private static void validate(final double[][] matrix) {
        if (matrix.length == 0 || matrix[0].length == 0) {
            throw new IllegalArgumentException("The matrix cannot be empty");
        }

        final int cols = matrix[0].length;
        if (Arrays.stream(matrix).anyMatch(row -> row.length != cols)) {
            throw new IllegalArgumentException("All matrix rows must have the same number of columns");
        }

        if (matrix.length < cols) {
            final var msg = "The matrix must have at least as many rows as columns, but it has %d rows and %d columns";
            throw new IllegalArgumentException(msg.formatted(matrix.length, cols));
        }
    }

    private static double[][] copy(final double[][] matrix) {
        return Arrays.stream(matrix).map(double[]::clone).toArray(double[][]::new);
    }

    /// Computes the Householder reflection that zeroes the values below the diagonal of column `k`,
    /// then applies it to the columns on the right of `k`.
    /// The reflection vector is stored in column `k`, from the diagonal down.
    ///
    /// @param k the index of the column to process
    /// @return the diagonal value of `R` for column `k`
    private double computeHouseholderReflection(final int k) {
        final double norm = columnNorm(k);
        if (norm == 0) {
            // All values from the diagonal down are already zero: there is nothing to reflect.
            return 0;
        }

        /* The column is reflected to -signedNorm·e1 (where e1 is the first unit vector).
         * The sign is the same as the diagonal value, to avoid subtracting
         * two close numbers when building the reflection vector (which would lose precision). */
        final double signedNorm = qr[k][k] < 0 ? -norm : norm;
        for (int i = k; i < rows; i++) {
            qr[i][k] /= signedNorm;
        }
        qr[k][k] += 1;

        for (int j = k + 1; j < cols; j++) {
            reflectColumn(k, j);
        }

        return -signedNorm;
    }

    /// {@return the Euclidean norm of column `k`, from the diagonal down}
    /// [Math#hypot(double, double)] is used to avoid overflow or underflow
    /// when squaring very large or very small values.
    /// @param k the index of the column
    private double columnNorm(final int k) {
        double norm = 0;
        for (int i = k; i < rows; i++) {
            norm = Math.hypot(norm, qr[i][k]);
        }

        return norm;
    }

    /// Applies the Householder reflection `k` to the column `j` of the matrix being decomposed.
    /// @param k the index of the reflection (which is the index of the column storing its vector)
    /// @param j the index of the column to reflect
    private void reflectColumn(final int k, final int j) {
        final double[] column = new double[rows];
        for (int i = k; i < rows; i++) {
            column[i] = qr[i][j];
        }

        reflect(k, column);
        for (int i = k; i < rows; i++) {
            qr[i][j] = column[i];
        }
    }

    /// Applies the Householder reflection `k` to a vector `v`, changing it.
    /// The reflection is `H = I - u·uᵀ/u[k]`, where `u` is the reflection vector stored
    /// in column `k` (from the diagonal down). It just changes the values of `v` from index `k` on.
    ///
    /// @param k the index of the reflection
    /// @param v the vector to reflect
    private void reflect(final int k, final double[] v) {
        double dotProduct = 0;
        for (int i = k; i < rows; i++) {
            dotProduct += qr[i][k] * v[i];
        }

        final double factor = -dotProduct / qr[k][k];
        for (int i = k; i < rows; i++) {
            v[i] += factor * qr[i][k];
        }
    }

    /// Checks if the matrix has full column rank, meaning its columns are linearly independent.
    /// Otherwise, the least squares problem has no unique solution.
    ///
    /// A column is considered linearly dependent on the previous ones when its diagonal value in `R`
    /// is too small compared to the largest one. The tolerance is the
    /// [machine epsilon][Math#ulp(double)] times the largest matrix dimension,
    /// the same default used by numerical libraries such as LAPACK and NumPy.
    ///
    /// @return true if the matrix has full column rank, false otherwise
    boolean isFullRank() {
        final double maxDiag = Arrays.stream(rDiag).map(Math::abs).max().orElse(0);
        final double tolerance = Math.max(rows, cols) * Math.ulp(1.0) * maxDiag;
        return Arrays.stream(rDiag).allMatch(value -> Math.abs(value) > tolerance);
    }

    /// Solves the least squares problem `A·β ≈ b`, finding the `β` that minimizes `‖A·β - b‖`
    /// (the Euclidean norm of the residuals), where `A` is the decomposed matrix.
    ///
    /// @param b the vector to approximate (it must have one value for each row of `A`). It's not changed.
    /// @return the vector `β`, which has one value for each column of `A`
    /// @throws IllegalArgumentException when the length of `b` is different from the number of rows of `A`
    /// @throws IllegalStateException when the matrix is not [full rank][#isFullRank()]
    double[] solve(final double[] b) {
        if (b.length != rows) {
            throw new IllegalArgumentException(
                "The vector must have %d values (the number of matrix rows), but it has %d".formatted(rows, b.length));
        }

        if (!isFullRank()) {
            throw new IllegalStateException("The matrix columns are linearly dependent, so there is no unique solution");
        }

        final double[] qtb = b.clone();
        for (int k = 0; k < cols; k++) {
            reflect(k, qtb);
        }

        return backSubstitution(qtb);
    }

    /// Solves `R·β = Qᵀ·b` by back substitution.
    /// Since `R` is upper triangular, its last row has a single unknown value,
    /// the row above it has two, and so on.
    /// This way, the values of `β` are found from the last to the first one.
    ///
    /// @param qtb the vector `Qᵀ·b`
    /// @return the solution `β`
    private double[] backSubstitution(final double[] qtb) {
        final double[] beta = new double[cols];
        for (int k = cols - 1; k >= 0; k--) {
            double sum = qtb[k];
            for (int j = k + 1; j < cols; j++) {
                sum -= qr[k][j] * beta[j];
            }

            beta[k] = sum / rDiag[k];
        }

        return beta;
    }
}
