package org.lmmarise.springfamework.orm.common;

import java.util.List;
import java.util.Map;

/**
 * 持久层顶层接口
 * <p>
 * 主要定义增删改查、统一参数列表和返回值
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/25 8:06 下午
 */
public interface BaseDao<T, PK> {

    /**
     * 获取列表
     *
     * @param queryRule 获取条件
     */
    List<T> select(QueryRule queryRule) throws Exception;

    /**
     * 根据SQL获取列表
     *
     * @param sql  SQL语句
     * @param args 参数
     */
    List<Map<String, Object>> selectBySql(String sql, Object... args) throws Exception;

    /**
     * 分页查询
     *
     * @param queryRule 查询条件
     * @param pageNo    页码
     * @param pageSize  每页条数
     */
    Page<?> select(QueryRule queryRule, int pageNo, int pageSize) throws Exception;

    /**
     * 根据SQL获取分页
     *
     * @param sql      SQL语句
     * @param param    SQL参数
     * @param pageNo   页码
     * @param pageSize 每页条数
     */
    Page<Map<String, Object>> selectBySqlToPage(String sql, Object[] param, int pageNo, int pageSize) throws Exception;

    /**
     * 删除一条记录
     *
     * @param entity 实体ID不能为空，ID为空其它条件不能为空，都为空不执行
     */
    boolean delete(T entity) throws Exception;

    /**
     * 批量删除
     *
     * @return 受影响的行数
     */
    int deleteAll(List<T> list) throws Exception;

    /**
     * 插入一条记录
     *
     * @return 返回插入后的ID
     */
    PK insertAndReturnId(T entity) throws Exception;

    /**
     * 插入一条记录，ID自增
     */
    boolean insert(T entity) throws Exception;

    /**
     * 批量插入
     *
     * @return 受影响的行数
     */
    int insertAll(List<T> list) throws Exception;

    /**
     * 修改一条记录
     *
     * @param entity 实体ID不能为空，ID为空其它条件不能为空，都为空不执行
     */
    boolean update(T entity) throws Exception;

}
