package cn.lonsun.content.onlineDeclaration.internal.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.onlineDeclaration.internal.dao.IOnlineDeclarationDao;
import cn.lonsun.content.onlineDeclaration.internal.entity.DeclarationRecordEO;
import cn.lonsun.content.onlineDeclaration.internal.entity.OnlineDeclarationEO;
import cn.lonsun.content.onlineDeclaration.internal.service.IDeclarationRecordService;
import cn.lonsun.content.onlineDeclaration.internal.service.IOnlineDeclarationService;
import cn.lonsun.content.onlineDeclaration.vo.DeclaQueryVO;
import cn.lonsun.content.onlineDeclaration.vo.OnlineDeclarationVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.system.filecenter.internal.service.IFileCenterService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-12<br/>
 */
@Service("onlineDeclarationService")
public class OnlineDeclarationServiceImpl extends MockService<OnlineDeclarationEO> implements IOnlineDeclarationService {

    @Autowired
    private IOnlineDeclarationDao declarationDao;

    @Autowired
    private IBaseContentService contentService;

    @Autowired
    private IDeclarationRecordService recordService;

    @Autowired
    private IFileCenterService fileCenterService;

    @Override
    public Pagination getPage(DeclaQueryVO pageVO) {
        return declarationDao.getPage(pageVO);
    }


    @Override
    public Pagination getFrontPage(DeclaQueryVO pageVO) {
        return declarationDao.getFrontPage(pageVO);
    }

    @Override
    public OnlineDeclarationEO saveVO(OnlineDeclarationVO vo) {
        BaseContentEO contentEO = new BaseContentEO();
        AppUtil.copyPropertiesWithoutEmpty(contentEO, vo);
        OnlineDeclarationEO declarationEO = new OnlineDeclarationEO();
        AppUtil.copyPropertiesWithoutEmpty(declarationEO, vo);
        contentEO.setTypeCode(BaseContentEO.TypeCode.onlineDeclaration.toString());
        Long id = contentService.saveEntity(contentEO);
        CacheHandler.saveOrUpdate(BaseContentEO.class,contentEO);
        String[] mongonIds  = vo.getAttachId().split(",");
        if(null!=mongonIds&&mongonIds.length>0){
            fileCenterService.setStatus(mongonIds,1);
        }
        declarationEO.setBaseContentId(id);
        String dateStr = DateUtil.getYearMonthStr();
        Long count = countList(contentEO.getSiteId(), contentEO.getColumnId() + "", null, null, null) + 1;
        String countStr = "";
        if (count < 10) {
            countStr += "00" + count;
        } else if (10 <= count && count < 100) {
            countStr += "0" + count;
        } else {
            countStr += count;
        }
        String randomCode = RandomCode.shortUrl(id + "");
        declarationEO.setRandomCode(randomCode);
        declarationEO.setDocNum(dateStr + countStr);
        declarationDao.save(declarationEO);
        SysLog.log("添加在线申报 >> ID：" + contentEO.getId(),
                "OnlineDeclarationEO", CmsLogEO.Operation.Add.toString());
        return declarationEO;
    }

    @Override
    public OnlineDeclarationVO getVO(Long id) {
        return declarationDao.getVO(id);
    }

    @Override
    public void deleteVOs(String ids) {
        Long[] idArr = AppUtil.getLongs(ids, ",");
        if (idArr != null && idArr.length > 0) {
            String[] arr=new String[idArr.length];
            int j=0;
            for (int i=0;i<idArr.length;i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("contentId", idArr[i]);
                List<OnlineDeclarationEO> list1 = getEntities(OnlineDeclarationEO.class, map);

                //删除网上信访辅表
                if (list1 != null && list1.size() > 0) {
                    OnlineDeclarationEO eo = list1.get(0);
                    if(!AppUtil.isEmpty(eo.getAttachId())){
                        arr[j++]=eo.getAttachId();
                    }
                }
                BaseContentEO contentEO=CacheHandler.getEntity(BaseContentEO.class,idArr[i]);
                if(contentEO!=null){
                    CacheHandler.delete(BaseContentEO.class,contentEO);
                }
            }
            //删除主表
            contentService.delete(BaseContentEO.class,idArr);
            SysLog.log("删除在线申报 >> ID：" + ids,
                    "BaseContentEO", CmsLogEO.Operation.Delete.toString());
            FileUploadUtil.deleteFileCenterEO(arr);
        }
    }

    @Override
    public void transfer(Long declarationId, Long recUnitId, String recUnitName, String remark) {
        OnlineDeclarationEO declarationEO=getEntity(OnlineDeclarationEO.class,declarationId);
        if(declarationEO==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数出错");

        }
        declarationEO.setRecUnitId(recUnitId);
        declarationEO.setRecUnitName(recUnitName);
        updateEntity(declarationEO);
        SysLog.log("转办在线申报 >> ID：" + declarationEO.getBaseContentId() ,
                "OnlineDeclarationEO", CmsLogEO.Operation.Update.toString());
        DeclarationRecordEO recordEO=new DeclarationRecordEO();
        recordEO.setDeclarationId(declarationId);
        recordEO.setTransUserName(LoginPersonUtil.getOrganName());
        recordEO.setTransToId(recUnitId);
        recordEO.setTransToName(recUnitName);
        recordEO.setRemark(remark);
        recordService.saveEntity(recordEO);
        SysLog.log("添加在线申请转办记录 >> ID：" + declarationEO.getBaseContentId() ,
                "DeclarationRecordEO", CmsLogEO.Operation.Add.toString());
    }

    @Override
    public OnlineDeclarationVO searchByCode(String randomCode, String docNum,Long siteId) {
//        Map<String,Object> map=new HashMap<String,Object>();
//        map.put("randomCode",randomCode);
//        map.put("docNum",docNum);
//        map.put("siteId",siteId);
//        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
//        List<OnlineDeclarationEO> list=getEntities(OnlineDeclarationEO.class,map);
//        OnlineDeclarationVO editVO=null;
//        if(list!=null&&list.size()>0){
//            editVO=new OnlineDeclarationVO();
//            OnlineDeclarationEO eo=list.get(0);
//            AppUtil.copyProperties(editVO,eo);
//            BaseContentEO contentEO=CacheHandler.getEntity(BaseContentEO.class,eo.getBaseContentId());
//            if(contentEO==null){
//                return null;
//            }
//            editVO.setTitle(contentEO.getTitle());
//            editVO.setColumnId(contentEO.getColumnId());
//            editVO.setSiteId(contentEO.getSiteId());
//            editVO.setPublishDate(contentEO.getPublishDate());
//            editVO.setIsPublish(contentEO.getIsPublish());
//        }
        OnlineDeclarationVO vo= declarationDao.searchByCode(randomCode,docNum,siteId);
        if(vo!=null){
            if (vo.getRecUnitId() != null) {
                OrganEO organEO = CacheHandler.getEntity(OrganEO.class, vo.getRecUnitId());
                if (organEO != null) {
                    vo.setRecUnitName(organEO.getName());
                }
            }
        }
        return vo;
    }

    @Override
    public Long countList(Long siteId, String columnIds, Date startDate, Integer isPublish, Integer isResponse) {
        return declarationDao.countList( siteId,  columnIds,  startDate,  isPublish,  isResponse) ;
    }
}
