package cn.lonsun.system.globalconfig.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.globalconfig.internal.entity.LimitIPEO;
import cn.lonsun.system.globalconfig.vo.LimitIPPageVO;

/**
 * @author Doocal
 * @ClassName: ILimitIPService
 * @Description: 限制IP业务逻辑层
 * @date 2015年8月25日 上午10:42:56
 */
public interface ILimitIPService extends IMockService<LimitIPEO> {

    /**
     * @param vo
     * @return
     * @Description IP限制列表
     */
    public Pagination getPage(LimitIPPageVO vo);

    /**
     * @param eo
     * @return
     * @Description 保存
     */
    public Object saveLimitIP(LimitIPEO eo);

    /**
     * @param id
     * @return
     * @Description 删除
     */
    public Object deleteLimitIP(Long id);

    /**
     * @param eo
     * @return
     * @Description 更新
     */
    public Object updateLimitIP(LimitIPEO eo);

    /**
     * @param eo
     * @return
     * @Description 查找IP
     */
    public List<LimitIPEO> checkIP(LimitIPEO eo);

    /**
     * @param id
     * @return
     * @Description 根据ID返回EO
     */
    public LimitIPEO getOneIP(Long id);

    /**
     * 检查IP黑名单
     *
     * @param ip
     * @return
     */
    public Boolean checkValidateIP(String ip);

}
