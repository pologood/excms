package cn.lonsun.webservice.special.impl;

import cn.lonsun.core.util.Pagination;
import cn.lonsun.webservice.core.WebServiceCaller;
import cn.lonsun.webservice.special.ISpecialWebService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 专题webservice
 */
@Service("specialWebService")
public class SpecialWebServiceImpl implements ISpecialWebService {
    /**
     * 服务编码
     */
    private enum Codes {
        /**专题分类列表*/
        Special_getSpecialTypeList,
        /**专题分页列表*/
        Special_getSpecialThemeList,
        /**下载专题*/
        Special_downloadSpecialTheme;
    }

    @Override
    public <T> List<T> getSpecialTypeList(Class<T> t) {
        return (List<T>) WebServiceCaller.getList(Codes.Special_getSpecialTypeList.toString(), new Object[] {}, t);
    }

    @Override
    public  Pagination getSpecialThemeList(String param) {
        return (Pagination)WebServiceCaller.getSimpleObject(Codes.Special_getSpecialThemeList.toString(), new Object[] {param}, Pagination.class);
    }

    @Override
    public <T> T downloadSpecialTheme(Long id, Long siteId, Class<T> t) {
        return (T)WebServiceCaller.getSimpleObject(Codes.Special_downloadSpecialTheme.toString(), new Object[] {id,siteId}, t);
    }
}
