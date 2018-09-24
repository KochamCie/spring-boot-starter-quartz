package com.beelego.support;

import org.quartz.Job;

import java.lang.annotation.*;

/**
 * @author : hama
 * @since : created in  2018/9/23
 */

@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.METHOD})
public @interface KochamcieJob {

    String name() default "";

    String group() default "";

    String cron() default "";

    Class<? extends Job> target();

    String description() default "";

}