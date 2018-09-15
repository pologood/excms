package cn.lonsun.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.internal.entity.ErrorContentEO;
import cn.lonsun.internal.metadata.DataModule;

import java.util.List;

/**
 * 失败记录
 * @author zhongjun
 */
public interface IErrorContentService extends IMockService<ErrorContentEO> {

    /**
     * 保存综合信息导入失败的记录
     * @author zhongjun
     */
    public void saveFailureContents(List<ErrorContentEO> errorContentList);

    /**
     * 获取导入失败的内容
     * @author zhongjun
     */
    public List<ErrorContentEO> getFailureContents();

}
