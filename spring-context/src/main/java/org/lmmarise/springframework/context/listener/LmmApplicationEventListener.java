package org.lmmarise.springframework.context.listener;

import org.lmmarise.springframework.context.event.LmmApplicationEvent;

import java.util.EventListener;

/**
 * 由应用程序事件侦听器实现的接口。
 * <p>
 * {@link EventListener} 监听 {@link LmmApplicationEvent} 事件
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/28 10:02 下午
 */
public interface LmmApplicationEventListener<E extends LmmApplicationEvent> extends EventListener {

    boolean supportsEventType(E event);

    void onApplicationEvent(E event);

}
