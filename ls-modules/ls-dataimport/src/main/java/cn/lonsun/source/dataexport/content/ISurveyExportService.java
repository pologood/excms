package cn.lonsun.source.dataexport.content;

import cn.lonsun.source.dataexport.IExportService;
import cn.lonsun.source.dataexport.vo.ContentQueryVO;
import cn.lonsun.target.datamodel.content.SurveyVO;

/**
 * @author caohaitao
 * @Title: ISurveyExportService
 * @Package cn.lonsun.source.dataexport.content
 * @Description: 网上调查
 * @date 2018/3/8 14:53
 */
public interface ISurveyExportService extends IExportService<SurveyVO, ContentQueryVO> {
}
