/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
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
package org.cloudsimplus.collections;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.events.CloudSimEvent;
import org.cloudbus.cloudsim.core.events.SimEvent;
import org.openjdk.jmh.annotations.*;

import java.util.*;

/**
 * A set of benchmarks to measure the performance of managing
 * a collection of ordered {@link SimEvent} using:
 * - a {@link java.util.SortedSet};
 * - a regular {@link java.util.LinkedList} and
 * custom algorithms to ensure order when adding elements.
 *
 * @author Manoel Campos da Silva Filho
 */
@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
@State(Scope.Thread)
public class SortedSetVsLinkedList {
    private SortedSet<SimEvent> sortedSet;
    private LinkedList<SimEvent> linkedList;
    private double time;
    private double maxTime;
    private int serial;
    private RandomGenerator prng;
    /**
     * Defines the probability to increase or not the {@link #time}
     * randomly.
     * Some benchmarks always increase the time while other ones
     * increase just when a pseudo-random number if lower
     * than the given probability.
     */
    private static final double VALUE_INCREASE_PROBABILITY = 0.7;

    @Setup(Level.Iteration)
    public void doSetup() {
        sortedSet = new TreeSet<>();
        linkedList = new LinkedList<>();
        prng = new Well19937c();
        serial = 0;
        time = 0;
        maxTime = 0;
    }

    /**
     * Adds an always-incremented value into a {@link TreeSet}.
     * @return
     */
    @Benchmark
    public Collection<SimEvent> testSortedSetAdd() {
        return sortedSetAdd(++time);
    }

    /**
     * Adds a value into a {@link TreeSet} which
     * is increased just with a probability defined by {@link #VALUE_INCREASE_PROBABILITY}.
     * This way, we can benchmark the {@link TreeSet} and {@link LinkedList} when
     * events for the same time are generated consecutively.
     * @return
     */
    @Benchmark
    public Collection<SimEvent> testSortedSetAddRandomIncrement() {
        return sortedSetAdd(increaseTimeRandomly());
    }

    /**
     * Adds an always-incremented value into a {@link LinkedList}
     * that uses the CloudSim's custom implementation to ensure order.
     * @return
     */
    @Benchmark
    public Collection<SimEvent> testCustomLinkedListAdd() {
        return sortedLinkedListAdd(++time);
    }

    @Benchmark
    public Collection<SimEvent> testCustomLinkedListAddReversed() {
        return sortedLinkedListAddReversed(++time);
    }

    /**
     * Adds a value into a {@link LinkedList} which
     * is increased just with a probability defined by {@link #VALUE_INCREASE_PROBABILITY}.
     * This way, we can benchmark the {@link TreeSet} and {@link LinkedList} when
     * events for the same time are generated consecutively.
     * Such a {@link LinkedList} uses the CloudSim's custom implementation to ensure order.
     * @return
     */
    @Benchmark
    public Collection<SimEvent> testCustomLinkedListAddRandomIncrement() {
        return sortedLinkedListAdd(increaseTimeRandomly());
    }

    @Benchmark
    public Collection<SimEvent> testCustomLinkedListAddReversedRandomIncrement() {
        return sortedLinkedListAddReversed(increaseTimeRandomly());
    }

    private Collection<SimEvent> sortedSetAdd(final double time) {
        sortedSet.add(createEvent(time));
        maxTime = time > maxTime ? time : maxTime;
        return sortedSet;
    }

    /**
     * CloudSim's DeferredQueue uses a LinkedList and a custom
     * algorithm to ensure order when adding elements.
     * @return
     */
    private Collection<SimEvent> sortedLinkedListAdd(final double time) {
        if (time >= maxTime) {
            linkedList.add(createEvent(time));
            maxTime = time;
            return linkedList;
        }

        final ListIterator<SimEvent> iterator = linkedList.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().getTime() > time) {
                iterator.previous();
                iterator.add(createEvent(time));
                return linkedList;
            }
        }

        linkedList.add(createEvent(time));
        return linkedList;
    }

    /**
     * An alternative to CloudSim's DeferredQueue that ensures order when adding
     * elements into a LinkedList. However, this implementations start looking
     * for the position to add an element from the end of the list,
     * since the time of the event is usually higher then the previous time.
     * @return
     */
    private Collection<SimEvent> sortedLinkedListAddReversed(final double time) {
        if (time >= maxTime) {
            linkedList.add(createEvent(time));
            maxTime = time;
            return linkedList;
        }

        final ListIterator<SimEvent> reverseIterator = linkedList.listIterator(linkedList.size()-1);
        while (reverseIterator.hasPrevious()) {
            if (reverseIterator.previous().getTime() <= time) {
                reverseIterator.next();
                reverseIterator.add(createEvent(time));
                return linkedList;
            }
        }

        linkedList.add(createEvent(time));
        return linkedList;
    }

    private CloudSimEvent createEvent(final double time) {
        final CloudSimEvent evt = new CloudSimEvent(time, SimEntity.NULL, SimEntity.NULL, 0, null);
        evt.setSerial(++serial);
        return evt;
    }

    private double increaseTimeRandomly() {
        if(prng.nextDouble() < VALUE_INCREASE_PROBABILITY) {
            time++;
        }

        return time;
    }
}

