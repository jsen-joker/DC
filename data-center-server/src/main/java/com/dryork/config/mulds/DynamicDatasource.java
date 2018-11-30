package com.dryork.config.mulds;

import com.dryork.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.sql.DataSource;

/**
 * <p>
 * </p>
 *
 * @author ${User}
 * @since 2018/4/2
 */
public class DynamicDatasource extends AbstractRoutingDataSource {
//    private static final Logger log = LoggerFactory.getLogger(DynamicDatasource.class);

    private DataSource defaultTargetDataSource;
    private DataSourceLookup dataSourceLookup = new JndiDataSourceLookup();

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDB();
    }

    public void setDefaultTargetDataSource(DataSource defaultTargetDataSource) {
        this.defaultTargetDataSource = defaultTargetDataSource;
    }

    @Override
    public void afterPropertiesSet() {
    }

    @Override
    public void setDataSourceLookup(@Nullable DataSourceLookup dataSourceLookup) {
        this.dataSourceLookup = (dataSourceLookup != null ? dataSourceLookup : new JndiDataSourceLookup());
    }

    @Override
    protected DataSource determineTargetDataSource() {
        Object lookupKey = determineCurrentLookupKey();
        if (lookupKey == null || Constants.DEFAULT.equals(lookupKey)) {
            return defaultTargetDataSource;
        } else {
            DataSource dataSource = this.dataSourceLookup.getDataSource(lookupKey.toString());
            if (dataSource != null) {
                return dataSource;
            }
            return defaultTargetDataSource;
        }
    }

}
