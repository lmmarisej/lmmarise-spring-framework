package org.lmmarise.aopalliance.intercept;

/**
 * 方法拦截器顶层接口
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/24 1:41 下午
 */
public interface LmmMethodInterceptor {

    Object invoke(LmmMethodInvocation mi) throws Throwable;

}
