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
