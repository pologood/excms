package cn.lonsun.pagestyle.internal.service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.pagestyle.internal.entity.PageStyleEO;
import cn.lonsun.pagestyle.internal.entity.PageStyleModelEO;
import cn.lonsun.pagestyle.internal.vo.PageStyleVO;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

public interface IPageStyleService extends IMockService<PageStyleEO> {
    /**
     * 获取已配置的栏目配置
     * @return
     */
    public Map<Long, Object> getAllWithColumn();

    public PageStyleEO getStyleByModel(String modelCodes);

    public PageStyleEO getStyleByColumn(Long columnId);

    public List<PageStyleModelEO> getEfficientModel(Long styleId);

    public List<PageStyleEO> getAll();

    public PageStyleEO getBaseStyle();

    public Pagination getPage(PageQueryVO vo, Map<String, Object> param);

    public void save(PageStyleEO eo);

    public void delete(Long id);


    public void saveModelConfig(Long id, String[] modelCodes);

}
