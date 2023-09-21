package com.sivan.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/17
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTest {
    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTest.class);

    //Spring 普通线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    //Spring 定时任务线程池
    @Autowired
    private ThreadPoolTaskScheduler scheduler;
    //JDK线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    //JDK执行定时任务线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);


    private void sleep(long m){
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    // 1.1 JDK线程池
    @Test
    public void testExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello testExecutorService");
            }
        };
        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }
        sleep(10000);
    }
    //1.2 JDK定时任务线程池
    @Test
    public void testScheduledExecutorService(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello testScheduledExecutorService");
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(task, 10000, 1000, TimeUnit.MILLISECONDS);

        sleep(30000);
    }
    //2. 1 Spring普通线程池
    @Test
    public void testSpringExecutor(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello testSpringExecutor");
            }
        };

        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(task);
        }
        sleep(10000);
    }
    //2.2 Spring定时任务线程池
    @Test
    public void testSpringSchedulerExecutor(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello testSpringExecutor");
            }
        };

        Date startTime = new Date(System.currentTimeMillis()  +10000);
        scheduler.scheduleAtFixedRate(task, startTime , 100);
        sleep(20000);
    }
    //2.3 Spring 线程池简化调用方式
    @Test
    public void testSimpleExecutor(){
        for (int i = 0; i < 10; i++) {
            //XXXService.加了注解的方法【@Async】
            //可以直接把该方法放入线程池调用。
        }
        sleep(10000);
    }
    //2.4 Spring 线程池简化调用方式
    @Test
    public void testSimpleSchedulerExecutor(){
        //方法自动调用。
        // 【@Scheduled(initialDelay = 10000 , fixedRate = 1000) 10s后执行，每隔1s执行一次】

        sleep(30000);
    }

}
