package net.cofcool.chaos.server.data.jpa.support;

import java.util.List;
import java.util.Optional;
import net.cofcool.chaos.server.common.core.ExceptionCodeDescriptor;
import net.cofcool.chaos.server.common.core.ExecuteResult;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.Result.ResultState;
import net.cofcool.chaos.server.common.core.SimpleService;
import net.cofcool.chaos.server.common.util.BeanUtils;
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
        ExecuteResult<T> result = queryById(entity);
        if (result.successful()) {
            jpaRepository.delete(result.getEntity());
            return ResultState.SUCCESSFUL;
        } else {
            return ResultState.FAILURE;
        }
    }

    /**
     * {@inheritDoc}
     * <br>
     *
     * 除了手动更新数据外, 也可直接修改查询到的实体数据("set"操作), 当"Hibernate"执行"flush"操作时,
     * {@link org.hibernate.event.internal.DefaultFlushEntityEventListener#onFlushEntity(FlushEntityEvent)} 会检查实体是否需要更新数据,
     * 但该操作可能耗时严重, 因此尽量避免触发自动更新操作
     */
    @Override
    public ExecuteResult<T> update(T entity) {
        ExecuteResult<T> result = queryById(entity);
        if (!result.successful()) {
            return result;
        }

        BeanUtils.overwriteNullProperties(entity, result.getEntity());

        return ExecuteResult.of(
            jpaRepository.save(entity), ResultState.SUCCESSFUL,
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
        Optional<T> data = jpaRepository.findById(getEntityId(entity));
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
                    getExceptionCodeManager().getCode(ExceptionCodeDescriptor.OPERATION_ERR),
                    getExceptionCodeManager().getDescription(ExceptionCodeDescriptor.OPERATION_ERR_DESC)
                ));
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
