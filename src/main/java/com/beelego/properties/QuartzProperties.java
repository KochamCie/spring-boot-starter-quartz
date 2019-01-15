package com.beelego.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * kochamcie quartz config properties
 *
 * @author : hama
 * @since : created in  2018/9/23
 */
@Data
@ConfigurationProperties("kochamcie.quartz")
public class QuartzProperties {


  /**
   * ui & initial switch, default false.
   */
  private boolean enabled = false;

  /**
   * root path
   */
  @Deprecated
  private String root = "quartz";

  private boolean recordEnable = false;

}
