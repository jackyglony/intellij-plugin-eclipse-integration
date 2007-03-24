package testng.jmock;

import org.jmock.Mock;
import org.jmock.core.Verifiable;
import org.jmock.util.Verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * An utility class to allow static usage of JMock (removes the restriction
 * to subclass JMock specific classes).
 *
 * @author Alexandru Popescu
 */
public class JMockNG extends JMock {
    private static final WeakHashMap<Thread, List<Verifiable>> s_mockObjects =
            new WeakHashMap<Thread, List<Verifiable>>();

    /**
     * Creates a mock object that mocks the given type.
     * The mock object is named after the type;  the exact
     * name is calculated by {@link #defaultMockNameForType}.
     *
     * @param mockedType The type to be mocked.
     * @return A {@link Mock} object that mocks <var>mockedType</var>.
     */
    public static <T> Mock2<T> mock(Class<T> mockedType) {
        return (Mock2<T>) mock(mockedType, defaultMockNameForType(mockedType));
    }

    /**
     * Creates a mock object that mocks the given type and is explicitly given a name.
     * The mock object is named after the type;
     * the exact name is calculated by {@link #defaultMockNameForType}.
     *
     * @param mockedType The type to be mocked.
     * @param roleName   The name of the mock object
     * @return A {@link Mock} object that mocks <var>mockedType</var>.
     */
    public static Mock mock(Class mockedType, String roleName) {
        Mock newMock = new Mock(newCoreMock(mockedType, roleName));
        registerToVerify(newMock);
        return newMock;
    }

    public static Mock mock(Class mockedClass,
                            String roleName,
                            Class[] constructorArgumentTypes,
                            Object[] constructorArguments) {
        Mock newMock = new Mock(newClassCoreMock(mockedClass,
                roleName,
                constructorArgumentTypes,
                constructorArguments));
        registerToVerify(newMock);
        return newMock;
    }

    public static Mock mock(Class mockedClass,
                            Class[] constructorArgumentTypes,
                            Object[] constructorArguments) {
        return mock(mockedClass,
                defaultMockNameForType(mockedClass),
                constructorArgumentTypes,
                constructorArguments);
    }

    /**
     * Verify the expected behavior for the mock registered by the current thread.
     */
    public static void verify() {
        List<Verifiable> mocks = s_mockObjects.get(Thread.currentThread());
        if (null != mocks) {
            for (Verifiable verifiable : mocks) {
                verifiable.verify();
            }
        }
    }

    /**
     * Verify the expected behavior for the mocks defined as fields of the arguement object.
     *
     * @param object the object to be inspected
     */
    public static void verifyObject(Object object) {
        Verifier.verifyObject(object);
    }

    /**
     * Helper method that delegates to {@link #verify()} and {@link verifyObject(Object)}.
     */
    @SuppressWarnings({"JavadocReference"})
    public static void verifyAll(Object object) {
        verify();
        verifyObject(object);
    }

    /**
     * Verify the expected behavior for the mocks registered by the current thread and
     * also releases them.
     */
    public static synchronized void verifyAndRelease() {
        Thread currentThread = Thread.currentThread();
        List<Verifiable> mocks = s_mockObjects.get(currentThread);
        if (null != mocks) {
            for (Verifiable verifiable : mocks) {
                verifiable.verify();
            }
        }
        mocks.clear();
        s_mockObjects.put(currentThread, null);
    }

    /**
     * Helper method delegating to {@link #verifyAndRelease()} and {@link #verifyObject(Object)}.
     *
     * @param object
     */
    public static void verifyAllAndRelese(Object object) {
        verifyAndRelease();
        verifyObject(object);
    }

    ///
    private static synchronized void registerToVerify(Verifiable verifiable) {
        List<Verifiable> mocks = s_mockObjects.get(Thread.currentThread());
        if (null == mocks) {
            mocks = new ArrayList<Verifiable>();
            s_mockObjects.put(Thread.currentThread(), mocks);
        }

        mocks.add(verifiable);
    }

}