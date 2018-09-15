package cn.lonsun.staticcenter.generate.tag.impl.filedownload;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cn.lonsun.content.filedownload.internal.service.IFileDownloadService;
import cn.lonsun.content.filedownload.vo.FileDownloadVO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.MongoUtil;
import cn.lonsun.staticcenter.generate.util.RegexUtil;

import com.alibaba.fastjson.JSONObject;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-6<br/>
 */
@Component
public class FileDownloadDetailBeanService extends AbstractBeanService {

    @Resource
    private IFileDownloadService downloadService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long contentId = context.getContentId();// 根据文章id查询文章
        return downloadService.getVO(contentId);
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        FileDownloadVO eo = (FileDownloadVO) resultObj;
        // 根据id去mongodb读取文件内容
        eo.setRemarks(MongoUtil.queryById(eo.getId()));
        return RegexUtil.parseProperty(content, eo);
    }
}