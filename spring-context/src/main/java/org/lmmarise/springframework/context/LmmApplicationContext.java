package org.lmmarise.springframework.context;

import org.lmmarise.framework.config.LmmAopConfig;
import org.lmmarise.framework.framework.LmmAdvisedSupport;
import org.lmmarise.framework.framework.LmmAopProxy;
import org.lmmarise.framework.framework.LmmCgLibProxy;
import org.lmmarise.framework.framework.LmmJdkDynamicAopProxy;
import org.lmmarise.springframework.beans.LmmBeanWrapper;
import org.lmmarise.springframework.beans.factory.LmmBeanFactory;
import org.lmmarise.springframework.beans.factory.LmmBeanPostProcessor;
import org.lmmarise.springframework.beans.factory.LmmInitializingBean;
import org.lmmarise.springframework.beans.factory.annotation.LmmAutowired;
import org.lmmarise.springframework.beans.factory.config.LmmBeanDefinition;
import org.lmmarise.springframework.beans.factory.support.LmmBeanDefinitionReader;
import org.lmmarise.springframework.context.stereotype.LmmComponent;
import org.lmmarise.springframework.context.stereotype.LmmController;
import org.lmmarise.springframework.context.stereotype.LmmService;
import org.lmmarise.springframework.context.support.LmmDefaultListableBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
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

    private static final Logger log = LoggerFactory.getLogger(LmmApplicationContext.class);

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
        this.reader = new LmmBeanDefinitionReader(this.configLocations);
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
                getBean(beanName);  // 优先初始化并装载非 Lazy 的 Bean，避免循环引用
            }
        }
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[0]);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }

    /**
     * 装饰器模式：保留原来的 OOP 关系，对其进行扩展，为后来的 AOP 打基础
     */
    @Override
    public Object getBean(String beanName) {
        LmmBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        // 通知事件
        LmmBeanPostProcessor beanPostProcessor = new LmmBeanPostProcessor() {
        };
        Object instance = instantiateBean(beanDefinition);
        if (instance == null) {
            return null;
        }
        // bean 实例化之前的回调
        try {
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            if (instance instanceof LmmInitializingBean) {
                ((LmmInitializingBean) instance).afterPropertiesSet();
            }
            LmmBeanWrapper beanWrapper = new LmmBeanWrapper(instance);
            this.factoryBeanInstanceCache.put(beanName, beanWrapper);
            // 实例初始化之后调用
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            populationBean(beanName, instance);
            return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 实例化 Bean，并放入容器
     */
    private Object instantiateBean(LmmBeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try {
            // 是否有实例
            if (this.factoryBeanObjectCache.containsKey(className)) {
                instance = this.factoryBeanObjectCache.get(className);  // 对于单例保持唯一性
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                // 处理 AOP
                LmmAdvisedSupport config = instantiationAopConfig(beanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);
                // 根据 Aspect 配置文件判断当前 Bean 是否需要被代理
                if (config.pointCutMatch()) {
                    instance = createProxy(config).getProxy();
                }

                // 注册实现类
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(), instance);
                // 注册实现类接口
                for (Class<?> interfaceClazz : instance.getClass().getInterfaces()) {
                    this.factoryBeanObjectCache.put(interfaceClazz.getName(), instance);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    private LmmAopProxy createProxy(LmmAdvisedSupport config) {
        Class targetClass = config.getTargetClass();
        if (targetClass.getInterfaces().length > 0) {
            return new LmmJdkDynamicAopProxy(config);
        }
        return new LmmCgLibProxy(config);
    }

    private LmmAdvisedSupport instantiationAopConfig(LmmBeanDefinition beanDefinition) throws Exception {
        LmmAopConfig config = new LmmAopConfig();
        config.setPointCut(reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new LmmAdvisedSupport(config);
    }

    /**
     * 对实例的 LmmAutowired 字段进行注入
     */
    private void populationBean(String beanName, Object instance) {
        Class<?> clazz = instance.getClass();
        if (!(clazz.isAnnotationPresent(LmmComponent.class))) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(LmmAutowired.class)) {
                continue;
            }
            LmmAutowired autowired = field.getAnnotation(LmmAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();  // 不指定 BeanName 则根据类型注入
            }
            field.setAccessible(true);
            Object bean = this.factoryBeanObjectCache.get(autowiredBeanName);
            if (bean == null) {
                throw new RuntimeException("Could not autowired bean, beanName[" + autowiredBeanName + "] does not found in ApplicationContext.");
            }
            try {
                field.set(instance, bean);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

}
