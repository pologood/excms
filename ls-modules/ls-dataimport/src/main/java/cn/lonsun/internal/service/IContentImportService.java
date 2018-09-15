package cn.lonsun.internal.service;

import cn.lonsun.internal.metadata.ImportType;

/**
 * 综合信息导入
 * @author zhongjun
 */
public interface IContentImportService {

    /**
     * 导入栏目
     * @param oldIds
     */
    public void importColumn(ImportType importType, String... oldIds);

    /**
     * 查询老系统中所有单位
     */
    public void getColumn();

    /**
     * 导入栏目内容
     * @param importType 导入类型
     * @param columnId 老数据栏目id
     * @param oldIds 老数据id
     */
    public void importColumnContent(ImportType importType, String columnId, String... oldIds);

}
