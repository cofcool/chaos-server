package net.cofcool.chaos.server.data.jpa.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.persistence.Transient;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.PageProcessor;
import net.cofcool.chaos.server.common.util.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author CofCool
 */
public class Paging<T> extends Page<T> {

    private static final long serialVersionUID = -1288144366898317311L;

    @SuppressWarnings("deprecation")
    public Paging() {}

    private Paging(List<T> content) {
        super(content);
    }

    /**
     * 根据Spring Data的Page创建Page
     * @param page Page实例
     * @param <T> 模型类
     * @return Page实例
     */
    @Nonnull
    public static <T> Page<T> of(@Nonnull org.springframework.data.domain.Page<T> page) {
        return createPaging(page);
    }

    private static <T> Paging<T> createPaging(org.springframework.data.domain.Page<T> page) {
        Paging<T> mPage = new Paging<>(page.getContent());
        mPage.setFirstPage(page.isFirst());
        mPage.setLastPage(page.isLast());
        mPage.setTotal(page.getTotalElements());
        mPage.setPageNumber(page.getNumber());
        mPage.setPageSize(page.getSize());
        mPage.setPages(page.getTotalPages());

        return mPage;
    }

    /**
     * Page中的元素为List时，本方法可合并List的元素到 primaryBean。
     * 当primaryBean中的getter方法使用@Transient标注时，即触发合并操作。<br>
     *
     * 注意：primaryBean需有getter和setter方法
     *
     * @param page page
     * @param primaryBean 主类
     * @param <T> 元素类型
     * @return page
     */
    @SuppressWarnings("unchecked")
    public static <T> Page<T> of(org.springframework.data.domain.Page<List<T>> page, Class<T> primaryBean) {
        Paging mPage = createPaging(page);
        List mPageList = mPage.getList();
        if (mPageList.isEmpty()) {
            return mPage;
        }

        Map<String, Class> propertyTypes = new HashMap<>();
        for (Method method : primaryBean.getMethods()) {
            if (method.getAnnotation(Transient.class) != null) {
                propertyTypes.put(method.getName(), method.getReturnType());
            }
        }

        if (propertyTypes.isEmpty()) {
            return mPage;
        }

        List mutableList = new ArrayList<>(mPageList.size());

        Map<Class, Object> classValueMap = new HashMap<>();
        for (int i = 0; i < mPageList.size(); i++) {
            Object item = mPageList.get(i);
            // 连表时有多个结果的话，item类型为数组，即Object[]
            if (item.getClass().isArray()) {
                Object[] result = (Object[]) item;
                for (Object cItem : result) {
                    if (cItem != null) {
                        classValueMap.put(cItem.getClass(), cItem);
                    }
                }
            }

            Object primaryObj = classValueMap.get(primaryBean);
            propertyTypes.forEach((key, val) -> {
                try {
                    primaryBean.getMethod(BeanUtils.getterToSetter(key), val).invoke(primaryObj, classValueMap.get(val));
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {
                }
            });
            mutableList.add(primaryObj);

            classValueMap.clear();
        }

        mPage.setList(Collections.unmodifiableList(mutableList));

        return mPage;
    }

    /**
     * 获取 Pageable 实例
     * @return Pageable
     *
     * @see JpaPage
     */
    @JsonIgnore
    public Pageable getPageable() {
        return new JpaPage(getPageNumber(), getPageSize());
    }

    /**
     * 获取 Pageable 实例
     */
    @JsonIgnore
    public Pageable getPageable(Sort sort) {
        return getPageable(getPageNumber(), getPageSize(), sort);
    }

    /**
     * 获取 Pageable 实例
     */
    @JsonIgnore
    public Pageable getPageable(Integer pageNumber, Integer pageSize, Sort sort) {
        return new JpaPage(getPageNumber(), getPageSize(), sort);
    }

    /**
     * 获取 Pageable 实例
     */
    @JsonIgnore
    public static Pageable getPageable(Page page, Sort sort) {
        return new JpaPage(page.getPageNumber(), page.getPageSize(), sort);
    }

    /**
     * 获取 Pageable 实例
     */
    @JsonIgnore
    public static Pageable getPageable(Page page) {
        return new JpaPage(page.getPageNumber(), page.getPageSize());
    }

    /**
     * 获取分页处理器
     */
    @SuppressWarnings("unchecked")
    public static <T> PageProcessor<T> getPageProcessor() {
        return (PageProcessor<T>) PAGE_PROCESSOR;
    }

    private static final PageProcessor<?> PAGE_PROCESSOR = new JpaPageProcessor<>();

    private static final class JpaPageProcessor<T> implements PageProcessor<T> {

        @SuppressWarnings("unchecked")
        @Override
        public Page<T> process(Page<T> condition, Object pageSomething) {
            Objects.requireNonNull(condition);
            Objects.requireNonNull(pageSomething);

            if (pageSomething instanceof org.springframework.data.domain.Page) {
                return of((org.springframework.data.domain.Page) pageSomething);
            }


            throw new IllegalArgumentException("must be Page instance");
        }

    }

}
