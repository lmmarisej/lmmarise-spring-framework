package org.lmmarise.springframework.beans.factory;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/23 7:11 上午
 */
public interface LmmBeanPostProcessor {

    default Object postProcessBeforeInitialization(Object bean, String beanName) throws LmmBeansException {
        return bean;
    }

    default Object postProcessAfterInitialization(Object bean, String beanName) throws LmmBeansException {
        return bean;
    }

}
