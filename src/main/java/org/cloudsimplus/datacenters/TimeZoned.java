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
package org.cloudsimplus.datacenters;

import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.cloudsimplus.vms.Vm;

import java.util.List;
import java.util.Objects;

import static org.cloudsimplus.util.TimeUtil.hoursToMinutes;

/**
 * An interface to be implemented by objects that
 * are physically placed into some time zone,
 * such as {@link Datacenter} and {@link Vm}.
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 4.6.0
 * @see <a href="https://en.wikipedia.org/wiki/Time_zone">Time zone</a>
 */
public interface TimeZoned {
    int MIN_TIME_ZONE_OFFSET = -12;
    int MAX_TIME_ZONE_OFFSET =  14;

    /**
     * Gets the time zone offset, a value between [{@link #MIN_TIME_ZONE_OFFSET} and {@link #MAX_TIME_ZONE_OFFSET}],
     * in which the object is physically located.
     *
     * @return the time zone offset
     */
    double getTimeZone();

    /**
     * Sets the time zone offset between [{@link #MIN_TIME_ZONE_OFFSET} and {@link #MAX_TIME_ZONE_OFFSET}].
     *
     * @param timeZone the new time zone offset
     * @return
     */
    TimeZoned setTimeZone(double timeZone);

    default double validateTimeZone(final double timeZone) {
        if(timeZone < MIN_TIME_ZONE_OFFSET || timeZone > MAX_TIME_ZONE_OFFSET){
            final var msg = "Timezone offset must be between [%d and %d].";
            throw new IllegalArgumentException(msg.formatted(MIN_TIME_ZONE_OFFSET, MAX_TIME_ZONE_OFFSET));
        }

        return timeZone;
    }

    /**
     * Selects the {@link Datacenter} closest to a given {@link Vm}, based on their timezone.
     * It considers the Datacenter list is already sorted by timezone.
     *
     * @param vm to Vm to try place into the closest Datacenter
     * @param datacenters the list of available Datacenters, sorted by timezone
     * @return the first selected Datacenter
     */
    static Datacenter closestDatacenter(final Vm vm, final List<Datacenter> datacenters){
        if(Objects.requireNonNull(datacenters).isEmpty()){
            throw new IllegalArgumentException("The list of Datacenters is empty.");
        }

        if(datacenters.size() == 1){
            return datacenters.get(0);
        }

        /* Since the datacenter list is expected to be sorted,
         * if the VM timezone is negative or zero, start looking from the beginning of the list.
         * If it's positive, start looking from the end. */
        final var iterator = vm.getTimeZone() <= 0 ? datacenters.listIterator() : new ReverseListIterator<>(datacenters);

        var currentDc = Datacenter.NULL;
        var previousDc = currentDc;
        while(iterator.hasNext()) {
            currentDc = iterator.next();
            /* Since the Datacenter list is expected to be sorted, after finding the first
            DC with a distance larger than the previous one, the previous is the closest one. */
            if(vm.distance(currentDc) > vm.distance(previousDc)){
                return previousDc;
            }

            previousDc = currentDc;
        }

        return currentDc;
    }

    /**
     * Computes the distance between this and other {@link TimeZoned} object,
     * considering their timezone offset values.
     *
     * @param other the other {@link TimeZoned} object to check the distance to
     * @return a positive integer value representing the distance between the objects
     */
    default double distance(final TimeZoned other) {
        return Math.abs(other.getTimeZone() - this.getTimeZone());
    }

    static String format(final double timeZone){
        final double decimals = timeZone - (int) timeZone;
        final String timezoneStr = decimals == 0 ?
                                    "GMT%+.0f".formatted(timeZone) :
                                    "GMT%+d:%2.0f".formatted((int)timeZone, hoursToMinutes(decimals));
        return "%-8s".formatted(timezoneStr);
    }

}
