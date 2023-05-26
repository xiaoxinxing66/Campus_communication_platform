package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/21
 **/
@RunWith(SpringRunner.class)
@org.springframework.boot.test.context.SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SpringBootTest {

    private DiscussPost data;
    @Autowired
    private DiscussPostService discussPostService;
    @BeforeClass
    public static void beforeClass(){
        System.out.println("==>beforeClass , 只调用一次，启动项目时执行");
    }

    @AfterClass
    public static void AfterClass(){
        System.out.println("==>AfterClass ， 只调用一次，启动项目时执行");
    }

    /**
     * 初始化数据
     */
        @Before
    public  void before(){
        System.out.println("==>before，方法前后执行");
        data = new DiscussPost();
        data.setUserId(111);
        data.setTitle("Test");
        data.setContent("Test");
        data.setCreateTime(new Date());
        discussPostService.insertDiscussPost(data);
    }

    /**
     * 删除初始化数据
     */
    @After
    public  void after(){
        System.out.println("==>After，方法前后执行");
        discussPostService.updateStatus(data.getId() , 2 );
    }

    @Test
    public void testFindDiscussPost(){
        DiscussPost discussPost = discussPostService.selectDiscussPostById(data.getId());
        Assert.assertNotNull(discussPost);
        Assert.assertEquals(data.getTitle() , discussPost.getTitle());
        Assert.assertEquals(data.getContent() , discussPost.getContent());
    }
}
