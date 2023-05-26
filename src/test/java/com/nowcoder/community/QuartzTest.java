package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/17
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class QuartzTest {
    @Autowired
    private Scheduler scheduler;
    @Test
    public void test(){
        try {
            boolean b = scheduler.deleteJob(new JobKey("alphaJob", "alphaJobGroup"));
            System.out.println(b);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
