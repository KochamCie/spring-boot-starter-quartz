package com.beelego;

import com.beelego.properties.QuartzProperties;
import com.beelego.support.KochamcieJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author : hama
 * @since : created in  2018/9/24
 */
@Slf4j
@ConditionalOnProperty(
        prefix = "kochamcie.quartz",
        name = {"enabled"},
        havingValue = "true"
)
@EnableConfigurationProperties(QuartzProperties.class)
public class QuartzAutoConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private QuartzProperties quartzProperties;

    @Autowired
    private Scheduler scheduler;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("QuartzAutoConfig onApplicationEvent========== in");
        if(!quartzProperties.isEnabled()){
            log.info("QuartzAutoConfig onApplicationEvent==========Processor disable");
            return;
        }
        ApplicationContext context = contextRefreshedEvent.getApplicationContext();
        Map<String, Object> beans = context.getBeansWithAnnotation(KochamcieJob.class);
        beans.forEach((bean, object) -> {
            // bean comes from KochamcieJob.class annotation, there's no need to check scheduleJob is present.
            KochamcieJob scheduleJob = object.getClass().getAnnotation(KochamcieJob.class);

            // when job already exist, meanwhile the cron expression change, reschedule called
            boolean reschedule = false;
            String name = Optional.ofNullable(scheduleJob.name()).isPresent() ? scheduleJob.name() : bean;
            String group = Optional.ofNullable(scheduleJob.group()).isPresent() ? scheduleJob.group() : bean;
            String cron = scheduleJob.cron();   // TODO: validation of cron expression
            Class clazz = scheduleJob.target(); // target jobClass which extends QuartJobBean.class or KochamcieJobBean.class
            String description = scheduleJob.description();
            JobKey jobKey = new JobKey(name, group);

            // only use withMisfireHandlingInstructionDoNothing, don't ask why, others sucks
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing();
            JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(name, group).build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group).withSchedule(scheduleBuilder).withDescription(description).build();
            try {
                // if jobKey exists, make sure whether job desires to reschedule.
                if (scheduler.checkExists(jobKey)) {
                    log.info("QuartzAutoConfig [checkExists] >>>>>> [{}] init job [already exist]: [{}][{}][{}}", name, name, group, clazz);
                    List<? extends Trigger> triggerList = scheduler.getTriggersOfJob(jobKey);
                    for (Trigger trigger1 : triggerList) {
                        if (trigger1 instanceof CronTrigger) {
                            CronTrigger cronTrigger = (CronTrigger) trigger1;
                            if (!cronTrigger.getCronExpression().equals(cron)) {
                                reschedule = true;
                            }
                        } else {
                            log.info("QuartzAutoConfig trigger not support: {}", trigger1);
                        }
                    }


                    if (reschedule) {
                        TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
                        scheduler.rescheduleJob(triggerKey, trigger);
                        log.info("QuartzAutoConfig [rescheduleJob] >>>>>> [{}] init job : [{}][{}][{}}", name, name, group, clazz);
                    }
                    return;
                }
            } catch (SchedulerException e) {
                log.error("QuartzAutoConfig [SchedulerException] >>>>>> init job failed due to [checkExists]: [{}][{}][{}}", name, group, clazz);
                log.error("QuartzAutoConfig [SchedulerException] >>>>>> init job failed :", e);
                return;
            }

            try {
                scheduler.scheduleJob(jobDetail, trigger);
                log.info("QuartzAutoConfig [scheduleJob] >>>>>> [{}] init job [scheduleJob]: [{}][{}][{}}", name, name, group, clazz);
            } catch (SchedulerException e) {
                log.error("QuartzAutoConfig [SchedulerException] >>>>>> [{}] init job failed due to [scheduleJob]: [{}][{}][{}}", name, name, group, clazz);
                log.error("QuartzAutoConfig [SchedulerException] >>>>>> init job failed :", e);
            }
            log.info("QuartzAutoConfig [{}] >>>>>> init job [success]:  [{}][{}][{}}", name, name, group, clazz);

        });
        log.info("QuartzAutoConfig onApplicationEvent==========out");
    }
}

