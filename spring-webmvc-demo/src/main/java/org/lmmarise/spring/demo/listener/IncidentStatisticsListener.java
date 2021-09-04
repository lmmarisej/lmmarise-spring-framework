package org.lmmarise.spring.demo.listener;


import org.lmmarise.springframework.context.event.LmmApplicationEvent;
import org.lmmarise.springframework.context.listener.LmmApplicationEventListener;
import org.lmmarise.springframework.context.stereotype.LmmComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 事件统计侦听器
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/28 11:54 下午
 */
@LmmComponent
public class IncidentStatisticsListener<E extends LmmApplicationEvent> implements LmmApplicationEventListener<E> {
    private static final Logger log = LoggerFactory.getLogger(IncidentStatisticsListener.class);

    @Override
    public boolean supportsEventType(E event) {
        return true;
    }

    @Override
    public void onApplicationEvent(E event) {
        log.info("{}", event);
        if (event.getEventType() == SystemEventType.READY) {
            log.info("应用{}", event.getEventType().getContent());
        } else if (event.getEventType() == SystemEventType.START) {
            log.info("应用{}", event.getEventType().getContent());
        } else if (event.getEventType() == SystemEventType.FINISH) {
            log.info("应用{}", event.getEventType().getContent());
        }
    }
}
