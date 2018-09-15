package cn.lonsun.special.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.special.internal.dao.ISpecialSkinsDao;
import cn.lonsun.special.internal.entity.SpecialSkinsEO;
import cn.lonsun.special.internal.service.ISpecialSkinsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhushouyong on 2016-10-15.
 */
@Service
public class SpecialSkinsServiceImpl extends MockService<SpecialSkinsEO> implements ISpecialSkinsService {

    @Autowired
    private ISpecialSkinsDao specialThemeSkinsDao;

    /**
     * 保存专题主题
     *
     * @param eo
     */
    @Override
    public void saveSpecialSkins(SpecialSkinsEO eo) {
        if (null == eo.getId()) {
            saveEntity(eo);
        } else {
            updateEntity(eo);
        }
    }

    @Override
    public List<SpecialSkinsEO> getThemeSkinList(SpecialSkinsEO eo) {
        return specialThemeSkinsDao.getThemeSkinList(eo);
    }

    /**
     * 获取专题皮肤信息
     *
     * @param siteId
     * @param themeId
     * @return
     */
    public List<SpecialSkinsEO> getSkinsItem(Long siteId, Long themeId) {

        SpecialSkinsEO eo = new SpecialSkinsEO();
        //eo.setSiteId(siteId);
        List<SpecialSkinsEO> list = getThemeSkinList(eo);
        List<SpecialSkinsEO> newList = new ArrayList<SpecialSkinsEO>();
        for (int i = 0, l = list.size(); i < l; i++) {
            Long tid = list.get(i).getThemeId();
            if (themeId.equals(tid)) {
                newList.add(list.get(i));
            }
        }
        return newList;
    }

    /**
     * 获取专题皮肤信息
     *
     * @param siteId
     * @param themeId
     * @return
     */
    public SpecialSkinsEO getDefaultSkins(Long siteId, Long themeId) {
        SpecialSkinsEO eo = new SpecialSkinsEO();
        eo.setSiteId(siteId);
        eo.setThemeId(themeId);
        eo.setDefaults(1);
        List<SpecialSkinsEO> list = specialThemeSkinsDao.getThemeSkinList(eo);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public void deleteByThemeId(Long id) {
        specialThemeSkinsDao.deleteByThemeId(id);
    }
}
