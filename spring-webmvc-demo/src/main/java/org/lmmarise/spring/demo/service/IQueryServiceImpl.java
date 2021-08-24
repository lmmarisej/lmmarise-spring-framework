package org.lmmarise.spring.demo.service;

import org.lmmarise.springframework.beans.factory.annotation.LmmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/23 10:16 下午
 */
@LmmService
public class IQueryServiceImpl implements IQueryService {

    private static final Logger log = LoggerFactory.getLogger(IQueryServiceImpl.class);

    @Override
    public String query(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String json = "{\"name\":\"" + name + "\", \"time\":\"" + time + "\"}";
        log.info("业务方法打印：" + json);
        return json;
    }

}
