package cn.lonsun.process.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.process.dao.IFormHandleRecordDao;
import cn.lonsun.process.entity.FormHandleRecordEO;
import cn.lonsun.process.vo.FormHandleRecordListVO;
import cn.lonsun.process.vo.ProcessFormQueryVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zhu124866 on 2015-12-23.
 */
@Repository
public class FormHandleRecordDaoImpl extends BaseDao<FormHandleRecordEO> implements IFormHandleRecordDao {


    @Override
    public Pagination getFormHandleRecordPagination(ProcessFormQueryVO queryVO) {
        List<Object> values = new ArrayList<Object>();

        StringBuilder hql = new StringBuilder("select t1.PROCESS_FORM_ID as processFormId,t1.TITLE as title,t1.CREATE_PERSON_NAME as createPersonName, " +
                "t1.CUR_ACTIVITY_NAME as curActivityName,t1.PROC_INST_ID as procInstId,t1.CUR_ACTINST_ID as curActinstId,t2.CREATE_DATE as createDate, " +
                "t1.COLUMN_ID as columnId , t1.COLUMN_NAME as columnName ");
        hql.append("from FORM_PROCESS_FORM t1 , (select t.PROCESS_FORM_ID,max(t.CREATE_DATE) as CREATE_DATE from FORM_HANDLE_RECORD t where  t.CREATE_USER_ID = ? and t.CREATE_UNIT_ID = ? ");
        values.add(queryVO.getUserId());
        values.add(queryVO.getUnitId());
        if(null != queryVO.getStartDate()){
            hql.append(" and t.CREATE_DATE > ?");
            values.add(queryVO.getStartDate());
        }
        if(null != queryVO.getEndDate()){
            hql.append(" and t.CREATE_DATE < ?");
            Calendar date = Calendar.getInstance();
            date.setTime(queryVO.getEndDate());
            date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);//结束日期增加一天
            values.add(date.getTime());
        }
        hql.append("group by t.PROCESS_FORM_ID ) t2  ");
        hql.append("where t1.PROCESS_FORM_ID = t2.PROCESS_FORM_ID  ");
        if(!AppUtil.isEmpty(queryVO.getModuleCode())){
            hql.append(" and t1.module_code = ?");
            values.add(queryVO.getModuleCode());
        }
        if(!AppUtil.isEmpty(queryVO.getTitle())){
            hql.append(" and t1.title like ?");
            values.add("%"+queryVO.getTitle()+"%");
        }
        if(!AppUtil.isEmpty(queryVO.getCreatePersonName())){
            hql.append(" and t1.CREATE_PERSON_NAME like ? ");
            values.add("%"+queryVO.getCreatePersonName()+"%");
        }
        hql.append(" order by t2.CREATE_DATE desc");
        String[] queryFields = new String[]{"processFormId","title","createPersonName","curActivityName","procInstId","curActinstId","createDate","columnId","columnName"};
        return this.getPaginationBySql(queryVO.getPageIndex(), queryVO.getPageSize(), hql.toString(), values.toArray(), FormHandleRecordListVO.class,queryFields);
    }
}
