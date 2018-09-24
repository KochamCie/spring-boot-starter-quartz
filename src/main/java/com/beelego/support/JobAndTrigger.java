package com.beelego.support;

import lombok.Data;
import org.quartz.JobDataMap;

import java.io.Serializable;

/**
 * @author : hama
 * @since : created in  2018/6/5
 */
@Data
public class JobAndTrigger extends BaseSerialVersion {

    /**
     * job name
     */
    private String jobName;

    /**
     * job group
     */
    private String jobGroup;

    /**
     * job description
     */
    private String description;

    /**
     * job status, actually job's trigger status
     */
    private String jobStatus;

    /**
     * only cron expression we expect
     */
    private String cronExpression;

    /**
     * actual class
     */
    private String jobClassName;

    /**
     * trigger name
     */
    private String triggerName;

    /**
     * trigger group
     */
    private String triggerGroup;

    /**
     * job data, very useful:)
     */
    private JobDataMap jobData;


}