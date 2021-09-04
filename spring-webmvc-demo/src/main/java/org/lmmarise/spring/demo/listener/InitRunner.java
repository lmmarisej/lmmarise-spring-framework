package org.lmmarise.spring.demo.listener;

import org.lmmarise.springframework.beans.factory.LmmInitializingBean;
import org.lmmarise.springframework.beans.factory.annotation.LmmAutowired;
import org.lmmarise.springframework.context.event.LmmAbstractApplicationEventMulticaster;
import org.lmmarise.springframework.context.stereotype.LmmComponent;

/**
 * 将IoC中事件装配到多事件监听器
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/9/3 11:01 下午
 */
@LmmComponent
public class InitRunner implements LmmInitializingBean {
    @LmmAutowired
    private LmmAbstractApplicationEventMulticaster applicationEventMulticaster;
    @LmmAutowired
    private IncidentStatisticsListener applicationEventListener;

    @Override
    public void afterPropertiesSet() throws Exception {
        applicationEventMulticaster.addApplicationListener(applicationEventListener);
    }
}
