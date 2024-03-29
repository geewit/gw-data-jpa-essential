package io.geewit.data.jpa.essential.domain;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 制造Pageable的工厂类
 @author geewit
 @since  2015-5-18
 */
@SuppressWarnings({"unused"})
public class PageableFactory {
    private final static int DEFAULT_SIZE = 20;
    private final static String DEFAULT_SORT = null;

    public static Pageable create(Integer page, Integer size) {
        return PageRequest.of(page, null != size && size > 0 ? size : DEFAULT_SIZE);
    }

    public static Pageable create(Integer page, Integer size, int defaultSize) {
        return PageRequest.of(page, null != size && size > 0 ? size : defaultSize);
    }

    public static Pageable create(Integer page, Integer size, Sort sort) {
        return PageRequest.of(page, null != size && size > 0 ? size : DEFAULT_SIZE, sort);
    }

    public static Pageable create(Integer page, Integer size, int defaultSize, Sort sort) {
        return PageRequest.of(page, null != size && size > 0 ? size : defaultSize, sort);
    }

    public static Pageable create(Integer page, Integer size, String sort, String order) {
        return create(page, size, DEFAULT_SIZE, sort, DEFAULT_SORT, order);
    }

    public static Pageable create(Integer page, Integer size, int defaultSize, String sort, String order) {
        return create(page, size, defaultSize, sort, DEFAULT_SORT, order);
    }

    public static Pageable create(Integer page, Integer size, String sort, String defaultSort, String order) {
        return create(page, size, DEFAULT_SIZE, sort, defaultSort, order);
    }

    public static Pageable create(Integer page, Integer size, int defaultSize, String sortProperty, String defaultSort, String order) {
        Sort sort = SortFactory.create(sortProperty == null ? defaultSort : sortProperty, order);
        return PageRequest.of(page, null != size && size > 0 ? size : defaultSize, sort);
    }

    public static Pageable create(Pageable pageable, String sortPropterty, Sort.Direction direction) {
        Sort sort = SortFactory.create(pageable.getSort(), sortPropterty, direction);
        return create(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    public static Pageable create(Pageable pageable, String sortPropterty) {
        Sort sort = SortFactory.create(pageable.getSort(), sortPropterty);
        return create(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    public static Pageable create(Pageable pageable, Sort sort) {
        sort = SortFactory.create(pageable.getSort(), sort);
        return create(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    public static Pageable create(Pageable pageable, Sort.Order... orders) {
        Sort sort = SortFactory.create(pageable.getSort(), orders);
        return create(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    public static Pageable ofDefaultSort(Pageable pageable, Sort defaultSort) {
        if(pageable == null) {
            pageable = PageRequest.ofSize(DEFAULT_SIZE);
        }
        if(pageable.getSort().stream().noneMatch(order -> defaultSort.stream().anyMatch(defaultOrder -> defaultOrder.getProperty().equals(order.getProperty())))) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort.and(pageable.getSort()));
        }
        return pageable;
    }
}
