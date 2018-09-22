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


    private String jobName;

    private String jobGroup;

    private String description;

    private String jobStatus;

    private String cronExpression;

    private String jobClassName;

    private String triggerName;

    private String triggerGroup;

    private JobDataMap jobData;


}