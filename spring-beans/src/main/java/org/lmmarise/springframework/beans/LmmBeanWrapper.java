package org.lmmarise.springframework.beans;

/**
 * 封装创建后的对象实例，存储代理对象和原生对象
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/22 4:33 下午
 */
public class LmmBeanWrapper {

    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public LmmBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    // 返回代理后的 Class
    public Class<?> getWrappedClass() {
        return wrappedInstance.getClass();
    }

}
