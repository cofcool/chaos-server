package net.cofcool.chaos.server.data.jpa.util;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
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

    public static <T, Y extends Comparable<? super Y>> Specification<T> andELessOrEqualTo(Specification<T> sp, String property, Y val) {
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

}
