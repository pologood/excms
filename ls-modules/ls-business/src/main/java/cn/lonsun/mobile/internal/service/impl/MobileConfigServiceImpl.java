package cn.lonsun.mobile.internal.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.mobile.internal.dao.IMobileConfigDao;
import cn.lonsun.mobile.internal.entity.MobileConfigEO;
import cn.lonsun.mobile.internal.service.IMobileConfigService;
import cn.lonsun.mobile.vo.MobileConfigVO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("MobileColumnService")
public class MobileConfigServiceImpl extends MockService<MobileConfigEO> implements IMobileConfigService {

    @Autowired
    private IMobileConfigDao mobileConfigDao;

    @DbInject("baseContent")
    private IBaseContentDao contentDao;

    @Override
    public List<MobileConfigEO> getMobileConfigList(Long siteId) {
        List<MobileConfigEO> list = mobileConfigDao.getMobileConfigList(siteId);
        /*IndicatorEO columnEO = CacheHandler.getEntity(IndicatorEO.class, list.get(0).getIndicatorId());
        IndicatorEO siteEO = CacheHandler.getEntity(IndicatorEO.class, columnEO.getSiteId());
        for (MobileConfigEO eo : list) {
            eo.setUri(siteEO.getUri().concat(eo.getUri()));
        }*/
        return list;
    }

    @Override
    public List<BaseContentEO> getMobileIdsConfig(Long siteId, Long[] columnId, Integer num) {
        Integer size = columnId.length;
        StringBuffer hql = new StringBuffer();
        Map<String, Object> map = new HashMap<String, Object>();

        hql.append("FROM BaseContentEO t WHERE columnId ").append(size == 1 ? " =:ids " : " in (:ids) ");
        hql.append(" AND isPublish=1 AND recordStatus =:recordStatus AND (typeCode=:typeCode OR typeCode=:typeCode2)");
        //hql.append(" AND (imageLink!='' AND imageLink IS NOT NULL)");
        hql.append(" AND imageLink IS NOT NULL ");

        map.put("typeCode", BaseContentEO.TypeCode.articleNews.toString());
        map.put("typeCode2", BaseContentEO.TypeCode.pictureNews.toString());
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());

        if (size == 1) {
            map.put("ids", columnId[0]);
        } else {
            map.put("ids", columnId);
        }
        //默认取第一个栏目的排序
        /*String order = ModelConfigUtil.getOrderTypeValue(columnId[0], siteId);
        if (!AppUtil.isEmpty(order)) {
            hql.append(" ").append(ModelConfigUtil.getOrderTypeValue(columnId[0], siteId));
        }*/

        hql.append(" ").append(" order by isTop desc,num desc,createDate desc,id desc");

