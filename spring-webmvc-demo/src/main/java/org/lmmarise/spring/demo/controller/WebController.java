package org.lmmarise.spring.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.lmmarise.spring.demo.service.IQueryService;
import org.lmmarise.springframework.beans.factory.annotation.*;
import org.lmmarise.springframework.web.servlet.LmmModelAndView;

import java.util.HashMap;

/**
 * @author lmmarise.j@gmail.com
 * @since 2021/8/23 10:20 下午
 */
@LmmController
@LmmRequestMapping("/web")
public class WebController {

    @LmmAutowired
    private IQueryService queryService;

    @LmmRequestMapping("/first.html")
    public LmmModelAndView query(@LmmRequestParam("name") String name) throws Exception {
        String result = queryService.query(name);
        HashMap<String, Object> model = new HashMap<>();
        model.put("name", name);
        model.put("data", result);
        model.put("token", "123456");
        return new LmmModelAndView("first.html", model);
    }

    @LmmResponseBody
    @LmmRequestMapping("/queryAll.json")
    public Object queryAll() throws Exception {
        return queryService.queryAll();
    }

}
