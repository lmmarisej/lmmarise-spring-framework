package org.lmmarise.springframework.beans.factory;

/**
 * 单例工厂顶层设计
 * </p>
 * 从容器中获取一个 Bean 实例
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/22 4:16 下午
 */
public interface LmmBeanFactory {

    Object getBean(String beanName);

    Object getBean(Class<?> beanClass) throws Exception;

}
