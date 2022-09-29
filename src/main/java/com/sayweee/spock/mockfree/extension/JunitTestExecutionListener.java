package com.sayweee.spock.mockfree.extension;

import com.google.auto.service.AutoService;
import com.sayweee.spock.mockfree.transformer.MockfreeTransformer;
import org.junit.platform.launcher.TestExecutionListener;

/**
 * @author wangdengwu
 * Date 2022/9/28
 */
@AutoService(TestExecutionListener.class)
public class JunitTestExecutionListener implements TestExecutionListener {
    //Transformer install before @MockFree's classes is loaded
    static {
        MockfreeTransformer.getInstance();
    }
}
