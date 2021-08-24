package org.lmmarise.framework.framework;

/**
 * 提供代理对象的顶层入口
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/24 2:37 下午
 */
public interface LmmAopProxy {

    /**
     * 获得代理对象
     */
    Object getProxy();

    /**
     * 通过自定义的类加载器获得代理对象
     */
    Object getProxy(ClassLoader classLoader);

}
