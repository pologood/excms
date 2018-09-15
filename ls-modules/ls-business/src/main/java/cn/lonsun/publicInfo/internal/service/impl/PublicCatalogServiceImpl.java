/*
 * PublicCatalogServiceImpl.java         2015年12月7日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.publicInfo.internal.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.publicInfo.internal.dao.IPublicCatalogDao;
import cn.lonsun.publicInfo.internal.entity.*;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogCountService;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogOrganRelService;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.publicInfo.vo.OrganCatalogQueryVO;
import cn.lonsun.publicInfo.vo.OrganCatalogVO;
import cn.lonsun.publicInfo.vo.OrganVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.role.internal.service.IInfoOpenRightsService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import cn.lonsun.util.PublicCatalogUtil;
import cn.lonsun.util.SysLog;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * TODO <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年12月7日 <br/>
 */
@Service
public class PublicCatalogServiceImpl extends MockService<PublicCatalogEO> implements IPublicCatalogService {

    @Resource
    private IOrganService organService;
    @DbInject("publicCatalog")
    private IPublicCatalogDao publicCatalogDao;
    @Resource
    private IInfoOpenRightsService infoOpenRightsService;
    @Resource
    private IPublicCatalogCountService publicCatalogCountService;
    @Resource
    private IPublicCatalogOrganRelService publicCatalogOrganRelService;

    /**
     * 保存或者修改目录
     * 单位id为空时 操作公共目录，否则操作私有目录
     * 操作公共目录时，修改直接修改，新增需要判断该父节点是否有单位做了隐藏操作
     * 操作私有目录时，修改直接修改，新增需要判断该父节点isParent属性值
     *
     * @param publicCatalogEO 目录信息
     * @param organId         单位id
     * @return
     */
    public Long saveEntity(PublicCatalogEO publicCatalogEO, Long organId) {
        Long catId = publicCatalogEO.getId();
        boolean isPrivate = null != organId && organId > 0L; // 私有
        if (null == catId) { // 新增操作
            publicCatalogEO.setType(isPrivate ? 2 : 1); // 设置类型
            // publicCatalogEO.setCode(UUID.randomUUID().toString().replace("-", "")); // 设置唯一编码
            catId = publicCatalogDao.save(publicCatalogEO);
            // 保存私有目录
            if (isPrivate) { // 公共目录不操作
                publicCatalogOrganRelService.savePrivateCatalog(organId, publicCatalogEO, Boolean.TRUE, Boolean.FALSE);
            }
            // 查询父对象
            Long parentId = publicCatalogEO.getParentId();
            if (null != parentId && parentId > 0L) { // 顶级目录的父目录不操作的
                PublicCatalogEO parentEO = publicCatalogDao.getEntity(PublicCatalogEO.class, parentId);
                if (null == parentEO) {
                    throw new BaseRunTimeException(TipsMode.Message.toString(), "该目录父节点不存在！");
                }
                if (isPrivate) {// 更新父目录的私有目录
                    publicCatalogOrganRelService.updatePrivateCatalog(organId, parentEO, Boolean.TRUE, Boolean.TRUE);
                } else { // 公共目录操作时，需要判断是否有单位针对该目录做了修改
                    if (!parentEO.getIsParent()) {
                        parentEO.setIsParent(Boolean.TRUE);
                        parentEO.setIsShow(Boolean.TRUE);
                        publicCatalogDao.update(parentEO);
                    }
                    // 只有是父节点的时候才会影响到单位的私有目录配置
                    // 如果单位对该节点做了修改且是影藏的状态，必须添加该子节点与单位的关系，否则节点关系会断开
                    List<PublicCatalogOrganRelEO> parentHideRelList = publicCatalogOrganRelService.getHideRelByCatId(parentId);
                    if (null != parentHideRelList && !parentHideRelList.isEmpty()) {// 有单位做了隐藏配置
                        for (PublicCatalogOrganRelEO relEO : parentHideRelList) { // 当前添加的目录针对所有单位做影藏配置
                            publicCatalogOrganRelService.savePrivateCatalog(relEO.getOrganId(), publicCatalogEO, Boolean.FALSE, Boolean.FALSE);
                            if (!relEO.getIsParent()) {// 如果单位的关系目录是叶子节点还要更新为父节点
                                relEO.setIsParent(Boolean.TRUE);
                                relEO.setAttribute(publicCatalogEO.getAttribute());
                                publicCatalogOrganRelService.updateEntity(relEO);
                            }
                        }
                    }
                }
            }
            SysLog.log("公共目录管理：添加目录（" + publicCatalogEO.getName() + "）", "PublicCatalogEO", CmsLogEO.Operation.Add.toString());
        } else { // 修改操作
            if (isPrivate) {// 如果有部门，说明是私有修改
                publicCatalogOrganRelService.updatePrivateCatalog(organId, publicCatalogEO, null, null);
            } else {
                publicCatalogDao.update(publicCatalogEO);
                SysLog.log("公共目录管理：修改目录（" + publicCatalogEO.getName() + "）", "PublicCatalogEO", CmsLogEO.Operation.Update.toString());
            }
        }
        return catId;
    }

