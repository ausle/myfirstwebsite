package com.asule.datasource;

import org.apache.commons.dbcp.BasicDataSource;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * created by asule on 2020-04-25 08:07
 */
public class MyBasicDataSource extends BasicDataSource {

    @Override
    public synchronized void close() throws SQLException {
        DriverManager.deregisterDriver(DriverManager.getDriver(getUrl()));
//        AbandonedConnectionCleanupThread.checkedShutdown();
        super.close();
    }
}
