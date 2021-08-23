package org.lmmarise.springframework.beans.factory.config;

/**
 * 保存 Bean 相关的配置信息
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/22 4:18 下午
 */
public class LmmBeanDefinition {

    private String beanClassName;   // 全限类名
    private String factoryBeanName; // simple 名
    private boolean lazyInit = false;   // 假设所有 Not Lazy 的 Bean 都不会发生循环引用，即可优先处理非 Lazy 的 Bean

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

}
