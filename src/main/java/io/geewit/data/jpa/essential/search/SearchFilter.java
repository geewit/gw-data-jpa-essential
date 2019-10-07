package io.geewit.data.jpa.essential.search;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 构造｛@link org.springframework.data.jpa.domain.Specification｝的工具类
 *
 * @author geewit
 * @since 2015-05-18
 */
public class SearchFilter {

    private SearchFilter(String fieldName, Operator operator) {
        this.fieldName = fieldName;
        this.operator = operator;
    }

    SearchFilter(String fieldName, Operator operator, Object value) {
        this.fieldName = fieldName;
        this.operator = operator;
        this.values = new Object[]{value};
    }

    SearchFilter(String fieldName, Operator operator, Object[] values) {
        this.fieldName = fieldName;
        this.operator = operator;
        this.values = values;
    }

    private String fieldName;
    private Operator operator;
    private Object[] values;

    @SuppressWarnings({"unused"})
    public static SearchFilter build(String fieldName, Operator operator) {
        return new SearchFilter(fieldName, operator);
    }

    @SuppressWarnings({"unused"})
    public static SearchFilter build(String fieldName, Operator operator, Object value) {
        return new SearchFilter(fieldName, operator, value);
    }

    @SuppressWarnings({"unused"})
    public static SearchFilter build(String fieldName, Operator operator, Object[] values) {
        return new SearchFilter(fieldName, operator, values);
    }


    /**
     * 解析输入查询语句
     * @param searchParams
     * @return
     */
    @SuppressWarnings({"unused"})
    public static Collection<SearchFilter> parse(Map<String, Object> searchParams) {
        Set<SearchFilter> filters = Sets.newHashSet();
        SearchFilter filter;
        for (Entry<String, Object> entry : searchParams.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String[] names = StringUtils.split(key, "_");
            if (names.length != 2) {
                continue;
            }
            String fieldName = names[1];
            Operator operator = Operator.valueOf(names[0].toUpperCase());
            if (Operator.BETWEEN.equals(operator)) {
                String[] fieldArray = StringUtils.split(value.toString(), ",", 2);
                filter = new SearchFilter(fieldName, operator, fieldArray);
            } else {
                filter = new SearchFilter(fieldName, operator, value.toString());
            }
            filters.add(filter);
        }
        return filters;
    }

    /**
     * 增加filter
     *
     * @param filters
     * @param filter
     */
    @SuppressWarnings({"unused"})
    public static void addFilter(Collection<SearchFilter> filters, SearchFilter filter) {
        boolean notExist = true;
        for (SearchFilter existFilter : filters) {
            if (existFilter.fieldName.equals(filter.fieldName)) {
                notExist = false;
                break;
            }
        }
        if (notExist) {
            filters.add(filter);
        }
    }


    /**
     * 是否存在filter
     *
     * @param filters
     * @param fieldName
     */
    @SuppressWarnings({"unused"})
    public static boolean exists(Collection<SearchFilter> filters, String fieldName) {
        for (SearchFilter existFilter : filters) {
            if (existFilter.fieldName.equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    public String fieldName() {
        return fieldName;
    }

    @SuppressWarnings({"unused"})
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Operator operator() {
        return operator;
    }

    public Object[] values() {
        return values;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchFilter that = (SearchFilter) o;
        return Objects.equal(fieldName, that.fieldName) && Objects.equal(operator, that.operator);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fieldName, operator);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fieldName", fieldName)
                .add("values", values)
                .add("operator", operator)
                .toString();
    }
}
