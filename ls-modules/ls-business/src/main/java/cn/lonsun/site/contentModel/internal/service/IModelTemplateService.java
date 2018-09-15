package cn.lonsun.site.contentModel.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.contentModel.vo.ContentModelVO;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-8<br/>
 */

public interface IModelTemplateService extends IMockService<ModelTemplateEO> {
   public  Pagination getPage(Long pageIndex, Integer pageSize, Long modelId);

   public void delEOs(String modelIds);

   public  void saveEO(ModelTemplateEO eo);

   public void delEO(Long tplId);

   public Boolean checkCode(Long modelId, String code,Long tplId);

   public List<ModelTemplateEO> getFirstModelType(Long modelId);

   public  void saveVO(ContentModelVO vo,Long id);

   public void delTpls(Long modelId);

   /**
    * 根据栏目类型获取第一个内容模型
    *
    * @param columnTypeCode
    * @return
    */
   public ModelTemplateEO getFirstModelByColumnCode(String columnTypeCode);
}
