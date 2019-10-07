package io.geewit.data.jpa.essential.search;

import com.google.common.collect.Lists;
import io.geewit.core.exception.ProcessedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 构造 {@link org.springframework.data.jpa.domain.Specification} 的工具类
 *
 * @author geewit
 * @since 2015-05-18
 */
@SuppressWarnings({"unchecked", "unused"})
public class DynamicSpecifications {
    private final static Logger logger = LoggerFactory.getLogger(DynamicSpecifications.class);

    /**
     * 从 {@link SearchFilter} 构造 {@link org.springframework.data.jpa.domain.Specification}
     *
     * @param filters {@link SearchFilter}
     * @return {@link org.springframework.data.jpa.domain.Specification}
     */
    public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> filters) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (filters != null && !filters.isEmpty()) {
                logger.debug("filters != null");
                List<Predicate> predicates = Lists.newArrayList();
                for (SearchFilter filter : filters) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("filter.fieldName = " + filter.fieldName());
                        logger.debug("filter.values = " + StringUtils.join(filter.values(), ","));
                        logger.debug("filter.operator = " + filter.operator());
                        logger.debug("root.model = " + root.getModel().getName());
                    }
                    Path path;
                    if (StringUtils.contains(filter.fieldName(), '$')) {
                        String[] fields = StringUtils.split(filter.fieldName(), '$');
                        path = root.<String>get(fields[0]);
                        for (int i = 1; i < fields.length; i++) {
                            path = path.get(fields[i]);
                            if (path == null) {
                                break;
                            }
                        }
                    } else {
                        path = root.<String>get(filter.fieldName());
                    }
                    if (path == null) {
                        break;
                    }

                    switch (filter.operator()) {
                        case EQ: {
                            logger.debug("case EQ");
                            if (filter.values() != null && filter.values().length > 0) {
                                Object value = parseValue(path, filter.values()[0]);
                                if (value != null) {
                                    predicates.add(criteriaBuilder.equal(path, value));
                                }
                            }
                            break;
                        }
                        case NE: {
                            logger.debug("case NE");
                            if (filter.values() != null && filter.values().length > 0) {
                                Object value = parseValue(path, filter.values()[0]);
                                if (value != null) {
                                    predicates.add(criteriaBuilder.notEqual(path, value));
                                }
                            }
                            break;
                        }
                        case LIKE: {
                            logger.debug("case LIKE");
                            if (filter.values() != null && filter.values().length > 0) {
                                String like;
                                if (filter.values()[0] instanceof String) {
                                    like = (String) filter.values()[0];
                                } else {
                                    like = filter.values()[0].toString();
                                }
                                if (StringUtils.isNotEmpty(like)) {
                                    predicates.add(criteriaBuilder.like(path, "%" + like + "%"));
                                }
                            }
                            break;
                        }
                        case LLIKE: {
                            logger.debug("case LLIKE");
                            if (filter.values() != null && filter.values().length > 0) {
                                String like;
                                if (filter.values()[0] instanceof String) {
                                    like = (String) filter.values()[0];
                                } else {
                                    like = filter.values()[0].toString();
                                }
                                if (StringUtils.isNotEmpty(like)) {
                                    predicates.add(criteriaBuilder.like(path, "%" + like));
                                }
                            }
                            break;
                        }
                        case RLIKE: {
                            logger.debug("case RLIKE");
                            if (filter.values() != null && filter.values().length > 0) {
                                String like;
                                if (filter.values()[0] instanceof String) {
                                    like = (String) filter.values()[0];
                                } else {
                                    like = filter.values()[0].toString();
                                }
                                if (StringUtils.isNotEmpty(like)) {
                                    predicates.add(criteriaBuilder.like(path, like + "%"));
                                }
                            }
                            break;
                        }
                        case GT: {
                            logger.debug("case GT");
                            if (filter.values() != null && filter.values().length > 0) {
                                Object value = parseValue(path, filter.values()[0]);
                                if (value instanceof Comparable) {
                                    predicates.add(criteriaBuilder.greaterThan(path, (Comparable) value));
                                }
                            }
                            break;
                        }
                        case GTE: {
                            logger.debug("case GTE");
                            if (filter.values() != null && filter.values().length > 0) {
                                Object value = parseValue(path, filter.values()[0]);
                                if (value instanceof Comparable) {
                                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, (Comparable) value));
                                }
                            }
                            break;
                        }
                        case LT: {
                            logger.debug("case LT");
                            if (filter.values() != null && filter.values().length > 0) {
                                Object value = parseValue(path, filter.values()[0]);
                                if (value instanceof Comparable) {
                                    predicates.add(criteriaBuilder.lessThan(path, (Comparable) value));
                                }
                            }
                            break;
                        }
                        case LTE: {
                            logger.debug("case LTE");
                            if (filter.values() != null && filter.values().length > 0) {
                                Object value = parseValue(path, filter.values()[0]);
                                if (value instanceof Comparable) {
                                    predicates.add(criteriaBuilder.lessThanOrEqualTo(path, (Comparable) value));
                                }
                            }
                            break;
                        }
                        case BETWEEN: {
                            logger.debug("case BETWEEN");
                            if (filter.values().length == 2) {
                                Object[] values = parseValues(path, filter.operator(), filter.values());
                                if (values != null && values.length == 2) {
                                    if (values[0] != null && values[1] != null) {
                                        predicates.add(criteriaBuilder.between(path, ((Comparable) values[0]), ((Comparable) values[1])));
                                    } else if (values[0] != null) {
                                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(path, ((Comparable) values[0])));
                                    } else if (values[1] != null) {
                                        predicates.add(criteriaBuilder.lessThanOrEqualTo(path, ((Comparable) values[1])));
                                    }
                                }
                            }
                            break;
                        }
                        case IN: {
                            logger.debug("case IN");
                            if (filter.values() != null && filter.values().length > 0) {
                                Object[] values = parseValues(path, filter.operator(), filter.values());
                                predicates.add(path.in(values));
                            }
                            break;
                        }
                        case NOTIN: {
                            logger.debug("case NOTIN");
                            if (filter.values() != null && filter.values().length > 0) {
                                Object[] values = parseValues(path, filter.operator(), filter.values());
                                predicates.add(criteriaBuilder.not(path.in(values)));
                            }
                            break;
                        }
                        case ISNULL: {
                            logger.debug("case IS NULL");
                            predicates.add(criteriaBuilder.isNull(path));
                            break;
                        }
                        case ISNOTNULL: {
                            logger.debug("case IS NOT NULL");
                            predicates.add(criteriaBuilder.isNotNull(path));
                            break;
                        }
                        default: {
                            throw new ProcessedException("错误的参数");
                        }
                    }
                }

                if (!predicates.isEmpty()) {
                    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                }
            } else {
                logger.debug("filters == null");
            }

            return criteriaBuilder.conjunction();
        };
    }

    /**
     * 合并多个 {@link org.springframework.data.jpa.domain.Specification}
     *
     * @param firstSpecification 第一个{@link org.springframework.data.jpa.domain.Specification}
     * @param specifications     需要合并的{@link org.springframework.data.jpa.domain.Specification}
     * @return {@link org.springframework.data.jpa.domain.Specification}
     */
    public static <T> Specification<T> mergeSpecification(Specification<T> firstSpecification, Specification<T>... specifications) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = Lists.newArrayList(firstSpecification.toPredicate(root, criteriaQuery, criteriaBuilder));
            if (specifications != null) {
                for (Specification<T> specification : specifications) {
                    predicates.add(specification.toPredicate(root, criteriaQuery, criteriaBuilder));
                }
            }
            if (!predicates.isEmpty()) {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
            return criteriaBuilder.conjunction();
        };
    }


    private static Object parseValue(Class pathClass, Object value) {
        if (pathClass == null || value == null) {
            return null;
        }
        if (String.class.isAssignableFrom(pathClass)) {
            if (value instanceof String) {
                return value;
            } else {
                return value.toString();
            }
        } else if (pathClass.isEnum()) {
            logger.debug("path is enum");
            try {
                if (value.getClass().isAssignableFrom(pathClass)) {
                    return value;
                } else {
                    return Enum.valueOf(pathClass, value.toString());
                }
            } catch (IllegalArgumentException e) {
                logger.warn(e.getMessage());
                return null;
            }
        } else if (Date.class.isAssignableFrom(pathClass)) {
            if (value.getClass().isAssignableFrom(pathClass)) {
                return value;
            } else {
                return date(value.toString());
            }
        } else if (Number.class.isAssignableFrom(pathClass)) {
            if (value.getClass().isAssignableFrom(pathClass)) {
                return value;
            } else {
                if (value instanceof String) {
                    return org.springframework.util.NumberUtils.parseNumber((String) value, pathClass);
                } else {
                    return org.springframework.util.NumberUtils.parseNumber(value.toString(), pathClass);
                }
            }

        }
        return value;
    }

    private static Object parseValue(Path path, Object value) {
        if (path == null || value == null) {
            return null;
        }
        Class pathClass = path.getJavaType();

        return parseValue(pathClass, value);
    }

    private static Object[] parseValues(Class pathClass, Operator operator, Object... values) {
        if (Operator.BETWEEN.equals(operator)) {
            if (values.length != 2 || !Comparable.class.isAssignableFrom(pathClass)) {
                return null;
            }
            Arrays.sort(values);
            if (Date.class.isAssignableFrom(pathClass)) {
                Date fromDate;
                if (values[0].getClass().isAssignableFrom(pathClass)) {
                    fromDate = (Date) values[0];
                } else {
                    if (values[0] instanceof String) {
                        fromDate = fromDate((String) values[0]);
                    } else {
                        fromDate = fromDate(values[0].toString());
                    }
                }
                Date toDate;
                if (values[1].getClass().isAssignableFrom(pathClass)) {
                    toDate = (Date) values[1];
                } else {
                    if (values[1] instanceof String) {
                        toDate = toDate((String) values[1]);
                    } else {
                        toDate = toDate(values[1].toString());
                    }
                }
                return new Date[]{fromDate, toDate};
            }
        }
        return Arrays.stream(values).map(value -> parseValue(pathClass, value)).toArray(Object[]::new);
    }

    private static Object[] parseValues(Path path, Operator operator, Object... values) {
        return parseValues(path.getJavaType(), operator, values);
    }

    private static final String[] PATTERNS = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "HH:mm:ss"};

    private static Date date(String date) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDateStrictly(date, PATTERNS);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 转换 {@link String} 类型的date为 {@link Date}类型 时分秒为00:00:00
     *
     * @param value {@link java.util.Date} 类型
     */
    private static Date fromDate(String value) {
        if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}", value)) {
            return date(value);
        } else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}", value)) {
            Calendar calendar = Calendar.getInstance();
            Date date = date(value);
            if (date == null) {
                return null;
            }
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime();
        } else {
            return null;
        }
    }

    /**
     * 转换 {@link String} 类型的date为 {@link Date}类型 时分秒为23:59:59
     *
     * @param value {@link java.util.Date} 类型
     */
    private static Date toDate(String value) {
        if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}", value)) {
            return date(value);
        } else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}", value)) {
            Calendar calendar = Calendar.getInstance();
            Date date = date(value);
            if (date == null) {
                return null;
            }
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            return calendar.getTime();
        } else {
            return null;
        }
    }
}

