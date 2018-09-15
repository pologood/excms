package cn.lonsun.nlp.utils;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.nlp.internal.service.INlpKeyWordsArticleRelService;
import cn.lonsun.nlp.internal.service.INlpKeyWordsMemberRelService;
import cn.lonsun.system.member.vo.MemberSessionVO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: liuk
 * @version: v1.0
 * @date:2018/7/9 11:47
 */
public class MemberLabelUtil {
    private static INlpKeyWordsMemberRelService nlpKeyWordsMemberRelService = SpringContextHolder.getBean(INlpKeyWordsMemberRelService.class);
    private static INlpKeyWordsArticleRelService nlpKeyWordsArticleRelService = SpringContextHolder.getBean(INlpKeyWordsArticleRelService.class);
    // mongodb服务
    private static ContentMongoServiceImpl contentMongoService = SpringContextHolder.getBean(ContentMongoServiceImpl.class);

    public static void handleMemberKeywordRel(String url,String ip,Long siteId){
        MemberSessionVO memberVO = (MemberSessionVO) ContextHolderUtils.getSession().getAttribute("member");
        Long contentId = isArticlePage(url);
        if(contentId!=null){
            if(!BloomFilter.getInstance().contains(contentId+"")){//新的文章页，需要进行关键词提取
                ContentMongoEO mongoEO = contentMongoService.queryById(contentId);
                if(mongoEO!=null&&!AppUtil.isEmpty(mongoEO.getContent())){
                    nlpKeyWordsArticleRelService.analyseKeyWords(contentId,siteId,mongoEO.getContent());
                }

            }
            if(memberVO!=null){//游客访问则不处理
                //保存会员对文章关键词的访问记录
                nlpKeyWordsMemberRelService.saveMemberRel(memberVO.getId(),ip,contentId,siteId);
            }
        }
    }

    public static void analyseKeyWords(Long contentId,Long siteId){
        if(AppUtil.isEmpty(contentId)){
            return;
        }
        ContentMongoEO mongoEO = contentMongoService.queryById(contentId);
        if(mongoEO!=null&&!AppUtil.isEmpty(mongoEO.getContent())){
            nlpKeyWordsArticleRelService.analyseKeyWords(contentId,siteId,mongoEO.getContent());
        }
        //将文章id添加到布隆过滤器中，访问文章页时可以不必再提取关键词
        BloomFilter.getInstance().contains(contentId+"");

    }

    /**
     * 判断是否是文章页,返回null则代表不是文章页，否则返回contentId
     * @param url
     * @return
     */
    public static Long isArticlePage(String url){
        if(AppUtil.isEmpty(url)){
            return null;
        }

        if(url.contains(".html")){//静态页面
            if(url.contains("index.html")){//首页或者新版栏目页
                return null;
            }
            url = url.replace("http://","").replace("https://","")
                    .replace("//","/");
            if(url.contains("?")){//带参数
                url = url.substring(0,url.indexOf("?"));
            }
            //匹配老版栏目页路径
            Pattern pattern = Pattern.compile("^[\\w]{1,}(?:\\.?[\\w]{1,})+[/]{1}[\\d]+(?:\\.html{1})[\\w-_/?&=#%:]*$");
            Matcher matcher = pattern.matcher(url);
            if(matcher.find()){//老版栏目页
                return null;
            }
            Long contentId = null;
            String id ;
            try {
                id = url.substring(url.lastIndexOf("/")+1,url.indexOf(".html"));//获取文章id
                contentId = Long.parseLong(id);
            }catch (Exception e){
                e.printStackTrace();
            }
            return contentId;

        }else{//动态页面
            if(url.contains("/content/article/")||url.contains("/public/content/")){//动态文章页
                Long contentId = null;
                String id ;
                try {
                    if(url.contains("?")){//带参数
                        id = url.substring(url.lastIndexOf("/")+1,url.indexOf("?"));//获取文章id
                    }else{//不带参数
                        id = url.substring(url.lastIndexOf("/")+1);//获取文章id
                    }
                    contentId = Long.parseLong(id);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return contentId;
            }
        }

        return null;
    }
}
