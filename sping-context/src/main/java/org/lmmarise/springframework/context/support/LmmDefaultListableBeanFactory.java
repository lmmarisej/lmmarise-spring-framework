package org.lmmarise.springframework.context.support;

import org.lmmarise.springframework.beans.factory.config.LmmBeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 众多 IoC 容器子类的典型代表。
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/22 4:40 下午
 */
public class LmmDefaultListableBeanFactory extends LmmAbstractApplicationContext {

    /**
     * 顶层的 IoC 缓存
     */
    protected final Map<String, LmmBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

}
