package cn.lonsun.source.dataexport.publicinfo;

import cn.lonsun.source.dataexport.IExportService;
import cn.lonsun.source.dataexport.vo.PublicContentQueryVO;
import cn.lonsun.target.datamodel.publicinfo.PublicDrivingContentVO;

/**
 * ex7主动公开导出
 * @author zhongjun
 */
public interface IDrivingContentExportService extends IExportService<PublicDrivingContentVO, PublicContentQueryVO> {

}
