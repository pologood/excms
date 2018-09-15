package cn.lonsun.key.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.key.entity.PrimarykeyEO;

/**
 * @author gu.fei
 * @version 2016-07-25 17:02
 */
public interface IPrimarykeyService extends IBaseService<PrimarykeyEO> {

    /**
     * 根据名称查询主键值
     * @param name
     * @return
     */
    public PrimarykeyEO getEntityByName(String name);

    /**
     * 获取数据库表对应的主键最大值
     * @param tableName
     * @param key
     * @return
     */
    public Long getMaxKeyValue(String tableName, String key);

    /**
     * 重写保存信息
     * @return
     */
    public void saveAEntity(PrimarykeyEO eo);
}
