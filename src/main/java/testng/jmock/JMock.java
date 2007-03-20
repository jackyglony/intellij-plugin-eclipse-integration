package testng.jmock;

import org.jmock.cglib.CGLIBCoreMock;
import org.jmock.core.*;
import org.jmock.core.constraint.*;
import org.jmock.core.matcher.InvokeAtLeastOnceMatcher;
import org.jmock.core.matcher.InvokeCountMatcher;
import org.jmock.core.matcher.InvokeOnceMatcher;
import org.jmock.core.matcher.TestFailureMatcher;
import org.jmock.core.stub.*;
import org.jmock.util.Dummy;

import java.util.Collection;

/**
 * User: piotrga
 * Date: 2007-03-19
 * Time: 20:58:18
 */
public class JMock {
    static final Constraint ANYTHING = new IsAnything();

    public static Stub returnValue(Object o) {
        return new ReturnStub(o);
    }

    public static Stub returnValue(boolean result) {
        return returnValue(Boolean.valueOf(result));
    }

    public static Stub returnValue(byte result) {
        return returnValue(new Byte(result));
    }

    public static Stub returnValue(char result) {
        return returnValue(new Character(result));
    }

    public static Stub returnValue(short result) {
        return returnValue(new Short(result));
    }

    public static Stub returnValue(int result) {
        return returnValue(new Integer(result));
    }

    public static Stub returnValue(long result) {
        return returnValue(new Long(result));
    }

    public static Stub returnValue(float result) {
        return returnValue(new Float(result));
    }

    public static Stub returnValue(double result) {
        return returnValue(new Double(result));
    }

    public static Stub returnIterator(Collection collection) {
        return new ReturnIteratorStub(collection);
    }

    public static Stub returnIterator(Object[] array) {
        return new ReturnIteratorStub(array);
    }

    public static Stub throwException(Throwable throwable) {
        return new ThrowStub(throwable);
    }

    public static InvocationMatcher once() {
        return new InvokeOnceMatcher();
    }

    public static InvocationMatcher atLeastOnce() {
        return new InvokeAtLeastOnceMatcher();
    }

    public static InvocationMatcher exactly(int expectedCount) {
        return new InvokeCountMatcher(expectedCount);
    }

    public static InvocationMatcher never() {
        return new TestFailureMatcher("not expected");
    }

    public static InvocationMatcher never(String errorMessage) {
        return new TestFailureMatcher("not expected (" + errorMessage + ")");
    }

    public static Stub onConsecutiveCalls(Stub... stubs) {
        return new StubSequence(stubs);
    }

    public static Stub doAll(Stub... stubs) {
        return new DoAllStub(stubs);
    }

    public static IsEqual eq(Object operand) {
        return new IsEqual(operand);
    }

    public static IsEqual eq(boolean operand) {
        return eq(Boolean.valueOf(operand));
    }

    public static IsEqual eq(byte operand) {
        return eq(new Byte(operand));
    }

    public static IsEqual eq(short operand) {
        return eq(new Short(operand));
    }

    public static IsEqual eq(char operand) {
        return eq(new Character(operand));
    }

    public static IsEqual eq(int operand) {
        return eq(new Integer(operand));
    }

    public static IsEqual eq(long operand) {
        return eq(new Long(operand));
    }

    public static IsEqual eq(float operand) {
        return eq(new Float(operand));
    }

    public static IsEqual eq(double operand) {
        return eq(new Double(operand));
    }

    public static IsCloseTo eq(double operand, double error) {
        return new IsCloseTo(operand, error);
    }

    public static IsSame same(Object operand) {
        return new IsSame(operand);
    }

    public static IsInstanceOf isA(Class operandClass) {
        return new IsInstanceOf(operandClass);
    }

    public static StringContains stringContains(String substring) {
        return new StringContains(substring);
    }

    public static StringContains contains(String substring) {
        return stringContains(substring);
    }

    public static StringStartsWith startsWith(String substring) {
        return new StringStartsWith(substring);
    }

    public static StringEndsWith endsWith(String substring) {
        return new StringEndsWith(substring);
    }

    public static IsNot not(Constraint c) {
        return new IsNot(c);
    }

    public static And and(Constraint left, Constraint right) {
        return new And(left, right);
    }

    public static Or or(Constraint left, Constraint right) {
        return new Or(left, right);
    }

    public static void assertThat(Object actual, Constraint constraint) {
        if (!constraint.eval(actual)) {
            StringBuffer message = new StringBuffer("\nExpected: ");
            constraint.describeTo(message);
            message.append("\n    got : ").append(actual).append('\n');
            throw new AssertionError(message);
        }
    }

    public static void assertThat(boolean actual, Constraint constraint) {
        assertThat(Boolean.valueOf(actual), constraint);
    }

