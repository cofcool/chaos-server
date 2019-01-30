package net.cofcool.chaos.server.core.datasource;

/**
 * 动态数据源资源管理
 */
public class DynamicDataSourceHolder {

    public static final String DATA_SOURCE_1 = "dataSource1";

    public static final String DATA_SOURCE_2 = "dataSource2";

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public static String getDataSource() {
        return CONTEXT_HOLDER.get();
    }

    public static void setDataSource(String customerType) {
        CONTEXT_HOLDER.set(customerType);
    }

    public static void removeDataSource() {
        CONTEXT_HOLDER.remove();
    }

}
