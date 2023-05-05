package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Author 不知名网友鑫
 * @Date 2023/4/18
 **/
@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 查询到用户id时，不显示id，显示用户的名称。
     * @param id
     * @return
     */
    public User findUserById(int id){
        User user = getCache(id);
        if(user == null){
            user = initCache(id);
        }
        return user;
//        return userMapper.selectById(id);
    }

    public Map<String , Object> register(User user){
        Map<String , Object> map = new HashMap<>();
        //对空值进行判断
        if(user == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }
        // 验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }

        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }
        // 注册用户
        //生成随机字符串
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        //对密码进行加密
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        //设置用户类型【普通用户】
        user.setType(0);
        //用户没有激活
        user.setStatus(0);
        //用户随机的激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        //用户随机的头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        //设置创建时间
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 激活邮件
        // 使用Context携带对象。
        Context context = new Context();
        //设置邮箱
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        //设置url路径，该路径可以获取到激活码
        context.setVariable("url", url);
        //设置内容，这里的内容使用模板引擎获取。
        String content = templateEngine.process("/mail/activation", context);
        //向用户邮箱发送邮件。
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    /**
     * 返回激活的状态。
     * @param userId
     * @param code
     * @return
     */
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            //改变激活码，就激活了。
            userMapper.updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 登录的业务层逻辑
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }

        // 验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 生成登录凭证，0状态，表示凭证有效
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        // 传入凭证过期秒数。
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
        //
//        loginTicketMapper.insertLoginTicket(loginTicket);

        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey , loginTicket);

        //返回ticket字符串。这个字符串最后要放到cookie中。
        map.put("ticket", loginTicket.getTicket());
        return map;
    }
    public void logout(String ticket){
        //设置登录状态为1，无效。
//        loginTicketMapper.updateStatus(ticket , 1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey , loginTicket);

    }
    public LoginTicket findLoginTicket(String ticket){
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int uploadPassword(int id , String password) {
        //对新密码采用加盐加密。
        return userMapper.updatePassword(id , password);
    }
    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }
    //1. 先从缓存中取值
    private User getCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }
    //2. 取不到，再初始化数据
    private User initCache(int userId){
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey , user , 3600 , TimeUnit.SECONDS);
        return user;
    }
    //3. 数据变化，清除缓存数据
    private void clearCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }
}
