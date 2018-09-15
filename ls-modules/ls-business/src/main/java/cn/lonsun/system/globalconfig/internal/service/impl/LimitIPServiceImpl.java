package cn.lonsun.system.globalconfig.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.globalconfig.internal.dao.ILimitIPDao;
import cn.lonsun.system.globalconfig.internal.entity.LimitIPEO;
import cn.lonsun.system.globalconfig.internal.service.ILimitIPService;
import cn.lonsun.system.globalconfig.vo.LimitIPPageVO;
import cn.lonsun.util.IPRegexUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("limitIPService")
public class LimitIPServiceImpl extends MockService<LimitIPEO> implements ILimitIPService {

    @Autowired
    private ILimitIPDao limitIPDao;

    @Override
    public Pagination getPage(LimitIPPageVO vo) {
        return limitIPDao.getPage(vo);
    }

    @Override
    public Object saveLimitIP(LimitIPEO eo) {
        return limitIPDao.saveLimitIP(eo);
    }

    @Override
    public Object deleteLimitIP(Long id) {
        return limitIPDao.deleteLimitIP(id);
    }

    @Override
    public Object updateLimitIP(LimitIPEO eo) {
        return limitIPDao.updateLimitIP(eo);
    }

    @Override
    public List<LimitIPEO> checkIP(LimitIPEO eo) {
        return limitIPDao.checkIP(eo);
    }

    @Override
    public LimitIPEO getOneIP(Long id) {
        return limitIPDao.getOneIP(id);
    }

    @Override
    public Boolean checkValidateIP(String ip) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<LimitIPEO> list = this.getEntities(LimitIPEO.class, map);

        if (!AppUtil.isEmpty(list) && list.size() > 0) {
            Boolean flag = false;
            for (int i = 0; i < list.size(); i++) {
                if (IPRegexUtil.validateIP(ip, list.get(i).getIp())) {
                    flag = true;
                    break;
                }
            }
            return flag;
        }

        return true;
    }


}
