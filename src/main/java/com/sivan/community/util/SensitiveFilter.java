package com.sivan.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 不知名网友鑫
 * @Date 2023/4/25
 **/

/**
 * 前缀树过滤器
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    //替换字符
    private static final String REPLACEMENT = "***";
    //初始化根节点
    private TrieNode rootNode = new TrieNode();

    /**
     * PostConstruct表示该方法是初始化方法，在Bean被初始化后被调用。
     */
    @PostConstruct
    public void init(){
        try(
            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
        ){
            String keyword;
            while((keyword = reader.readLine()) != null ){
                //添加前缀树节点
                this.addKeyword(keyword);
            }
        }catch(IOException e){
            logger.error("加载敏感词失效"+ e.getMessage()) ;
        }

    }
    //添加前缀树节点
    private void addKeyword(String keyword){
        TrieNode trieNode = rootNode;
        //遍历‘赌’与‘博’字符
        for(int i = 0 ; i < keyword.length(); i ++){
            char c = keyword.charAt(i);
            TrieNode subNode = trieNode.getSubNode(c);
            if(subNode == null){
                //初始化子节点
                subNode = new TrieNode();
                trieNode.addsubNode(c , subNode);
            }
            //指向子节点
            trieNode = subNode;

            // 设置结束标识
            if(i == keyword.length() - 1){
                trieNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 返回替换后字符串[过滤敏感词]
     * @param text
     * @return 返回过滤后文本
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        //指针1：指向树的节点
        TrieNode trieNode = rootNode;
        //指针2。指针三：遍历text
        int begin = 0 ,position = 0;
        //保存结果
        StringBuilder sb = new StringBuilder();
        //遍历每一个Character
        while(position < text.length()){
             char c = text.charAt(position);
             //跳过符号 赌&博的情况
            if(isSymbol(c)){
                //若指针1处于根节点，将此符号计入结果，让指针2向下走一步
                if(trieNode == rootNode){
                    sb.append(c);
                    begin ++;
                }
                //无论符号在开头或中间，指针3都向下走
                position ++;
                continue;
            }
            //检查下级节点
            trieNode = trieNode.getSubNode(c);
            if(trieNode == null){
                //说明begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                 ++ begin;
                 position = begin;
                 //指针3归位
                trieNode = rootNode;
            }else if(trieNode.isKeywordEnd()){
                //找到了敏感词，将begin——position替换
                sb.append(REPLACEMENT);
                //指针进入下一个位置
                 begin = ++ position;
                //指针3归位
                trieNode = rootNode;
            }else{
                //继续检查下一个字符
                position ++;
            }
        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }
    //判断是否为符号
    private boolean isSymbol(Character c){
        //东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
    //前缀树结构
    private class TrieNode{
        private boolean isKeywordEnd = false;

        //子节点:key是下级字符，value是下级节点
        private Map<Character , TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd(){
            return isKeywordEnd;
        }
        public void setKeywordEnd(boolean keywordEnd){
            isKeywordEnd = keywordEnd;
        }
        public void addsubNode(Character c , TrieNode node){
            subNodes.put(c , node);
        }
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
