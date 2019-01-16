package com.beelego.support.jobrecord;

import com.alibaba.fastjson.JSON;
import com.beelego.properties.QuartzProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author : rns
 * @since : 2019/1/4 下午7:21
 */
@Slf4j
@Aspect
@Component
public class JobAspect {


  @Autowired
  Scheduler scheduler;

  @Autowired
  JobRecordStore jobRecordStore;

  @Autowired
  QuartzProperties quartzProperties;

  @Pointcut("execution(protected void *.executeInternal(..))")
  public void cut() {
  }

  @Around("cut()")
  public void logic(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

    if(!quartzProperties.isRecordEnable()){
      return;
    }
    JobExecutionContext jobExecutionContext = (JobExecutionContext) proceedingJoinPoint.getArgs()[0];
    JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
    Date start = new Date();
    JobKey jobKey = jobExecutionContext.getTrigger().getJobKey();
    boolean exception = false;
    try {
      Object object = proceedingJoinPoint.proceed();
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      // JOB proceeding threw an exception
      // TODO: notify
      log.info("Job({}, {}) exception: {}", jobKey.getName(), jobKey.getGroup(), throwable);
      exception = true;
    }
    Date finish = new Date();
    Trigger.TriggerState status = scheduler.getTriggerState(TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup()));
    JobRecord jobRecord = new JobRecord(
      UUID.randomUUID().toString(),
      jobKey.getName(),
      jobKey.getGroup(),
      proceedingJoinPoint.getSignature().getDeclaringTypeName(),
      start,
      finish,
      finish,
      exception ? Trigger.TriggerState.ERROR.name() : status.name(),
      JSON.toJSONString(jobDataMap)
    );
    jobRecordStore.saveRecord(jobRecord);
//        List<JobRecord> jobRecordList = jobRecordStore.re(jobKey);
//        log.info("job record list is :{}", jobRecordList.size());
  }

}
