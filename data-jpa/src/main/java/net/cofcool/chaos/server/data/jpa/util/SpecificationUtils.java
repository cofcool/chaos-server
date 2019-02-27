package net.cofcool.chaos.server.data.jpa.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.query.criteria.LiteralHandlingMode;
import org.hibernate.query.criteria.internal.compile.ExplicitParameterInfo;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.predicate.CompoundPredicate;
import org.hibernate.type.Type;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification 的一些方便方法
 *
 * @author CofCool
 */
public class SpecificationUtils {

    public static <T, Y extends Comparable<? super Y>> Specification<T> andGreaterOrEqualTo(Specification<T> sp, String property, Y val) {
        return sp.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get(property), val));
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> andLessOrEqualTo(Specification<T> sp, String property, Y val) {
        return sp.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get(property), val));
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> andBetween(Specification<T> sp, String property, Y minVal, Y maxVal) {
        return sp.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get(property), minVal, maxVal));
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> andEqualTo(Specification<T> sp, String property, Y val) {
        return sp.and((root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get(property), val));
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> andNotEqualTo(Specification<T> sp, String property, Y val) {
        return sp.and((root, query, criteriaBuilder) ->
            criteriaBuilder.notEqual(root.get(property), val));
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> andLikeTo(Specification<T> sp, String property, Y val, boolean fullLike) {
        return sp.and((root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get(property), fullLike ? ("%" + val + "%") : (val + "%")));
    }

    public static <T, Y> Specification<T> andIn(Specification<T> sp, String property, List<Y> vals) {
        return sp.and((root, query, criteriaBuilder) -> {
            Path<Y> pro = root.get(property);
            CriteriaBuilder.In<Y> in = criteriaBuilder.in(pro);
            for (Y data : vals){
                in.value(data);
            }
            return criteriaBuilder.and(in);
        });
    }

    /**
     * 根据 <code>Specification</code> 生成 SQL 语句
     * @param baseSql 未带条件的基本 SQL
     * @param alias Entity 别名
     * @param sp Specification
     * @param entityManager SessionImplementor
     * @param type 表对应的 Entity 类型
     * @param sort 排序
     * @param <T> 表对应的 Entity 类型
     * @return 根据 Specification 生成的新 SQL
     */
    public static <T> String render(String baseSql, String alias, Specification<T> sp, SessionImplementor entityManager, Class<T> type, @Nullable Sort sort) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(type);
        Root<T> root = query.from(type);
        Predicate p = sp.toPredicate(root, query, builder);

        StringBuilder sql = new StringBuilder(baseSql)
            .append(" where ")
            .append(
                ((CompoundPredicate)p)
                    .render(
                        CACHED_RENDERING_CONTEXTS.computeIfAbsent(
                            alias,
                            a1 -> new SimpleRenderingContext(alias, entityManager)
                        )
                    )
            );
        if (sort != null) {
            sort.stream()
                .forEach(
                    order ->
                        sql.append(" order by ")
                            .append(alias)
                            .append(".")
                            .append(order.getProperty())
                            .append(" ")
                            .append(order.getDirection().toString())
                );
        }

        return sql.toString();
    }

    private static final Map<String, RenderingContext> CACHED_RENDERING_CONTEXTS = new HashMap<>();


    /**
     * @see org.hibernate.query.criteria.internal.compile.CriteriaCompiler
     */
    private static class SimpleRenderingContext implements RenderingContext {

        private final Map<ParameterExpression<?>, ExplicitParameterInfo<?>> explicitParameterInfoMap = new HashMap<>();

        private String alias;
        private SessionImplementor entityManager;
        private Dialect dialect;

        public SimpleRenderingContext(String alias, SessionImplementor entityManager) {
            this.alias = alias;
            this.entityManager = entityManager;
            this.dialect = entityManager.getSessionFactory().getServiceRegistry().getService( JdbcServices.class ).getDialect();
        }

        @Override
        public String generateAlias() {
            return alias;
        }

        @SuppressWarnings("unchecked")
        @Override
        public ExplicitParameterInfo registerExplicitParameter(
            ParameterExpression<?> criteriaQueryParameter) {
            ExplicitParameterInfo parameterInfo = explicitParameterInfoMap.get( criteriaQueryParameter );
            if ( parameterInfo == null ) {
                if ( StringHelper.isNotEmpty( criteriaQueryParameter.getName() ) ) {
                    parameterInfo = new ExplicitParameterInfo(
                        criteriaQueryParameter.getName(),
                        null,
                        criteriaQueryParameter.getJavaType()
                    );
                }
                else if ( criteriaQueryParameter.getPosition() != null ) {
                    parameterInfo = new ExplicitParameterInfo(
                        null,
                        criteriaQueryParameter.getPosition(),
                        criteriaQueryParameter.getJavaType()
                    );
                }
                else {
                    throw new IllegalArgumentException("can not invoke for generated parameter");
                }

                explicitParameterInfoMap.put( criteriaQueryParameter, parameterInfo );
            }

            return parameterInfo;
        }

        public String registerLiteralParameterBinding(final Object literal, final Class javaType) {
            return literal.toString();
        }

        public String getCastType(Class javaType) {
            SessionFactoryImplementor factory = entityManager.getFactory();
            Type hibernateType = factory.getTypeResolver().heuristicType( javaType.getName() );
            if ( hibernateType == null ) {
                throw new IllegalArgumentException(
                    "Could not convert java type [" + javaType.getName() + "] to Hibernate type"
                );
            }
            return hibernateType.getName();
        }

        @Override
        public Dialect getDialect() {
            return dialect;
        }

        @Override
        public LiteralHandlingMode getCriteriaLiteralHandlingMode() {
            return LiteralHandlingMode.INLINE;
        }
    }

}
