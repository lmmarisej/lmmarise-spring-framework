package org.lmmarise.springframework.context.event;

/**
 * 事件类型
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/28 11:40 下午
 */
public interface LmmEventType {

    int getCode();          // 类型码

    String getContent();    // 消息内容

}
