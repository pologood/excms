package cn.lonsun.source.dataexport.impl.ex7.organperson;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.engine.vo.ExportQueryVO;
import cn.lonsun.internal.entity.DataimportMemberImportEO;
import cn.lonsun.internal.service.IDataimportMemberImportService;
import cn.lonsun.jdbc.JdbcAble;

import cn.lonsun.source.dataexport.organperson.IMemberExportService;
import cn.lonsun.target.datamodel.organperson.MemberExportQueryVO;
import cn.lonsun.target.datamodel.organperson.MemberPageVO;
import cn.lonsun.target.datamodel.organperson.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lonsun on 2018-3-7.
 */
@Service("ex7MemberExportService")
public class Ex7MemberExportService extends JdbcAble<MemberVO> implements IMemberExportService {

    private final static Long siteId=4133646l;
    @Autowired
    private IDataimportMemberImportService dataimportMemberImportService;
    @Override
    public List<MemberVO> getDataList(MemberExportQueryVO queryVO) {


        return null;
    }

    @Override
    public List<MemberVO> getDataByIds(MemberExportQueryVO queryVO, Object... ids) {

        return null;
    }

    /**
     * 获取老数据列表(不用)
     * @param memberExportQueryVO
     * @return
     */
    @Override
    public Pagination getOldPage(MemberExportQueryVO memberExportQueryVO) {
        Long num=memberExportQueryVO.getPageIndex()*memberExportQueryVO.getPageSize();
        //分页查询
        StringBuffer sql =new StringBuffer("select top "+memberExportQueryVO.getPageSize()+" m_ID oldId,m_Login uid,m_Name name,SS_ID  oldSiteId from dbo.Member where id not in (select top "+num+" m_ID from dbo.Member where 1=1");
        if(!AppUtil.isEmpty(memberExportQueryVO.getName())){
            sql.append(" and m_Name like %"+memberExportQueryVO.getName()+"%");

        }
        if(!AppUtil.isEmpty(memberExportQueryVO.getUid())){
            sql.append(" and m_Login like %"+memberExportQueryVO.getUid()+"%");

        }
        sql.append("order by m_ID) order by m_ID");
        List<Map<String, Object>> mapList = this.queryMap(sql.toString(),null);
        List<MemberPageVO>  memberPageVOs =new ArrayList<MemberPageVO>();
        if(null!=mapList&&mapList.size()>0){
            for( Map<String, Object> map : mapList){
                MemberPageVO memberPageVO =new MemberPageVO();
                memberPageVO.setOldSiteId(Long.valueOf(map.get("oldId").toString()));
                memberPageVO.setUid(map.get("uid").toString());
                memberPageVO.setName(map.get("name").toString());
                memberPageVO.setOldSiteId(Long.valueOf(map.get("oldSiteId").toString()));
                memberPageVO.setSiteId(siteId);
                List<DataimportMemberImportEO> dataimportMemberImportEOs = dataimportMemberImportService.getDataByOldId(memberPageVO.getOldId());
                if(null!=dataimportMemberImportEOs&&dataimportMemberImportEOs.size()>0){
                    memberPageVO.setIsImport(1);
                }

            }
        }
        Pagination pagination =new Pagination();
        pagination.setData(memberPageVOs);
        pagination.setPageIndex(memberExportQueryVO.getPageIndex());
        pagination.setPageSize(memberExportQueryVO.getPageSize());

        StringBuffer totalSql = new StringBuffer("SELECT count(1) as total from dbo.Member where 1=1");

        mapList =  this.queryMap(totalSql.toString(),null);
        Map<String, Object> map = mapList.get(0);
        Long total =Long.valueOf(map.get("total").toString()) ;
        Long pageCount=total/memberExportQueryVO.getPageSize();
        pagination.setTotal(total);
        pagination.setPageCount(total%memberExportQueryVO.getPageSize()>0?pageCount+1:pageCount);
        return pagination;
    }

    @Override
    public List<MemberPageVO> getOldAll(MemberExportQueryVO memberExportQueryVO) {
        return null;
    }
}
