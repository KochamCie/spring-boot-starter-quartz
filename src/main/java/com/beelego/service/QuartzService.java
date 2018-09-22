package com.beelego.service;

import com.beelego.enums.JobOption;
import com.beelego.enums.TriggerOption;
import com.beelego.support.JobAndTrigger;
import com.beelego.support.Page;
import org.quartz.SchedulerException;

/**
 * @author : hama
 * @since : created in  2018/9/22
 */
public interface QuartzService {

    Page<JobAndTrigger> jobList(int pageNum, int pageSize);

    Object jobOption(String jobName,
                     String jobGroup,
                     String jobClassName,
                     String cronExpression,
                     String description,
                     String jobData,
                     JobOption option) throws Exception;

    /**
     *
     * @param jobName
     * @param jobGroup
     * @param option
     * @return
     * @throws SchedulerException
     */
    Object triggerOption(String jobName,
                     String jobGroup,
                     TriggerOption option) throws SchedulerException;
}
