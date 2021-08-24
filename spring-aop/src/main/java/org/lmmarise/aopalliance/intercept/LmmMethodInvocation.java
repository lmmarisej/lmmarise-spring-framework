package org.lmmarise.aopalliance.intercept;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行拦截器链
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/24 1:42 下午
 */
public class LmmMethodInvocation implements LmmJoinpoint {

    private int currentInterceptorIndex = -1;

    private Object proxy;               // 代理对象
    private Object target;              // 代理目标方法
    private Method method;              // 代理的目标方法
    private Object[] arguments;         // 代理的方法实参列表
    private Class<?> targetClass;       // 代理的目标类
    private List<Object> interceptorsAndDynamicMethodMatchers;      // 回调方法链

    private Map<String, Object> userAttributes;     // 自定义属性

    public LmmMethodInvocation(Object proxy, Object target, Method method, Object[] arguments, Class<?> targetClass,
                               List<Object> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.targetClass = targetClass;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        if (value != null) {
            if (this.userAttributes == null) {
                this.userAttributes = new HashMap<>();
            }
            this.userAttributes.put(key, value);
        } else {
            if (this.userAttributes != null) {
                this.userAttributes.remove(key);
            }
        }
    }

    @Override
    public Object getUserAttribute(String key) {
        return (this.userAttributes != null ? this.userAttributes.get(key) : null);
    }

    /**
     * Interceptor 执行完成后，执行 joinPoint
     */
    public Object proceed() throws Throwable {
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            return this.method.invoke(this.target, this.arguments);
        }
        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
        if (interceptorOrInterceptionAdvice instanceof LmmMethodInterceptor) {
            LmmMethodInterceptor mi = (LmmMethodInterceptor) interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        } else {
            return proceed();
        }
    }
}
