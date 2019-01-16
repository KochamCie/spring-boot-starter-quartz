package com.beelego.support;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author : hama
 * @since : created in  2018/9/23
 */
@Slf4j
public abstract class KochamcieJobBean extends QuartzJobBean {

    public KochamcieJobBean() {
    }

    protected abstract void executeInternal(JobExecutionContext var1) throws JobExecutionException;

    private static final int DEFAULT_PAGE_NO = 1;

    private static final int DEFAULT_PAGE_SIZE = 6;

    /**
     * IPS:     if you wanna some specific ip execute
     * FORCE:   if you wanna force execute this job
     *
     * @param jobExecutionContext jobExecutionContext
     * @return boolean
     */
    public boolean executableQuartz(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        if ("TRUE".equals(jobDataMap.get("FORCE"))) {
            return true;
        }

        if (jobDataMap.containsKey("IPS")) {
            String ips = jobDataMap.get("IPS").toString();
            //
            InetAddress addr = null;
            try {
                addr = InetAddress.getLocalHost();
                log.info("addr.getHostAddress: {}", addr.getHostAddress());
                String hostName = addr.getHostName();// host name
                String hostAddress = addr.getHostAddress();// host ip
                String canonicalHostName = addr.getCanonicalHostName();//
                boolean reachable = addr.isReachable(2000);//
                byte[] address = addr.getAddress();//
                int a = 0;
                if (address[3] < 0) {
                    a = address[3] + 256;
                }
                log.info(addr.toString());
                log.info("hostName is [{}], canonicalHostName is [{}], original ipAddr is [{}], ipAddr is [{}], reachable is [{}]"
                        , hostName, canonicalHostName, address[0] + "." + address[1] + "." + address[2] + "." + a, hostAddress, reachable);
                return addr.getHostAddress().indexOf(ips) > -1;
            } catch (UnknownHostException e) {
                e.printStackTrace();
                log.error("executableQuartz error: ", e);
            } catch (IOException e) {
                log.error("executableQuartz error: ", e);
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * the PAGENO in current job's jobExecutionContext
     *
     * @param jobExecutionContext jobExecutionContext
     * @return int
     */
    public int executePageNo(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        try {
            if (jobDataMap.containsKey("PAGENO")) {
                return Integer.parseInt(jobDataMap.get("PAGENO").toString());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            log.error("executePageNo exception:", e);
            return DEFAULT_PAGE_NO;
        }
        return DEFAULT_PAGE_NO;
    }

    /**
     * the PAGESIZE in current job's jobExecutionContext
     *
     * @param jobExecutionContext jobExecutionContext
     * @return int
     */
    public int executePageSize(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        try {
            if (jobDataMap.containsKey("PAGESIZE")) {
                return Integer.parseInt(jobDataMap.get("PAGESIZE").toString());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            log.error("executePageSize exception:", e);
            return DEFAULT_PAGE_SIZE;
        }
        return DEFAULT_PAGE_SIZE;
    }
}
