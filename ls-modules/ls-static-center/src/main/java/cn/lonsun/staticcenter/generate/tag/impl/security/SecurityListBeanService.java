package cn.lonsun.staticcenter.generate.tag.impl.security;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.security.internal.dao.IMateriaDao;
import cn.lonsun.security.internal.vo.MateriaNumVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lonsun on 2016-12-14.
 */
@Component
public class SecurityListBeanService extends AbstractBeanService {

    @Autowired
    private IMateriaDao materiaDao;
    @Value("${cotextUrl}")
    private String cotextUrl;

    @Override
    public Object getObject(JSONObject paramObj) {
        // 需要显示条数.
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        StringBuilder hql =new StringBuilder();
        Map<String, Object> param  =new HashMap<String, Object>();
        hql.append("select s.materiaName as materiaName,s.year as year,s.periodical as periodical,s.filePath as filePath,c.imageLink as imageLink from BaseContentEO c,SecurityMateria s where c.id=s.baseContentId and c.recordStatus='" + AMockEntity.RecordStatus.Normal.toString() + "'  and s.siteId=:siteId  and c.isPublish=1 and c.typeCode='" + BaseContentEO.TypeCode.securityMateria.toString() + "' ");
        param.put("siteId",ContextHolder.getContext().getSiteId());
        hql.append(" order by s.year desc,s.periodical desc");
        List<MateriaNumVO> list =(List<MateriaNumVO>) materiaDao.getBeansByHql(hql.toString(),param, MateriaNumVO.class, num);
//        String fileServerPath= PathUtil.getPathConfig().getFileServerPath();
        for(MateriaNumVO materiaNumVO:  list){
            if(!AppUtil.isEmpty(materiaNumVO.getFilePath())){
                materiaNumVO.setFilePath(cotextUrl+materiaNumVO.getFilePath());

            }
            if(!AppUtil.isEmpty(materiaNumVO.getImageLink())){
                materiaNumVO.setImageLink(PathUtil.getUrl(materiaNumVO.getImageLink()));

            }
        }


        return list;
    }
}
