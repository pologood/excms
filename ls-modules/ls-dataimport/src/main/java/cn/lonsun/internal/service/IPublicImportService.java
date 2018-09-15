package cn.lonsun.internal.service;

import cn.lonsun.internal.metadata.ImportType;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 信息公开导入
 * @author zhongjun
 */
public interface IPublicImportService {

    /**
     * 异步导入所有内容
     * @param importType 导入类型
     * @param siteId 导入站点
     * @param organId 导入单位
     * @param oldIds 导入内容id
     */
    public void importSite(ImportType importType, Long siteId, String organId, String... oldIds);

    /**
     * 导入依申请公开目录
     * @param importType 导入类型
     * @param organId 公开单位id
     * @param oldIds 内容id
     * @return
     */
    public void importPublicCatalog(ImportType importType, Long siteId, String organId, String... oldIds);

    /**
     * 导入主动公开目录
     * @param importType 导入类型
     * @param organId 公开单位id
     * @param catId 内容id
     * @return
     */
    public void importCatalog(ImportType importType, Long siteId, String organId, String... catId);

    /**
     * 获取单位的主动公开目录
     * @param organId
     * @return
     */
    public List getCatalog(Long siteId, String organId);

    /**
     * 获取信息公开单位
     * @return
     */
    public List getPublicOrgan();


}
