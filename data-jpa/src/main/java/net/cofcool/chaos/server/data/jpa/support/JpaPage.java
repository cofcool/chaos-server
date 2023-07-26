/*
 * Copyright 2019 cofcool
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cofcool.chaos.server.data.jpa.support;

import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * JPA分页模型, 实现 {@link Pageable} 接口
 *
 * @author CofCool
 *
 * @see org.springframework.data.domain.PageRequest
 */
public class JpaPage extends AbstractPageRequest {

    private static final long serialVersionUID = -76703640096945681L;

    private final Sort sort;


    public JpaPage(Integer pageNumber, Integer pageSize) {
        this(pageNumber, pageSize, Sort.unsorted());
    }

    public JpaPage(Integer pageNumber, Integer pageSize, Sort sort) {
        super(pageNumber, pageSize);

        Assert.notNull(sort, "Sort must not be null!");
        this.sort = sort;
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
    public Pageable previous() {
        return getPageNumber() == 0 ? this : new JpaPage(getPageNumber() - 1, getPageSize(), getSort());
    }

    @Override
    public Pageable first() {
        return new JpaPage(0, getPageSize(), getSort());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new JpaPage(pageNumber, getPageSize(), getSort());
    }

    @Override
    public String toString() {
        return String.format("Page request [number: %d, size %d, sort: %s]", getPageNumber(), getPageSize(), sort);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + sort.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof JpaPage)) {
            return false;
        }

        JpaPage that = (JpaPage) obj;

        return super.equals(that) && this.sort.equals(that.sort);
    }
}
