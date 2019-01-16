package com.beelego.api;

import com.beelego.support.Page;
import com.beelego.support.jobrecord.JobRecord;
import com.beelego.support.jobrecord.JobRecordStore;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

/**
 * @author: rns
 * @since: 2019/1/5 下午10:06
 */
@Slf4j
@RestController
@RequestMapping("/record")
public class RecordAPI {

    @Autowired
    JobRecordStore jobRecordStore;

  /**
   *
   * @param pageNum pageNum
   * @param pageSize pageSize
   * @param jobName jobName
   * @param jobGroup  jobGroup
   * @return object
   * @throws SQLException SQLException
   */
    @RequestMapping(method = RequestMethod.GET, value = "/job/{jobName}/{jobGroup}")
    public Page<JobRecord> recordList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                      @PathVariable String jobName,
                                      @PathVariable String jobGroup) throws SQLException {

        return jobRecordStore.recordList(JobKey.jobKey(jobName, jobGroup), pageNum, pageSize);
    }

}
