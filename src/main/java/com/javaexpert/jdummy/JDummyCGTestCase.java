package com.javaexpert.jdummy;

import net.sf.jdummy.Mockery;
import net.sf.jdummy.generator.DummyValueFactory;
import net.sf.jdummy.generator.Generator;
import org.jmock.Mock;
import org.jmock.builder.NameMatchBuilder;
import org.jmock.cglib.MockObjectTestCase;
import org.jmock.core.Stub;

import java.lang.reflect.Array;
import java.util.Collection;


public abstract class JDummyCGTestCase extends MockObjectTestCase {
    protected Mockery mockery = new Mockery();
    protected DummyValueFactory dummyValueFactory = new DummyValueFactory(mockery);

    public JDummyCGTestCase() {
        super();
    }

    public JDummyCGTestCase(String name) {
        super(name);
    }

    /**
     * Create a new mock and return its proxy.
     *
     * @param type
     * @return
     */
    public Object mimic(Class type) {
        return mockery.mock(type);
    }

    public Object mimic(Class type, String name) {
        return mockery.mock(type, name);
    }

    /**
     * Creates a new mock object of the given type
     * with the given name and dummy value method results.
     *
     * @param type
     * @param name
     * @return
     */
    public Object mimicWithDummyValues(Class type, String name) {
        return dummyValueFactory.dummy(type, name);
    }

    /**
     * Returns new dummy with a unique name
     *
     * @param type
     * @return
     */
    public Object mimicWithDummyValues(Class type) {
        return dummyValueFactory.dummy(type);
    }

    /**
     * Gets the mock whose proxy()is dummy so that you may add expectations.
     *
     * @param dummy
     * @return mockForProxy(dummy)
     */
    public Mock assertBehavior(Object dummy) {
        return dummyValueFactory.mockForProxy(dummy);
    }

    /**
     * Gets the stubs() mock whose proxy()is dummy. Intended use is
     * to add behavior, i.e., a stub.
     *
     * @param dummy
     * @return mockForProxy(dummy).stubs()
     */
    public NameMatchBuilder precondition(Object dummy) {
        return dummyValueFactory.mockForProxy(dummy).stubs();
    }

    /**
     * A stub that is useful for methods returning enumerations.
     * Example:
     * <pre>
     *   precondition(dummy).method("foo").will(returnOneOf(MyEnum.values))
     * </pre>
     *
     * @param choices an array
     * @return a stub that returns a psuedo-random element from the array
     */
    public Stub returnOneOf(Object[] choices) {
        return returnValue(pickOne(choices));
    }

    public Object pickOne(Object[] choices) {
        return choices[dummyValueFactory.nextInt(choices.length)];
    }

    /**
     * @param choices
     * @return returnOneOf(choices.toArray())
     * @see #returnOneOf(Object[])
     */
    public Stub returnOneOf(Collection choices) {
        return returnOneOf(choices.toArray());
    }

    /**
     * Fills an array with dummies or random values of a given type. Handles arrays
     * of primitives.
     *
     * @param array
     * @return
     */
    public Object fillWithDummies(Object array) {
        if (!array.getClass().getComponentType().isPrimitive()) {
            return fillWithDummies((Object[]) array);
        }
        Class type = array.getClass().getComponentType();
        int length = Array.getLength(array);
        for (int i = 0; i < length; ++i) {
            Array.set(array, i, mimicWithDummyValues(type));
        }
        return array;
    }

    /**
     * Fills an array of object swith dummies values of a given type.
     *
     * @param array
     * @return
     */
    public Object[] fillWithDummies(Object[] array) {
        Class type = array.getClass().getComponentType();
        for (int i = 0; i < array.length; ++i) {
            array[i] = mimicWithDummyValues(type);
        }
        return array;
    }

    /**
     * Fills the collection with count new dummies
     *
     * @param collection
     * @param type
     * @param count
     * @return
     */
    public Collection fillWithDummies(Collection collection, Class type,
                                      int count) {
        while (count-- > 0) {
            collection.add(mimicWithDummyValues(type));
        }
        return collection;
    }


    public Generator getGenerator(Class type) {
        return dummyValueFactory.getGenerator(type);
    }

    public void setGenerator(Class type, Generator generator) {
        dummyValueFactory.setGenerator(type, generator);
    }


    public Mock mockForProxy(Object dummy) {
        return mockery.asMock(dummy);
    }

    /**
     * Gets the next value of the sequence baseName + "-" + i
     *
     * @param baseName
     * @return
     */
    public String nextName(String baseName) {
        return dummyValueFactory.nextName(baseName);
    }

    public void setSeed(long seed) {
        dummyValueFactory.setSeed(seed);
    }

    public void verify() {
        mockery.verify();
        super.verify();
    }
}