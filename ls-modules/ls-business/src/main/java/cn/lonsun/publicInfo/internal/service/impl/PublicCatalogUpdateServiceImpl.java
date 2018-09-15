package cn.lonsun.publicInfo.internal.service.impl;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.CSVUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.publicInfo.internal.dao.IPublicCatalogUpdateDao;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogUpdateEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogUpdateService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.PublicCatalogUpdateQueryVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.util.HibernateHandler;
import cn.lonsun.util.HibernateSessionUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.PublicCatalogUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by fth on 2017/6/9.
 */
@Service
public class PublicCatalogUpdateServiceImpl extends BaseService<PublicCatalogUpdateEO> implements IPublicCatalogUpdateService {
    @Resource
    private TaskExecutor taskExecutor;
    @Resource
    private IPublicCatalogService publicCatalogService;
    @Resource
    private IPublicContentService publicContentService;
    @Resource
    private IPublicCatalogUpdateDao publicCatalogUpdateDao;

    private void processResultList(List<?> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            Map<String, String> cacheMap = new HashMap<String, String>();
            for (Object o : dataList) {
                PublicCatalogUpdateEO update = (PublicCatalogUpdateEO) o;
                Long organId = update.getOrganId();
                Long catId = update.getCatId();
                if (!cacheMap.containsKey("organId_" + organId)) {
                    OrganEO organEO = CacheHandler.getEntity(OrganEO.class, organId);
                    if (null != organEO) {
                        cacheMap.put("organId_" + organId, organEO.getName());
                    }
                }
                update.setOrganName(cacheMap.get("organId_" + organId));
                if (!cacheMap.containsKey("catId_" + catId)) {
                    OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
                    if (null != organConfigEO) {
                        List<String> resultList = new ArrayList<String>();
                        this.getParent(resultList, organId, organConfigEO.getCatId(), catId);
                        Collections.reverse(resultList);// 反转
                        cacheMap.put("catId_" + catId, StringUtils.join(resultList, " > "));
                    }
                }
                update.setCatName(cacheMap.get("catId_" + catId));
                update.setWarningTypeName(Enum.valueOf(PublicCatalogUpdateEO.WarningType.class, update.getWarningType()).getTypeName());
            }
        }
    }

    /**
     * 获取父栏目
     *
     * @param resultList
     * @param organId
     * @param parentId
     * @param catId
     */
    private void getParent(List<String> resultList, Long organId, Long parentId, Long catId) {
        if (null == parentId || catId <= 0L) {
            return;
        }
        PublicCatalogEO catalogEO = CacheHandler.getEntity(PublicCatalogEO.class, catId);
        if (null == catalogEO) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "目录ID不存在！");
        }
        PublicCatalogOrganRelEO relEO = CacheHandler.getEntity(PublicCatalogOrganRelEO.class, CacheGroup.CMS_JOIN_ID, organId, catId);
        resultList.add(null == relEO ? catalogEO.getName() : relEO.getName());
        if (!parentId.equals(catalogEO.getParentId())) {
            this.getParent(resultList, organId, parentId, catalogEO.getParentId());
        }
    }

    @Override
    public Pagination getPagination(PublicCatalogUpdateQueryVO queryVO) {
        Pagination p = publicCatalogUpdateDao.getPagination(queryVO);
        this.processResultList(p.getData());
        return p;
    }

    @Override
    public int getCountByOrganId(PublicCatalogUpdateQueryVO queryVO) {
        return publicCatalogUpdateDao.getCountByOrganId(queryVO);
    }

    @Override
    public List<PublicCatalogEO> getEmptyCatalogByOrganId(Long organId) {
        OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
        if (null == organConfigEO || null == organConfigEO.getCatId()) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), String.format("单位[%s]配置信息不存在", organId));
        }
        List<PublicCatalogEO> childList = publicCatalogService.getAllChildListByCatId(organConfigEO.getCatId());
        // List<PublicCatalogEO> childList = publicCatalogService.getAllLeafListByCatId(organConfigEO.getCatId());//查询所有叶子节点
        if (null == childList || childList.isEmpty()) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), String.format("单位[%s]没有信息公开目录", organId));
        }
        PublicCatalogUtil.filterCatalogList(childList, organId, true);// 过滤目录列表
        /**
         * 去除目录中非叶子节点的目录
         */
        if(null != childList && childList.size() > 0){
            for (Iterator<PublicCatalogEO> it = childList.iterator(); it.hasNext(); ) {
                PublicCatalogEO eo = it.next();
                if(Boolean.TRUE.equals(eo.getIsParent())){
                    it.remove();
                }
            }
        }
        List<Long> catalogIdList = publicContentService.getCatalogIdByOrganId(organId);//查询出该单位下有数据的栏目的列表
        List<PublicCatalogEO> resultList = new ArrayList<PublicCatalogEO>();
        for (Iterator<PublicCatalogEO> it = childList.iterator(); it.hasNext(); ) {
            PublicCatalogEO eo = it.next();
            // 排除转链和调用第三方的目录、父目录
            if (StringUtils.isNotEmpty(eo.getLink()) || StringUtils.isNotEmpty(eo.getRelCatIds()) || eo.getIsParent()) {
                continue;
            }
            PublicCatalogEO newEO = new PublicCatalogEO();
            if (null == catalogIdList || !catalogIdList.contains(eo.getId())) {// 表示在数据库中没有有数据
                BeanUtils.copyProperties(eo, newEO);
                // 设置名称
                List<String> tempList = new ArrayList<String>();
                this.getParent(tempList, organId, organConfigEO.getCatId(), eo.getId());
                Collections.reverse(tempList);// 反转
                newEO.setName(StringUtils.join(tempList, " > "));
                resultList.add(newEO);
            }
        }
        return resultList;
    }

    @Override
    public void export(PublicCatalogUpdateQueryVO queryVO, HttpServletResponse response) {
        List<PublicCatalogUpdateEO> list = publicCatalogUpdateDao.getList(queryVO);
        this.processResultList(list);
        String[] titles = new String[]{"部门名称", "栏目名称", "警示类型", "最后更新日期", "警示消息"};
        List<String[]> rowList = new ArrayList<String[]>();
        if (null != list && !list.isEmpty()) {
            for (PublicCatalogUpdateEO update : list) {
                String[] row = new String[5];
                row[0] = update.getOrganName();
                row[1] = update.getCatName();
                row[2] = update.getWarningTypeName();
                row[3] = DateFormatUtils.format(update.getLastPublishDate(), "yyyy-MM-dd HH:mm:ss");
                row[4] = update.getMessage();
                rowList.add(row);
            }
        }
        // 导出
        String name = "信息公开栏目更新预警";
        try {
            CSVUtils.download(name, titles, rowList, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exportEmptyCatalog(Long organId, HttpServletResponse response) {
        OrganEO organEO = CacheHandler.getEntity(OrganEO.class, organId);
        if (null == organEO) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), String.format("单位[%s]信息不存在", organId));
        }
        Long siteId = LoginPersonUtil.getSiteId();
        SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class,siteId);
        List<PublicCatalogEO> resultList = this.getEmptyCatalogByOrganId(organId);
        String[] titles = new String[]{"栏目名称","栏目URL地址"};
        List<String[]> rowList = new ArrayList<String[]>();
        if (null != resultList && !resultList.isEmpty()) {
            for (PublicCatalogEO eo : resultList) {
                String[] row = new String[2];
                row[0] = eo.getName();
                row[1] = siteMgrEO.getUri()+"/public/column/"+organId+"?type=4&catId="+eo.getId()+"&action=list";
                rowList.add(row);
            }
        }
        // 导出
        String name = "信息公开空栏目[" + organEO.getName() + "]";
        try {
            CSVUtils.download(name, titles, rowList, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void sendMessageByCurrentUser(final Long userId) {
        final Long siteId = LoginPersonUtil.getSiteId();
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                HibernateSessionUtil.execute(new HibernateHandler<String>() {
                    @Override
                    public String execute() throws Throwable {
                        PublicCatalogUpdateQueryVO queryVO = new PublicCatalogUpdateQueryVO();
                        queryVO.setUserId(userId);
                        List<PublicCatalogUpdateEO> list = publicCatalogUpdateDao.getList(queryVO);
                        if (null != list && !list.isEmpty()) {
                            for (PublicCatalogUpdateEO eo : list) {
                                MessageSystemEO message = new MessageSystemEO();
                                message.setSiteId(siteId);
                                message.setColumnId(eo.getOrganId());
                                message.setMessageType(MessageSystemEO.TIP);
                                message.setModeCode(BaseContentEO.TypeCode.public_content.toString());
                                message.setRecUserIds(userId.toString());
                                message.setResourceId(eo.getId());
                                message.setTitle(eo.getMessage());
                                message.setContent(eo.getMessage());
                                message.setMessageStatus(MessageSystemEO.MessageStatus.warning.toString());
                                message.setData(eo);//数据存入消息中
                                MessageSender.sendMessage(message);
                            }
                        }
                        return null;
                    }

                    @Override
                    public String complete(String result, Throwable exception) {
                        if (null != exception) {
                            throw new BaseRunTimeException(TipsMode.Message.toString(), "信息公开更新预警发送消息失败！");
                        }
                        return result;
                    }
                });
            }
        });
    }
}
