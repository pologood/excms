package cn.lonsun.util;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataDictionaryUtil {

    /**
     * 根据数据字典code获取数据字段项列表
     *
     * @param code
     * @return
     * @author fangtinghua
     */
    private static List<DataDictItemEO> getDictItemList(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        DataDictEO ddeo = CacheHandler.getEntity(DataDictEO.class, CacheGroup.CMS_CODE, code);
        if (null == ddeo) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "数据字典数据不存在！");
        }

        return CacheHandler.getList(DataDictItemEO.class, CacheGroup.CMS_PARENTID, ddeo.getDictId());
    }

    /**
     * 根据数据字典code获取数据字段项列表
     *
     * @param code
     * @return
     * @author fangtinghua
     */
    public static List<DataDictVO> getDDList(String code) {
        List<DataDictItemEO> itemList = getDictItemList(code);
        if (null == itemList || itemList.isEmpty()) {
            return Collections.emptyList();
        }
        List<DataDictVO> list = new ArrayList<DataDictVO>();
        for (DataDictItemEO eo : itemList) {
            if (eo.getIsHide() == 1) {
                continue;
            }
            DataDictVO vo = new DataDictVO();
            vo.setName(eo.getName());
            vo.setKey(eo.getName());
            vo.setValue(eo.getValue());
            vo.setCode(eo.getCode());
            vo.setDefault(eo.getIsDefault() == 1);
            list.add(vo);
        }
        return list;
    }

    /**
     * 根据编码 code 和站点 siteid 获取数据字典项列表的工具类
     *
     * @param code
     * @param siteId
     * @return
     * @author doocal
     */
    public static List<DataDictVO> getItemList(String code, Long siteId) {
        List<DataDictItemEO> itemList = getDictItemList(code);
        if (null == itemList || itemList.isEmpty()) {
            return Collections.emptyList();
        }
        List<DataDictVO> list = new ArrayList<DataDictVO>();
        for (DataDictItemEO eo : itemList) {
            if (eo.getIsHide() == 1) {
                continue;
            }
            if (eo.getSiteId() == null || eo.getSiteId().equals(siteId)) {
                DataDictVO vo = new DataDictVO();
                vo.setId(eo.getItemId());
                vo.setPid(eo.getDataDicId());
                vo.setKey(eo.getName());
                vo.setValue(eo.getValue());
                vo.setCode(eo.getCode());
                vo.setDefault(eo.getIsDefault() == 1);

                list.add(vo);
            }
        }
        return list;
    }

    public static DataDictVO getDefuatItem(String pCode, Long siteId) {
        List<DataDictItemEO> itemList = getDictItemList(pCode);
        if (null == itemList || itemList.isEmpty()) {
            return null;
        }
        DataDictVO _vo = new DataDictVO();
        for (DataDictItemEO eo : itemList) {
            if (eo.getIsHide() == 1) {
                continue;
            }
            if (eo.getIsDefault() == 1 && (null == siteId || siteId.equals(eo.getSiteId()))) {
                _vo.setId(eo.getItemId());
                _vo.setCode(eo.getCode());
                _vo.setKey(eo.getName());
                _vo.setValue(eo.getValue());
                break;
            }
        }
        return _vo;
    }

    public static DataDictVO getItem(String pCode, String cCode) {
        List<DataDictItemEO> itemList = getDictItemList(pCode);
        if (null == itemList || itemList.isEmpty()) {
            return null;
        }
        DataDictVO _vo = new DataDictVO();
        for (DataDictItemEO eo : itemList) {
            if (eo.getIsHide() == 1) {
                continue;
            }
            if (eo.getCode().equals(cCode)) {
                _vo.setId(eo.getItemId());
                _vo.setCode(cCode);
                _vo.setKey(eo.getName());
                _vo.setValue(eo.getValue());
                break;
            }
        }
        return _vo;
    }
}