        return contentDao.getEntities(hql.toString(), map, num);
    }

    private Integer chkArr(String[] arr, String targetValue) {
        if (arr == null || arr.length <= 0) {
            return 0;
        }
        for (String s : arr) {
            if (s.equals(targetValue)) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public Object saveConfig(MobileConfigVO vo) {

        String _columnIds = vo.getColumnIds();
        String _checkedIds = vo.getCheckedIds();
        // 转换数组
        String[] columnIds = StringUtils.isEmpty(_columnIds) ? null : _columnIds.split(",");
        String[] checkedIds = StringUtils.isEmpty(_checkedIds) ? null : _checkedIds.split(",");

        //删除所有同类的数据，重新添加。导航除外
        if (vo.getType() != null) {
            mobileConfigDao.deleteAllbyType(vo.getType());
        }

        if (null != columnIds && columnIds.length > 0) {
            // 循环处理
            for (int i = 0; i < columnIds.length; i++) {
                //String[] columnIdSiteId = columnIds[i].split("_");
                //final Long columnId = Long.valueOf(columnIdSiteId[0]);
                //final Long siteId = Long.valueOf(columnIdSiteId[1]);
                Long columnId = Long.valueOf(columnIds[i]);
                MobileConfigEO eo = new MobileConfigEO();
                //从缓存查找栏目信息
                IndicatorEO columnEO = CacheHandler.getEntity(IndicatorEO.class, columnId);
                String siteUrl = CacheHandler.getEntity(IndicatorEO.class, columnEO.getSiteId()).getUri();

                //获取站点ID
                Long siteId = columnEO.getSiteId();
                //查找栏类型
                String columnName = columnEO.getShortName();
                if (AppUtil.isEmpty(columnName)) {
                    columnName = columnEO.getName();
                }
                ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, columnEO.getIndicatorId());
                eo.setSiteId(siteId);
                eo.setIndicatorId(columnId);
                eo.setType(vo.getType());
                eo.setName(columnName);
                eo.setCode(columnMgrEO.getColumnTypeCode());
                eo.setIsChecked(chkArr(checkedIds, String.valueOf(columnId)));
                eo.setNum(1L);
                eo.setUri(siteUrl + "/mobile/column/" + siteId + "/" + columnId);
                mobileConfigDao.saveConfig(eo);
            }

        }

        //默认显示处理
        if (null != checkedIds && checkedIds.length > 0) {
            for (int i = 0; i < checkedIds.length; i++) {
                //String[] columnIdSiteId = checkedIds[i].split("_");
                //final Long columnId = Long.valueOf(columnIdSiteId[0]);
                //final Long siteId = Long.valueOf(columnIdSiteId[1]);

                //从缓存查找栏目信息
                Long columnId = Long.valueOf(checkedIds[i]);
                IndicatorEO columnEO = CacheHandler.getEntity(IndicatorEO.class, columnId);
                //获取站点ID
                Long siteId = columnEO.getSiteId();

                MobileConfigEO eo = new MobileConfigEO();
                //从缓存查找栏目信息
                eo.setIndicatorId(columnId);
                eo.setSiteId(siteId);
                eo.setType(vo.getType());
                eo.setIsChecked(1);
                mobileConfigDao.updateConfigChecked(eo);
            }
        }

        return null;
        //return mobileConfigDao.saveMobileConfig(vo);
    }


    @Override
    public Object savePublicConfig(MobileConfigVO vo) {

        String _columnIds = vo.getColumnIds();
//        String _checkedIds = vo.getCheckedIds();
        // 转换数组
        String[] columnIds = StringUtils.isEmpty(_columnIds) ? null : _columnIds.split(",");
//        String[] checkedIds = StringUtils.isEmpty(_checkedIds) ? null : _checkedIds.split(",");

        //删除所有同类的数据，重新添加。导航除外
        if (vo.getType() != null) {
            mobileConfigDao.deleteAllbyType(vo.getType());
        }

        if (null != columnIds && columnIds.length > 0) {
            // 循环处理
            for (int i = 0; i < columnIds.length; i++) {
                //String[] columnIdSiteId = columnIds[i].split("_");
                //final Long columnId = Long.valueOf(columnIdSiteId[0]);
                //final Long siteId = Long.valueOf(columnIdSiteId[1]);
                Long columnId = Long.valueOf(columnIds[i]);
                MobileConfigEO eo = new MobileConfigEO();
                //从缓存查找栏目信息
//                IndicatorEO columnEO = CacheHandler.getEntity(IndicatorEO.class, columnId);
//                String siteUrl = CacheHandler.getEntity(IndicatorEO.class, columnEO.getSiteId()).getUri();

                //获取站点ID
                Long siteId = LoginPersonUtil.getSiteId();
                //查找栏类型
//                String columnName = columnEO.getShortName();
//                if (AppUtil.isEmpty(columnName)) {
//                    columnName = columnEO.getName();
//                }
//                ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, columnEO.getIndicatorId());
                eo.setSiteId(siteId);
                eo.setIndicatorId(columnId);
                eo.setType(vo.getType());
//                eo.setName(columnName);
//                eo.setCode(columnMgrEO.getColumnTypeCode());
//                eo.setChecked(chkArr(checkedIds, String.valueOf(columnId)));
                eo.setNum(1L);
//                eo.setUri(siteUrl + "/mobile/column/" + siteId + "/" + columnId);
                mobileConfigDao.saveConfig(eo);
            }

        }

        //默认显示处理
//        if (null != checkedIds && checkedIds.length > 0) {
//            for (int i = 0; i < checkedIds.length; i++) {
//                //String[] columnIdSiteId = checkedIds[i].split("_");
//                //final Long columnId = Long.valueOf(columnIdSiteId[0]);
//                //final Long siteId = Long.valueOf(columnIdSiteId[1]);
//
//                //从缓存查找栏目信息
//                Long columnId = Long.valueOf(checkedIds[i]);
//                IndicatorEO columnEO = CacheHandler.getEntity(IndicatorEO.class, columnId);
//                //获取站点ID
//                Long siteId = columnEO.getSiteId();
//
//                MobileConfigEO eo = new MobileConfigEO();
//                //从缓存查找栏目信息
//                eo.setIndicatorId(columnId);
//                eo.setSiteId(siteId);
//                eo.setType(vo.getType());
//                eo.setChecked(true);
//                mobileConfigDao.updateConfigChecked(eo);
//            }
//        }

        return null;
        //return mobileConfigDao.saveMobileConfig(vo);
    }


    @Override
    public Object deleteConfig(Long id) {
        return mobileConfigDao.deleteConfig(id);
    }

    @Override
    public Object deleteAllbyType(String type) {
        return mobileConfigDao.deleteAllbyType(type);
    }

    @Override
    public Object updateConfig(MobileConfigEO eo) {
        return mobileConfigDao.updateConfigChecked(eo);
    }

    @Override
    public MobileConfigEO getOneConfig(Long id) {
        return mobileConfigDao.getOneConfig(id);
    }

}
