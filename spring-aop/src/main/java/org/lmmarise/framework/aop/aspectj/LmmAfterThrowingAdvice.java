package org.lmmarise.framework.aop.aspectj;

import org.lmmarise.aopalliance.aop.LmmAdvice;
import org.lmmarise.aopalliance.intercept.LmmMethodInterceptor;
import org.lmmarise.aopalliance.intercept.LmmMethodInvocation;

import java.lang.reflect.Method;

/**
 * invoke 中控制异常通知的调用顺序
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/24 5:07 下午
 */
public class LmmAfterThrowingAdvice extends LmmAbstractAspectJAdvice implements LmmAdvice, LmmMethodInterceptor {
    private String throwingName;
    private LmmMethodInvocation mi;

    public LmmAfterThrowingAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    public void setThrowingName(String throwingName) {
        this.throwingName = throwingName;
    }

    @Override
    public Object invoke(LmmMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        } catch (Exception e) {
            invokeAdviceMethod(mi, null, e.getCause());
            throw e;
        }
    }
}
