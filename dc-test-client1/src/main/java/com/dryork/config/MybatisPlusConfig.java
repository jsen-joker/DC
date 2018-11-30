package com.dryork.config;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.MybatisMapWrapperFactory;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.baomidou.mybatisplus.extension.plugins.SqlExplainInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * <p>
 *     mbp config
 * </p>
 *
 * @author jsen
 * @since 2018-04-08
 */

@Configuration
@MapperScan("com.dryork.mapper")
public class MybatisPlusConfig {

    @Value("${spring.application.name}")
    String appName;

    @Bean
    @ConfigurationProperties("spring.datasource" )
    public DataSource dataSource() {
        DataSource dataSource = DataSourceBuilder.create().type(com.alibaba.druid.pool.DruidDataSource.class).build();
        return dataSource;
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {

        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();

        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*Mapper.xml"));
        sqlSessionFactory.setTypeAliasesPackage("com.dryork.entity");

        sqlSessionFactory.setDataSource(dataSource());
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);

        configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        //*注册Map 下划线转驼峰*
        configuration.setObjectWrapperFactory(new MybatisMapWrapperFactory());

        sqlSessionFactory.setConfiguration(configuration);

        GlobalConfig globalConfig = GlobalConfigUtils.defaults();
        globalConfig.setSqlParserCache(true);
        globalConfig.getDbConfig().setColumnLike(false);
        globalConfig.getDbConfig().setLogicDeleteValue("0");
        globalConfig.getDbConfig().setLogicNotDeleteValue("1");
        /*
         * 当更新数据库的时候设置字段为null可以更新
         */
        globalConfig.getDbConfig().setFieldStrategy(FieldStrategy.IGNORED);
        sqlSessionFactory.setGlobalConfig(globalConfig);
        //...其他配置
        return sqlSessionFactory.getObject();
    }




    /*
     * 分页插件，自动识别数据库类型
     * 多租户，请参考官网【插件扩展】
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
//        paginationInterceptor.setLocalPage(true);
//        List<ISqlParser> iSqlParserList = new ArrayList<>();
//        TenantSqlParser tenantSqlParser = new TenantSqlParser();
//        tenantSqlParser.setTenantHandler(new TenantHandler() {
//            @Override
//            public Expression getTenantId() {
//                return new LongValue(1L);
//            }
//
//            @Override
//            public String getTenantIdColumn() {
//                return "TenantID";
//            }
//
//            @Override
//            public boolean doTableFilter(String tableName) {
//                if (tableName.equals("testtable")) {
//                    return false;
//                }
//                return true;
//            }
//        });
//        iSqlParserList.add(tenantSqlParser);
//        paginationInterceptor.setSqlParserList(iSqlParserList);
        return paginationInterceptor;
    }


    @Bean
    public ISqlInjector sqlInjector() {
        return new LogicSqlInjector();
    }

    @Bean
    // @Profile({"dev", "test"})
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }

    @Bean
    public SqlExplainInterceptor sqlExplainInterceptor() {
        return new SqlExplainInterceptor();
    }

}
