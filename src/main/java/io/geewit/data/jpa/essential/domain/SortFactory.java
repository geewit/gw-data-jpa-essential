package io.geewit.data.jpa.essential.domain;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 制造Sort的工厂类
 @author gelif
 @since  2015-5-18
 */
@SuppressWarnings({"unused"})
public class SortFactory {
    private final static Direction defaultDirection = Direction.DESC;

    public static Sort create(String sortProperty, Direction direction) {
        if(sortProperty == null) {
            return null;
        }
        return Sort.by(direction, sortProperty);
    }

    public static Sort create(String sortProperty, String order) {
        if(sortProperty == null) {
            return null;
        }
        Direction direction = (StringUtils.isNotBlank(order) && Direction.DESC.name().equalsIgnoreCase(order)) ? Direction.DESC : Direction.ASC;
        return Sort.by(direction, sortProperty);
    }

    public static Sort create(Sort sort, String sortPropterty, Direction direction) {
        List<Order> orderList;
        if(sort != null) {
            orderList = sort.toList();
        } else {
            orderList = new ArrayList<>();
        }
        if(sortPropterty != null) {
            orderList.add(new Order(direction != null ? direction : defaultDirection, sortPropterty));
        }
        return orderList.isEmpty() ? null : Sort.by(orderList);
    }

    public static Sort create(Sort sort, String sortPropterty) {
        return create(sort, sortPropterty, defaultDirection);
    }


    public static Sort create(Sort sort, Order... orders) {
        List<Order> orderList;
        if(sort != null) {
            orderList = sort.toList();
        } else {
            orderList = new ArrayList<>();
        }
        if(orders != null) {
            Collections.addAll(orderList, orders);
        }
        return CollectionUtils.isEmpty(orderList) ? null : Sort.by(orderList);
    }

    public static Sort create(Sort... sorts) {
        if(ArrayUtils.isEmpty(sorts)) {
            return null;
        }
        List<Order> orderList = new ArrayList<>();
        for(Sort sort : sorts) {
            if(sort != null) {
                orderList.addAll(sort.toList());
            }
        }
        return Sort.by(orderList);
    }
}
