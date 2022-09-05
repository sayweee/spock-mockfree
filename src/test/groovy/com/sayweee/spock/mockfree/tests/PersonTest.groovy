package com.sayweee.spock.mockfree.tests

import com.sayweee.spock.mockfree.annotation.MockFree
import org.spockframework.mock.MockUtil
import spock.lang.Specification

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

    def 'private on method is now protected'() {
        setup:
        Person person = Mock()

        when:
        def res = person.lastName

        then:
        1 * person.lastName >> 'Weee!'

        expect:
        res == 'Weee!'
    }

    def 'final is removed and private on method is now protected'() {
        setup:
        Person person = Mock()

        when:
        def res = person.address.street

        then:
        1 * person.address >> new Person.Address('Fremont Blvd!')

        expect:
        res == 'Fremont Blvd!'
    }

}
