package cn.lonsun.staticcenter.generate.tag.impl.govbbs;

import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.govbbs.internal.dao.IBbsFileDao;
import cn.lonsun.govbbs.internal.vo.BbsFileVO;
import cn.lonsun.govbbs.util.BbsStaticCenterUtil;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 论坛图片
 * Created by zhangchao on 2016/12/26.
 */
@Component
public class GovBbsPicsBeanService extends AbstractBeanService {

    @Autowired
    private IBbsFileDao bbsFileDao;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        //站点id
        Long siteId = paramObj.getLong("siteId");
        if (!(siteId != null && siteId != 0)) {
            siteId = context.getSiteId();
        }
        // 需要显示条数.
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        num =(num == null?8:num);
        //版块ids
        List<Long> plateIds = null;
        //查询
        String plateIdStr = paramObj.getString("plateIds");
        if (!StringUtils.isEmpty(plateIdStr)) {
            plateIds = new ArrayList<Long>();
            plateIds.addAll(Arrays.asList((Long[]) (ConvertUtils.convert(plateIdStr.split(","), Long.class))));
        }
        //默认排序
        String sortField = paramObj.getString("sortField");
        if(StringUtils.isEmpty(sortField)){
            sortField = BbsStaticCenterUtil.defaultDateOrder;
        }
        List<BbsFileVO> files = null;
        List<Object> values = new ArrayList<Object>();
        String hqlPost = "select b.postId as postId,b.title as title " +
                "from BbsPostEO b where b.siteId = ? and b.recordStatus = 'Normal' and b.isPublish = 1";
        values.add(siteId);
        if(plateIds != null && !plateIds.isEmpty()){
            hqlPost +=" and (1=0";
            for(Long plateId:plateIds){
                hqlPost +=" or b.plateId = ?";
                values.add(plateId);
            }
            hqlPost +=")";
        }
        hqlPost +=" order by "+ BbsStaticCenterUtil.defaultPostOrder;
        List<BbsFileVO> posts = (List<BbsFileVO>)bbsFileDao.getBeansByHql(hqlPost.toString(),values.toArray(),BbsFileVO.class,num);
        if(posts != null){
            values.clear();
            HashMap<Long,String> pm = new HashMap<Long, String>();
            String hqlFile = "select b.id as id,b.postId as postId,b.mongoId as mongoId,b.fileName as fileName from BbsFileEO b " +
                    "where b.siteId = ? and b.recordStatus='Normal' and b.status = 1 and b.auditStatus = 1 " +
                    "and (lower(b.suffix) = 'gif' or lower(b.suffix) = 'png' or lower(b.suffix) = 'jpg' or lower(b.suffix) = 'jpeg')";
            values.add(siteId);
            if(posts != null && !posts.isEmpty()){
                hqlFile +=" and (1=0";
                for(BbsFileVO post:posts){
                    if(post != null && post.getPostId() != null){
                        hqlFile +=" or b.postId = ?";
                        values.add(post.getPostId());
                        pm.put(post.getPostId(),post.getTitle());
                    }
                }
                hqlFile +=")";
            }
            hqlFile +=" order by "+sortField;
            List<BbsFileVO> fs = (List<BbsFileVO>)bbsFileDao.getBeansByHql(hqlFile,values.toArray(),BbsFileVO.class);
            if(fs != null && !fs.isEmpty()){
                files = new ArrayList<BbsFileVO>();
                for(BbsFileVO file:fs){
                    if(pm.containsKey(file.getPostId())){
                        String title = pm.get(file.getPostId());
                        file.setTitle(title);
                        files.add(file);
                        pm.remove(file.getPostId());
                    }
                }
            }
        }
        return files;
    }
}
