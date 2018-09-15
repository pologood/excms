package cn.lonsun.source.dataexport.content;

import cn.lonsun.engine.vo.ExportQueryVO;
import cn.lonsun.source.dataexport.IExportService;
import cn.lonsun.target.datamodel.content.VideoNewsVO;

/**
 * @author liuk
 * @Title: IArticleNewsExportService
 * @Package cn.lonsun.source.dataexport.content
 * @Description: 视频新闻
 * @date 2018/3/8 14:52
 */
public interface IVideoNewsExportService extends IExportService<VideoNewsVO, ExportQueryVO> {
}
