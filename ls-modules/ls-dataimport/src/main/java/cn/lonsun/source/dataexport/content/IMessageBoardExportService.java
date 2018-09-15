package cn.lonsun.source.dataexport.content;

import cn.lonsun.source.dataexport.IExportService;
import cn.lonsun.source.dataexport.vo.ContentQueryVO;
import cn.lonsun.target.datamodel.content.MessageBoardVO;

/**
 * @author caohaitao
 * @Title: IMessageBoardExportService
 * @Package cn.lonsun.source.dataexport.content
 * @Description: 留言信箱
 * @date 2018/3/8 14:51
 */
public interface IMessageBoardExportService extends IExportService<MessageBoardVO, ContentQueryVO> {
}
