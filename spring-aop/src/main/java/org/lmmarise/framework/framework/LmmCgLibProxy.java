package org.lmmarise.framework.framework;

/**
 * 使用CGLib生成代理类
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/24 2:55 下午
 */
public class LmmCgLibProxy implements LmmAopProxy {

    private final LmmAdvisedSupport config;

    public LmmCgLibProxy(LmmAdvisedSupport config) {
        this.config = config;
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