    public static void assertThat(byte actual, Constraint constraint) {
        assertThat(new Byte(actual), constraint);
    }

    public static void assertThat(short actual, Constraint constraint) {
        assertThat(new Short(actual), constraint);
    }

    public static void assertThat(char actual, Constraint constraint) {
        assertThat(new Character(actual), constraint);
    }

    public static void assertThat(int actual, Constraint constraint) {
        assertThat(new Integer(actual), constraint);
    }

    public static void assertThat(long actual, Constraint constraint) {
        assertThat(new Long(actual), constraint);
    }

    public static void assertThat(float actual, Constraint constraint) {
        assertThat(new Float(actual), constraint);
    }

    public static void assertThat(double actual, Constraint constraint) {
        assertThat(new Double(actual), constraint);
    }

    public static HasPropertyWithValue hasProperty(String propertyName, Constraint expectation) {
        return new HasPropertyWithValue(propertyName, expectation);
    }

    public static HasProperty hasProperty(String propertyName) {
        return new HasProperty(propertyName);
    }

    public static HasToString toString(Constraint toStringConstraint) {
        return new HasToString(toStringConstraint);
    }

    public static IsCompatibleType compatibleType(Class baseType) {
        return new IsCompatibleType(baseType);
    }

    public static IsIn isIn(Collection collection) {
        return new IsIn(collection);
    }

    public static IsIn isIn(Object[] array) {
        return new IsIn(array);
    }

    public static IsCollectionContaining collectionContaining(Constraint elementConstraint) {
        return new IsCollectionContaining(elementConstraint);
    }

    public static IsCollectionContaining collectionContaining(Object element) {
        return collectionContaining(eq(element));
    }

    public static IsArrayContaining arrayContaining(Constraint elementConstraint) {
        return new IsArrayContaining(elementConstraint);
    }

    public static IsArrayContaining arrayContaining(Object element) {
        return arrayContaining(eq(element));
    }

    public static IsArrayContaining arrayContaining(boolean element) {
        return arrayContaining(Boolean.valueOf(element));
    }

    public static IsArrayContaining arrayContaining(byte element) {
        return arrayContaining(new Byte(element));
    }

    public static IsArrayContaining arrayContaining(short element) {
        return arrayContaining(new Short(element));
    }

    public static IsArrayContaining arrayContaining(char element) {
        return arrayContaining(new Character(element));
    }

    public static IsArrayContaining arrayContaining(int element) {
        return arrayContaining(new Integer(element));
    }

    public static IsArrayContaining arrayContaining(long element) {
        return arrayContaining(new Long(element));
    }

    public static IsArrayContaining arrayContaining(float element) {
        return arrayContaining(new Float(element));
    }

    public static IsArrayContaining arrayContaining(double element) {
        return arrayContaining(new Double(element));
    }

    public static IsMapContaining mapContaining(Constraint keyConstraint, Constraint valueConstraint) {
        return new IsMapContaining(keyConstraint, valueConstraint);
    }

    public static IsMapContaining mapContaining(Object key, Object value) {
        return mapContaining(eq(key), eq(value));
    }

    public static IsMapContaining mapWithKey(Object key) {
        return mapWithKey(eq(key));
    }

    public static IsMapContaining mapWithKey(Constraint keyConstraint) {
        return new IsMapContaining(keyConstraint, ANYTHING);
    }

    public static IsMapContaining mapWithValue(Object value) {
        return mapWithValue(eq(value));
    }

    public static IsMapContaining mapWithValue(Constraint valueConstraint) {
        return new IsMapContaining(ANYTHING, valueConstraint);
    }

    /**
     * Calculates a default role name for a mocked type.
     *
     * @param mockedType
     * @return
     */
    protected static String defaultMockNameForType(Class mockedType) {
        return "mock" + Formatting.classShortName(mockedType);
    }

    protected static DynamicMock newCoreMock(Class mockedType, String roleName) {
        return new CoreMock(mockedType, roleName);
    }

    protected static DynamicMock newClassCoreMock(Class mockedClass,
                                                  String roleName,
                                                  Class[] constructorArgumentTypes,
                                                  Object[] constructorArguments) {
        return new CGLIBCoreMock(mockedClass, roleName, constructorArgumentTypes, constructorArguments);
    }

    /// HINT: not sure about these following 3
    public static Object newDummy(Class dummyType) {
        return Dummy.newDummy(dummyType);
    }/// HINT: what is this?

    public static Object newDummy(Class dummyType, String name) {
        return Dummy.newDummy(dummyType, name);
    }/// HINT: what is this?

    public static Object newDummy(String name) {
        return Dummy.newDummy(name);
    }
}
