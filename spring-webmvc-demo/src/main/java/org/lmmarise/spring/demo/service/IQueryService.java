package org.lmmarise.spring.demo.service;

import java.util.List;

/**
 * 查询业务
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/23 10:14 下午
 */
public interface IQueryService {

    String query(String name) throws Exception;

    List<String> queryAll() throws Exception;

    Object findById(String id);
}
