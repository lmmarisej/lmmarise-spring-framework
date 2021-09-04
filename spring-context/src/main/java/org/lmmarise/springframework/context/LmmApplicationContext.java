package org.lmmarise.springframework.context;

import org.lmmarise.framework.config.LmmAopConfig;
import org.lmmarise.framework.framework.LmmAdvisedSupport;
import org.lmmarise.framework.framework.LmmAopProxy;
import org.lmmarise.framework.framework.LmmCgLibProxy;
import org.lmmarise.framework.framework.LmmJdkDynamicAopProxy;
import org.lmmarise.springframework.beans.LmmBeanWrapper;
import org.lmmarise.springframework.beans.factory.LmmBeanFactory;
import org.lmmarise.springframework.beans.factory.LmmBeanPostProcessor;
import org.lmmarise.springframework.beans.factory.LmmBeansException;
import org.lmmarise.springframework.beans.factory.LmmInitializingBean;
import org.lmmarise.springframework.beans.factory.annotation.LmmAutowired;
import org.lmmarise.springframework.beans.factory.config.LmmBeanDefinition;
import org.lmmarise.springframework.beans.factory.support.LmmBeanDefinitionReader;
import org.lmmarise.springframework.context.stereotype.LmmComponent;
import org.lmmarise.springframework.context.support.LmmDefaultListableBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;
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
            if (!beanDefinitionEntry.getValue().isLazyInit()) {         // 优先初始化并装载非 Lazy 的 Bean，避免循环引用
                getBean(beanName);                        // 获取并填充Bean
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
        // 避免Bean多次初始化 todo 加入Bean的单例多例模式
        if (this.factoryBeanInstanceCache.get(beanName) != null) {
            return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
        }
        LmmBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            return null;
        }
        Object instance = instantiateBean(beanDefinition);
        if (instance == null) {
            return null;
        }
        // bean 实例化之前的回调
        try {
            // 实例初始化之后调用
            populationBean(beanName, instance);
            // Ware回调
            if (instance instanceof LmmApplicationContextAware) {
                ((LmmApplicationContextAware) instance).setApplicationContext(this);    // Aware接口
            }
            // Bean初始化方法回调
            initializeBean(beanName, instance);                         // 初始化Bean
            // 缓存创建完成的Bean
            this.factoryBeanInstanceCache.put(beanName, new LmmBeanWrapper(instance));
            return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化Bean
     */
    private void initializeBean(String beanName, Object bean) {
        // 通知事件
        LmmBeanPostProcessor beanPostProcessor = new LmmBeanPostProcessor() {
        };
        try {
            // 初始化前置
            beanPostProcessor.postProcessAfterInitialization(bean, beanName);
            // @PostConstruct
            // InitializingBean
            if (bean instanceof LmmInitializingBean) {
                ((LmmInitializingBean) bean).afterPropertiesSet();
            }
            // init-method
            // 初始化后置
            beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
        } catch (Exception e) {
            log.error("初始化Bean[{}]失败", bean.getClass().getName());
        }
    }

    /**
     * 实例化 Bean 之前需要提前处理 AOP
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
                populationBean(className, instance);

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
     * 判断 class 是否属于 LmmComponent，只要"继承"了 LmmComponent 注解的都是
     * <p>
     * 是则需要被作为 Spring 的 Bean 来处理
     */
    public static boolean isComponentClass(Class<?> clazz) {
        if (clazz.isAnnotationPresent(LmmComponent.class)) {
            return true;
        }
        for (Annotation annotation : clazz.getAnnotations()) {
            // 排除 Java 标准注解，如 @Target，@Documented 等，它们因相互依赖，将导致递归不断
            if (Target.class.getPackage().equals(annotation.annotationType().getPackage())) {
                continue;
            }
            if (annotation.annotationType().isAnnotationPresent(LmmComponent.class)
                    || isComponentClass(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 对实例的 LmmAutowired 字段进行注入
     */
    private void populationBean(String beanName, Object instance) {
        Class<?> clazz = instance.getClass();
        if (!isComponentClass(clazz)) {
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
            // 尝试获取实例化完成的Bean
            Object bean = this.factoryBeanObjectCache.get(autowiredBeanName);
            if (bean == null) {
                // 根据接口名找到实现类，走Bean的生命周期，创建Bean实例
                bean = getBean(this.beanDefinitionMap.get(field.getType().getName()).getBeanClassName());
                if (bean == null) {
                    throw new RuntimeException("Could not autowired bean, beanName[" + autowiredBeanName + "] does not found in ApplicationContext.");
                }
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
