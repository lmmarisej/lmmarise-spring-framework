package org.lmmarise.springframework.context.listener;

import org.lmmarise.springframework.context.event.LmmApplicationEvent;

/**
 * 管理多个 LmmApplicationListener 对象并向其发布事件。
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/28 9:58 下午
 */
public interface LmmApplicationEventMulticaster<E extends LmmApplicationEvent> {

    /**
     * 添加要通知所有事件的侦听器。
     */
    void addApplicationListener(LmmApplicationEventListener<E> listener);

    /**
     * 从通知列表中删除侦听器。
     */
    void removeApplicationListener(LmmApplicationEventListener<E> listener);

    /**
     * 从通知列表中删除侦听器bean。
     */
    void removeAllListeners();

    /**
     * 将给定的应用程序事件多播到适当的侦听器。
     */
    void multicastEvent(E event);

}
