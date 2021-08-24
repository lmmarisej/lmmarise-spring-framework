package org.lmmarise.framework.config;

import lombok.Data;

/**
 * AOP 配置封装
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/24 1:45 下午
 */
@Data
public class LmmAopConfig {
    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
