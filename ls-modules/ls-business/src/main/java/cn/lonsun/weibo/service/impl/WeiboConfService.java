package cn.lonsun.weibo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import weibo4j.util.WeiboConfig;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.weibo.dao.IWeiboConfDao;
import cn.lonsun.weibo.entity.WeiboConfEO;
import cn.lonsun.weibo.service.IWeiboConfService;

import com.qq.connect.utils.QQConnectConfig;

/**
 * @author gu.fei
 * @version 2015-12-8 17:13
 */
@Service
public class WeiboConfService extends BaseService<WeiboConfEO> implements IWeiboConfService {

    @Autowired
    private IWeiboConfDao weiboConfDao;

    @Override
    public void saveEO(WeiboConfEO eo, String type) throws Exception {

        if(null != eo.getId()) {
            eo.setType(type);
            this.updateEntity(eo);
        } else {
            eo.setType(type);
            this.saveEntity(eo);
        }

        weiboConfDao.clearDate(LoginPersonUtil.getSiteId());
        saveProperties(eo, type);

    }

    @Override
    public WeiboConfEO getByType(String type,Long siteId) {
        return weiboConfDao.getByType(type,siteId);
    }

    private void saveProperties(WeiboConfEO eo, String type) throws Exception {
        if(type.trim().equals(WeiboConfEO.Type.Tencent.toString())) {
            QQConnectConfig.updateProperties("app_ID", eo.getAppKey());
            QQConnectConfig.updateProperties("app_KEY", eo.getToken());
        } else if(type.trim().equals(WeiboConfEO.Type.Sina.toString())) {
            WeiboConfig.updateProperties("client_ID",eo.getAppKey());
            WeiboConfig.updateProperties("client_SERCRET",eo.getAppSecret());
            WeiboConfig.updateProperties("redirect_URI",eo.getValidUrl());
        }
    }
}
