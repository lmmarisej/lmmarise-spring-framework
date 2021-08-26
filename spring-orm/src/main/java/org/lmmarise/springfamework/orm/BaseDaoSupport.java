package org.lmmarise.springfamework.orm;

import org.lmmarise.springfamework.orm.common.BaseDao;
import org.lmmarise.springfamework.orm.common.Page;
import org.lmmarise.springfamework.orm.common.QueryRule;
import org.lmmarise.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BaseDao 扩展类，主要功能是自动拼装 SQL 语句，必须继承方可使用
 * <p>
 * 主要对 JdbcTemplate 进行包装
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/26 3:49 下午
 */
public abstract class BaseDaoSupport<T extends Serializable, PK extends Serializable> implements BaseDao<T, PK> {
    private static final Logger log = LoggerFactory.getLogger(BaseDaoSupport.class);

    private String tableName = "";

    private JdbcTemplate jdbcTemplateWrite;
    private JdbcTemplate jdbcTemplateReadOnly;

    private DataSource dataSourceReadOnly;
    private DataSource dataSourceWrite;

    private EntityOperation<T> op;

    protected BaseDaoSupport() {
        try {
            Class<T> entityClass = (Class<T>) getClass().getGenericSuperclass();
            op = new EntityOperation<T>(entityClass, this.getPkColumn());
            this.setTableName(op.tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String getTableName() {
        return tableName;
    }

    protected DataSource getDataSourceReadOnly() {
        return dataSourceReadOnly;
    }

    protected DataSource getDataSourceWrite() {
        return dataSourceWrite;
    }

    /**
     * 动态切换表名
     */
    protected void setTableName(String tableName) {
        if (StringUtils.isEmpty(tableName)) {
            this.tableName = op.tableName;
        } else {
            this.tableName = tableName;
        }
    }

    protected void setDataSourceWrite(DataSource dataSourceWrite) {
        this.dataSourceWrite = dataSourceWrite;
        jdbcTemplateWrite = new JdbcTemplate(dataSourceWrite);
    }

    protected void setDataSourceReadOnly(DataSource dataSourceReadOnly) {
        this.dataSourceReadOnly = dataSourceReadOnly;
        jdbcTemplateReadOnly = new JdbcTemplate(dataSourceReadOnly);
    }

    private JdbcTemplate jdbcTemplateReadOnly() {
        return this.jdbcTemplateReadOnly;
    }

    private JdbcTemplate jdbcTemplateWrite() {
        return this.jdbcTemplateWrite;
    }

    protected void restoreTableName() {
        this.setTableName(op.tableName);
    }

    protected abstract String getPkColumn();

    protected abstract void setDataSource(DataSource dataSource);

    @Override
    public List<T> select(QueryRule queryRule) throws Exception {
        QueryRuleBuilder builder = new QueryRuleBuilder(queryRule);
        String ws = removeFirstAnd(builder.getWhereSql());
        String whereSql = ("".equals(ws) ? ws : (" where " + ws));
        String sql = "select " + op.allColumn + " from " + getTableName() + whereSql;
        Object[] values = builder.getValues();
        String orderSql = builder.getOrderSql();
        orderSql = (StringUtils.isEmpty(orderSql) ? " " : (" order by " + orderSql));
        sql += orderSql;
        log.debug(sql);
        return this.jdbcTemplateReadOnly().query(sql, values, this.op.rowMapper);
    }

    @Override
    public List<Map<String, Object>> selectBySql(String sql, Object... args) throws Exception {
        return this.jdbcTemplateReadOnly().queryForList(sql, args);
    }

    @Override
    public Page<?> select(QueryRule queryRule, int pageNo, int pageSize) throws Exception {
        QueryRuleBuilder builder = new QueryRuleBuilder(queryRule);
        Object[] values = builder.getValues();
        String ws = removeFirstAnd(builder.getWhereSql());
        String whereSql = ("".equals(ws) ? ws : (" where " + ws));
        String countSql = "select count(1) from " + getTableName() + whereSql;
        long count = (Long) this.jdbcTemplateReadOnly().queryForMap(countSql, values).get("count(1)");
        if (count == 0) {
            return new Page<>();
        }
        long start = (pageNo - 1) * pageSize;
        String orderSql = builder.getOrderSql();
        orderSql = (StringUtils.isEmpty(orderSql) ? " " : (" order by " + orderSql));
        String sql = "select " + op.allColumn + " from " + getTableName() + whereSql + orderSql + " limit " + start + "," + pageSize;
        List<T> list = this.jdbcTemplateReadOnly().query(sql, values, this.op.rowMapper);
        log.debug(sql);
        return new Page<>(start, count, pageSize, list);
    }

    private String removeFirstAnd(String whereSql) {
        int index = whereSql.indexOf("and");
        return whereSql.substring(0, index) + whereSql.substring(index + 3);
    }

    @Override
    public Page<Map<String, Object>> selectBySqlToPage(String sql, Object[] param, int pageNo, int pageSize) throws Exception {
        String countSql = "select count(1) from (" + sql + ") a";
        long count = (long) this.jdbcTemplateReadOnly().queryForMap(countSql, param).get("count(1)");
        if (count == 0) {
            return new Page<>();
        }
        long start = (pageNo - 1) * pageSize;
        sql = sql + " limit " + start + "," + pageSize;
        List<Map<String, Object>> list = this.jdbcTemplateReadOnly().queryForList(sql, param);
        return new Page<>(start, count, pageSize, list);
    }

    /**
     * 获取默认的实例对象
     */
    private <T> T doLoad(Object pkValue, RowMapper rowMapper) {
        Object obj = this.doLoad(getTableName(), getPkColumn(), pkValue, rowMapper);
        if (obj != null) {
            return (T) obj;
        }
        return null;
    }

    private Object doLoad(String tableName, String pkColumn, Object pkValue, RowMapper rowMapper) {
        return null;
    }

    @Override
    public PK insertAndReturnId(T entity) throws Exception {
        return (PK) this.doInsertReturnKey(parse(entity));
    }

    @Override
    public boolean insert(T entity) throws Exception {
        return this.doInsert(parse(entity));
    }

    private Map<String, Object> parse(T entity) {
        HashMap<String, Object> map = new HashMap<>();
        for (Method method : entity.getClass().getDeclaredMethods()) {
            if (method.getName().startsWith("get")) {
                try {
                    map.put(method.getName().substring(3), method.invoke(entity));
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
            if (method.getName().startsWith("is")) {
                try {
                    map.put(method.getName().substring(2), method.invoke(entity));
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return map;
    }

    @Override
    public int insertAll(List<T> list) throws Exception {
        int count = 0, len = list.size(), step = 50000;
        Map<String, EntityOperation.PropertyMapping> pm = op.mappings;
        int maxPage = (len % step == 0) ? (len / step) : (len / step + 1);
        for (int i = 0; i < maxPage; i++) {
            Page<T> page = pagination(list, i, step);
            String sql = "insert into " + getTableName() + "(" + op.allColumn + ") values ";
            StringBuffer valStr = new StringBuffer();
            Object[] values = new Object[pm.size() * page.getRows().size()];
            for (int j = 0; j < page.getRows().size(); j++) {
                if (j > 0 && j < page.getRows().size()) {
                    valStr.append(",");
                }
                valStr.append("(");
                int k = 0;
                for (EntityOperation.PropertyMapping p : pm.values()) {
                    values[(j * pm.size()) + k] = p.getter.invoke(page.getRows().get(j));
                    if (k > 0 && k < pm.size()) {
                        valStr.append(",");
                    }
                    valStr.append("?");
                    ++k;
                }
                valStr.append(")");
            }
            int result = jdbcTemplateWrite().update(sql + valStr, values);
            count += result;
        }
        return count;
    }

    private Serializable doInsertReturnKey(Map<String, Object> params) {
        final ArrayList<Object> values = new ArrayList<>();
        final String sql = makeSimpleInsertSql(getTableName(), params, values);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSourceWrite());
        try {
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                for (int i = 0; i < values.size(); i++) {
                    ps.setObject(i + 1, values.get(i) == null ? null : values.get(i));
                }
                return ps;
            }, keyHolder);
        } catch (Exception e) {
            log.error("error", e);
        }
        if (keyHolder == null) {
            return "";
        }
        Map<String, Object> keys = null;
        try {
            keys = keyHolder.getKeys();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (keys == null || keys.size() == 0 || keys.values().size() == 0) {
            return "";
        }
        Object key = keys.values().toArray()[0];
        if (key == null || !(key instanceof Serializable)) {
            return "";
        }
        if (key instanceof Number) {
            Class<?> clazz = key.getClass();
            return (clazz == int.class || clazz == Integer.class) ? ((Number) key).intValue() : ((Number) key).longValue();
        } else if (key instanceof String) {
            return (String) key;
        } else {
            return (Serializable) key;
        }
    }

    private boolean doInsert(Map<String, Object> params) {
        String sql = this.makeSimpleInsertSql(this.getTableName(), params);
        int ret = this.jdbcTemplateWrite().update(sql, params.values().toArray());
        return ret > 0;
    }

    private String makeSimpleInsertSql(String tableName, Map<String, Object> params) {
        return null;
    }

    @Override
    public boolean delete(T entity) throws Exception {
        return this.doDelete(getTableName(), op.pkField.getName(), op.pkField.get(entity)) > 0;
    }

    @Override
    public int deleteAll(List<T> list) throws Exception {
        String pkName = op.pkField.getName();
        int count = 0, len = list.size(), step = 1000;
        Map<String, EntityOperation.PropertyMapping> pm = op.mappings;
        int maxPage = (len % step == 0) ? (len / step) : (len / step) + 1;
        for (int i = 0; i < maxPage; i++) {
            StringBuffer valStr = new StringBuffer();
            Page<T> page = pagination(list, i, step);
            Object[] values = new Object[page.getRows().size()];
            for (int j = 0; j < page.getRows().size(); j++) {
                if (j > 0 && j < page.getRows().size()) {
                    valStr.append(",");
                }
                values[j] = pm.get(pkName).getter.invoke(page.getRows().get(j));
                valStr.append("?");
            }
            String sql = "delete from " + getTableName() + " where " + pkName + " in (" + valStr + ")";
            int result = jdbcTemplateWrite().update(sql, values);
            count += result;
        }
        return count;
    }

    private Page<T> pagination(List<T> list, int i, int step) {
        return new Page<>(Page.getStartOfPage(i, step), list.size(), step, list);
    }

    protected void deleteByPk(PK id) throws Exception {
        this.doDelete(getTableName(), getPkColumn(), id);
    }

    private int doDelete(String tableName, String pkName, Object pkValue) {
        String sb = "delete from " + tableName + " where " + pkName + " = ? ";
        return this.jdbcTemplateWrite().update(sb, new Object[]{pkValue});
    }

    @Override
    public boolean update(T entity) throws Exception {
        return this.doUpdate(op.pkField.get(entity), parse(entity)) > 0;
    }

    private int doUpdate(Object pkValue, Map<String, Object> params) {
        String sql = this.makeDefaultSimpleUpdateSql(pkValue, params);
        params.put(this.getPkColumn(), pkValue);
        return this.jdbcTemplateWrite().update(sql, params.values().toArray());
    }

    private String makeSimpleInsertSql(String tableName, Map<String, Object> params, ArrayList<Object> values) {
        return  "insert into " + tableName + "( " + String.join(",", params.keySet()) + ") values("
                + String.join(",", (CharSequence) values) + ")";
    }

    private String makeDefaultSimpleUpdateSql(Object pkValue, Map<String, Object> params) {
        StringBuilder kvs = new StringBuilder();
        for (String key : params.keySet()) {
            kvs.append(key).append("=").append(params.get(key));
        }
        return "update " + getTableName() + " set " + kvs +" where " + op.pkField.getName() + " = " + pkValue;
    }
}
