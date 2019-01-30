package net.cofcool.chaos.server.data.jpa.support;

import java.io.Serializable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * JPA分页模型，实现Pageable接口
 *
 * @author CofCool
 *
 * @see Pageable
 * @see org.springframework.data.domain.PageRequest
 */
public class JpaPage implements Pageable, Serializable {

    private static final long serialVersionUID = -76703640096945681L;

    private final Integer pageNumber;

    private final Integer pageSize;

    private final Sort sort;


    public JpaPage(Integer pageNumber, Integer pageSize) {
        this(pageNumber, pageSize, Sort.unsorted());
    }

    public JpaPage(Integer pageNumber, Integer pageSize, Sort sort) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public long getOffset() {
        return (long) pageNumber * (long) pageSize;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new JpaPage(getPageNumber() + 1, getPageSize(), getSort());
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new JpaPage(0, getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return pageNumber > 0;
    }

    public JpaPage previous() {
        return getPageNumber() == 0 ? this : new JpaPage(getPageNumber() - 1, getPageSize(), getSort());
    }
}
