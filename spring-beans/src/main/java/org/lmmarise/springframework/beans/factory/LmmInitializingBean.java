package org.lmmarise.springframework.beans.factory;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/26 6:40 下午
 */
public interface LmmInitializingBean {

    void afterPropertiesSet() throws Exception;

}
