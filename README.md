## Spock
[![CI](https://github.com/sayweee/spock-mockfree/actions/workflows/maven.yml/badge.svg)](https://github.com/sayweee/spock-mockfree/actions/workflows/maven.yml)
[![Coverage](.github/badges/jacoco.svg)](https://github.com/sayweee/spock-mockfree/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.sayweee/spock.mockfree)](https://search.maven.org/search?q=g:com.sayweee%20AND%20a:spock.mockfree)
[![License](https://img.shields.io/badge/license-Apache%202-blue)](https://www.apache.org/licenses/LICENSE-2.0.html)

[Spock](https://github.com/spockframework/spock) is a testing and specification framework for Java and Groovy applications.
It combines the main advantages of JUnit, BDD and Mocking and is considered a game changer for unit testing.
It ensures that all tests written using the framework follow BDD conventions; therefore, the test case is very readable
and parameterized. 

The only pity is that Spock do not support Mocking static methods, private methods write with java and final classes,
final fields also can't be Mocked.

Introducing PowerMock to mock static methods is too cumbersome, it is not prepared for spock, there may be incompatibility

For this, we developed this spock plugin to enhance its capabilities in Spock way

## Example

### Prepare

In addition to introducing Spock dependencies, add dependencies on spock-mockfree

```xml
<dependency>
    <groupId>com.sayweee</groupId>
    <artifactId>spock.mockfree</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```
Now you have the ability to mock static methods and final classes

### Example

#### Mock Static Method 
We used a static method returnA of StaticMethodClass class
```java
public class StaticMethodClass {
    public static String returnA() {
        return "A";
    }
}
```
here is the calling code
```java
public class CallStaticMethodClass {
    public String useStatic() {
        return StaticMethodClass.returnA();
    }
}
```
Now we need to test the useStatic method of CallStaticMethodClass class
But spock itself does not support mock static methods, and we support

```groovy
class CallStaticMethodClassTest extends Specification {

    def 'call static method is mocked method'() {
        given:
        CallStaticMethodClass callStaticMethodClass = Spy()
        println("useStatic")
        expect:
        callStaticMethodClass.useStatic() == 'M'
    }

    @MockStatic(StaticMethodClass)
    public static String returnA() {
        return "M";
    }
}
```
We use the @MockStatic annotation to mark which class needs to be mocked  
Directly implement the static method that requires mocking under it, 
the method signature remains the same, but the implementation is different

Run the test, you will find that the StaticMethodClass.returnA() called in the tested class is replaced by the implementation of CallStaticMethodClassTest.returnA

#### Mock Final Class

Spock does not support mock Final class and will throw an Exception  
Just like Person class
```java
public final class Person {

    private String firstName = "Weee";
    private String lastName = "Weee";

    private static final String country = "";

    private final Address address = new Address("Fremont Blvd");

    private Person() {
    }

    protected final String getFirstName() {
        return firstName;
    }

    private String getLastName() {
        return lastName;
    }

    private final Address getAddress() {
        return address;
    }

    private final static class Address {
        private final String street;

        private Address(final String street) {
            this.street = street;
        }

        public String getStreet() {
            return street;
        }
    }
}
```
Even we have a private final class Address inside，Let's write a test  
there's nothing we can't mock, that's why we call it mockfree

```groovy
@MockFree([Person, Person.Address])
class PersonTest extends Specification {

    def mockUtil = new MockUtil()

    def 'final from class is removed'() {
        setup:
        Person person = Mock()

        expect:
        mockUtil.isMock person
    }

    def 'final from subclass is removed'() {
        setup:
        Person.Address address = Mock()

        expect:
        mockUtil.isMock address
    }

    def 'final from method is removed'() {
        setup:
        Person person = Mock()

        when:
        def res = person.firstName

        then:
        1 * person.firstName >> 'Weee!'

        expect:
        res == 'Weee!'
    }

    def 'private on method is now public'() {
        setup:
        Person person = Mock()

        when:
        def res = person.lastName

        then:
        1 * person.lastName >> 'Weee!'

        expect:
        res == 'Weee!'
    }

    def 'final is removed and private on method is now public'() {
        setup:
        Person person = Mock()

        when:
        def res = person.address.street

        then:
        1 * person.address >> new Person.Address('Fremont Blvd!')

        expect:
        res == 'Fremont Blvd!'
    }

    def 'private final on property is removed'() {
        setup:
        Person person = new Person()

        when:
        person.address = new Person.Address("street")
        person.country = "china"

        then:
        person.address.street == "street"
        person.country == "china"
    }
}
```
We use the @MockFree annotation to annotate the classes that need to remove the Final Private restriction
And That is All，make you free to mock everything！

## Release Notes

### 1.0.0 (2022-09-09)
 * First Release
### 1.0.1 (2022-09-27)
 * Bug Fix
### 1.0.2 (2022-09-27)
 * Support mock final properties