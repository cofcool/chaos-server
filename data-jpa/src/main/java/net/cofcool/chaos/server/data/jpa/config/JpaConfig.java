package net.cofcool.chaos.server.data.jpa.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;

/**
 * JPA相关配置, 如下示例:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableJpaRepositories(basePackages = "net.cofcool.chaos")
 * &#064;EntityScan(value = "net.cofcool.chaos")
 * &#064;EnableTransactionManagement
 * public class JpaConfig {
 *
 *     &#064;Bean
 *     &#064;ConfigurationProperties(prefix = "spring.datasource")
 *     public DataSource dataSource(DataSourceProperties dataSourceProperties) {
 *         return DataSourceBuilder
 *             .create()
 *             .url(dataSourceProperties.getUrl())
 *             .username(dataSourceProperties.getUsername())
 *             .password(dataSourceProperties.getPassword())
 *             .driverClassName(dataSourceProperties.getDriverClassName())
 *             .type(HikariDataSource.class)
 *             .build();
 *     }
 * }
 * </pre>
 *
 * @author CofCool
 */
public class JpaConfig {


///    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
//        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
//        entityManagerFactoryBean.setPackagesToScan("net.cofcool.chaos.control.service");
//        entityManagerFactoryBean.setDataSource(dataSource);
//        entityManagerFactoryBean.setPackagesToScan("net.cofcool.chaos.api");

//        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
//        jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL57Dialect");
//        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
//
//
//        return entityManagerFactoryBean;
//    }

//    @Bean
//    public JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(entityManagerFactory);
//
//        return transactionManager;
//    }

    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return DataSourceBuilder
                .create()
                .url(dataSourceProperties.getUrl())
                .username(dataSourceProperties.getUsername())
                .password(dataSourceProperties.getPassword())
                .driverClassName(dataSourceProperties.getDriverClassName())
                .type(HikariDataSource.class)
                .build();
    }

}
