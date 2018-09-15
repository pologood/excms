package cn.lonsun.weibo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.weibo.dao.IWeiboRadioContentDao;
import cn.lonsun.weibo.entity.WeiboRadioContentEO;
import cn.lonsun.weibo.entity.vo.SinaWeiboContentVO;
import cn.lonsun.weibo.service.ISinaWeiboService;
import cn.lonsun.weibo.service.ITencentWeiboService;
import cn.lonsun.weibo.service.IWeiboRadioContentService;

/**
 * @author gu.fei
 * @version 2015-12-8 17:13
 */
@Service
public class WeiboRadioContentService extends BaseService<WeiboRadioContentEO> implements IWeiboRadioContentService {

    @Autowired
    private IWeiboRadioContentDao weiboRadioContentDao;

    @Autowired
    private ISinaWeiboService sinaWeiboService;

    @Autowired
    private ITencentWeiboService tencentWeiboService;

    @Override
    public WeiboRadioContentEO getByType(String type) {
        return weiboRadioContentDao.getByType(type);
    }

    @Override
    public Pagination getPageEOs(ParamDto dto) {
        return weiboRadioContentDao.getPageEOs(dto);
    }

    @Override
    public void batchDel(Long[] ids) {
        if (null != ids && ids.length > 0) {
            for (Long id : ids) {
                weiboRadioContentDao.delete(WeiboRadioContentEO.class, id);
            }
        }
    }

    @Override
    public void batchPublish(Long[] ids) throws Exception {
        if (null != ids && ids.length > 0) {
            for (Long id : ids) {
                WeiboRadioContentEO radio = this.getEntity(WeiboRadioContentEO.class, id);
                if (null != radio) {
                    if (radio.getType().equals(WeiboRadioContentEO.Type.Sina.toString())) {
                        SinaWeiboContentVO sina = new SinaWeiboContentVO();
                        sina.setText(radio.getContent());
                        sina.setOriginalPic(radio.getPicUrl());
                        sinaWeiboService.publishWeibo(sina);
                    } else {
                        tencentWeiboService.publishWeibo(radio.getContent());
                    }
                }

                radio.setStatus(1);
                this.updateEntity(radio);
            }
        }
    }
}
