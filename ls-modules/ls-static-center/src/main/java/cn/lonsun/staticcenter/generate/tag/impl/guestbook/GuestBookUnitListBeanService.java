package cn.lonsun.staticcenter.generate.tag.impl.guestbook;

import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-7-26<br/>
 */
@Component
public class GuestBookUnitListBeanService  extends AbstractBeanService {

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        //如果ID全为空取地址栏参数
        if (columnId==null||columnId==0) {
            String columnIdStr=context.getParamMap().get("columnId");
            if(!StringUtils.isEmpty(columnIdStr)){
                columnId = Long.parseLong(columnIdStr);
            }
        }
        List<ContentModelParaVO> list=ModelConfigUtil.getParam(columnId,context.getSiteId(),null);
        if(list!=null&&list.size()>0){
            for(ContentModelParaVO vo:list){
                String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(),columnId, null) + "?uid=" + vo.getRecUnitId();
                vo.setLink(path);
            }
        }
        return list;
    }
}
