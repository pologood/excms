package cn.lonsun.msg.submit.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.vo.ContentChartQueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.msg.submit.dao.IMsgSubmitDao;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitClassifyEO;
import cn.lonsun.msg.submit.entity.CmsMsgSubmitEO;
import cn.lonsun.msg.submit.entity.CmsMsgToColumnEO;
import cn.lonsun.msg.submit.service.IMsgSubmitService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.statistics.ContentChartVO;
import cn.lonsun.statistics.StatisticsQueryVO;
import cn.lonsun.statistics.SubmitListVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gu.fei
 * @version 2015-11-18 13:47
 */
@Service
public class MsgSubmitService extends BaseService<CmsMsgSubmitEO> implements IMsgSubmitService {

    @DbInject("msgSubmit")
    private IMsgSubmitDao msgSubmitDao;

    @Autowired
    private IOrganService organService;

    @Autowired
    private IPersonService personService;

    @Override
    public List<CmsMsgSubmitEO> getEOs() {
        return msgSubmitDao.getEOs();
    }

    @Override
    public Pagination getPageEOs(ParamDto dto) {
        Pagination page = msgSubmitDao.getPageEOs(dto);
        List<CmsMsgSubmitEO> list = (List<CmsMsgSubmitEO>) page.getData();
        for(CmsMsgSubmitEO eo:list) {
            CmsMsgSubmitClassifyEO cEO = CacheHandler.getEntity(CmsMsgSubmitClassifyEO.class, eo.getClassifyId());
            if(null != cEO) {
                eo.setClassifyName(cEO.getName());
                eo.setColumnId(cEO.getColumnId());
                if(null != cEO.getColumnId()) {
                    IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class,cEO.getColumnId());
                    if(null != indicatorEO) {
                        eo.setColumnName(indicatorEO.getName());
                    }
                }
            }

            DataDictVO dictVO = DataDictionaryUtil.getItem("sourceMgr", eo.getFromCode());
            if(null != dictVO) {
                System.out.println(dictVO.getKey());
                eo.setFromName(dictVO.getKey());
            }
        }
        return page;
    }

    @Override
    public List<CmsMsgSubmitEO> getEOs(ParamDto dto) {
        return msgSubmitDao.getEOs(dto);
    }

    @Override
    public Long getCountByClassifyId(Long classifyId) {
        return msgSubmitDao.getCountByClassifyId(classifyId);
    }

    @Override
    public void saveEO(CmsMsgSubmitEO eo) {
        this.saveEntity(eo);
    }

    @Override
    public void updateEO(CmsMsgSubmitEO eo) {
        CmsMsgSubmitEO eos = this.getEntity(CmsMsgSubmitEO.class,eo.getId());
        eos.setName(eo.getName());
        eos.setClassifyId(eo.getClassifyId());
        eos.setProvider(eo.getProvider());
        eos.setAuthor(eo.getAuthor());
        eos.setFromCode(eo.getFromCode());
        eos.setImageLink(eo.getImageLink());
        eos.setStatus(eo.getStatus());
        eos.setBackReason(eo.getBackReason());
        eos.setPublishDate(eo.getPublishDate());
        eos.setContent(eo.getContent());
        eos.setSiteId(eo.getSiteId());
        eos.setCreateUnitId(LoginPersonUtil.getUnitId());
        this.updateEntity(eos);
    }

    @Override
    public void deleteEO(Long[] ids) {
        for(Long id : ids) {
            this.delete(CmsMsgSubmitEO.class, id);
        }
}

    @Override
    public Long getCountChart(ContentChartQueryVO queryVO) {
        return msgSubmitDao.getCountChart(queryVO);
    }

    @Override
    public List<ContentChartVO> getContentChart(ContentChartQueryVO queryVO) {
        List<ContentChartVO> list=null;
        if(queryVO.getOrderType()==1||queryVO.getOrderType()==2){
            list = getContentChartType1(queryVO);
        }else if(queryVO.getOrderType()==3){
            list = getContentChartType2(queryVO);
        }

        return list;
    }

    private List<ContentChartVO> getContentChartType1(ContentChartQueryVO queryVO){
        List<ContentChartVO> list=null;
        if(queryVO.getOrderType()==1){
            list = msgSubmitDao.getContentChart(queryVO);
        }else if(queryVO.getOrderType()==2){
            list = msgSubmitDao.getContentChart1(queryVO);
        }
        if (list != null && list.size() >= 10) {
            return list;
        } else {
            SiteMgrEO siteEO=CacheHandler.getEntity(SiteMgrEO.class,queryVO.getSiteId());
            List<OrganEO> newList=new ArrayList<OrganEO>();
            if(siteEO!=null&&!StringUtils.isEmpty(siteEO.getUnitIds())){
                Long[] arr=AppUtil.getLongs(siteEO.getUnitIds(),",");
                if(arr!=null&&arr.length>0){
                    if(queryVO.getOrderType()==1){
                        newList=organService.getOrgansByDn(arr[0],OrganEO.Type.Organ.toString());//单位
                    }else if(queryVO.getOrderType()==2){
                        newList=organService.getOrgansByDn(arr[0],OrganEO.Type.OrganUnit.toString());//部门
                    }

                }
            }else{
                throw new BaseRunTimeException(TipsMode.Message.toString(), "此站点下没有绑定单位");
            }
            if (newList == null || newList.size() == 0) {
                return list;
            }
            if (list == null) {
                list = new ArrayList<ContentChartVO>();
            }
            List<ContentChartVO> list_1 = new ArrayList<ContentChartVO>();
            list_1.addAll(list);
            if (list.size() == 0) {
                for (OrganEO eo : newList) {
                    if (list.size() < 10) {
                        ContentChartVO chartVO = new ContentChartVO();
                        chartVO.setOrganId(eo.getOrganId());
                        chartVO.setOrganName(eo.getName());
                        list.add(chartVO);
                    } else {
                        break;
                    }
                }
            } else {
                boolean flag=true;
                for (OrganEO eo : newList) {
                    flag=true;
                    if (list.size() >= 10) {
                        break;
                    }
                    for (ContentChartVO vo : list_1) {
                        if (eo.getOrganId().equals(vo.getOrganId())) {
                            flag=false;
                            break;
                        }
                    }
                    if(flag){
                        ContentChartVO chartVO = new ContentChartVO();
                        chartVO.setOrganId(eo.getOrganId());
                        chartVO.setOrganName(eo.getName());
                        list.add(chartVO);
                    }
                }
            }
        }
        if (list != null && list.size() > 1) {
            for(int i=1;i<list.size();i+=2){
                list.get(i).setOrganName("\n"+list.get(i).getOrganName());
            }
        }
        return list;
    }

    private List<ContentChartVO> getContentChartType2(ContentChartQueryVO queryVO) {
        List<ContentChartVO> list=msgSubmitDao.getContentChart2(queryVO);
        if (list != null && list.size() >= 10) {
            return list;
        } else {
            SiteMgrEO siteEO=CacheHandler.getEntity(SiteMgrEO.class,queryVO.getSiteId());
            List<PersonEO> newList=new ArrayList<PersonEO>();
            if(siteEO!=null&&!StringUtils.isEmpty(siteEO.getUnitIds())){
                Long[] arr=AppUtil.getLongs(siteEO.getUnitIds(),",");
                if(arr!=null&&arr.length>0){
                    newList=personService.getPersonsByDn(arr[0]);//获取单位下所有的人员
                }
            }else{
                throw new BaseRunTimeException(TipsMode.Message.toString(), "此站点下没有绑定单位");
            }
            if (newList == null || newList.size() == 0) {
                return list;
            }
            if (list == null) {
                list = new ArrayList<ContentChartVO>();
            }
            List<ContentChartVO> list_1 = new ArrayList<ContentChartVO>();
            for(ContentChartVO vo:list){
                if(vo.getOrganId()!=null){
                   // CacheHandler.getEntity(PersonEO.class,)
                }
            }
            list_1.addAll(list);
            if (list.size() == 0) {
                for (PersonEO eo : newList) {
                    if (list.size() < 10) {
                        ContentChartVO chartVO = new ContentChartVO();
                        chartVO.setOrganId(eo.getUserId());
                        chartVO.setOrganName(eo.getName());
                        list.add(chartVO);
                    } else {
                        break;
                    }
                }
            } else {
                boolean flag=true;
                for (PersonEO eo : newList) {
                    flag=true;
                    if (list.size() >= 10) {
                        break;
                    }
                    for (ContentChartVO vo : list_1) {
                        if (eo.getUserId().equals(vo.getOrganId())) {
                            flag=false;
                            break;
                        }
                    }
                    if(flag){
                        ContentChartVO chartVO = new ContentChartVO();
                        chartVO.setOrganId(eo.getUserId());
                        chartVO.setOrganName(eo.getName());
                        list.add(chartVO);
                    }
                }
            }
        }
        if (list != null && list.size() > 1) {
            for(int i=1;i<list.size();i+=2){
                list.get(i).setOrganName("\n"+list.get(i).getOrganName());
            }
        }
        return list;
    }

    @Override
    public List<ContentChartVO> getEmpContentChart(ContentChartQueryVO queryVO) {

        List<ContentChartVO> list =null;
        if(queryVO.getOrderType()==1||queryVO.getOrderType()==2){
            list=getEmpContentChartType1(queryVO);
        }else if(queryVO.getOrderType()==3){
            list=getEmpContentChartType2(queryVO);
        }
        return list;
    }

    private List<ContentChartVO> getEmpContentChartType1(ContentChartQueryVO queryVO) {
        List<ContentChartVO> list =null;
        if(queryVO.getOrderType()==1){
            list=msgSubmitDao.getEmpContentChart(queryVO);
        }else if(queryVO.getOrderType()==2){
            list=msgSubmitDao.getEmpContentChart1(queryVO);
        }
        if (list != null && list.size() >= 10) {
            return list;
        } else {
            SiteMgrEO siteEO=CacheHandler.getEntity(SiteMgrEO.class,queryVO.getSiteId());
            List<OrganEO> newList=new ArrayList<OrganEO>();
            if(siteEO!=null&&!StringUtils.isEmpty(siteEO.getUnitIds())){
                Long[] arr=AppUtil.getLongs(siteEO.getUnitIds(),",");
                if(arr!=null&&arr.length>0){
                    if(queryVO.getOrderType()==1){
                        newList=organService.getOrgansByDn(arr[0],OrganEO.Type.Organ.toString());//单位
                    }else if(queryVO.getOrderType()==2){
                        newList=organService.getOrgansByDn(arr[0],OrganEO.Type.OrganUnit.toString());//部门
                    }
                }
            }else{
                throw new BaseRunTimeException(TipsMode.Message.toString(), "此站点下没有绑定单位");
            }
            if (newList == null || newList.size() == 0) {
                return list;
            }
            if (list == null) {
                list = new ArrayList<ContentChartVO>();
            }
            List<ContentChartVO> list_1 = new ArrayList<ContentChartVO>();
            list_1.addAll(list);
            if (list.size() == 0) {
                for (OrganEO eo : newList) {
                    if (list.size() < 10) {
                        ContentChartVO chartVO = new ContentChartVO();
                        chartVO.setOrganId(eo.getOrganId());
                        chartVO.setOrganName(eo.getName());
                        list.add(chartVO);
                    } else {
                        break;
                    }
                }
            } else {
                boolean flag=true;
                for (OrganEO eo : newList) {
                    flag=true;
                    if (list.size() >= 10) {
                        break;
                    }
                    for (ContentChartVO vo : list_1) {
                        if (eo.getOrganId().equals(vo.getOrganId())) {
                            flag=false;
                            break;
                        }
                    }
                    if(flag){
                        ContentChartVO chartVO = new ContentChartVO();
                        chartVO.setOrganId(eo.getOrganId());
                        chartVO.setOrganName(eo.getName());
                        list.add(chartVO);
                    }
                }
            }
        }
        if (list != null && list.size() > 1) {
            for(int i=1;i<list.size();i+=2){
                list.get(i).setOrganName("\n"+list.get(i).getOrganName());
            }
        }
        return list;

    }
    private List<ContentChartVO> getEmpContentChartType2(ContentChartQueryVO queryVO) {
        List<ContentChartVO> list =msgSubmitDao.getEmpContentChart2(queryVO);
        if (list != null && list.size() >= 10) {
            return list;
        } else {
            SiteMgrEO siteEO=CacheHandler.getEntity(SiteMgrEO.class,queryVO.getSiteId());
            List<PersonEO> newList=new ArrayList<PersonEO>();
            if(siteEO!=null&&!StringUtils.isEmpty(siteEO.getUnitIds())){
                Long[] arr=AppUtil.getLongs(siteEO.getUnitIds(),",");
                if(arr!=null&&arr.length>0){
                    newList=personService.getPersonsByDn(arr[0]);//获取单位下所有的人员
                }
            }else{
                throw new BaseRunTimeException(TipsMode.Message.toString(), "此站点下没有绑定单位");
            }
            if (newList == null || newList.size() == 0) {
                return list;
            }
            if (list == null) {
                list = new ArrayList<ContentChartVO>();
            }
            List<ContentChartVO> list_1 = new ArrayList<ContentChartVO>();
            list_1.addAll(list);
            if (list.size() == 0) {
                for (PersonEO eo : newList) {
                    if (list.size() < 10) {
                        ContentChartVO chartVO = new ContentChartVO();
                        chartVO.setOrganId(eo.getUserId());
                        chartVO.setOrganName(eo.getName());
                        list.add(chartVO);
                    } else {
                        break;
                    }
                }
            } else {
                boolean flag=true;
                for (PersonEO eo : newList) {
                    flag=true;
                    if (list.size() >= 10) {
                        break;
                    }
                    for (ContentChartVO vo : list_1) {
                        if (eo.getUserId().equals(vo.getOrganId())) {
                            flag=false;
                            break;
                        }
                    }
                    if(flag){
                        ContentChartVO chartVO = new ContentChartVO();
                        chartVO.setOrganId(eo.getUserId());
                        chartVO.setOrganName(eo.getName());
                        list.add(chartVO);
                    }
                }
            }
        }
        if (list != null && list.size() > 1) {
            for(int i=1;i<list.size();i+=2){
                list.get(i).setOrganName("\n"+list.get(i).getOrganName());
            }
        }
        return list;
    }

    @Override
    public List<SubmitListVO> getSubmitList(StatisticsQueryVO queryVO) {
        List<SubmitListVO> listData=null;
        if(queryVO.getSelectOne()==1){//单位
            listData=msgSubmitDao.getSubmitList(queryVO);
        }else{//部门
            listData=msgSubmitDao.getSubmitList1(queryVO);
        }
        List<SubmitListVO> newList=new ArrayList<SubmitListVO>();
        SiteMgrEO siteEO=CacheHandler.getEntity(SiteMgrEO.class,queryVO.getSiteId());
        List<OrganEO> organList=new ArrayList<OrganEO>();
        if(siteEO!=null&&!StringUtils.isEmpty(siteEO.getUnitIds())){
            Long[] arr= AppUtil.getLongs(siteEO.getUnitIds(), ",");
            if(arr!=null&&arr.length>0){
                if(queryVO.getSelectOne()==1){//单位
                    organList=organService.getOrgansByDn(arr[0],OrganEO.Type.Organ.toString());//单位
                }else{//部门
                    organList=organService.getOrgansByDn(arr[0],OrganEO.Type.OrganUnit.toString());//部门
                }

            }
        }else{
            throw new BaseRunTimeException(TipsMode.Message.toString(), "此站点下没有绑定单位");
        }
        if(listData!=null&&listData.size()>=0){

            String organName=queryVO.getOrganName();
            for(int i=0;i<organList.size();i++){
                SubmitListVO vo=new SubmitListVO();
                vo.setOrganName(organList.get(i).getName());
                vo.setOrganId(organList.get(i).getOrganId());
                for(SubmitListVO submitListVO:listData){
                    if(vo.getOrganId().equals(submitListVO.getOrganId())) {
                        vo.setCount(submitListVO.getCount()==null?0:submitListVO.getCount());
                        vo.setEmployCount(submitListVO.getEmployCount()==null?0:submitListVO.getEmployCount());
                        vo.setUnEmployCount(vo.getCount()-vo.getEmployCount());
                        vo.setRate(Math.round(100*(vo.getEmployCount()/vo.getCount())));
                    }
                }
                newList.add(vo);
                if(!StringUtils.isEmpty(organName)){
                    if(!organList.get(i).getName().contains(organName)){
                        newList.remove(vo);
                    }
                }
            }
        }
        return newList;
    }

    @Override
    public Pagination getSubmitPage(StatisticsQueryVO queryVO) {
        List<SubmitListVO> list=null;
        if(queryVO.getSelectOne()==1||queryVO.getSelectOne()==2){
            list=getSubmitList(queryVO);
        }else {
            list=getSubmitList2(queryVO);
        }
        Pagination page=new Pagination();
        int index = Integer.parseInt(String.valueOf(queryVO.getPageIndex()));
        int size=queryVO.getPageSize();
        int startRow=index*size;
        int endRow=(index+1)*size;
        if(list!=null){
            if(list.size()<endRow){
                endRow=list.size();
            }
        }
        page.setData(list.subList(startRow,endRow));
        page.setTotal( Long.parseLong(String.valueOf(list.size())));
        page.setPageSize(size);
        page.setPageIndex(queryVO.getPageIndex());
        return page;
    }

    @Override
    public Pagination getDetailPage(Long pageIndex,Integer pageSize,Long uId, String uName) {
        Pagination page=msgSubmitDao.getDetailPage(pageIndex,pageSize,uId,uName);
        if(page.getData()!=null&&page.getData().size()>0){
           List<CmsMsgToColumnEO> list=( List<CmsMsgToColumnEO>)page.getData();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for(CmsMsgToColumnEO eo:list) {
                IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class,eo.getColumnId());
                if(null != indicatorEO) {
                    eo.setColumnName(indicatorEO.getName());
                    IndicatorEO _indicatorEO = CacheHandler.getEntity(IndicatorEO.class,indicatorEO.getSiteId());
                    if(null != _indicatorEO) {
                        eo.setSiteName(_indicatorEO.getName());
                    }
                }
                PersonEO personEO = personService.getPersonByUserId(eo.getCreateOrganId(),eo.getCreateUserId());
                if(null != personEO) {
                    eo.setUserName(personEO.getName());
                }
            }
        }
        return page;
    }

    @Override
    public List<SubmitListVO> getSubmitList2(StatisticsQueryVO queryVO) {
        List<SubmitListVO> listData=msgSubmitDao.getSubmitList2(queryVO);
        List<SubmitListVO> newList=new ArrayList<SubmitListVO>();
        List<PersonEO> personList=new ArrayList<PersonEO>();
        SiteMgrEO siteEO=CacheHandler.getEntity(SiteMgrEO.class,queryVO.getSiteId());
        if(siteEO!=null&&!StringUtils.isEmpty(siteEO.getUnitIds())){
            Long[] arr= AppUtil.getLongs(siteEO.getUnitIds(), ",");
            if(arr!=null&&arr.length>0){
                personList=personService.getPersonsByDn(arr[0]);//获取单位下所有的人员
            }
        }else{
            throw new BaseRunTimeException(TipsMode.Message.toString(), "此站点下没有绑定单位");
        }
        if(listData!=null&&listData.size()>=0){
            String personName=queryVO.getPersonName();
            for(int i=0;i<personList.size();i++){
                SubmitListVO vo=new SubmitListVO();
                vo.setPersonName(personList.get(i).getName());
                vo.setOrganName(personList.get(i).getUnitName());
                vo.setUserId(personList.get(i).getUserId());
                for(SubmitListVO submitListVO:listData){
                    if(vo.getUserId().equals(submitListVO.getUserId())) {
                        vo.setCount(submitListVO.getCount()==null?0:submitListVO.getCount());
                        vo.setEmployCount(submitListVO.getEmployCount()==null?0:submitListVO.getEmployCount());
                        vo.setUnEmployCount(vo.getCount()-vo.getEmployCount());
                        vo.setRate(Math.round(100*(vo.getEmployCount()/vo.getCount())));
                    }
                }
                newList.add(vo);
                if(!StringUtils.isEmpty(personName)){
                    if(!personList.get(i).getName().contains(personName)){
                        newList.remove(vo);
                    }
                }
            }
        }
        return newList;
    }
}
