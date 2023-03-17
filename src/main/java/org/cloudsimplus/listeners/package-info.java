/**
 * Provides {@link org.cloudsimplus.listeners.EventListener}
 * implementations to enable event notifications during simulation execution.
 *
 * <p>These notifications are related to changes in the state of simulation entities.
 * The listeners enable, for instance, notifying when:
 * <ul>
 *     <li>a Host updates the processing of its VMs, it is allocated to a Vm or it is deallocated from a Vm;</li>
 *     <li>a Vm has its processing updated or fails to be placed at a Host due to lack of resources;</li>
 *     <li>a Cloudlet has its processing updated, it finishes its execution inside a Vm;</li>
 *     <li>a simulation processes any kind of event.</li>
 * </ul>
 * </p>
 *
 * <p>These listeners were implemented using Java 8+ functional interfaces,
 * enabling the use of
 * <a href="http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/Lambda-QuickStart/index.html">Lambda Expressions</a>
 * that allow a function reference to be passed as parameter to another function.
 * Such a reference will be used to automatically call the function every time the listened event is
 * fired. Researchers developing using just Java 7 features can also use these listeners in the old-way
 * by passing an anonymous class to them.
 * </p>
 *
 * <p>Listeners allow developers to perform specific tasks when different events happen and can be
 * largely used for monitoring purposes, metrics collection and dynamic creation of objects,
 * such as VMs and Cloudlets, at runtime.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
package org.cloudsimplus.listeners;
