package com.beelego.demo;

import com.beelego.support.KochamcieJobBean;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author : hama
 * @since : created in  2018/9/22
 */
@Slf4j
public class KochamcieDemoJob extends KochamcieJobBean {


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        int pageNo = executePageNo(jobExecutionContext);
        int pageSize = executePageSize(jobExecutionContext);
        log.info("======{}====={}==== {}", pageNo, pageSize, System.currentTimeMillis());
    }


}
