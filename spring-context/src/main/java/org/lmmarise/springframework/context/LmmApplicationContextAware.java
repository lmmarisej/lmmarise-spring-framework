package org.lmmarise.springframework.context;

/**
 * 解耦，获得 IoC 容器顶层设计。
 * <p>
 * 通过监听器扫描所有的类，只要实现了本接口，将自动调用 set 方法，将 IoC 容器注入到目标类。
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/22 8:55 下午
 */
public interface LmmApplicationContextAware {

    void setApplicationContext(LmmApplicationContext applicationContext);

}