    @Override
    public void delete(Long id, Long organId) {
        PublicCatalogEO publicCatalogEO = publicCatalogDao.getEntity(PublicCatalogEO.class, id);
        if (null == publicCatalogEO) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "目录不存在！");
        }
        boolean isPrivate = publicCatalogEO.getType() == 2; // 目录私有
        boolean operate = null != organId && organId > 0L; // 操作私有，单位也可能删除公共目录
        if (!operate) {// 只要是没有单位属性都认为是公有目录
            throw new BaseRunTimeException(TipsMode.Message.toString(), "公共目录不允许删除！");
        }
        if (isPrivate) {// 删除该单位私有目录
            PublicCatalogOrganRelEO relEO = publicCatalogOrganRelService.getByOrganIdCatId(organId, id);
            if (null == relEO || relEO.getIsParent()) {// 私有目录关系不存在或者有子节点
                throw new BaseRunTimeException(TipsMode.Message.toString(), "该目录有子节点，不允许删除！");
            }
            publicCatalogDao.delete(publicCatalogEO); // 删除私有目录
            publicCatalogOrganRelService.delete(relEO);// 删除关系表目录
        } else {// 单位删除公有目录，相当于隐藏公共栏目
            if (publicCatalogEO.getIsParent()) {//  单位操作不允许操作父目录
                throw new BaseRunTimeException(TipsMode.Message.toString(), "该目录有子节点，不允许删除！");
            }
            publicCatalogOrganRelService.updatePrivateCatalog(organId, publicCatalogEO, Boolean.FALSE, Boolean.FALSE);
        }
        boolean updateParentEO = true;// 是否更新父节点
        Long parentId = publicCatalogEO.getParentId();
        List<PublicCatalogEO> childrenList = publicCatalogDao.getChildListByCatId(parentId);// 该父目录下的子节点，包含公用私有
        if (null == childrenList || childrenList.isEmpty()) {
            return; // 父目录下不存在子节点
        }
        List<Long> catIdList = publicCatalogOrganRelService.getListByOrganId(organId, false);// 私有目录id列表，显示的
        // 循环判断父目录所有子节点，当自节点为公用且显示或者私有目录中包含，说明无需修改父节点属性
        for (PublicCatalogEO children : childrenList) {// 当子节点
            Long childId = children.getId();
            if (childId.equals(id)) {
                continue; // 本节点不算
            }
            if ((children.getType() == 1 && children.getIsShow()) || (null != catIdList && catIdList.contains(childId))) {
                updateParentEO = false; // 公有显示目录或者私有本单位目录
                break;
            }
        }
        if (updateParentEO) { // 不管删除公有目录还是私有目录都相当于隐藏操作
            PublicCatalogEO parentEO = publicCatalogDao.getEntity(PublicCatalogEO.class, parentId);
            publicCatalogOrganRelService.updatePrivateCatalog(organId, parentEO, Boolean.TRUE, Boolean.FALSE);
        }
    }

    @Override
    public List<OrganCatalogVO> getOrganCatalogTree(OrganCatalogQueryVO queryVO) {
        List<OrganCatalogVO> voList = getWebOrganCatalogTree(queryVO);
        return !queryVO.getAll() ? infoOpenRightsService.getInfoOpenRights(voList) : voList;// 过滤权限
    }

    @Override
    public List<PublicCatalogEO> getAllChildListByCatId(Long catId) {
        return publicCatalogDao.getAllChildListByCatId(catId);
    }

    @Override
    public List<PublicCatalogEO> getAllLeafListByCatId(Long catId) {
        return publicCatalogDao.getAllLeafListByCatId(catId);
    }

    @Override
    public void saveImportCatalogList(Map<String, List<PublicCatalogEO>> mapList, int startRow) {
        if (null == mapList || mapList.isEmpty()) {
            return;
        }
        publicCatalogDao.deleteAll();// 删除所有
        publicCatalogOrganRelService.deleteAll();// 删除所有
        int mapSortNum = 1;
        for (Map.Entry<String, List<PublicCatalogEO>> entry : mapList.entrySet()) {
            PublicCatalogEO topEO = new PublicCatalogEO();
            topEO.setName(entry.getKey());
            topEO.setCode(UUID.randomUUID().toString());
            topEO.setIsParent(Boolean.TRUE);
            topEO.setSortNum(mapSortNum++ * 10);//排序
            topEO.setParentId(0L);//顶级栏目的父栏目ID为0
            topEO.setType(1);//公共目录
            Long id = publicCatalogDao.save(topEO);
            //循环处理list
            int listIndex = 1, listSortNum = 1;
            List<PublicCatalogEO> list = entry.getValue();
            Map<Integer, PublicCatalogEO> cacheMap = new HashMap<Integer, PublicCatalogEO>();
            for (PublicCatalogEO eo : list) {
                eo.setType(1);
                if (eo.getRowIndex() == 0) {
                    eo.setParentId(id);
                }
                eo.setSortNum(listSortNum++ * 10);
                eo.setCode(UUID.randomUUID().toString());
                cacheMap.put(listIndex++, eo);
            }
            publicCatalogDao.save(list);// 入库
            for (PublicCatalogEO eo : list) {
                int rowIndex = eo.getRowIndex();
                if (rowIndex != 0 && cacheMap.containsKey(rowIndex - startRow)) {//有父级的目录
                    PublicCatalogEO parent = cacheMap.get(rowIndex - startRow);
                    eo.setParentId(parent.getId());//设置当前目录的父id
                    parent.setIsParent(Boolean.TRUE);
                }
            }
            publicCatalogDao.update(list);// 分析父子关系
        }
    }

    @Override
    public List<OrganCatalogVO> getWebOrganCatalogTree(OrganCatalogQueryVO queryVO) {
        Long organId = queryVO.getOrganId();// 部门id
        List<OrganCatalogVO> voList = new ArrayList<OrganCatalogVO>();
        if (null == organId) {// 当部门为空时，表示查询部门列表
            SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, queryVO.getSiteId());
            List<OrganEO> organList = organService.getPublicOrgans(Long.valueOf(siteConfigEO.getUnitIds()));
            if (null == organList || organList.isEmpty()) {
                return voList;
            }
            List<OrganVO> organVOList = new ArrayList<OrganVO>();
            PublicCatalogUtil.filterOrgan(organList, organVOList);// 过滤单位列表
            PublicCatalogUtil.sortOrgan(organVOList);// 排序
            // 生成数据
            for (OrganVO organVO : organVOList) {
                Long id = organVO.getOrganId();
                OrganCatalogVO vo = new OrganCatalogVO();
                vo.setId(id);
                vo.setName(organVO.getName());
                vo.setCode(organVO.getCode());
                vo.setType(organVO.getType());
                vo.setOrganId(id);
                vo.setIsParent(true);
                vo.setSiteId(queryVO.getSiteId());

                // 当查询全部单位时不需要配置信息
                if (!queryVO.getCatalog()) {
                    voList.add(vo);
                } else if (null != organVO.getData()) {
                    OrganConfigEO organConfigEO = (OrganConfigEO) organVO.getData();
                    ContentModelEO contentModeEO = CacheHandler.getEntity(ContentModelEO.class, CacheGroup.CMS_CODE, organConfigEO.getContentModelCode());
                    if (null != contentModeEO) {
                        ModelTemplateEO modelEO = ModelConfigUtil.getTemplateByModelId(contentModeEO.getId());
                        // 栏目code借用模型code显示
                        if (null != modelEO) {
                            contentModeEO.setCode(modelEO.getModelTypeCode());
                        }
                        contentModeEO.setConfig(JSON.parse(contentModeEO.getContent()));
                        contentModeEO.setContent(null);
                        vo.setData(contentModeEO);// 设置内容模型参数
                    }
                    voList.add(vo);
                }
            }
        } else {// 查询其他，包括菜单、目录
            DataDictEO dictEO = CacheHandler.getEntity(DataDictEO.class, CacheGroup.CMS_CODE, PublicContentEO.PUBLIC_ITEM_CODE);
            List<DataDictItemEO> itemList = CacheHandler.getList(DataDictItemEO.class, CacheGroup.CMS_PARENTID, dictEO.getDictId());
            if (null == itemList || itemList.isEmpty()) {
                return voList;
            }
            // 查询出所有目录信息
            OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
            if (null == organConfigEO) {
                return voList;
            }
            Long catId = organConfigEO.getCatId();
            Long publicId = null;// 主动公开id
            for (DataDictItemEO item : itemList) {
                OrganCatalogVO vo = new OrganCatalogVO();

                vo.setId(item.getItemId());
                vo.setName(item.getName());
                vo.setParentId(organId);
                vo.setOrganId(organId);
                vo.setCode(item.getCode());
                vo.setType(item.getValue());
                vo.setSiteId(queryVO.getSiteId());
                if (PublicContentEO.Type.DRIVING_PUBLIC.toString().equals(item.getCode())) {// 主动公开需要查询目录
                    publicId = item.getItemId();
                    if (null != catId) {
                        vo.setIsParent(true);
                    }
                } // 公开制度并且需要按照等级却分
                else if (organConfigEO.getIsInstitutionLevel() && PublicContentEO.Type.PUBLIC_INSTITUTION.toString().equals(item.getCode())) {
                    DataDictEO dict = CacheHandler.getEntity(DataDictEO.class, CacheGroup.CMS_CODE, PublicContentEO.PUBLIC_ITEM_INSTITUTION_CODE);
                    if (null == dict) {
                        return null;
                    }
                    List<DataDictItemEO> dictList = CacheHandler.getList(DataDictItemEO.class, CacheGroup.CMS_PARENTID, dict.getDictId());
                    if (null == dictList || dictList.isEmpty()) {
                        continue;
                    }
                    for (DataDictItemEO i : dictList) {
                        OrganCatalogVO v = new OrganCatalogVO();
                        v.setId(i.getItemId());
                        v.setName(i.getName());
                        v.setParentId(item.getItemId());
                        v.setOrganId(organId);
                        v.setCode(i.getCode());
                        v.setType(item.getValue());
                        v.setSiteId(queryVO.getSiteId());
                        voList.add(v);
                    }
                } else if (organConfigEO.getIsGuideLevel() && PublicContentEO.Type.PUBLIC_GUIDE.toString().equals(item.getCode())) {// 信息公开制度分类
                    DataDictEO dict = CacheHandler.getEntity(DataDictEO.class, CacheGroup.CMS_CODE, PublicContentEO.PUBLIC_ITEM_GUIDE_CODE);
                    if (null == dict) {
                        return null;
                    }
                    List<DataDictItemEO> dictList = CacheHandler.getList(DataDictItemEO.class, CacheGroup.CMS_PARENTID, dict.getDictId());
                    if (null == dictList || dictList.isEmpty()) {
                        continue;
                    }
                    for (DataDictItemEO ii : dictList) {
                        OrganCatalogVO vv = new OrganCatalogVO();
                        vv.setId(ii.getItemId());
                        vv.setName(ii.getName());
                        vv.setParentId(item.getItemId());
                        vv.setOrganId(organId);
                        vv.setType(item.getValue());
                        vv.setCode(ii.getCode());
                        vv.setSiteId(queryVO.getSiteId());
                        voList.add(vv);
                    }
                }
                voList.add(vo);
            }
            // 查询主动公开目录
            if (null != publicId) {
                // 查询出单位配置的私有目录和隐藏目录
                List<PublicCatalogEO> childList = publicCatalogDao.getAllChildListByCatId(catId);
                PublicCatalogUtil.filterCatalogList(childList, organId, true);// 过滤目录列表
                PublicCatalogUtil.sortCatalog(childList);
                // 过滤出显示的
                if (null != childList && !childList.isEmpty()) {
                    // 查询出该单位的条数
                    List<PublicCatalogCountEO> catalogCountEOList = publicCatalogCountService.getListByOrganId(organId);
                    Map<Long, PublicCatalogCountEO> catMap = new HashMap<Long, PublicCatalogCountEO>();
                    if (null != catalogCountEOList && !catalogCountEOList.isEmpty()) {
                        for (PublicCatalogCountEO countEO : catalogCountEOList) {
                            catMap.put(countEO.getCatId(), countEO);
                        }
                    }
                    Map<Long, Long> childNum = PublicCatalogUtil.getChildNumMap(childList);// 获取每个目录的子节点数量
                    for (PublicCatalogEO eo : childList) {
                        OrganCatalogVO vo = new OrganCatalogVO();
                        vo.setId(eo.getId());
                        vo.setName(eo.getName());
                        vo.setParentId(catId.equals(eo.getParentId()) ? publicId : eo.getParentId());
                        vo.setCode(eo.getCode());
                        vo.setOrganId(organId);
                        vo.setIsParent(eo.getIsParent());
                        if(vo.getIsParent() && childNum.get(eo.getId()) == null){
                            vo.setIsParent(false);
                        }
                        vo.setSiteId(queryVO.getSiteId());
                        vo.setType(PublicContentEO.Type.DRIVING_PUBLIC.toString());
                        vo.setAttribute(eo.getAttribute());
                        if (catMap.containsKey(eo.getId())) {// 设置目录条数
                            vo.setPublishCount(catMap.get(eo.getId()).getPublishCount());
                            vo.setUnPublishCount(catMap.get(eo.getId()).getUnPublishCount());
                        }
                        voList.add(vo);
                    }
                }
            }
        }
        return voList;
    }

    /**
     * 这里的join 失效掉，所有子节点都要进行相同操作
     *
     * @param eo   目录信息
     * @param join 是否级联操作（废弃，为true）
     * @return
     */
    @Override
    public List<Long> updateShowOrHideCatalog(PublicCatalogOrganRelEO eo, boolean join) {
        Long catId = eo.getCatId();
        Long organId = eo.getOrganId();
        boolean isPrivate = null != organId && organId > 0L; // 私有
        List<Long> changeList = new ArrayList<Long>();// 返回的节点列表
        PublicCatalogEO self = publicCatalogDao.getEntity(PublicCatalogEO.class, catId);
        if (null == self) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "目录不存在！");
        }
        List<PublicCatalogEO> catalogList = new ArrayList<PublicCatalogEO>();// 更新的目录列表
        catalogList.add(self);// 本节点
        List<PublicCatalogEO> childList = publicCatalogDao.getAllChildListByCatId(catId);
        if (null != childList && !childList.isEmpty()) {
            catalogList.addAll(childList);
        }
        List<Long> organPrivateList = null;
        if (isPrivate) {// 查询本单位私有目录，影藏的也要查出来，防止影藏的无法更改为显示的
            organPrivateList = publicCatalogOrganRelService.getListByOrganId(organId, true);
        }
        /**
         * create by caoht at 2018/5/3 子目录显示时父目录也要同步显示
         */
        if (!eo.getIsParent() && eo.getIsShow()) {
            Long topCatId = 0L;
            Long parentId = self.getParentId();
            while (parentId.longValue() != topCatId.longValue()) {
                PublicCatalogEO parentEO = publicCatalogDao.getEntity(PublicCatalogEO.class, parentId);
                parentId = parentEO.getParentId();
                if (parentEO.getParentId().longValue() != topCatId.longValue()) {
                    parentEO.setIsShow(true);
                    catalogList.add(parentEO);
                }
            }
        }
        // 循环目录列表
        for (PublicCatalogEO e : catalogList) {
            if (e.getType() == 1) {// 公共目录全部都要影藏
                if (isPrivate) { // 针对单位影藏
                    if (publicCatalogOrganRelService.updatePrivateCatalog(organId, e, eo.getIsShow(), e.getIsParent())) {
                        changeList.add(e.getId());
                    }
                } else if (e.getIsShow() != eo.getIsShow()) {
                    changeList.add(e.getId());
                    e.setIsShow(eo.getIsShow());
                    String opt = "隐藏";
                    if (eo.getIsShow()) {
                        opt = "显示";
                    }
                    publicCatalogDao.update(e);
                    SysLog.log("公共目录管理：" + opt + "目录（" + e.getName() + "）", "PublicCatalogEO", CmsLogEO.Operation.Update.toString());

                } else if (!eo.getIsParent() && eo.getIsShow()) {
                    changeList.add(e.getId());
                    e.setIsShow(eo.getIsShow());
                    String opt = "隐藏";
                    if (eo.getIsShow()) {
                        opt = "显示";
                    }
                    publicCatalogDao.update(e);
                    SysLog.log("公共目录管理：" + opt + "目录（" + e.getName() + "）", "PublicCatalogEO", CmsLogEO.Operation.Update.toString());
                }
            } else if (null != organPrivateList && organPrivateList.contains(e.getId())) {// 只操作本单位私有目录
                if (publicCatalogOrganRelService.updatePrivateCatalog(organId, e, eo.getIsShow(), e.getIsParent())) {
                    changeList.add(e.getId());
                }
            }
        }
        return changeList;
    }

    @Override
    public List<Object> getAllCatIdHaveContent(Long organId, Long parentId) {
        return publicCatalogDao.getAllCatIdHaveContent(organId, parentId);
    }

    @Override
    public List<PublicCatalogEO> getOrganCatalog(Long organId) {
        // 查询出所有目录信息
        OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
        Long catId = organConfigEO.getCatId();
        // 查询主动公开目录
        // 查询出单位配置的私有目录和隐藏目录
        List<PublicCatalogEO> childList = publicCatalogDao.getAllChildListByCatId(catId);
        PublicCatalogUtil.filterCatalogList(childList, organId, false);// 过滤目录列表
//        PublicCatalogUtil.sortCatalog(childList);
        return childList;
    }
}