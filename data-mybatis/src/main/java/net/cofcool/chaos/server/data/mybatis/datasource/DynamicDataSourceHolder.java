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

package net.cofcool.chaos.server.data.mybatis.datasource;

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
