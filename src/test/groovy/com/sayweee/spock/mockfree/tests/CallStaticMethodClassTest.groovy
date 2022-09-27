package com.sayweee.spock.mockfree.tests

import com.sayweee.spock.mockfree.annotation.MockStatic
import spock.lang.Specification

/**
 * @author wangdengwu
 * Date 2022/8/27
 */
class CallStaticMethodClassTest extends Specification {
    static String result

    def 'call static method is mocked method'() {
        given:
        CallStaticMethodClass callStaticMethodClass = Spy()
        when:
        result = "M"
        then:
        callStaticMethodClass.useReturnA() == 'M'
        when:
        result = "N"
        then:
        callStaticMethodClass.useAnother() == 'N'
    }

    @MockStatic(StaticMethodClass)
    public static String returnA() {
        return result
    }

    @MockStatic(StaticMethodClass)
    public static String another() {
        return result
    }
}
