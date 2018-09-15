package cn.lonsun.staticcenter.exproject.ahszjj;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.staticcenter.util.JdbcUtils;
import cn.lonsun.util.HotWordsCheckUtil;

import java.util.*;

/**
 * Created by 1960274114 on 2016-10-13.
 * 综合信息-->质量工程
 */
public class SyncZqcmsArticleNews extends AbSyncInfo{

    public SyncZqcmsArticleNews(JdbcUtils jdbcUtils, Long siteId, Long createUserId, Long curColumdId) {
        super(jdbcUtils, siteId, createUserId, curColumdId);
    }
    public SyncZqcmsArticleNews(JdbcUtils jdbcUtils, Long siteId) {
        super(jdbcUtils, siteId, null, null);
    }


    /**
     * 质量品牌栏目列表
     * @return
     */
    public List<OldSiteColumnPairVO> getOldColumnList(){
        List<OldSiteColumnPairVO> ret = new ArrayList<OldSiteColumnPairVO>();
        String sql = "select id,ClassName,Followid,classCode from zqcms_class where Followid>0 order by Followid asc";
        List<Object> list =  jdbcUtils.excuteQuery(sql, null);
        if(null !=list && list.size()>0){
            for(Object obj:list){
                Map<String, Object> map = (HashMap<String, Object>) obj;
                String code = map.get("id") + "~" + map.get("Followid") + "~" + map.get("classCode");
                OldSiteColumnPairVO vo = new OldSiteColumnPairVO(code,(String)map.get("ClassName"));
                ret.add(vo);
            }
        }
        return ret;
    }

    private List<Object> getSysNewsList(String classId,String classcode,String followid){
        String sql = "select title,ftitle,newinfo,idate,author,ly,pic from zqcms_news where classcode=? and followid=? and sid=?";
        return  jdbcUtils.excuteQuery(sql, new Object[]{classcode, AppUtil.getInteger(followid),AppUtil.getLong(classId)});
    }

    public void imp(String classCode,String columnTypeCode){
        String[] classCodeArgs = classCode.split("~");
        String classId = classCodeArgs[0];
        String followid = classCodeArgs[1];
        String code = classCodeArgs[2];
        List<Object> newsList = getSysNewsList(classId,code,followid);
        if(null !=newsList && newsList.size()>0){
            for(Object obj:newsList){
                if(step>limitSize){
                    break;
                }
                step ++;
                Map<String, Object> map = (HashMap<String, Object>) obj;
                //标题
                String title =(String) map.get("title");
                //副标题
                String subTitle = (String) map.get("ftitle");
                //作者
                String author = (String) map.get("author");
                //来源
                String resources = (String) map.get("ly");
                //图片
                String pic = (String)map.get("pic");
                //视频
                String video =null;
                //发布日期
                Date publishDate = getDateValue( map.get("idate"));
                //内容
                String content = (String) map.get("newinfo");
                if(null !=content)
                    content = HotWordsCheckUtil.replaceAll(siteId, content);
                //图片


                BaseContentEO contentEO = new BaseContentEO();
                contentEO.setSiteId(siteId);
                contentEO.setColumnId(curColumdId);

                contentEO.setTypeCode(columnTypeCode);

                //导入标识
                contentEO.setEditor(imp_tag);
                contentEO.setIsPublish(isPublish);

                //导入数据
                contentEO.setTitle(title);
                contentEO.setSubTitle(subTitle);
                contentEO.setAuthor(author);
                contentEO.setResources(resources);
                contentEO.setPublishDate(publishDate);
                //标题新闻
                contentEO.setIsTitle(1);


                //保存
                IBaseContentService baseContentService = SpringContextHolder.getBean(IBaseContentService.class);
                baseContentService.saveArticleNews(contentEO,content,null,null,null,null);
            }
        }

    }
}
