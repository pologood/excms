package cn.lonsun.site.contentModel.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.site.contentModel.vo.ContentModelVO;

import java.util.List;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-8<br/>
 */

public interface IContentModelService extends IMockService<ContentModelEO> {
    public Pagination getPage(Long pageIndex, Integer pageSize, String name, Long siteId, Integer isPublic);

    public String delEO(Long modelId);

    public boolean checkNameExisted(String name, Long modelId, Long siteId, Integer isPublic);

    public ContentModelEO saveEO(ContentModelVO vo);

    public List<ContentModelEO> getList(Long siteId);

    public boolean checkCodeExisted(String code, Long modelId, Long siteId, Integer isPublic);

    public String getFirstModelType(String code);

    public ContentModelVO getVO(Long id, Long siteId);

    public ContentModelEO getByCode(String code);

    public List<ContentModelParaVO> getParam(Long columnId, Long siteId, Integer isTurn);

    public List<ContentModelParaVO> getClassCode(Long columnId, Long siteId);

    public Boolean IsLoginComment(Long columnId);

    public boolean checkModelType(String modelTypeCode, Long siteId, Long modelId, Integer isPublic);

    public void setContentModelVO(ContentModelVO vo);

    public List<OrganEO> getAllBindUnit(Long siteId);

    public List<ContentModelParaVO> getDealStatus(Long columnId, Long siteId);

    //根据栏目CODE获取模型
    public List<ContentModelVO> getByCodes(Long siteId, String[] codes);

    public List<ContentModelVO> getByColumnTypeCode(Long siteId, String columnTypeCode);
}
