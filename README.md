### 项目

> 利用springboot自动配置的特性，自制了quartz-starter，
>
> 提供了简单的控制台ui、自动装配jobBean、常用方法封装等功能。

Github：https://github.com/KochamCie/spring-boot-starter-quartz

### 版本

```
<version.java>1.8</version.java>
<version.spring-boot>2.0.5.RELEASE</version.spring-boot>
<version.quartz>2.3.0</version.quartz>
```

使用springboot2。所以在部分写法上会有些许出入，不过问题不大，本章主要介绍这个starter的用法。

### 背景

随着业务的发展，需要一个module做调度中心，专门用于处理业务的调度任务。springboot自带的调度非常简便但是功能单一且可控性低，这样就需要开发额外的做很多工作。这里选择Quartz作为调度解决方案，它有很多特性比如支持任务信息的存储、集群环境下调度的控制与负载均衡、调度任务的可控、misfire等。更多的可以去瞅瞅：http://www.quartz-scheduler.org/overview/features.html

通过了解，springboot2对quartz调度器做了自动装配的支持（org.springframework.boot.autoconfigure.quartz包下），首先quartz支持job信息存储在内存和数据库中，而我们使用上当然选择后者，讲道理你的项目会配置数据源的吧...使用方式如下：

```
spring.quartz.job-store-type=jdbc
spring.quartz.jdbc.initialize-schema=never
spring.quartz.properties.org.quartz.scheduler.instanceName=[instanceNameUsuallyApplicationName]
spring.quartz.properties.org.quartz.scheduler.instanceId=AUTO
spring.quartz.properties.org.quartz.jobStore.class=org.quartz.impl.jdbcjobstore.JobStoreTX
spring.quartz.properties.org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.StdJDBCDelegate
spring.quartz.properties.org.quartz.jobStore.tablePrefix=QRTZ_
spring.quartz.properties.org.quartz.jobStore.isClustered=true
spring.quartz.properties.org.quartz.jobStore.clusterCheckinInterval=20000
spring.quartz.properties.org.quartz.jobStore.useProperties=false
spring.quartz.properties.org.quartz.jobStore.misfireThreshold=5000
spring.quartz.properties.org.quartz.threadPool.class=org.quartz.simpl.SimpleThreadPool
spring.quartz.properties.org.quartz.threadPool.threadCount=10
spring.quartz.properties.org.quartz.threadPool.threadPriority=5
spring.quartz.properties.org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread=true
```

以上的配置信息就不一一介绍了，从quartz的配置属性的命名上就能脑补出来的。

前提是你有配置数据源能正常使用哈！

### 使用

在quartz的官方介绍中，其面向使用者的核心是scheduler，通过它可以控制job的创建、暂停、恢复、触发、删除、修改、中断等行为。所以本starter对这些功能做了简单的封装，并提供简单的web控制台，只需要简单引入一个jar包，配置少许佐料就能开箱享用。

#### 引入jar包

jar包三维：

```
<dependency>
  <groupId>com.beelego</groupId>
  <artifactId>spring-boot-starter-quartz</artifactId>
  <version>1.0.1-RELEASE</version>
</dependency>
```

#### 启用ui等功能

开启ui及自动装配jobBean功能，在application.properties中增加：

```
kochamcie.quartz.enabled=true
```

####访问调度控制台 

访问路径为：/quartz/index.html，页面上可能会没数据，你需要F12查看下network是否有接口返回404的情况，这是由于springboot自动装配机制导致的。这里要在你的启动类上加@ComponentScan让springboot自动装配下俺的组件：

```
@SpringBooApplication
@ComponentScan(basePackages= {"com.beelego"})
public class Application{
  
}
```

你就能愉快的增删改查啦，quartz的使用方式未曾改变。

#### 自动装配jobBean

项目启动时，starter会自动装配有@KochamcieJob修饰的JobBean，这个注解是job的基本信息，并未对信息做强制添加或者是校验，调度类名其实可以在装配器中自动获取，但是放出来觉着提神醒脑一点：

```
public @interface KochamcieJob {
    String name() default "";	// job name

    String group() default "";	// job group

    String cron() default "";	// 调度的cron表达式

    Class<? extends Job> target();	// 指出调度类名

    String description() default "";	// 描述
}
```

下面举个例子：

```
@Component	// 如果想要被装配还需要表明自己想跟spring玩耍
@KochamcieJob(name = "FirstJob", group = "FirstJob", cron = "0/2 * * * * ?", target = FirstJob.class, description = "66666")
public class FirstJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        int pageNo = 6;
        System.out.println(pageNo + "========================");
    }
}
```

可以看到，如果去掉1、2行的代码，就是quartz的使用方式并且可以到控制台添加这个FirstJob！

starter还封装了一些我觉得使用频次高的东东，比如集群环境下你想要哪个机器执行？每次执行的条数？这些看个人吧，你们也可以自行扩展。

quartz还有个特别重要的模块就是JobData，这个可以在job运行期间获取到。通过这个我们可以定制化的去控制job的业务代码逻辑，比如控制每批次处理的记录数，指定某个ip的机器可以执行等，只需要对上面的FirstJob做点小改动：

```
public class FirstJob extends KochamcieJobBean {// 没错，就是更换了所要继承的类

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
      // 直接从jobData获取，而jobData可以通过页面去配置实时生效
        int pageNo = executePageNo(jobExecutionContext);
        System.out.println(pageNo + "========================");
    }
}
```

需要说明一点，页面上的jobData也就是[Customize]那一列，是json格式。starter中约定了几项常用的key：

```
{
  "PAGENO":1,
  "PAGESIZE":10,
  "FORCE":"TRUE",
  "IPS":"101.200.53.20,101.200.53.21"
}
```

### 附录

选择使用quartz将job信息存储到数据库，需要提前将相关表生成好，初始化生成表的sql要么？