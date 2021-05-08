package io.geewit.data.jpa.essential.domain;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.util.Streamable;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 制造Sort的工厂类
 @author gelif
 @since  2015-5-18
 */
@SuppressWarnings({"unused"})
public class SortFactory {
    private final static Direction DEFAULT_DIRECTION = Direction.DESC;

    public static Sort create(String sortProperty, Direction direction) {
        if(sortProperty == null) {
            return Sort.unsorted();
        }
        return Sort.by(direction, sortProperty);
    }

    public static Sort create(String sortProperty, String order) {
        if(sortProperty == null) {
            return Sort.unsorted();
        }
        Direction direction = (StringUtils.isNotBlank(order) && Direction.DESC.name().equalsIgnoreCase(order)) ? Direction.DESC : Direction.ASC;
        return Sort.by(direction, sortProperty);
    }

    public static Sort create(Sort sort, String sortPropterty, Direction direction) {
        List<Order> orderList = new ArrayList<>();
        if(sort != null) {
            orderList.addAll(sort.toList());
        }
        if(sortPropterty != null) {
            orderList.add(new Order(direction != null ? direction : DEFAULT_DIRECTION, sortPropterty));
        }
        return orderList.isEmpty() ? Sort.unsorted() : Sort.by(orderList);
    }

    public static Sort create(Sort sort, String sortPropterty) {
        return create(sort, sortPropterty, DEFAULT_DIRECTION);
    }


    public static Sort create(Sort sort, Order... orders) {
        List<Order> orderList = new ArrayList<>();
        if(sort != null) {
            orderList.addAll(sort.toList());
        }
        if(orders != null) {
            Collections.addAll(orderList, orders);
        }
        return CollectionUtils.isEmpty(orderList) ? Sort.unsorted() : Sort.by(orderList);
    }

    public static Sort create(Sort... sorts) {
        if(ArrayUtils.isEmpty(sorts)) {
            return Sort.unsorted();
        }
        List<Order> orderList = new ArrayList<>();
        Arrays.stream(sorts).filter(Objects::nonNull).map(Streamable::toList).forEach(orderList::addAll);
        return Sort.by(orderList);
    }

    public static Sort ofDefaultSort(Sort sort, Sort defaultSort) {
        if(sort == null) {
            sort = defaultSort;
        } else if(sort.stream().noneMatch(order -> defaultSort.stream().anyMatch(defaultOrder -> defaultOrder.getProperty().equals(order.getProperty())))) {
            sort = defaultSort.and(sort);
        }
        return sort;
    }
}
