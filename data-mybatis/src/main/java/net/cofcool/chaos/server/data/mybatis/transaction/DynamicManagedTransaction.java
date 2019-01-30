package net.cofcool.chaos.server.data.mybatis.transaction;

import static org.springframework.util.Assert.notNull;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.transaction.SpringManagedTransaction;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Manual manager Transaction when use DynamicDataSource
 *
 * @see SpringManagedTransaction
 * @see org.mybatis.spring.SqlSessionFactoryBean
 *
 * @author CofCool
 */
public class DynamicManagedTransaction implements Transaction {

    private static final ThreadLocal<DynamicContextHolder> DYNAMIC_STATUS = new ThreadLocal<>();

    private static final Log LOGGER = LogFactory.getLog(SpringManagedTransaction.class);

    private final DataSource dataSource;

    private Connection connection;

    private boolean isConnectionTransactional;

    private boolean autoCommit;

    public DynamicManagedTransaction(DataSource dataSource) {
        notNull(dataSource, "No DataSource specified");
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (getDynamicContextHolder().isDynamic || getDynamicContextHolder().isNewConnection || this.connection == null) {
            openConnection();
            getDynamicContextHolder().reset();
        }
        return this.connection;
    }

    /**
     * Gets a connection from Spring transaction manager and discovers if this
     * {@code Transaction} should manage connection or let it to Spring.
     * <p>
     * It also reads autocommit setting because when using Spring Transaction MyBatis
     * thinks that autocommit is always false and will always call commit/rollback
     * so we need to no-op that calls.
     */
    private void openConnection() throws SQLException {
        if (getDynamicContextHolder().isDynamic || getDynamicContextHolder().isNewConnection) {
            this.connection = this.dataSource.getConnection();
        } else {
            this.connection = DataSourceUtils.getConnection(this.dataSource);
        }
        this.autoCommit = this.connection.getAutoCommit();
        this.isConnectionTransactional = DataSourceUtils.isConnectionTransactional(this.connection, this.dataSource);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                    "JDBC Connection ["
                            + this.connection
                            + "] will"
                            + (this.isConnectionTransactional ? " " : " not ")
                            + "be managed by Spring");
        }
    }

    @Override
    public void commit() throws SQLException {
        if (this.connection != null && !this.isConnectionTransactional && !this.autoCommit) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Committing JDBC Connection [" + this.connection + "]");
            }
            this.connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (this.connection != null && !this.isConnectionTransactional && !this.autoCommit) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Rolling back JDBC Connection [" + this.connection + "]");
            }
            this.connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        DataSourceUtils.releaseConnection(this.connection, this.dataSource);
        DYNAMIC_STATUS.remove();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getTimeout() throws SQLException {
        ConnectionHolder holder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
        if (holder != null && holder.hasTimeout()) {
            return holder.getTimeToLiveInSeconds();
        }
        return null;
    }

    /**
     * When use DynamicDataSource, should invoke the method to get a new connection about the dataSource has changed.
     */
    public static void cancelConnection() {
        getDynamicContextHolder().dynamic();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("cancelConnection Connection");
        }
    }

    /**
     * create a new connection to avoid use latest connection
     */
    public static void resetConnection() {
        if (getDynamicContextHolder().isDynamic) {
            getDynamicContextHolder().newConnection();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("resetConnection Connection");
        }
    }

    private static DynamicContextHolder getDynamicContextHolder() {
        if (DYNAMIC_STATUS.get() == null) {
            DYNAMIC_STATUS.set(new DynamicContextHolder());
        }

        return DYNAMIC_STATUS.get();
    }

    static class DynamicContextHolder {

        boolean isDynamic = false;

        boolean isNewConnection = false;

        void dynamic() {
            isDynamic = true;
            isNewConnection = false;
        }

        void reset() {
            isDynamic = false;
            isNewConnection = false;
        }

        void newConnection() {
            isDynamic = false;
            isNewConnection = true;
        }

    }
}
