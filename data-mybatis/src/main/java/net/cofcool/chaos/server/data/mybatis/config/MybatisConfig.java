package net.cofcool.chaos.server.data.mybatis.config;

import com.github.pagehelper.PageInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import net.cofcool.chaos.server.core.config.ChaosConfiguration;
import net.cofcool.chaos.server.core.config.ChaosProperties;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * @author CofCool
 */
@Configuration
@AutoConfigureAfter(ChaosConfiguration.class)
public class MybatisConfig {

    @Bean
    public org.apache.ibatis.session.Configuration configuration() {
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setUseGeneratedKeys(true);

        return configuration;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource, org.apache.ibatis.session.Configuration configuration, Interceptor[] mybatisPlugins, ApplicationContext applicationContext, ChaosProperties chaosProperties)
        throws IOException {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setMapperLocations(
            ResourcePatternUtils
                .getResourcePatternResolver(applicationContext)
                .getResources(
                    chaosProperties.getData().getXmlPath()
                )
        );
        factoryBean.setDataSource(dataSource);
        factoryBean.setPlugins(mybatisPlugins);

        factoryBean.setConfiguration(configuration);

        return factoryBean;
    }

    @Bean
    public Interceptor[] mybatisPlugins() {
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mysql");
        properties.setProperty("reasonable", "true");
        interceptor.setProperties(properties);

        return new Interceptor[] {interceptor};
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
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

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(ChaosProperties chaosProperties) {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage(chaosProperties.getData
().getMapperPackage());
        configurer.setSqlSessionFactoryBeanName("sqlSessionFactory");

        return configurer;
    }

}
