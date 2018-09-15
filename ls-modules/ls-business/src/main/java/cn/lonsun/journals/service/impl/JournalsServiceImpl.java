package cn.lonsun.journals.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.journals.dao.IJournalsDao;
import cn.lonsun.journals.entity.JournalsEO;
import cn.lonsun.journals.service.IJournalsService;
import cn.lonsun.journals.vo.JournalsQueryVO;
import cn.lonsun.journals.vo.JournalsSearchVO;
import cn.lonsun.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lonsun on 2017-1-3.
 */
@Service("journalsService")
public class JournalsServiceImpl extends MockService<JournalsEO> implements IJournalsService {
    @Autowired
    private IBaseContentService baseContentService;
    @Autowired
    private IJournalsDao journalsDao;

    @Override
    public Pagination getJournalsList(JournalsQueryVO journalsQueryVO) {
        return journalsDao.getJournalsList(journalsQueryVO);
    }

    @Override
    public JournalsEO saveJournals(JournalsEO journalsEO,Long columnId) {
        Calendar calendar =   Calendar.getInstance();
        calendar.setTime(new Date());
        journalsEO.setYear(calendar.get(Calendar.YEAR));
        journalsEO.setMonth(calendar.get(Calendar.MONTH));
        Long baseContentId=journalsEO.getBaseContentId();
        BaseContentEO baseContentEO =new BaseContentEO();
        if(AppUtil.isEmpty(journalsEO.getMateriaId())){
            List<JournalsEO> journalsEOs = journalsDao.checkMateria(journalsEO);
            if(null!=journalsEOs&&journalsEOs.size()>0){

                throw new BaseRunTimeException(TipsMode.Message.toString(),"该年度期刊号重复!");
            }

            baseContentEO.setSiteId(journalsEO.getSiteId());
            baseContentEO.setIsPublish(journalsEO.getIsPublish());
            baseContentEO.setTitle(journalsEO.getMateriaName());
//            baseContentEO.setTypeCode(BaseContentEO.TypeCode.securityMateria.toString());
            baseContentEO.setAuthor(journalsEO.getAuthor());
            baseContentEO.setImageLink(journalsEO.getImageLink());
            baseContentEO.setColumnId(columnId);
            if(journalsEO.getIsPublish()==1){
                baseContentEO.setPublishDate(new Date());
            }
            baseContentId = baseContentService.saveEntity(baseContentEO);
            journalsEO.setBaseContentId(baseContentId);
            saveEntity(journalsEO);
            if(!AppUtil.isEmpty(journalsEO.getFilePath())){
                FileUploadUtil.setStatus(journalsEO.getFilePath(), 1);
            }
            baseContentEO.setTypeCode(BaseContentEO.TypeCode.journal.toString());
        }
        else{
            baseContentEO=baseContentService.getEntity(BaseContentEO.class,journalsEO.getBaseContentId());
            baseContentEO.setIsPublish(journalsEO.getIsPublish());
            baseContentEO.setTitle(journalsEO.getMateriaName());
            baseContentEO.setAuthor(journalsEO.getAuthor());
            baseContentEO.setImageLink(journalsEO.getImageLink());
            if(journalsEO.getIsPublish()==1){
                baseContentEO.setPublishDate(new Date());
            }

            baseContentService.updateEntity(baseContentEO);
            updateEntity(journalsEO);
        }
        CacheHandler.saveOrUpdate(BaseContentEO.class,baseContentEO);
        return journalsEO;
    }

    @Override
    public List<JournalsSearchVO> getAllSearchVO() {
        return journalsDao.getAllSearchVO();
    }
}
