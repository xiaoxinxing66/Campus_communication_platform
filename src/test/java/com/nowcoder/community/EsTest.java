package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author 不知名网友鑫
 * @Date 2023/5/15
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class EsTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 添加数据
     */
    @Test
    public void test(){
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));


    }
    /**
     * 添加多条数据
     */
    @Test
    public void test1(){

//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101,0,100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102 , 0  ,100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0 ,100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0 ,100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0 ,100));
////        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(113, 0 ,100));
//        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0 ,100));
    }

    /**
     * es的搜索功能
     */
    @Test
    public void test2(){
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬" , "title" , "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of( 0 , 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>")
                                .postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>")
                                .postTags("</em>")
                ).build();
//        elasticsearchTemplate.queryForPage(searchQuery, class, SearchResultMapper)
        //底层获取了高亮显示的值，但是没有处理.
        Page<DiscussPost> page = discussPostRepository.search(searchQuery);
        System.out.println(page.getTotalPages());
        System.out.println(page.getTotalElements());
        for(DiscussPost post : page){
            System.out.println(post);
        }
    }
    @Test
    public void test3(){
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬" , "title" , "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of( 0 , 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>")
                                .postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>")
                                .postTags("</em>")
                ).build();

        Page<DiscussPost> page = elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                SearchHits hits = searchResponse.getHits();
                if(hits.getTotalHits() <= 0){
                    return null;
                }
                List<DiscussPost> list = new ArrayList<>();
                for(SearchHit hit : hits){
                    DiscussPost post =  new DiscussPost();
                    String id = hit.getSourceAsMap().get("id").toString();
                    post.setId(Integer.valueOf(id));

                    String userId = hit.getSourceAsMap().get("userId").toString();
                    post.setUserId(Integer.valueOf(userId));

                    String title = hit.getSourceAsMap().get("title").toString();
                    post.setTitle(title);
                    String status = hit.getSourceAsMap().get("status").toString();
                    post.setStatus(Integer.valueOf(status));

                    String createTime = hit.getSourceAsMap().get("createTime").toString();
                    post.setCreateTime(new Date(Long.valueOf(createTime)));

                    String commentCount  = hit.getSourceAsMap().get("commentCount").toString();
                    post.setCommentCount(Integer.valueOf(commentCount));

                    //处理高亮显示
                    HighlightField titleFiled = hit.getHighlightFields().get("title");
                    if(titleFiled != null){
                        post.setTitle(titleFiled.getFragments()[0].toString());
                    }
                    //处理高亮显示
                    HighlightField contentFiled = hit.getHighlightFields().get("content");
                    if(contentFiled != null){
                        post.setContent(contentFiled.getFragments()[0].toString());
                    }
                    list.add(post);
                }
                return new AggregatedPageImpl(list , pageable ,
                        hits.getTotalHits() , searchResponse.getAggregations() ,
                        searchResponse.getScrollId() , hits.getMaxScore()
                        );
            }
        });
//        Page<DiscussPost> page = discussPostRepository.search(searchQuery);
        System.out.println(page.getTotalPages());
        System.out.println(page.getTotalElements());
        for(DiscussPost post : page){
            System.out.println(post);
        }
    }
}
