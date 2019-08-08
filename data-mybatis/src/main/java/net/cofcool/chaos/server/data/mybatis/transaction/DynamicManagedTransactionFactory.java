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

package net.cofcool.chaos.server.data.mybatis.transaction;

import java.sql.Connection;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

/**
 * DynamicManagedTransactionFactory extends SpringManagedTransactionFactory,
 * can create DynamicManagedTransaction.
 *
 * @see SpringManagedTransactionFactory
 * @see DynamicManagedTransaction
 * @see org.mybatis.spring.SqlSessionFactoryBean
 *
 * @author CofCool
 */
public class DynamicManagedTransactionFactory extends SpringManagedTransactionFactory {

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new DynamicManagedTransaction(dataSource);
    }

    @Override
    public Transaction newTransaction(Connection conn) {
        throw new UnsupportedOperationException("New Spring transactions require a DataSource");
    }

    @Override
    public void setProperties(Properties props) {
        // not needed in this version
    }
}
