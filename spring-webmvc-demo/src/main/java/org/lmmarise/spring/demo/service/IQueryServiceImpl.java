package org.lmmarise.spring.demo.service;

import org.lmmarise.springframework.context.stereotype.LmmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/23 10:16 下午
 */
@LmmService
public class IQueryServiceImpl implements IQueryService {

    private static final Logger log = LoggerFactory.getLogger(IQueryServiceImpl.class);

    @Override
    public String query(String name) throws Exception {
        if (1 == 1) {
            throw new Exception("测试异常通知！");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String json = "{\"name\":\"" + name + "\", \"time\":\"" + time + "\"}";
        log.info("业务方法打印：" + json);
        return json;
    }

    @Override
    public List<String> queryAll() throws Exception {
        ArrayList<String> list = new ArrayList<>();
//        list.add(query("1"));
//        list.add(query("2"));
//        list.add(query("3"));
//        list.add(query("4"));
//        list.add(query("5"));
//        list.add(query("6"));
        return list;
    }

}
