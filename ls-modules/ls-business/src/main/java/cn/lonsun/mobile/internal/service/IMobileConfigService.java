package cn.lonsun.mobile.internal.service;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.mobile.internal.entity.MobileConfigEO;
import cn.lonsun.mobile.vo.MobileConfigVO;
import cn.lonsun.mobile.vo.MobileDataVO;

import java.util.List;

/**
 * @author Doocal
 * @ClassName: ILimitIPService
 * @Description: 限制IP业务逻辑层
 * @date 2015年8月25日 上午10:42:56
 */
public interface IMobileConfigService extends IMockService<MobileConfigEO> {

    /**
     * @param vo
     * @return
     * @Description 配置列表
     */
    public List<MobileConfigEO> getMobileConfigList(Long siteId);

    /**
     * @param siteId
     * @param columnId
     * @param num
     * @return
     * @Description 配置列表
     */
    public List<BaseContentEO> getMobileIdsConfig(Long siteId, Long[] columnId, Integer num);

    /**
     * @param vo
     * @return
     * @Description 保存
     */
    public Object saveConfig(MobileConfigVO vo);

    /**
     * @param vo
     * @return
     * @Description 保存
     */
    public Object savePublicConfig(MobileConfigVO vo);

    /**
     * @param id
     * @return
     * @Description 删除
     */
    public Object deleteConfig(Long id);

    /**
     * @param type
     * @return
     * @Description 根据类型删除
     */
    public Object deleteAllbyType(String type);

    /**
     * @param eo
     * @return
     * @Description 更新
     */
    public Object updateConfig(MobileConfigEO eo);

    /**
     * @param id
     * @return
     * @Description 根据ID返回EO
     */
    public MobileConfigEO getOneConfig(Long id);


}
