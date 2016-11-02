package org.cloudbus.cloudsim;

import java.util.function.Consumer;
import org.cloudbus.cloudsim.core.CloudSim;
import org.easymock.EasyMock;
import org.powermock.api.easymock.PowerMock;

/**
 * A class that provides a set of methods to mock the {@link CloudSim} class
 * using {@link PowerMock}. Realize that we aren't talking about mocking objects
 * but mocking classes. Due to the extensive use of static methods in CloudSim class,
 * it is difficult to mock it. The regular way of mocking
 * objects doesn't work for classes. Alternatively, PowerMock can be used
 * to enable such a feature.
 * 
 * <p>Each method in this class provides a mock for a method with the same
 * name in the CloudSim class.</p>
 * 
 * @author Manoel Campos da Silva Filho
 */
public final class CloudSimMocker {
    
    /**
     * Builds a mock of the CloudSim class. It creates the mocker
     * object that will initialize the mocked CloudSim class.
     * It requires a {@link Consumer} object as parameter 
     * (that can be a lambda expression) where the developer
     * defines what methods of the Mocker class will be called.
     * Each CloudSimMocker's method call defines a method in the mocked 
     * CloudSim class that is expected to be called.
     * <p>
     * An usage example can be as follows:
     * <br>
     * {@code CloudSimMocker.build(mocker -> mocker.clock(10).getEntityName(1));}
     * </p>
     * This example will mock the static methods clock and getEntityName
     * from CloudSim class, making it return 10 and 1, respectively.
     * 
     * <p>
     * If you a mocked method to return 2 different values in different calls,
     * use a code like that:
     * <br>
     * {@code CloudSimMocker.build(mocker -> mocker.clock(10).clock(11));}
     * </p>
     * This example will make the mocked CloudSim.clock() method to return
     * 10 for the first time it is called inside a unit test and 11
     * for the second time.
     * 
     * <p>Realize that the provided examples uses lambda expression
     * as a parameter to the build method, defining
     * which method calls are expected in the mocked CloudSim class.</p>
     * 
     * @param consumer 
     */
    public static void build(Consumer<CloudSimMocker> consumer){
        CloudSimMocker mocker = new CloudSimMocker();
        consumer.accept(mocker);
        mocker.replay();
    }
    
    /**
     * Instantiates a CloudSimMocker object and enable the mocking of
     * static methods in the CloudSim class.
     * The constructor is used just internally by the {@link #build(java.util.function.Consumer)}
     * method to create a Mocker object.
     */
    private CloudSimMocker(){
        PowerMock.mockStatic(CloudSim.class);
    }

    /**
     * Makes the {@link CloudSim#clock()} method from the mocked CloudSim class
     * to return a given value.
     * The method from the mocked CloudSim class is expected to be called
     * just once.
     * 
     * @param clockTimeToReturn the value that the {@link CloudSim#clock()} method 
     * must return
     * @return 
     */
    public CloudSimMocker clock(final double clockTimeToReturn) {
        clock(clockTimeToReturn, 1);
        return this;
    }

    /**
     * Makes the {@link CloudSim#clock()} method from the mocked CloudSim class
     * to return a given value.
     * The method from the mocked CloudSim class is expected to be called
     * a given number of times.
     * 
     * @param clockTimeToReturn the value that the {@link CloudSim#clock()} method 
     * must return
     * @param numberOfExpectedCalls the number of times the {@link CloudSim#clock()} method
     * is expected to be called
     * @return 
     */
    public CloudSimMocker clock(final double clockTimeToReturn, int numberOfExpectedCalls) {
        EasyMock
            .expect(CloudSim.clock())
            .andReturn(clockTimeToReturn)
            .times(numberOfExpectedCalls);
        return this;
    }

    public CloudSimMocker getEntityName(final int datacenterId) {
        EasyMock
            .expect(CloudSim.getEntityName(datacenterId))
            .andReturn("datacenter" + datacenterId);
        return this;
    }
    
    /**
     * Finishes the mocking process, making the mocked CloudSim class ready to use.
     * The method is used just internally as the final step in the
     * {@link #build(java.util.function.Consumer)} method.
     */
    private void replay(){
        PowerMock.replay(CloudSim.class);
    }
    
    /**
     * Checks if the expected calls to CloudSim class were in fact made.
     * Calling this method is an optional step that might be performed,
     * at the end of the test case using the mocked CloudSim class,
     * to verify if the methods in CloudSim class that were expected
     * to be called were in fact called the number of expected times.
     */
    public static void verify(){
        PowerMock.verify(CloudSim.class);
    }
    
}
