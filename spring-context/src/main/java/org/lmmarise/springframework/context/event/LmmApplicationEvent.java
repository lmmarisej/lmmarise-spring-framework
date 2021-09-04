package org.lmmarise.springframework.context.event;

import java.time.Clock;
import java.util.EventObject;

/**
 * 类将由所有应用程序事件扩展。抽象，因为直接发布泛型事件没有意义。
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/28 10:05 下午
 */
public class LmmApplicationEvent extends EventObject {
    private final long timestamp;           // 事件发生时的系统时间
    private final LmmEventType eventType;   // 事件类型

    public LmmApplicationEvent(String source, LmmEventType eventType) {
        super(source);
        this.eventType = eventType;
        this.timestamp = System.currentTimeMillis();
    }

    public LmmApplicationEvent(Object source, LmmEventType eventType) {
        super(source);
        this.eventType = eventType;
        this.timestamp = System.currentTimeMillis();
    }

    public LmmApplicationEvent(Object source, LmmEventType eventType, Clock clock) {
        super(source);
        this.eventType = eventType;
        this.timestamp = clock.millis();
    }

    /**
     * 返回事件发生时的时间（以毫秒为单位）。
     */
    public final long getTimestamp() {
        return this.timestamp;
    }

    public LmmEventType getEventType() {
        return this.eventType;
    }
}
