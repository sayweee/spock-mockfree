package com.sayweee.spock.mockfree.tests;

/**
 * @author wangdengwu
 * Date 2022/8/27
 */
public class CallStaticMethodClass {
    public String useStatic() {
        return StaticMethodClass.returnA();
    }
}
