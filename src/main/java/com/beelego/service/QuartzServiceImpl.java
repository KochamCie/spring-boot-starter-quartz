package com.beelego.service;

import com.alibaba.fastjson.JSON;
import com.beelego.support.JobAndTrigger;
import com.beelego.enums.JobOption;
import com.beelego.enums.TriggerOption;
import com.beelego.support.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : hama
 * @since : created in  2018/9/22
 */
@Slf4j
@Service("quartzService")
public class QuartzServiceImpl implements QuartzService {


    @Autowired
    private Scheduler scheduler;

    @Override
    public Page<JobAndTrigger> jobList(int pageNum, int pageSize) {
        GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
        Set<JobKey> jobKeys;
        List<JobAndTrigger> jobList = new ArrayList<JobAndTrigger>();
        try {
            jobKeys = scheduler.getJobKeys(matcher);
            for (JobKey jobKey : jobKeys) {
                JobAndTrigger job = new JobAndTrigger();

                JobDetail detail = null;
                try {
                    detail = scheduler.getJobDetail(jobKey);
                } catch (SchedulerException e) {
                    // triggerOption(jobKey.getName(), jobKey.getGroup(), TriggerOption.DELETE);
                    continue;
                }
                job.setJobClassName(detail.getJobClass().getName());
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    if ("DEFAULT".equalsIgnoreCase(trigger.getKey().getGroup())) {
                        continue;
                    }
                    job.setDescription(trigger.getDescription());
                    job.setJobName(jobKey.getName());
                    job.setJobGroup(jobKey.getGroup());
                    job.setTriggerName(trigger.getKey().getName());
                    job.setTriggerGroup(trigger.getKey().getGroup());
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                    job.setJobStatus(triggerState.name());
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        String cronExpression = cronTrigger.getCronExpression();
                        job.setCronExpression(cronExpression);
                        job.setJobData(trigger.getJobDataMap());
                    }
                    jobList.add(job);
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            log.error("SchedulerException:", e);
        }
        jobList.sort(Comparator.comparing(JobAndTrigger::getJobName));
        int offset = (pageNum - 1) * pageSize;
        List<JobAndTrigger> content = jobList.stream().skip(offset).limit(pageSize).collect(Collectors.toList());
        log.info("total[{}]", jobList.size());
        return new Page<>(content, jobList.size(), pageNum, pageSize);
    }



    @Override
    public Object jobOption(String jobName,
                            String jobGroup,
                            String jobClassName,
                            String cronExpression,
                            String description,
                            String jobData,
                            JobOption option) throws Exception {

        switch (option) {
            case SCHEDULE:
                JobDetail jobDetail = JobBuilder.newJob(getClass(jobClassName).getClass())
                        .withIdentity(jobName, jobGroup)
                        .build();
                CronScheduleBuilder newScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression)
                        .withMisfireHandlingInstructionDoNothing();
                CronTrigger newTrigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup)
                        .withSchedule(newScheduleBuilder)
                        .withDescription(description)
                        .build();
                try {
                    scheduler.scheduleJob(jobDetail, newTrigger);
                } catch (SchedulerException e) {
                    log.error("create new job failed !" + e);
                }
                break;
            case RESCHEDULE:
                TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression)
                        .withMisfireHandlingInstructionDoNothing();
                CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
                trigger = trigger.getTriggerBuilder()
                        .withIdentity(triggerKey)
                        .withDescription(description)
                        .withSchedule(scheduleBuilder).build();
                Map<String, Object> dataMap = new HashMap<>();
                if (Optional.ofNullable(jobData).isPresent()) {
                    try {
                        dataMap = JSON.parseObject(jobData, Map.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("job RESCHEDULE [{}, {}] exception :{}", jobName, jobGroup, e);

                    }
                }
                trigger.getJobDataMap().clear();
                trigger.getJobDataMap().putAll(dataMap);
                scheduler.rescheduleJob(triggerKey, trigger);
                break;
        }
        return Response.SC_OK;
    }


    /**
     * common
     *
     * @param jobName
     * @param jobGroup
     * @param option
     * @throws SchedulerException
     */
    @Override
    public Object triggerOption(String jobName,
                                String jobGroup,
                                TriggerOption option) throws SchedulerException {
        log.info("[{},{}] triggerOption in , option is [{}]",
                jobName, jobGroup, option);
        switch (option) {
            case PAUSE:
                scheduler.pauseJob(JobKey.jobKey(jobName, jobGroup));
                break;
            case RESUME:
                scheduler.resumeJob(JobKey.jobKey(jobName, jobGroup));
                break;
            case DELETE:
                scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, jobGroup));
                boolean unscheduleJob = scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, jobGroup));
                if (unscheduleJob) {
                    boolean result = scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));
                    log.info("deleteJob result : {}", result);
                }
                break;
            case TRIGGER:
                JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
                JobDataMap jobDataMap = null;
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger2 : triggers) {
                    jobDataMap = trigger2.getJobDataMap();
                }
                scheduler.triggerJob(JobKey.jobKey(jobName, jobGroup), jobDataMap);
                break;
        }
        log.info("[{},{}] jobOption[{}] end", jobName, jobGroup, option);
        return Response.SC_OK;
    }

    public static QuartzJobBean getClass(String classname) throws Exception {
        Class<?> class1 = Class.forName(classname);
        return (QuartzJobBean) class1.newInstance();
    }

}
