package cn.lonsun.site.smartquery;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.solrManage.SolrUtil;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.datadictionary.internal.service.IDataDictItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 智能检索配置
 * @author zhangmf
 */
@RequestMapping(value = "/site/smartquery/config")
@Controller
public class SmartQueryConfigController extends BaseController {

    private static final String FILE_BASE = "/site/smartquery";

    @Autowired
    private IDataDictItemService dataDictItemService;

    @RequestMapping(value = "/index")
    public String index(ModelMap map){
        return FILE_BASE + "/index";
    }

    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(Long pageIndex, Integer pageSize) {
        Pagination dictPage = dataDictItemService.getPageByDictId(pageIndex, pageSize, 6384876L, null);

        List<DataDictItemEO> list = (List<DataDictItemEO>)dictPage.getData();
        //重新排序
        Collections.sort(list,new Comparator<DataDictItemEO>() {
            @Override
            public int compare(DataDictItemEO m1, DataDictItemEO m2) {
                return Integer.parseInt(m1.getValue()) - Integer.parseInt(m2.getValue());
            }
        });
        return list;
    }

    /**
     * @param
     * @return
     * @Description 保存
     */
    @RequestMapping("saveItems")
    @ResponseBody
    public Object saveItem(Long[] itemIds,Integer[] sortNums,Boolean[] isShows) {
        StringBuffer ids = new StringBuffer();
        for (int i = 0 ; i < itemIds.length ; i++) {
            DataDictItemEO dictItem = dataDictItemService.getEntity(DataDictItemEO.class, itemIds[i]);
            StringBuffer sb = new StringBuffer();
            //设置是否显示
            if (isShows != null && isShows.length > 0) {
                sb.append("{\"isShow\":").append(isShows[i]).append("}");
                dictItem.setDescription(sb.toString());
            }
            //设置排序
            if (sortNums != null && sortNums.length > 0) {
                dictItem.setValue(sortNums[i].toString());
            }

            // 更新数据字典
            CacheHandler.saveOrUpdate(DataDictItemEO.class, dictItem);

            ids.append(dataDictItemService.saveItem(dictItem));
        }

        SolrUtil.resetTypeSort();//重置排序map

        return getObject(ids);
    }
}
