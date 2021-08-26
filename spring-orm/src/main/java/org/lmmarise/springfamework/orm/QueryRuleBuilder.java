package org.lmmarise.springfamework.orm;

import org.lmmarise.springfamework.orm.common.Order;
import org.lmmarise.springfamework.orm.common.QueryRule;
import org.lmmarise.springframework.util.ArrayUtils;
import org.lmmarise.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 根据用户构建好的 QueryRule 生成 SQL
 *
 * @author lmmarise.j@gmail.com
 * @since 2021/8/26 1:14 下午s
 */
public class QueryRuleBuilder {
    private int CURR_INDEX = 0;         // 参数所在位置
    private List<String> properties;    // 列名列表
    private List<Object> values;        // 保存参数值列表
    private List<Order> orders;         // 排序规则列表

    private String whereSql = "";
    private String orderSql = "";
    private Object[] valueArr = new Object[]{};
    private Map<Object, Object> valueMap = new HashMap<>();

    public String getWhereSql() {
        return whereSql;
    }

    public String getOrderSql() {
        return orderSql;
    }

    public Map<Object, Object> getValueMap() {
        return valueMap;
    }

    public QueryRuleBuilder(QueryRule queryRule) {
        CURR_INDEX = 0;
        properties = new ArrayList<>();
        values = new ArrayList<>();
        for (QueryRule.Rule rule : queryRule.getRuleList()) {
            switch (rule.getType()) {
                case QueryRule.BETWEEN:
                    processBetween(rule);
                    break;
                case QueryRule.EQ:
                    processEqual(rule);
                    break;
                case QueryRule.LIKE:
                    processLike(rule);
                    break;
                case QueryRule.NOTEQ:
                    processNotEqual(rule);
                    break;
                case QueryRule.GT:
                    processGreaterThan(rule);
                    break;
                case QueryRule.GE:
                    processGreaterEqual(rule);
                    break;
                case QueryRule.LT:
                    processLessThan(rule);
                    break;
                case QueryRule.LE:
                    processLessEqual(rule);
                    break;
                case QueryRule.IN:
                    processIn(rule);
                    break;
                case QueryRule.NOTIN:
                    processNotIn(rule);
                    break;
                case QueryRule.ISNULL:
                    processIsNull(rule);
                    break;
                case QueryRule.ISNOTNULL:
                    processIsNotNull(rule);
                    break;
                case QueryRule.ISEMPTY:
                    processIsEmpty(rule);
                    break;
                case QueryRule.ISNOTEMPTY:
                    processIsNotEmpty(rule);
                    break;
                case QueryRule.ASC_ORDER:
                case QueryRule.DESC_ORDER:
                    processOrder(rule);
                    break;
                default:
                    throw new IllegalArgumentException("type " + rule.getType() + " not supported.");
            }
        }
        appendWhereSql();
        appendOrderSql();
        appendValues();
    }

    private void appendValues() {
        Object[] val = new Object[values.size()];
        for (int i = 0; i < values.size(); i++) {
            val[i] = values.get(i);
            valueMap.put(i, values.get(i));
        }
        this.valueArr = val;
    }

    private void appendWhereSql() {
        StringBuilder sb = new StringBuilder();
        for (String property : properties) {
            sb.append(property);
        }
        this.whereSql = removeSelect(removeOrders(sb.toString()));
    }

