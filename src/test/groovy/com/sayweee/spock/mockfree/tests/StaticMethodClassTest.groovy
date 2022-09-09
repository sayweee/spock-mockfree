package com.sayweee.spock.mockfree.tests


import spock.lang.Specification
/**
 * @author wangdengwu
 * Date 2022/8/27
 */
class StaticMethodClassTest extends Specification {
    def "test static method"() {
        expect: "result is A"
        println("execute returnA")
        StaticMethodClass.returnA() == "A"
    }
}
