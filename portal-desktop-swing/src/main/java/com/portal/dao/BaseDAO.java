package com.portal.dao;

import com.portal.config.DatabaseConfig;
import java.sql.Connection;

public abstract class BaseDAO {

    protected Connection getConnection() {
        return DatabaseConfig.getConnection();
    }
}