    private void appendOrderSql() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < orders.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(orders.get(i).toString());
        }
        this.orderSql = removeSelect(removeOrders(sb.toString()));
    }

    private void processOrder(QueryRule.Rule rule) {
        switch (rule.getType()) {
            case QueryRule.ASC_ORDER:
                if (!StringUtils.isEmpty(rule.getPropertyName())) {
                    orders.add(Order.asc(rule.getPropertyName()));
                }
                break;
            case QueryRule.DESC_ORDER:
                if (!StringUtils.isEmpty(rule.getPropertyName())) {
                    orders.add(Order.desc(rule.getPropertyName()));
                }
                break;
            default:
                break;
        }
    }

    private void add(int andOr, String key, String split, Object value) {
        add(andOr, key, split, "", value, "");
    }

    /**
     * 加入 SQL 查询规则队列
     *
     * @param andOr  and 或 or
     * @param key    列名
     * @param split  列名与值之间的间隔
     * @param prefix 前缀值
     * @param value  值
     * @param suffix 后缀值
     */
    private void add(int andOr, String key, String split, String prefix, Object value, String suffix) {
        String andOrStr = (0 == andOr ? "" : (QueryRule.AND == andOr ? " and " : " or "));
        properties.add(CURR_INDEX, andOrStr + key + " " + split + prefix + (null != value ? " ? " : " " + suffix));
        if (null != value) {
            values.add(CURR_INDEX++, value);
        }
    }

    private void processNotIn(QueryRule.Rule rule) {
        inAndNotIn(rule, "not in");
    }

    private void processIn(QueryRule.Rule rule) {
        inAndNotIn(rule, "in");
    }

    private void inAndNotIn(QueryRule.Rule rule, String name) {
        if (ArrayUtils.isEmpty(rule.getValues())) {
            return;
        }
        // 参数是 [List] 类型
        if ((rule.getValues().length == 1) && (rule.getValues()[0] != null) && (rule.getValues()[0] instanceof List)) {
            List<Object> list = (List) rule.getValues()[0];
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    // 只有一个参数
                    if (i == 0 && list.size() == 1) {
                        add(rule.getAndOr(), rule.getPropertyName(), "", name + " (", list.get(i), ")");
                    }
                    // 有多个参数，当前是第一个参数
                    else if (i == 0) {
                        add(rule.getAndOr(), rule.getPropertyName(), "", name + " (", list.get(i), "");
                    }
                    // 有多个参数，当前是中间参数
                    if (i > 0 && i < list.size() - 1) {
                        add(0, "", "", "", list.get(i), "");
                    }
                    // 有多个参数，当前是最后一个参数
                    else if (i == list.size() - 1 && i != 0) {
                        add(0, "", "", "", list.get(i), ")");
                    }
                }
            }
        }
        // 参数是 [value, value...] 类型
        else {
            Object[] values = rule.getValues();
            for (int i = 0; i < values.length; i++) {
                if (i == 0 && values.length == 1) {
                    add(rule.getAndOr(), rule.getPropertyName(), "", name + " (", values[i], ")");
                } else if (i == 0) {
                    add(rule.getAndOr(), rule.getPropertyName(), "", name + " (", values[i], "");
                }
                if (i > 0 && i < values.length - 1) {
                    add(0, "", "", "", values[i], "");
                } else if (i == values.length - 1 && i != 0) {
                    add(0, "", "", "", values[i], ")");
                }
            }
        }
    }

    private void processIsNotEmpty(QueryRule.Rule rule) {
        if (ArrayUtils.isEmpty(rule.getValues())) {
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "<>", "''");
    }

    private void processIsEmpty(QueryRule.Rule rule) {
        if (ArrayUtils.isEmpty(rule.getValues())) {
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "=", "''");
    }

    private void processIsNotNull(QueryRule.Rule rule) {
        if (ArrayUtils.isEmpty(rule.getValues())) {
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "is not null", null);
    }

    private void processIsNull(QueryRule.Rule rule) {
        if (ArrayUtils.isEmpty(rule.getValues())) {
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "is null", null);
    }

    private void processLessEqual(QueryRule.Rule rule) {
        if (ArrayUtils.isEmpty(rule.getValues())) {
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "<=", rule.getValues()[0]);
    }

    private void processLessThan(QueryRule.Rule rule) {
        if (ArrayUtils.isEmpty(rule.getValues())) {
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "<", rule.getValues()[0]);
    }

    private void processGreaterEqual(QueryRule.Rule rule) {
        if (ArrayUtils.isEmpty(rule.getValues())) {
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), ">=", rule.getValues()[0]);
    }

    private void processGreaterThan(QueryRule.Rule rule) {
        if (ArrayUtils.isEmpty(rule.getValues())) {
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), ">", rule.getValues()[0]);
    }

    private void processNotEqual(QueryRule.Rule rule) {
        if (ArrayUtils.isEmpty(rule.getValues())) {
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "<>", rule.getValues()[0]);
    }

    private void processEqual(QueryRule.Rule rule) {
        if (ArrayUtils.isEmpty(rule.getValues())) {
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "=", rule.getValues()[0]);
    }

    private void processLike(QueryRule.Rule rule) {
        if (ArrayUtils.isEmpty(rule.getValues())) {
            return;
        }
        Object obj = rule.getValues()[0];
        if (obj == null) {
            String value = obj.toString();
            if (!StringUtils.isEmpty(value)) {
                value = value.replace('*', '%');
                obj = value;
            }
        }
        add(rule.getAndOr(), rule.getPropertyName(), "like", "%" + rule.getValues()[0] + "%");
    }

    private void processBetween(QueryRule.Rule rule) {
        if ((ArrayUtils.isEmpty(rule.getValues()) || (rule.getValues().length < 2))) {
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "", "between", rule.getValues()[0], "and");
        add(0, "", "", "", rule.getValues()[1], "");
    }

    /**
     * 去掉 order
     */
    protected String removeOrders(String sql) {
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 去掉 select
     */
    protected String removeSelect(String sql) {
        if (sql.toLowerCase().matches("from\\s+")) {
            int beginPos = sql.toLowerCase().indexOf("from");
            return sql.substring(beginPos);
        } else {
            return sql;
        }
    }


    public Object[] getValues() {
        return this.valueArr;
    }
}
