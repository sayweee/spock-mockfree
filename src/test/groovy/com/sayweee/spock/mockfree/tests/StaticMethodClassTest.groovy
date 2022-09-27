package com.sayweee.spock.mockfree.tests


import spock.lang.Specification
/**
 * @author wangdengwu
 * Date 2022/8/27
 */
class StaticMethodClassTest extends Specification {
    def "test static returnA"() {
        expect: "result is A"
        StaticMethodClass.returnA() == "A"
    }

    def "test static another"() {
        expect: "result is B"
        StaticMethodClass.another() == "B"
    }
}
