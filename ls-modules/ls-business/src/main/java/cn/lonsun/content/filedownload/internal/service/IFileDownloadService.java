package cn.lonsun.content.filedownload.internal.service;

import cn.lonsun.content.filedownload.internal.entity.FileDownloadEO;
import cn.lonsun.content.filedownload.vo.FileDownloadVO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-23<br/>
 */

public interface IFileDownloadService extends IMockService<FileDownloadEO> {
    public Pagination getPage(ContentPageVO pageVO);

    public FileDownloadVO getVO(Long id);

    public void saveVO(FileDownloadVO vo);

    public void deleteVOs(String ids);

    public void addCount(Long downId);
}
