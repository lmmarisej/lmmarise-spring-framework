package org.lmmarise.framework.framework;

import org.lmmarise.aopalliance.intercept.LmmMethodInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * 使用 JDK Proxy API 生成代理类
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/24 2:57 下午
 */
public class LmmJdkDynamicAopProxy implements LmmAopProxy, InvocationHandler {
    private final LmmAdvisedSupport config;

    public LmmJdkDynamicAopProxy(LmmAdvisedSupport config) {
        this.config = config;
    }

    /**
     * 调用方法 {@link LmmAdvisedSupport#getInterceptorsAndDynamicInterceptionAdvice(Method, Class)} 获得拦截器链。
     * <p>
     * 在目标类中，每一个被增强的目标方法都对应一个拦截器链。
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 将每一个 JoinPoint 被代理的业务方法封装成一个拦截器，组合成一个拦截器链
        List<Object> interceptorsAndDynamicMethodMatchers = config.getInterceptorsAndDynamicInterceptionAdvice(
                method, this.config.getTargetClass()
        );
        // 交给拦截器链的 proceed 方法执行
        LmmMethodInvocation invocation = new LmmMethodInvocation(
                proxy, this.config.getTarget(), method, args, this.config.getTargetClass(), interceptorsAndDynamicMethodMatchers
        );
        return invocation.proceed();
    }

    @Override
    public Object getProxy() {
        return getProxy(this.config.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, this.config.getTargetClass().getInterfaces(), this);
    }

}
