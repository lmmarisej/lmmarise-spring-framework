package org.lmmarise.spring.demo.service;

import org.lmmarise.spring.demo.listener.SystemEventType;
import org.lmmarise.springframework.beans.factory.annotation.LmmAutowired;
import org.lmmarise.springframework.context.event.LmmAbstractApplicationEventMulticaster;
import org.lmmarise.springframework.context.event.LmmApplicationEvent;
import org.lmmarise.springframework.context.stereotype.LmmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * application.properties 中，配置了AOP代理所有的service。
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/23 10:16 下午
 */
@LmmService
public class IQueryServiceImpl implements IQueryService {

    private static final Logger log = LoggerFactory.getLogger(IQueryServiceImpl.class);

    @LmmAutowired
    private LmmAbstractApplicationEventMulticaster lmmcommonApplicationEventMulticaster;

    /**
     * 加入AOP前置通知、后置通知、异常通知测试。
     */
    @Override
    public String query(String name) throws Exception {
        if (new Random().nextInt(2) == 1) {
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

    @Override
    public Object findById(String id) {
        // 创建事件
        LmmApplicationEvent applicationEvent = new LmmApplicationEvent("findById被触发", SystemEventType.START);
        // 触发所有同类型事件
        lmmcommonApplicationEventMulticaster.multicastEvent(applicationEvent);

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", "cxk");
        map.put("age", 12);
        return map;
    }

}
