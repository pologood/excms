package cn.lonsun.source.dataexport.organperson;

import cn.lonsun.core.util.Pagination;
import cn.lonsun.source.dataexport.IExportService;
import cn.lonsun.target.datamodel.organperson.MemberExportQueryVO;
import cn.lonsun.target.datamodel.organperson.MemberPageVO;
import cn.lonsun.target.datamodel.organperson.MemberVO;

import java.util.List;

/**
 * Created by lonsun on 2018-3-7.
 */
public interface IMemberExportService extends IExportService<MemberVO, MemberExportQueryVO> {

    Pagination getOldPage(MemberExportQueryVO memberExportQueryVO);
    List<MemberPageVO> getOldAll(MemberExportQueryVO memberExportQueryVO);
}
