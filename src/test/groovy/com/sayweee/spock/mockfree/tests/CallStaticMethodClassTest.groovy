package com.sayweee.spock.mockfree.tests

import com.sayweee.spock.mockfree.annotation.MockStatic
import spock.lang.Specification

/**
 * @author wangdengwu
 * Date 2022/8/27
 */
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
