package com.nowcoder.community.config;

import com.nowcoder.community.Quartz.AlphaJob;
import com.nowcoder.community.Quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/17
 **/
//配置类——》数据库——》调用
@Configuration
public class QuartzConfig {

    // 配置JobDetail
    //@Bean
    public JobDetailFactoryBean alphaJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        //任务是否可以持久保存
        factoryBean.setDurability(true);
        //任务是否可恢复
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }
    //配置Trigger（SimpleTriggerFactoryBean、CronTriggerFactoryBean）
    //@Bean
    public SimpleTriggerFactoryBean alphaSimpleTrigger(JobDetail alphaJobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
    // 刷新帖子分数
    @Bean
    public JobDetailFactoryBean PostScoreRefreshJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("PostScoreRefreshJob");
        factoryBean.setGroup("PostScoreRefreshJobGroup");
        //任务是否可以持久保存
        factoryBean.setDurability(true);
        //任务是否可恢复
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }
    //配置Trigger（SimpleTriggerFactoryBean、CronTriggerFactoryBean）
    @Bean
    public SimpleTriggerFactoryBean PostScoreRefreshSimpleTrigger(JobDetail PostScoreRefreshJobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(PostScoreRefreshJobDetail);
        factoryBean.setName("PostScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 2);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
