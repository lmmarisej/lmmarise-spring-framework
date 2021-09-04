package org.lmmarise.springframework.context.event;

import org.lmmarise.springframework.context.listener.LmmApplicationEventListener;
import org.lmmarise.springframework.context.listener.LmmApplicationEventMulticaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 将所有事件多播到所有注册的侦听器，让监听器忽略他们不感兴趣的事件。
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/9/3 6:20 下午
 */
public abstract class LmmAbstractApplicationEventMulticaster<E extends LmmApplicationEvent> implements LmmApplicationEventMulticaster<E> {
    private static final Logger log = LoggerFactory.getLogger(LmmAbstractApplicationEventMulticaster.class);

    public final Set<LmmApplicationEventListener<E>> applicationListeners = new LinkedHashSet<>();
    private final Object mutex = new Object();

    @Override
    public void addApplicationListener(LmmApplicationEventListener<E> listener) {
        synchronized (this.mutex) {
            this.applicationListeners.add(listener);
        }
    }

    @Override
    public void removeApplicationListener(LmmApplicationEventListener<E> listener) {
        synchronized (this.mutex) {
            this.applicationListeners.remove(listener);
        }
    }

    @Override
    public void removeAllListeners() {
        synchronized (this.mutex) {
            this.applicationListeners.clear();
        }
    }

    @Override
    public void multicastEvent(E event) {
        try {
            for (LmmApplicationEventListener<E> applicationListener : this.applicationListeners) {
                if (applicationListener.supportsEventType(event)) {
                    applicationListener.onApplicationEvent(event);
                }
            }
        } catch (Exception e) {
            log.error("{}", Arrays.toString(e.getStackTrace()));
        }
    }
}
