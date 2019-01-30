package net.cofcool.chaos.server.common.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;

/**
 * 忽略数据
 *
 * @author CofCool
 */
public interface ExcludeType {

    /**
     * 不忽略
     */
    String NONE = "NONE";

    /**
     * 忽略全部
     */
    String ALL = "ALL";


    default Exclude getValue(String property) {
        return Exclude.valueOf(property);
    }

    final class Exclude implements Comparable<Exclude> {

        private static final Map<String, Exclude> CACHED_EXCLUDE = new ConcurrentHashMap<>();

        private int ordinal;

        private String property;

        private Exclude(int order, String property) {
            this.ordinal = order;
            this.property = property;
        }

        public String getProperty() {
            return property;
        }

        public int getOrdinal() {
            return ordinal;
        }

        public static Exclude valueOf(int ordinal, String property) {
            return CACHED_EXCLUDE.putIfAbsent(property, new Exclude(ordinal, property));
        }

        public static Exclude valueOf(String property) {
            return valueOf(property.hashCode(), property);
        }

        @Override
        public int compareTo(@Nonnull Exclude o) {
            return ordinal - o.getOrdinal();
        }

        @Override
        public String toString() {
            return property;
        }

    }
}
