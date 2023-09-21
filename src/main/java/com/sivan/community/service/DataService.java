package com.sivan.community.service;

import com.sivan.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/17
 **/
@Service
public class DataService {
    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    // 指定IP加入UV
    public void recordUV(String ip){
        String redisKey = RedisKeyUtil.getUvKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey , ip);
    }
    //统计范围内的UV
    public long recordUVRange(Date start , Date end){
        if(start == null || end == null){
            throw  new IllegalArgumentException("null Date !!!");
        }
        //整理Key
        List<String> keyList = new ArrayList<>();
        //循环日期
        Calendar calender = Calendar.getInstance();
        calender.setTime(start);
        //不晚于end，继续循环
        while(!calender.getTime().after(end)){
            String key = RedisKeyUtil.getUvKey(df.format(calender.getTime()));
            keyList.add(key);
            calender.add(Calendar.DATE , 1);
        }
        //合并Key
        String redisKey = RedisKeyUtil.getUvKey(df.format(start) , df.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey , keyList.toArray());
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }
    // 指定用户加入DAU
    public void recordDAU(int userId){
        String redisKey = RedisKeyUtil.getDauKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey , userId , true);
    }
    //指定日期范围用户DAU
    public long recordDAURange(Date start , Date end){
        if(start == null || end == null){
            throw  new IllegalArgumentException("null Date !!!");
        }
        //整理Key
        List<byte[]> keyList = new ArrayList<>();
        //循环日期
        Calendar calender = Calendar.getInstance();
        calender.setTime(start);
        //不晚于end，继续循环
        while(!calender.getTime().after(end)){
            String key = RedisKeyUtil.getDauKey(df.format(calender.getTime()));
            keyList.add(key.getBytes());
            calender.add(Calendar.DATE , 1);
        }
        //对所有Key进行OR运算
        String redisKey = RedisKeyUtil.getDauKey(df.format(start) , df.format(end));
        return (long)redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDauKey(df.format(start) , df.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR ,
                        redisKey.getBytes() , keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });
    }
}
