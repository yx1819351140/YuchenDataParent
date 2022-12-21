package com.yuchen.data.service.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Value("${datasource.druid.initialSize}")
    private int initialSize;

    @Value("${datasource.druid.minIdle}")
    private int minIdle;

    @Value("${datasource.druid.maxActive}")
    private int maxActive;

    @Value("${datasource.druid.maxWait}")
    private int maxWait;

    @Value("${datasource.druid.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${datasource.druid.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${datasource.druid.maxEvictableIdleTimeMillis}")
    private int maxEvictableIdleTimeMillis;

    @Value("${datasource.druid.validationQuery}")
    private String validationQuery;

    @Value("${datasource.druid.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${datasource.druid.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${datasource.druid.testOnReturn}")
    private boolean testOnReturn;

    @Value("${datasource.dbType:mysql}")
    private String dbType;
    @Value("${datasource.showSql:false}")
    private boolean showSql;

    @Value("${datasource.formatSql:true}")
    private boolean formatSql;

    public DruidDataSource druidDataSource(DruidDataSource dataSource) {
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
//        try {
//            dataSource.addFilters("stat,wall");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        return dataSource;
    }

    @Bean
    public WallFilter wallFilter() {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig());
        return wallFilter;
    }

    @Bean
    public WallConfig wallConfig() {
        WallConfig wallConfig = new WallConfig();
        wallConfig.setMultiStatementAllow(true);//允许一次执行多条语句
        wallConfig.setNoneBaseStatementAllow(true);//允许一次执行多条语句
        return wallConfig;
    }


    /**
     * 配置数据库后使用该数据源
     *
     * @param dataSourceConfig druid配置属性
     * @return DruidDataSource
     */
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "datasource")
    public DataSource mysqlDataSource(DataSourceConfig dataSourceConfig) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        return dataSourceConfig.druidDataSource(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}