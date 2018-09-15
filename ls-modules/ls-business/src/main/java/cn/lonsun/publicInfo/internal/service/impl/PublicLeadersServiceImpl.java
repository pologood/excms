package cn.lonsun.publicInfo.internal.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.ContentReferRelationEO;
import cn.lonsun.content.vo.SortVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitEO;
import cn.lonsun.publicInfo.internal.dao.IPublicLeadersDao;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.internal.entity.PublicLeadersEO;
import cn.lonsun.publicInfo.internal.service.IPublicLeadersService;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.publicInfo.vo.PublicLeadersQueryVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.ArrayFormat;
import cn.lonsun.util.FileUploadUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 单位领导service <br/>
 * 
 * @date 2016年9月19日 <br/>
 * @author liukun <br/>
 * @version v1.0 <br/>
 */
@Service
public class PublicLeadersServiceImpl extends MockService<PublicLeadersEO> implements IPublicLeadersService {

    @Resource
    private IPublicLeadersDao publicLeadersDao;
    @Resource
    private ContentMongoServiceImpl contentMongoService;
    @Resource
    private IOrganService organService;

    @Override
    public Pagination getPagination(PublicLeadersQueryVO queryVO) {
        return publicLeadersDao.getPagination(queryVO);
    }

    @Override
    public List<PublicLeadersEO> getPublicLeadersList(PublicLeadersQueryVO queryVO) {
        return publicLeadersDao.getPublicLeadersList(queryVO);
    }

    @Override
    public Long saveOrUpdateLeaders(PublicLeadersEO publicLeadersEO) {
        Long id = publicLeadersEO.getLeadersId();
        if (AppUtil.isEmpty(id)) {
            // publicLeadersEO.setSiteId(LoginPersonUtil.getSiteId());
            publicLeadersEO.setOrganId(LoginPersonUtil.getUnitId());
            publicLeadersEO.setOrganName(LoginPersonUtil.getUnitName());
            publicLeadersEO.setCreateUserId(LoginPersonUtil.getUserId());
            publicLeadersEO.setCreateDate(new Date());
            publicLeadersEO.setCreateOrganId(LoginPersonUtil.getUnitId());

            id = saveEntity(publicLeadersEO);
            FileUploadUtil.setStatus(publicLeadersEO.getImageLink(), 1, id, 0L, LoginPersonUtil.getSiteId());

            // 保存内容
            ContentMongoEO _eo = new ContentMongoEO();
            _eo.setId(id);
            _eo.setContent(publicLeadersEO.getExperience());
            contentMongoService.save(_eo);
        } else {// 修改
                // 保存内容
            ContentMongoEO _eo = new ContentMongoEO();
            _eo.setId(id);
            _eo.setContent(publicLeadersEO.getExperience());
            contentMongoService.save(_eo);

            publicLeadersDao.merge(publicLeadersEO);
        }
        return id;
    }

    @Override
    public Long admin_saveOrUpdateLeaders(PublicLeadersEO publicLeadersEO) {
        Long id = publicLeadersEO.getLeadersId();
        if (AppUtil.isEmpty(id)) {
            // OrganEO organ =
            // organService.getEntity(OrganEO.class,publicLeadersEO.getOrganId());
            // publicLeadersEO.setSiteId(organ.getSiteId());

            publicLeadersEO.setCreateUserId(LoginPersonUtil.getUserId());
            publicLeadersEO.setCreateDate(new Date());
            publicLeadersEO.setCreateOrganId(LoginPersonUtil.getUnitId());

            id = saveEntity(publicLeadersEO);
            FileUploadUtil.setStatus(publicLeadersEO.getImageLink(), 1, id, 0L, LoginPersonUtil.getSiteId());

            // 保存内容
            ContentMongoEO _eo = new ContentMongoEO();
            _eo.setId(id);
            _eo.setContent(publicLeadersEO.getExperience());
            contentMongoService.save(_eo);
        } else {// 修改
                // 保存内容
            ContentMongoEO _eo = new ContentMongoEO();
            _eo.setId(id);
            _eo.setContent(publicLeadersEO.getExperience());
            contentMongoService.save(_eo);

            // OrganEO organ =
            // organService.getEntity(OrganEO.class,publicLeadersEO.getOrganId());
            // publicLeadersEO.setSiteId(organ.getSiteId());
            publicLeadersDao.merge(publicLeadersEO);
        }
        return id;
    }
}