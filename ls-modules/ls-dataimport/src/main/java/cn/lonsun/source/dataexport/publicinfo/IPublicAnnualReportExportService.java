package cn.lonsun.source.dataexport.publicinfo;

import cn.lonsun.source.dataexport.IExportService;
import cn.lonsun.source.dataexport.vo.PublicContentQueryVO;
import cn.lonsun.target.datamodel.publicinfo.PublicContentVO;

/**
 * 公开年报
 * @author zhongjun
 */
public interface IPublicAnnualReportExportService extends IExportService<PublicContentVO, PublicContentQueryVO> {

}
