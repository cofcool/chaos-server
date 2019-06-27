package net.cofcool.chaos.server.demo.api;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExecuteResult;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.PageSupport;
import net.cofcool.chaos.server.common.core.QueryResult;
import net.cofcool.chaos.server.common.core.Result.ResultState;
import net.cofcool.chaos.server.common.util.BeanUtils;
import net.cofcool.chaos.server.data.jpa.support.SimpleJpaService;
import net.cofcool.chaos.server.data.jpa.util.SpecificationUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Service 基本实现
 */
public abstract class BaseServiceImpl<T, ID, J extends JpaRepository<T, ID>> extends
    SimpleJpaService<T, ID, J> implements BaseService<T> {

    @Override
    protected Object queryWithPage(Page<T> condition, T entity) {
        Specification<T> sp = getSp();

        if (condition.getStartDate() != null) {
            sp = SpecificationUtils.andGreaterOrEqualTo(sp, "createTime", condition.startDate());
        }
        if (condition.getEndDate() != null) {
            sp = SpecificationUtils.andLessOrEqualTo(sp, "updateTime", condition.endDate());
        }

        if (shouldCheck()) {
            sp = checkId(sp, entity);
        }

        return queryWithSp(sp, condition, entity);
    }

    @Override
    public QueryResult<T, ?> simpleQuery(Page<T> condition, T entity) {
        Page<T> page = PageSupport.checkPage(condition);

        Objects.requireNonNull(getPageProcessor());
        return QueryResult.of(
            getPageProcessor().process(page, super.queryWithPage(page, entity)),
            getExceptionCodeManager().getCode(ExceptionCodeDescriptor.SERVER_OK),
            getExceptionCodeManager().getDescription(ExceptionCodeDescriptor.SERVER_OK_DESC)
        );
    }

    @Override
    public ExecuteResult<List<T>> simpleQueryAll(T entity) {
        return super.queryAll(entity);
    }

    @Override
    public ExecuteResult<T> simpleQueryById(T entity) {
        return super.queryById(entity);
    }

    /**
     * 是否需要检查 {@link #ROLE_IDS}
     * @return 是否需要检查
     */
    protected boolean shouldCheck() {
        return true;
    }

    @Override
    public ExecuteResult<T> queryById(T entity) {
        if (getEntityId(entity) != null) {
            return super.queryById(entity);
        } else {
            return createExecuteResultBy(getJpaRepository().findOne(Example.of(entity)));
        }
    }


    @Override
    public ResultState delete(T entity) {
        if (physicDelete()) {
            return super.delete(entity);
        }

        ExecuteResult<T> result = queryById(entity);

        if (result.successful()) {
            logicDeleteCallback().apply(result.entity());

            return update(result.entity()).state();
        }

        return result.state();
    }

    /**
     * 是否物理删除
     * @return 是否物理删除
     */
    protected boolean physicDelete() {
        return true;
    }

    /**
     * 处理逻辑删除前的逻辑, 即修改数据删除标志等
     * @return Function
     */
    protected Function<T, T> logicDeleteCallback() {
        return null;
    }

    protected Specification<T> getSp() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    protected abstract Object queryWithSp(Specification<T> sp, Page<T> condition, T entity);

    protected Specification<T> checkId(Specification<T> sp, T entity) {

        for (String id : ROLE_IDS) {
            try {
                Long v = (Long) BeanUtils.getPropertyDescriptor(entity.getClass(), id).getReadMethod().invoke(entity);
                sp = andEqual(sp, v, id);
            } catch (IllegalAccessException | InvocationTargetException | NullPointerException ignore) {
            }
        }

        return sp;
    }

    public static  <T, Y extends Comparable<? super Y>> Specification<T> andEqual(Specification<T> sp, Y val, String property) {
        if (val != null) {
            sp = SpecificationUtils.andEqualTo(sp, property, val);
        }

        return sp;
    }

    public static  <T, Y extends Comparable<? super Y>> Specification<T> andLike(Specification<T> sp, Y val, String property) {
        if (val != null) {
            sp = SpecificationUtils.andLikeTo(sp, property, val, true);
        }

        return sp;
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> andGreater(Specification<T> sp, Y val, String property) {
        if (val != null) {
            sp = SpecificationUtils.andGreaterOrEqualTo(sp, property, val);
        }

        return sp;
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> andLess(Specification<T> sp, Y val, String property) {
        if (val != null) {
            sp = SpecificationUtils.andLessOrEqualTo(sp, property, val);
        }

        return sp;
    }

    public <R> ExecuteResult<R> createExecuteResultBy(Optional<R> data) {
        return data.map(t ->
            ExecuteResult.of(t, ResultState.SUCCESSFUL, getExceptionCodeManager().getCode(
                ExceptionCodeDescriptor.SERVER_OK),
                getExceptionCodeManager().getDescription(ExceptionCodeDescriptor.SERVER_OK)))
            .orElseGet(() ->
                ExecuteResult.of(null, ResultState.FAILURE,
                    getExceptionCodeManager().getCode(ExceptionCodeDescriptor.OPERATION_ERR),
                    getExceptionCodeManager().getDescription(ExceptionCodeDescriptor.OPERATION_ERR)));
    }

    public static List<String> ROLE_IDS = Arrays.asList("vipId", "userId");
    public static List<String> ROLE_P_IDS = ROLE_IDS;

    public static void copyBaseId(Object target, Object data) {
        for (String id : ROLE_P_IDS) {
            try {
                Object val = BeanUtils.getPropertyDescriptor(data.getClass(), id).getReadMethod().invoke(data);
                BeanUtils.getPropertyDescriptor(target.getClass(), id).getWriteMethod().invoke(target, val);
            } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
            }
        }
    }


    protected <E> ExecuteResult<E> getResult(E entity, ResultState state) {
        if (state == ResultState.SUCCESSFUL) {
            return ExecuteResult
                .of(entity, state, getExceptionCodeManager().getCode(ExceptionCodeDescriptor.SERVER_OK), getExceptionCodeManager().getDescription(
                    ExceptionCodeDescriptor.SERVER_OK));
        } else {
            return ExecuteResult
                .of(entity, state, getExceptionCodeManager().getCode(ExceptionCodeDescriptor.OPERATION_ERR), getExceptionCodeManager().getDescription(
                    ExceptionCodeDescriptor.OPERATION_ERR));
        }
    }

}
