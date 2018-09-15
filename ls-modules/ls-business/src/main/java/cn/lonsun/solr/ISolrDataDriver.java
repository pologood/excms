package cn.lonsun.solr;

import cn.lonsun.solr.vo.SolrIndexVO;

import java.util.Collection;
import java.util.Map;

/**
 * solr索引驱动，该接口用来处理第三方或者独立jar包不通过maven依赖调用的方法，
 * 调用dataopen.jar包中的方法，dataopen实现该接口，然后在solrBaseIndexUtil注册驱动即可实现调用
 */
public interface ISolrDataDriver<T> {

    /**
     * 获取索引数据
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Collection<T> getData(Long pageIndex, int pageSize);

    /**
     * 获取索引数据
     * @return
     */
    public Collection<T> getData();

    /**
     * 获取索引数据
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public Collection<T> getData(Long pageIndex, int pageSize, Map<String, Object> param);

    /**
     * 将Object转换成solrVo
     * @param data
     * @return
     */
    public SolrIndexVO convertToSolrVo(T data);

}
