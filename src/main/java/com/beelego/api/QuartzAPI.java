package com.beelego.api;

import com.beelego.enums.JobOption;
import com.beelego.enums.TriggerOption;
import com.beelego.service.QuartzService;
import com.beelego.support.JobAndTrigger;
import com.beelego.support.Page;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : hama
 * @since : created in  2018/9/22
 */
@Slf4j
@RestController
@RequestMapping(value = "/quartz")
public class QuartzAPI {

    @Autowired
    private QuartzService quartzService;

    /**
     * paged job list
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/job")
    public Page<JobAndTrigger> jobList(@RequestParam(value = "pageNum") Integer pageNum, @RequestParam(value = "pageSize") Integer pageSize) {
        return quartzService.jobList(pageNum, pageSize);
    }


    /**
     * add or update a job
     *
     * @param jobName
     * @param jobGroup
     * @param jobClassName
     * @param cronExpression
     * @param description
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.POST, value = "/job")
    public Object addSchedule(@RequestParam(value = "jobName") String jobName,
                              @RequestParam(value = "jobGroup") String jobGroup,
                              @RequestParam(value = "jobClassName") String jobClassName,
                              @RequestParam(value = "cronExpression") String cronExpression,
                              @RequestParam(value = "description", required = false) String description,
                              @RequestParam(value = "jobData", required = false) String jobData,
                              @RequestParam(value = "option") JobOption option) throws Exception {
        return quartzService.jobOption(jobName, jobGroup, jobClassName, cronExpression, description, jobData, option);
    }


    /**
     * @param jobName
     * @param jobGroup
     * @param option
     * @return
     * @throws SchedulerException
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/trigger")
    public Object updateSchedule(@RequestParam(value = "jobName") String jobName,
                                 @RequestParam(value = "jobGroup") String jobGroup,
                                 @RequestParam(value = "option") TriggerOption option) throws SchedulerException {
        return quartzService.triggerOption(jobName, jobGroup, option);
    }


  /**
   * jobs list
   *
   * @return
   */
  @RequestMapping(method = RequestMethod.GET, value = "/jobs")
  public List<JobAndTrigger> jobList() {
    return quartzService.jobList();
  }

}
