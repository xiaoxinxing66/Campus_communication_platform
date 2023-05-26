package com.nowcoder.community.controller;

import com.nowcoder.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/17
 **/
@Controller
public class DataController {
    @Autowired
    private DataService dataService;
    //写两个请求，要求支持被转发。
    @RequestMapping(value = "/data" , method = {RequestMethod.GET , RequestMethod.POST})
    public String getDataPage(){
        return "/site/admin/data";
    }
    //统计UV,页面上要点击按钮，提交表单,POST。
    @RequestMapping(value = "/data/uv" , method = RequestMethod.POST)
    public String getUV(Model model , @DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @DateTimeFormat(pattern = "yyyy-MM-dd") Date end){
        //统计区间UV。【数据在拦截其中保存了，这里只统计即可】
        long uv = dataService.recordUVRange(start , end);
        model.addAttribute("uvResult" , uv);
        model.addAttribute("uvStartDate" , start);
        model.addAttribute("uvEndDate" , end);
        return "forward:/data";
    }
    //统计DAU,页面上要点击按钮，提交表单,POST。
    @RequestMapping(value = "/data/dau" , method = RequestMethod.POST)
    public String getDAU(Model model , @DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @DateTimeFormat(pattern = "yyyy-MM-dd") Date end){
        //统计区间UV。【数据在拦截其中保存了，这里只统计即可】
        long dau = dataService.recordDAURange(start , end);
        model.addAttribute("dauResult" , dau);
        model.addAttribute("dauStartDate" , start);
        model.addAttribute("dauEndDate" , end);
        return "forward:/data";
    }

}
