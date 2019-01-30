package net.cofcool.chaos.server.data.jpa.util;

import java.util.List;
import java.util.Optional;
import net.cofcool.chaos.server.data.jpa.support.Paging;
import org.springframework.data.domain.PageImpl;

/**
 * @author CofCool
 */
public class DataUtils {

    /**
     * 处理连表结果，适用于单条数据，多条分页数据参考 {@link Paging}
     * <br>
     *
     * @see Paging
     * @return primaryBean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> mergeJoinResult(List<T> result, Class<T> primaryBean) {
        List<T> resultData = Paging.of(new PageImpl<>((List<List<T>>) result), primaryBean).getList();
        if (resultData.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(resultData.get(0));
        }
    }

    /**
     * 处理连表结果，适用于多条数据，分页数据参考 {@link Paging}
     * <br>
     *
     * @see Paging
     * @return primaryBean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> mergeMultiJoinResult(List<T> result, Class<T> primaryBean) {
        return Paging.of(new PageImpl<>((List<List<T>>) result), primaryBean).getList();
    }

}
