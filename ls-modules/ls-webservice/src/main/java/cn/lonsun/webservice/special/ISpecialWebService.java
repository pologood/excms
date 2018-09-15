package cn.lonsun.webservice.special;

import cn.lonsun.core.util.Pagination;
import java.util.List;

/**
 * 专题webservice接口
 * @author zhongjun
 */
public interface ISpecialWebService {

    /**
     * 获取专题分类列表
     * @return
     */
    public <T> List<T> getSpecialTypeList(Class<T> t);

    /**
     * 获取专题分页数据
     * @return
     */
    public Pagination getSpecialThemeList(String param);

    /**
     * 下载主题
     * @param id
     * @param siteId
     * @param <T>
     * @return
     */
    public <T> T downloadSpecialTheme(Long id, Long siteId, Class<T> t);


}
