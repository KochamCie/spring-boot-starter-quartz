package com.beelego.support.jobrecord;

import com.beelego.support.Page;
import com.beelego.support.jdbc.CommonJDBC;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : rns
 * @since : 2019/1/5 下午4:50
 */
@Slf4j
@Component
public class JobRecordStore extends CommonJDBC {

    @Value("${spring.quartz.properties.org.quartz.jobStore.tablePrefix}")
    private String tablePrefix;

    @Autowired
    DataSource dataSource;

    public int saveRecord(JobRecord jobRecord) throws SQLException {
        Connection conn = dataSource.getConnection();
        PreparedStatement ps = null;
        int insertResult;
        try {
            log.info("tablePrefix is :{}", tablePrefix);
            String sql = "INSERT INTO " + tablePrefix + "JOB_RECORD (ID, JOB_NAME, JOB_GROUP, JOB_BEAN, START_DATE, FINISH_DATE, RECORD_DATE, STATUS, DATA_MAP)  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, jobRecord.getId());
            ps.setString(2, jobRecord.getJobName());
            ps.setString(3, jobRecord.getJobGroup());
            ps.setString(4, jobRecord.getJobBean());
            ps.setTimestamp(5, new java.sql.Timestamp(jobRecord.getStartDate().getTime()), cal);
            ps.setTimestamp(6, new java.sql.Timestamp(jobRecord.getFinishDate().getTime()), cal);
            ps.setTimestamp(7, new java.sql.Timestamp(jobRecord.getRecordDate().getTime()), cal);
            ps.setString(8, jobRecord.getStatus());
            ps.setString(9, jobRecord.getDataMap());
            insertResult = ps.executeUpdate();
        } finally {
            closeStatement(ps);
            closeConn(conn);
        }

        return insertResult;
    }

    public Page<JobRecord> recordList(JobKey jobKey, int pageNum, int pageSize) throws SQLException {
        Connection conn = dataSource.getConnection();

        PreparedStatement ps = null;
        ResultSet rs = null;
        List<JobRecord> result = new ArrayList<>();
        int total = 0;
        try {
            String sql = "SELECT ID, JOB_NAME, JOB_GROUP, JOB_BEAN, START_DATE, FINISH_DATE, RECORD_DATE, STATUS, DATA_MAP  FROM " + tablePrefix + "JOB_RECORD WHERE JOB_NAME=? AND JOB_GROUP=? ORDER BY START_DATE ASC ";
            ps = conn.prepareStatement(sql);
            ps.setString(1, jobKey.getName());
            ps.setString(2, jobKey.getGroup());
            ps.setMaxRows(pageNum*pageSize);
            rs = ps.executeQuery();
            rs.relative((pageNum-1)*pageSize);
            while (rs.next()) {
                String id = rs.getString("ID");
                String jobBean = rs.getString("JOB_BEAN");
                result.add(new JobRecord(id,
                        jobKey.getName(),
                        jobKey.getGroup(),
                        jobBean,
                        new Date(rs.getTimestamp("START_DATE", cal).getTime()),
                        new Date(rs.getTimestamp("FINISH_DATE", cal).getTime()),
                        new Date(rs.getTimestamp("RECORD_DATE", cal).getTime()),
                        rs.getString("STATUS"),
                        rs.getString("DATA_MAP")));
            }

            sql = "SELECT COUNT(ID)  FROM " + tablePrefix + "JOB_RECORD WHERE JOB_NAME=? AND JOB_GROUP=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, jobKey.getName());
            ps.setString(2, jobKey.getGroup());
            rs = ps.executeQuery();
            while (rs.next()) {
                total = rs.getInt(1);
            }


        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            closeResultSet(rs);
            closeStatement(ps);
        }
        return new Page<JobRecord>(result, total, pageNum, pageSize);
    }


    public int count(JobKey jobKey) throws SQLException {
        Connection conn = dataSource.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT COUNT(ID)  FROM " + tablePrefix + "JOB_RECORD WHERE JOB_NAME=? AND JOB_GROUP=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, jobKey.getName());
            ps.setString(2, jobKey.getGroup());
            rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("No record of this job key count returned.");
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
        }

    }


}
