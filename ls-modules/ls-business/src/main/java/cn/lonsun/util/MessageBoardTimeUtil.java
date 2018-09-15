package cn.lonsun.util;

import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 留言红黄牌处理<br/>
 *
 * @version v1.0 <br/>
 * @date 2016-6-1<br/>
 */
public class MessageBoardTimeUtil {

    public static List<MessageBoardEditVO> setTimes(List<MessageBoardEditVO> editVOs) throws ParseException {
        if (null != editVOs && editVOs.size() > 0) {
            for (MessageBoardEditVO editVO : editVOs) {
                Map<String, ColumnTypeConfigVO> cacheMap = new HashMap<String, ColumnTypeConfigVO>();
                Long siteId = editVO.getSiteId();
                Long columnId = editVO.getColumnId();
                String key = null;
                if (null != columnId) {
                    key = siteId + "_" + columnId;
                }
                ColumnTypeConfigVO setting = null;
                if (cacheMap.containsKey(key)) {
                    setting = cacheMap.get(key);
                } else {
                    setting = ModelConfigUtil.getCongfigVO(columnId, siteId);
                    cacheMap.put(key, setting);
                }

                if (null != setting) {
                    editVO.setRecType(setting.getRecType());
                }

                if (!StringUtils.isEmpty(editVO.getDealStatus())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("deal_status", editVO.getDealStatus());
                    if (dictVO != null) {
                        editVO.setStatusName(dictVO.getKey());
                    }
                }
                if (null != setting && !StringUtils.isEmpty(setting.getClassCodes()) && setting.getClassCodes().contains(editVO.getClassCode())) {
                    DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", editVO.getClassCode());
                    if (dictVO != null) {
                        editVO.setClassName(dictVO.getKey());
                    }
                }
            }
        }
        return editVOs;
    }
}