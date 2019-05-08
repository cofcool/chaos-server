package net.cofcool.chaos.server.data.jpa.support;

import static net.cofcool.chaos.server.common.util.BeanUtils.getPropertyDescriptors;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import javax.persistence.Transient;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExecuteResult;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.Result.ResultState;
import net.cofcool.chaos.server.common.core.SimpleService;
import org.hibernate.event.spi.FlushEntityEvent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 基于 {@link JpaRepository} 的简单 <b>Service</b> 实现
 *
 * @param <T> 实体
 * @param <ID> 实体的 ID
 * @param <J> 实体对应的 JpaRepository
 * @author CofCool
 */
public abstract class SimpleJpaService<T, ID, J extends JpaRepository<T, ID>> extends SimpleService<T> implements InitializingBean {

    private J jpaRepository;

    protected J getJpaRepository() {
        return jpaRepository;
    }

    @Autowired
    public void setJpaRepository(J jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    protected Object queryWithPage(Page<T> condition, T entity) {
        return jpaRepository.findAll(Paging.getPageable(condition));
    }

    @Override
    public ExecuteResult<T> add(T entity) {
        return ExecuteResult.of(
            jpaRepository.save(entity), ResultState.SUCCESSFUL,
            getExceptionCodeManager().getCode(ExceptionCodeDescriptor.SERVER_OK),
            getExceptionCodeManager().getDescription(ExceptionCodeDescriptor.SERVER_OK_DESC)
        );
    }

    @Override
    public ResultState delete(T entity) {
        Optional<T> result = findById(entity);
        if (result.isPresent()) {
            jpaRepository.delete(result.get());
            return ResultState.SUCCESSFUL;
        } else {
            return ResultState.FAILURE;
        }
    }

    /**
     * {@inheritDoc}, 方法返回值中的"entity"为传入的"entity"
     * <br>
     *
     * 除了手动更新数据外, 也可直接修改查询到的实体数据("set"操作), 当"Hibernate"执行"flush"操作时,
     * {@link org.hibernate.event.internal.DefaultFlushEntityEventListener#onFlushEntity(FlushEntityEvent)} 会检查实体是否需要更新数据,
     * 但该操作可能耗时严重, 因此尽量避免触发自动更新操作
     */
    @Override
    public ExecuteResult<T> update(T entity) {
        Optional<T> result = findById(entity);
        if (!result.isPresent()) {
            return ExecuteResult.of(
                null,
                ResultState.FAILURE,
                getExceptionCodeManager().getCode(ExceptionCodeDescriptor.DATA_ERROR),
                getExceptionCodeManager().getDescription(ExceptionCodeDescriptor.DATA_ERROR_DESC)
            );
        }

        T dirtyEntity = result.get();

        for (PropertyDescriptor descriptor : getPropertyDescriptors(dirtyEntity.getClass())) {
            try {
                if (descriptor.getReadMethod() != null
                        && descriptor.getWriteMethod() != null
                        && !descriptor.getReadMethod().isAnnotationPresent(Transient.class)
                ) {
                    Object val = descriptor.getReadMethod().invoke(entity);
                    if (val != null) {
                        descriptor.getWriteMethod().invoke(dirtyEntity, val);
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException ignore) {}
        }

        jpaRepository.save(dirtyEntity);

        return ExecuteResult.of(
            entity,
            ResultState.SUCCESSFUL,
            getExceptionCodeManager().getCode(ExceptionCodeDescriptor.SERVER_OK),
            getExceptionCodeManager().getDescription(ExceptionCodeDescriptor.SERVER_OK_DESC)
        );
    }

    @Override
    public ExecuteResult<List<T>> queryAll(T entity) {
        return ExecuteResult.of(
            jpaRepository.findAll(Example.of(entity)),
            ResultState.SUCCESSFUL,
            getExceptionCodeManager().getCode(ExceptionCodeDescriptor.SERVER_OK),
            getExceptionCodeManager().getDescription(ExceptionCodeDescriptor.SERVER_OK_DESC)
        );
    }

    @Override
    public ExecuteResult<T> queryById(T entity) {
        Optional<T> data = findById(entity);
        return data
            .map(t ->
                ExecuteResult.of(
                    t,
                    ResultState.SUCCESSFUL,
                    getExceptionCodeManager().getCode(ExceptionCodeDescriptor.SERVER_OK),
                    getExceptionCodeManager().getDescription(ExceptionCodeDescriptor.SERVER_OK_DESC)
                )
            )
            .orElseGet(() ->
                ExecuteResult.of(
                    null,
                    ResultState.FAILURE,
                    getExceptionCodeManager().getCode(ExceptionCodeDescriptor.DATA_ERROR),
                    getExceptionCodeManager().getDescription(ExceptionCodeDescriptor.DATA_ERROR_DESC)
                ));
    }

    /**
     * 根据实体的"ID"查询数据
     * @param entity 实体
     * @return 查询结果
     */
    protected Optional<T> findById(T entity) {
        return jpaRepository.findById(getEntityId(entity));
    }

    /**
     * 读取实体主键
     */
    protected abstract ID getEntityId(T entity);

    @Override
    public void afterPropertiesSet() throws Exception {
        setPageProcessor(Paging.getPageProcessor());
    }

}
