package cn.lonsun.mobile.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.mobile.internal.entity.MobileConfigEO;

import java.util.List;


/**
 * @author Doocal
 * @ClassName: IMobileColumnDao
 * @Description: 手机APP栏目配置数据访问层
 */
public interface IMobileConfigDao extends IMockDao<MobileConfigEO> {

    public List<MobileConfigEO> getMobileConfigList(Long siteId);

    public Object saveConfig(MobileConfigEO eo);

    public Object deleteConfig(Long id);

    public Object deleteAllbyType(String type);

    public Object updateConfigChecked(MobileConfigEO eo);

    public MobileConfigEO getOneConfig(Long id);

}
