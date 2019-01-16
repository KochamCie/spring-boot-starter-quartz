package com.beelego.support.jobrecord;

import com.beelego.support.markup.SensitiveInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author : rns
 * @since : 2019/1/5 下午4:31
 */
@Data
@AllArgsConstructor
public class JobRecord {

    private String id;

    private String jobName;

    private String jobGroup;

    private String jobBean;

    @SensitiveInfo
    private Date startDate;

    @SensitiveInfo
    private Date finishDate;

    @SensitiveInfo
    private Date recordDate;

    private String status;

    private String dataMap;

}
