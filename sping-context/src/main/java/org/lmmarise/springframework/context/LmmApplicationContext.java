package org.lmmarise.springframework.context;

import org.lmmarise.springframework.beans.LmmBeanWrapper;
import org.lmmarise.springframework.beans.factory.LmmBeanFactory;
import org.lmmarise.springframework.beans.factory.config.LmmBeanDefinition;
import org.lmmarise.springframework.beans.factory.support.LmmBeanDefinitionReader;
import org.lmmarise.springframework.context.support.LmmDefaultListableBeanFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 直接接触用户的入口，主要实现refresh方法和getBean方法，完成 IoC、DI、AOP 的衔接。
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/22 4:44 下午
 */
public class LmmApplicationContext extends LmmDefaultListableBeanFactory implements LmmBeanFactory {

    private final String[] configLocations;
    private LmmBeanDefinitionReader reader;
    // 单例 IoC 容器缓存
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();
    // 通用的 IoC 容器
    private final Map<String, LmmBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

    public LmmApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        // 定位配置文件
        reader = new LmmBeanDefinitionReader(this.configLocations);
        // 加载配置文件
        List<LmmBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        // 注册，将配置信息收集到容器
        doRegisterBeanDefinition(beanDefinitions);
        // 把不是延时加载的类提前初始化
        doAutowired();
    }

    private void doRegisterBeanDefinition(List<LmmBeanDefinition> beanDefinitions) throws Exception {
        for (LmmBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The '" + beanDefinition.getFactoryBeanName() + "' has already exists!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
        // 容器初始化完毕
    }

    /**
     * 处理非延时加载的情况
     */
    private void doAutowired() {
        for (Map.Entry<String, LmmBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    public int getBeanDefinitionCount() {
        return beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return reader.getConfig();
    }

    /**
     * 装饰器模式：保留原来的 OOP 关系，对其进行扩展，为后来的 AOP 打基础
     */
    @Override
    public Object getBean(String beanName) {
        // TODO: 2021/8/22 9:20 下午 by@lmmarise.j
        return null;
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

}
