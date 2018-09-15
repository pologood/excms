package cn.lonsun.site.thirdLoginManage.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.site.thirdLoginManage.internal.entity.ThirdLoginMgrEO;

/**
 * 第三方登录配置service层
 *
 * @author: liuk
 * @version: v1.0
 * @date:2018/4/23 16:21
 */
public interface IThirdLoginMgrService extends IBaseService<ThirdLoginMgrEO>{

    /**
     * 根据站点id和类型获取配置信息
     * @Author: liuk
     * @Date: 2018-4-23 17:22:46
     */
    ThirdLoginMgrEO getMgrInfoByType(Long siteId,String type);

    /**
     * 保存第三方登录配置信息
     * @Author: liuk
     * @Date: 2018-4-23 17:38:19
     */
    void saveThirdLoginMgr(String appId,String appSecret,Long siteId,String type);


    /**
     * 变更启用状态
     * @Author: liuk
     * @Date: 2018-4-23 17:38:19
     */
    void changeStatus(Long siteId,String type,Integer status);

}
