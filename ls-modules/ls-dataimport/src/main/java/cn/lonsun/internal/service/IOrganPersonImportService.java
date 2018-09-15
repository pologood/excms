package cn.lonsun.internal.service;

import cn.lonsun.internal.metadata.ImportType;

public interface IOrganPersonImportService {

    /**
     * 批量导入
     * @param oldIds
     */
    public void importOrgan(ImportType importType, String... oldIds);

    /**
     * 查询老系统中所有单位
     */
    public void getOrgan();

    /**
     * 批量导入
     * @param importType 导入类型
     * @param oldUnitId 老单位id
     * @param oldIds 老数据id
     */
    public void importUser(ImportType importType, String oldUnitId, String... oldIds);

    /**
     * 查询老系统中某个单位的用户
     */
    public void getUser(String... unitId);

    /**
     * 批量导入
     * @param importType 导入类型
     * @param oldIds 老数据id
     */
    public void importMember(ImportType importType, String... oldIds);

    /**
     * 查询老系统中的会员
     */
    public void getMember();


}
