package cn.lonsun.site.thirdLoginManage.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.site.thirdLoginManage.internal.dao.IThirdLoginMgrDao;
import cn.lonsun.site.thirdLoginManage.internal.entity.ThirdLoginMgrEO;
import cn.lonsun.site.thirdLoginManage.internal.service.IThirdLoginMgrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 第三方登录配置service层
 *
 * @author: liuk
 * @version: v1.0
 * @date:2018/4/23 16:23
 */
@Service("thirdLoginMgrService")
public class ThirdLoginMgrServiceImpl extends BaseService<ThirdLoginMgrEO> implements IThirdLoginMgrService{

    @Autowired
    private IThirdLoginMgrDao thirdLoginMgrDao;

    /**
     * 根据站点id和类型获取配置信息
     * @Author: liuk
     * @Date: 2018-4-23 17:22:46
     */
    @Override
    public ThirdLoginMgrEO getMgrInfoByType(Long siteId, String type) {
        if(AppUtil.isEmpty(siteId)){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"站点id不能为空");
        }

        if(AppUtil.isEmpty(type)){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"分类不能为空");
        }

        Map<String,Object> param = new HashMap<String,Object>();
        param.put("siteId",siteId);
        param.put("type",type);

        return thirdLoginMgrDao.getEntity(ThirdLoginMgrEO.class,param);
    }

    /**
     * 保存第三方登录配置信息
     * @Author: liuk
     * @Date: 2018-4-23 17:38:19
     */
    @Override
    public void saveThirdLoginMgr(String appId, String appSecret, Long siteId, String type) {
        ThirdLoginMgrEO thirdLoginMgrEO = getMgrInfoByType(siteId,type);

        if(thirdLoginMgrEO==null){//新增配置信息
            thirdLoginMgrEO = new ThirdLoginMgrEO();
            thirdLoginMgrEO.setAppId(appId);
            thirdLoginMgrEO.setAppSecret(appSecret);
            thirdLoginMgrEO.setSiteId(siteId);
            thirdLoginMgrEO.setType(type);
            thirdLoginMgrDao.save(thirdLoginMgrEO);
        }else{//修改
            if(1==thirdLoginMgrEO.getStatus().intValue()){
                if(AppUtil.isEmpty(appId)||AppUtil.isEmpty(appSecret)){
                    throw new BaseRunTimeException(TipsMode.Message.toString(),"该应用当前处于启用状态，App ID跟App Secret不能为空");
                }
            }
            thirdLoginMgrEO.setAppId(appId);
            thirdLoginMgrEO.setAppSecret(appSecret);
            thirdLoginMgrDao.update(thirdLoginMgrEO);
        }
    }

    /**
     * 变更启用状态
     * @Author: liuk
     * @Date: 2018-4-23 17:38:19
     */
    @Override
    public void changeStatus(Long siteId, String type, Integer status) {
        ThirdLoginMgrEO thirdLoginMgrEO = getMgrInfoByType(siteId,type);
        if(thirdLoginMgrEO==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"请先配置app信息");
        }
        if(status==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"启用状态不能为空");
        }
        if(1==status.intValue()){
            if(AppUtil.isEmpty(thirdLoginMgrEO.getAppId())||AppUtil.isEmpty(thirdLoginMgrEO.getAppSecret())){
                throw new BaseRunTimeException(TipsMode.Message.toString(),"App ID或App Secret为空，不能启用");
            }
        }
        thirdLoginMgrEO.setStatus(status);
        thirdLoginMgrDao.update(thirdLoginMgrEO);
    }
}
