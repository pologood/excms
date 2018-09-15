package cn.lonsun.security.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.security.internal.dao.IMateriaDao;
import cn.lonsun.security.internal.entity.SecurityMateria;
import cn.lonsun.security.internal.service.IMateriaService;
import cn.lonsun.security.internal.vo.MateriaQueryVO;
import cn.lonsun.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lonsun on 2016-12-12.
 */
@Service
public class MateriaServiceImpl extends MockService<SecurityMateria> implements IMateriaService {
   @Autowired
   private IMateriaDao materiaDao;
   @Autowired
   private IBaseContentService baseContentService;

    @Override
    public Pagination getmMateriaList(MateriaQueryVO materiaQueryVO) {
        return materiaDao.getgetmMateriaList(materiaQueryVO);
    }

    @Override
    public SecurityMateria saveMateria(SecurityMateria securityMateria) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        securityMateria.setYear(securityMateria.getYear());
        Integer month=calendar.get(Calendar.MONTH)+1;
        if(month==13){
            month=1;
        }
        securityMateria.setMonth(month);
        Long baseContentId=securityMateria.getBaseContentId();
        BaseContentEO baseContentEO =new BaseContentEO();
        if(AppUtil.isEmpty(securityMateria.getMateriaId())){
           List<SecurityMateria> securityMaterias = materiaDao.checkMateria(securityMateria);
           if(null!=securityMaterias&&securityMaterias.size()>0){

               throw new BaseRunTimeException(TipsMode.Message.toString(),"该年度期刊号重复!");
           }

            baseContentEO.setSiteId(securityMateria.getSiteId());
            baseContentEO.setIsPublish(securityMateria.getIsPublish());
            baseContentEO.setTitle(securityMateria.getMateriaName());
            baseContentEO.setTypeCode(BaseContentEO.TypeCode.securityMateria.toString());
            baseContentEO.setAuthor(securityMateria.getAuthor());
            baseContentEO.setImageLink(securityMateria.getImageLink());

            if(securityMateria.getIsPublish()==1){
                baseContentEO.setPublishDate(new Date());
            }
            baseContentId = baseContentService.saveEntity(baseContentEO);
            securityMateria.setBaseContentId(baseContentId);
            saveEntity(securityMateria);
            if(!AppUtil.isEmpty(securityMateria.getFilePath())){
                FileUploadUtil.setStatus(securityMateria.getFilePath(),1);
            }

        }
        else{
            List<SecurityMateria> securityMaterias = materiaDao.checkMateria(securityMateria);
            if(null!=securityMaterias&&securityMaterias.size()>0){

                throw new BaseRunTimeException(TipsMode.Message.toString(),"该年度期刊号重复!");
            }
            baseContentEO=baseContentService.getEntity(BaseContentEO.class,securityMateria.getBaseContentId());
            baseContentEO.setIsPublish(securityMateria.getIsPublish());
            baseContentEO.setTitle(securityMateria.getMateriaName());
            baseContentEO.setAuthor(securityMateria.getAuthor());
            baseContentEO.setImageLink(securityMateria.getImageLink());
            if(securityMateria.getIsPublish()==1){
                baseContentEO.setPublishDate(new Date());
            }

            baseContentService.updateEntity(baseContentEO);
            updateEntity(securityMateria);
        }
        return securityMateria;
    }
}
