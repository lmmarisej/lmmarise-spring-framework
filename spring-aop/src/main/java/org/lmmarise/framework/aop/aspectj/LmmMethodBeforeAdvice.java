package org.lmmarise.framework.aop.aspectj;

import org.lmmarise.aopalliance.aop.LmmAdvice;
import org.lmmarise.aopalliance.intercept.LmmJoinpoint;
import org.lmmarise.aopalliance.intercept.LmmMethodInterceptor;
import org.lmmarise.aopalliance.intercept.LmmMethodInvocation;

import java.lang.reflect.Method;

/**
 * 在 invoke 中控制前置通知的调用顺序
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/24 3:51 下午
 */
public class LmmMethodBeforeAdvice extends LmmAbstractAspectJAdvice implements LmmAdvice, LmmMethodInterceptor {
    private LmmJoinpoint joinpoint;

    public LmmMethodBeforeAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    public void before(Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(this.joinpoint, null, null);
    }

    @Override
    public Object invoke(LmmMethodInvocation mi) throws Throwable {
        this.joinpoint = mi;
        this.before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
