package org.lmmarise.spring.demo.listener;

import org.lmmarise.springframework.context.event.LmmEventType;

/**
 * 事件类型
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/28 11:57 下午
 */
public enum SystemEventType implements LmmEventType {
    READY(1, "就绪事件"), START(2, "开始事件"), FINISH(3, "完成事件");

    private final int code;
    private final String content;

    SystemEventType(int code, String content) {
        this.code = code;
        this.content = content;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getContent() {
        return content;
    }
}
