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

/**
 * Utility class that provides a set of methods for bit/bytes conversion.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 6.5.1
 * @see PowerConversion
 */
public final class BytesConversion {

    /**
     * The value of 1 KiloByte in Bytes or 1 Kilo-bit in bits.
     * It is declared as double because such a value is commonly used
     * in divisions. This way, it avoids explicit double casts
     * to ensure a double instead of an integer division.
     */
    public static final double KILO = 1024;

    /**
     * The value of 1 MegaByte in Bytes or 1 Mega-bit in bits.
     * @see #KILO
     */
    public static final double MEGA = KILO * KILO;

    /**
     * The value of 1 GigaByte in Bytes or 1 Giga-bit in bits.
     * @see #MEGA
     */
    public static final double GIGA = MEGA * KILO;

    /**
     * The value of 1 TeraByte in Bytes or 1 Tera-bit in bits.
     * @see #GIGA
     */
    public static final double TERA = GIGA * KILO;

    /**
     * A private constructor to avoid class instantiation.
     */
    private BytesConversion(){/**/}

    /**
     * Converts a value in bytes to MegaBytes (MB)
     * @param bytes the value in bytes
     * @return the value in MegaBytes (MB)
     */
    public static double bytesToMegaBytes(final double bytes){
        return bytes / MEGA;
    }

    /**
     * Converts a value in bytes to GigaBytes (GB)
     * @param bytes the value in bytes
     * @return the value in GigaBytes (GB)
     */
    public static double bytesToGigaBytes(final double bytes){
        return bytes / GIGA;
    }

    /**
     * Converts a value in bytes to KiloBytes (KB)
     * @param bytes the value in bytes
     * @return the value in KiloBytes (KB)
     */
    public static double bytesToKiloBytes(final double bytes){
        return bytes / KILO;
    }

    /**
     * Converts a value in bytes to the most suitable unit,
     * such as Kilobytes (KB), MegaBytes (MB) or Gigabytes (GB)
     * @param bytes the value in bytes
     * @return the converted value concatenated with the unit converted to (KB, MB or GB)
     */
    public static String bytesToStr(final double bytes){
        if(bytes < KILO) {
            return "%.0f bytes".formatted(bytes);
        }

        if(bytes < MEGA) {
            return "%.1f KB".formatted(bytesToKiloBytes(bytes));
        }

        if(bytes < GIGA) {
            return "%.1f MB".formatted(bytesToMegaBytes(bytes));
        }

        return "%.1f GB".formatted(bytesToGigaBytes(bytes));
    }

    /**
     * Converts a value in bytes to Megabits (Mb)
     * @param bytes the value in bytes
     * @return the value in Megabits (Mb)
     */
    public static double bytesToMegaBits(final double bytes){
        return bytesToBits(bytesToMegaBytes(bytes));
    }

    /**
     * Converts any value in bytes to bits,
     * doesn't matter if the unit is Kilobytes (KB), Megabytes (MB), Gigabytes (GB), etc.
     *
     * @param bytes the value in bytes, KB, MB, GB, etc.
     * @return the value in bites, kilo-bits, megabits, gigabits and so on, according to the given value
     */
    public static double bytesToBits(final double bytes){
        return bytes * 8;
    }

    /**
     * Converts any value in bits to bytes,
     * doesn't matter if the unit is Kilobits (Kb), Megabits (Mb), Gigabits (Gb), etc.
     *
     * @param bits the value in bites, Kb, Mb, Gb, etc.
     * @return the value in bites, Kbytes, Mbytes, Gbytes and so on, according to the given value
     */
    public static double bitsToBytes(final double bits){
        return bits / 8.0;
    }

    /**
     * Converts a value in MegaBytes (MB) to bytes
     * @param megaBytes the value in MegaBytes (MB)
     * @return the value in bytes
     */
    public static double megaBytesToBytes(final double megaBytes){
        return megaBytes * MEGA;
    }

    /**
     * Converts any value in mega to giga,
     * doesn't matter if it's megabits or megabytes.
     *
     * @param mega the value in megabits or megabytes
     * @return the value in gigabits or gigabytes (according to the input value)
     */
    public static double megaToGiga(final double mega){
        return mega / KILO;
    }

    /**
     * Converts any value in mega to tera,
     * doesn't matter if it's megabits or megabytes.
     *
     * @param mega the value in megabits or megabytes
     * @return the value in terabits or terabytes (according to the input value)
     */
    public static double megaToTera(final double mega){
        return mega / MEGA;
    }

    /**
     * Converts any value in giga to mega,
     * doesn't matter if it's gigabits or gigabytes.
     *
     * @param giga the value in gigabits or gigabytes
     * @return the value in megabits or megabytes (according to the input value)
     */
    public static double gigaToMega(final double giga){
        return giga * KILO;
    }

    /**
     * Converts any value in tera to mega,
     * doesn't matter if it's terabits or terabytes.
     *
     * @param tera the value in terabits or terabytes
     * @return the value in megabits or megabytes (according to the input value)
     */
    public static double teraToMega(final double tera){
        return teraToGiga(tera) * KILO;
    }

    /**
     * Converts any value in tera to giga,
     * doesn't matter if it's terabits or terabytes.
     *
     * @param tera the value in terabits or terabytes
     * @return the value in gigabits or gigabytes (according to the input value)
     */
    public static double teraToGiga(final double tera){
        return tera * KILO;
    }
}
