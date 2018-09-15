package cn.lonsun.staticcenter.generate.tag.impl.article;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import cn.lonsun.staticcenter.generate.util.MongoUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HandleItemsPageListBeanService extends AbstractBeanService {

    @DbInject("baseContent")
    private IBaseContentDao baseContentDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        String strSiteId = context.getParamMap().get("siteId");
        Long siteId = null;
        if (StringUtils.isEmpty(strSiteId)) {
            siteId = context.getSiteId();
        } else {
            siteId = Long.valueOf(strSiteId);
        }
        AssertUtil.isEmpty(siteId, "站点id不能为空！");
        Long[] columnIds = super.getQueryColumnIdByChild(paramObj, BaseContentEO.TypeCode.handleItems.toString());
        AssertUtil.isEmpty(columnIds, "栏目不能为空！");
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        Boolean isImg = paramObj.getBoolean("isImg");// 是否含有图片
        // 查询
        Integer size = columnIds.length;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        StringBuffer hql = new StringBuffer();
        hql.append(" from BaseContentEO c where c.siteId = :siteId and c.isPublish = :isPublish ");
        hql.append(" and c.columnId ").append(size == 1 ? " = :columnIds" : " in (:columnIds) ");
        if (isImg != null && isImg == true) {// 查询有图片的
            hql.append(" and (c.imageLink != '' OR c.imageLink IS NOT NULL) ");
        }
        // 搜索关键字
        String keyWords = context.getParamMap().get("SearchWords");
        if (StringUtils.isNotEmpty(keyWords)) {
            hql.append(" and c.title like '%" + SqlUtil.prepareParam4Query(keyWords) + "%' ");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String startDate = context.getParamMap().get("startDate");
        if (StringUtils.isNotEmpty(startDate)) {
            try {
                paramMap.put("startDate", sdf.parse(startDate));
                hql.append(" and c.publishDate >= :startDate");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String endDate = context.getParamMap().get("endDate");
        if (StringUtils.isNotEmpty(endDate)) {
            try {
                paramMap.put("endDate", sdf.parse(endDate));
                hql.append(" and c.publishDate <= :endDate");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // 排序依第一个栏目为准
        String order = ModelConfigUtil.getOrderByHql(columnIds[0], siteId, BaseContentEO.TypeCode.articleNews.toString());
        hql.append(" and c.recordStatus = :recordStatus ").append(order);
        paramMap.put("siteId", siteId);
        paramMap.put("isPublish", 1);// 发布
        paramMap.put("columnIds", size == 1 ? columnIds[0] : columnIds);
        paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return baseContentDao.getPagination(pageIndex, pageSize, hql.toString(), paramMap);
    }

    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        // 获取分页对象
        Pagination pagination = (Pagination) resultObj;
        if (null != pagination) {
            Context context = ContextHolder.getContext();// 上下文
            List<?> resultList = pagination.getData();
            // 处理查询结果
            if (null != resultList && !resultList.isEmpty()) {
                for (Object o : resultList) {
                    BaseContentEO eo = (BaseContentEO) o;
                    String path = "";
                    if (!AppUtil.isEmpty(eo.getRedirectLink())) {
                        path = eo.getRedirectLink();
                    } else {
                        path = PathUtil.getLinkPath(eo.getColumnId(), eo.getId());// 拿到栏目页和文章页id
                    }
                    eo.setLink(path);
                    // 根据id去mongodb读取文件内容
                    String content = MongoUtil.queryById(eo.getId());
                    if (!AppUtil.isEmpty(content)) {
                        String txtcontent = content.replace("&nbsp;", ""); // 剔出&nbsp;
                        eo.setArticle(txtcontent);
                    }
                    //图片新闻没有缩略图时抓取内容中第一张图片作为缩略图
                    if(eo.getTypeCode().equals(BaseContentEO.TypeCode.pictureNews.toString())){
                        if(AppUtil.isEmpty(eo.getImageLink())){
                            getImageLinkFromContent(eo);
                        }
                    }

                }
            }
            // 设置连接地址
            if (context.getColumnId() != null) {
                String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), context.getColumnId(), null);
                pagination.setLinkPrefix(path);
            } else {
                pagination.setLinkPrefix(context.getPath());
            }
        }
        return super.doProcess(resultObj, paramObj);
    }


    /**
     * 图片新闻没有缩略图时抓取内容中第一张图片作为缩略图
     * @param eo
     * @return
     */
    private BaseContentEO getImageLinkFromContent(BaseContentEO eo){
        if(eo.getTypeCode().equals(BaseContentEO.TypeCode.pictureNews.toString())){
            Pattern p_image;
            Matcher m_image;
            if(AppUtil.isEmpty(eo.getImageLink())){
                //去mongoDB查询内容
                String content = MongoUtil.queryById(eo.getId());;
                if(!AppUtil.isEmpty(content)){
                    String img = "";
                    //正则获取图片
                    p_image = Pattern.compile
                            ("<img.*src\\s*=\\s*(.*?)[^>]*?>", Pattern.CASE_INSENSITIVE);
                    m_image = p_image.matcher(content);
                    while (m_image.find()) {
                        boolean exitFlag = false;
                        // 得到<img />数据
                        img = m_image.group();
                        // 匹配<img>中的src数据
                        Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
                        while (m.find()) {
                            String imagelink = m.group(1);
                            if(!AppUtil.isEmpty(imagelink)&&imagelink.contains("/mongo/")){
                                imagelink = imagelink.substring(imagelink.lastIndexOf("/")+1);
                            }
                            eo.setImageLink(imagelink);
                            exitFlag = true;
                            break;//退出当前循环

                        }
                        if(exitFlag){
                            break;
                        }
                    }
                }
            }
        }
        return eo;
    }
}