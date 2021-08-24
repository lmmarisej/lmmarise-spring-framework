package org.lmmarise.framework.aop.aspectj;

import org.lmmarise.aopalliance.aop.LmmAdvice;
import org.lmmarise.aopalliance.intercept.LmmJoinpoint;

import java.lang.reflect.Method;

/**
 * 封装拦截器同调的通用逻辑，主要封装反射动态调用方法
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/24 3:43 下午
 */
public abstract class LmmAbstractAspectJAdvice implements LmmAdvice {
    private Method aspectMethod;
    private Object aspectTarget;

    public LmmAbstractAspectJAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    protected Object invokeAdviceMethod(LmmJoinpoint joinpoint, Object returnValue, Throwable ex) throws Throwable {
        Class<?>[] paramsTypes = this.aspectMethod.getParameterTypes();
        if (paramsTypes.length == 0) {
            return this.aspectMethod.invoke(aspectTarget);
        } else {
            Object[] args = new Object[paramsTypes.length];
            for (int i = 0; i < args.length; i++) {
                if (paramsTypes[i] == LmmJoinpoint.class) {
                    args[i] = joinpoint;
                } else if (paramsTypes[i] == Throwable.class) {
                    args[i] = ex;
                } else if (paramsTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget, args);
        }
    }

}
