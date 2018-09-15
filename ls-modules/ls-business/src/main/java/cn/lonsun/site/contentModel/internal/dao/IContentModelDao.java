package cn.lonsun.site.contentModel.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.vo.ContentModelVO;

import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-8<br/>
 */

public interface IContentModelDao extends IMockDao<ContentModelEO> {

    public Pagination getPage(Long pageIndex, Integer pageSize, String name, Long siteId, Integer isPublic);

    public ContentModelVO getVO(Long id, Long siteId);

    public boolean checkModelType(String modelTypeCode, Long siteId, Long modelId, Integer isPublic);

    public List<ContentModelVO> getByColumnTypeCode(Long siteId, String codes);

    public List<ContentModelVO> getByCodes(Long siteId, String[] codes);

}
