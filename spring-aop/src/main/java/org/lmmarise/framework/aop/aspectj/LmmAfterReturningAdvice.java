package org.lmmarise.framework.aop.aspectj;

import org.lmmarise.aopalliance.aop.LmmAdvice;
import org.lmmarise.aopalliance.intercept.LmmJoinpoint;
import org.lmmarise.aopalliance.intercept.LmmMethodInterceptor;
import org.lmmarise.aopalliance.intercept.LmmMethodInvocation;

import java.lang.reflect.Method;

/**
 * 前置通知具体实现
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/24 4:03 下午
 */
public class LmmAfterReturningAdvice extends LmmAbstractAspectJAdvice implements LmmAdvice, LmmMethodInterceptor {
    private LmmJoinpoint joinpoint;

    public LmmAfterReturningAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(LmmMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinpoint = mi;
        this.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
        return retVal;
    }

    public void afterReturning(Object returnVal, Method method, Object[] arguments, Object aThis)  throws Throwable{
        invokeAdviceMethod(joinpoint, returnVal, null);
    }

}
