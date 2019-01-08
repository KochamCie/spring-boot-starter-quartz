package com.beelego.support.jdbc;

import java.sql.*;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @Author: rns
 * @Date: 2019/1/5 下午4:27
 * @Description: CommonJDBC
 */
public class CommonJDBC {

    public static final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));


    protected static void closeResultSet(ResultSet rs) {
        if (null != rs) {
            try {
                rs.close();
            } catch (SQLException var2) {
                ;
            }
        }

    }

    protected static void closeStatement(Statement statement) {
        if (null != statement) {
            try {
                statement.close();
            } catch (SQLException var2) {

            }
        }

    }
    protected static void closeConn(Connection conn) {
        if (null != conn) {
            try {
                conn.close();
            } catch (SQLException var2) {

            }
        }

    }



}
