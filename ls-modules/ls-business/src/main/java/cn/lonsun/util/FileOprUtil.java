package cn.lonsun.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.system.filecenter.internal.service.IFileCenterService;

import com.mongodb.gridfs.GridFSDBFile;

/**
 * 
 * @ClassName: FileOprUtil
 * @Description: 文件操作
 * @author Hewbing
 * @date 2015年10月20日 上午8:33:07
 *
 */
public class FileOprUtil {
    private static Logger logger = LoggerFactory.getLogger(FileOprUtil.class);
    private static final String REGEX = "<img[^>]*src\\s*=\\s*[\"\'][\\S]*/mongo/([^\"\'/]*)[\"\']";
    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static IMongoDbFileServer mongoDbFileServer = SpringContextHolder.getBean(IMongoDbFileServer.class);
    private static IFileCenterService fileCenterService = SpringContextHolder.getBean(IFileCenterService.class);
    private static ContentMongoServiceImpl contentMongoService = SpringContextHolder.getBean(ContentMongoServiceImpl.class);

    /**
     * 回调接口 ADD REASON. <br/>
     *
     * @date: 2016年5月6日 下午3:55:33 <br/>
     * @author fangtinghua
     */
    public static class Callback<T> {

        public T t;

        public Callback(T t) {
            this.t = t;
        }

        /**
         * 文件上传回调函数
         *
         * @author fangtinghua
         * @param fileId
         * @return
         * @throws Throwable
         */
        T get(String fileId) throws Throwable {
            this.execute(fileId, t);
            return t;
        }

        /**
         * 文件上传回调函数
         *
         * @author fangtinghua
         * @param fileId
         */
        public void execute(String fileId, T t) {
            // 空实现
        };
    }

    /**
     * 文件拷贝
     *
     * @author fangtinghua
     * @param fileId
     * @return
     */
    public static String copyFile(String fileId, String type, Callback<BaseContentEO> callback) {
        boolean hasImage = true;
        try {
            if (StringUtils.isEmpty(fileId)) {
                hasImage = false;
                return "";
            }

            System.out.println(fileId);

            if(fileId.contains(".")){ //老数据的图片不在mongoDB里，复制不了
                callback.get(fileId);
                return fileId;
            }else{
                GridFSDBFile f = mongoDbFileServer.getGridFSDBFile(fileId, null);
                if (f == null) {
                    hasImage = false;
                    return "";
                }
                String fileName = f.getFilename();
                byte[] buffer = StreamUtils.copyToByteArray(f.getInputStream());
                String md5 = MD5Util.getMd5ByByte(buffer);
                MongoFileVO mongoVO = mongoDbFileServer.uploadByteFile(buffer, fileName, null, null);
                BaseContentEO baseContentEO = callback.get(mongoVO.getMongoId());
                String singleName = fileName.substring(0, fileName.lastIndexOf("."));
                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                FileCenterEO fileCenterEO =
                        FileUploadUtil.transFileEO(singleName, suffix, (long) buffer.length, md5, mongoVO.getMongoId(),mongoVO.getFileName(), type, FileCenterEO.Code.Default.toString(),
                                baseContentEO.getSiteId(), baseContentEO.getColumnId(), baseContentEO.getId(), "图片复制");
                fileCenterEO.setStatus(1);// 标记为引用
                fileCenterService.saveEntity(fileCenterEO);
                return mongoVO.getMongoId();
            }
        } catch (Throwable e) {
            logger.error("文件拷贝失败.", e);
        } finally {
            if (!hasImage) {
                try {
                    callback.get(null);
                } catch (Throwable e) {
                }
            }
        }
        return "";
    }

    /**
     * 内容拷贝，内容中图片也一起拷贝
     *
     * @author fangtinghua
     * @param id
     */
    public static void copyContentFile(Long contentId, Long id, BaseContentEO baseContentEO) {
        copyContent(contentId, baseContentEO, true);
    }

    /**
     * 内容拷贝，内容中图片不拷贝
     *
     * @author fangtinghua
     */
    public static void copyContent(Long contentId, BaseContentEO baseContentEO) {
        copyContent(contentId, baseContentEO, false);
    }

    /**
     * 拷贝内容
     *
     * @author fangtinghua
     * @param contentId
     * @param copyFile
     */
    private static void copyContent(final Long contentId, final BaseContentEO baseContentEO, boolean copyFile) {
        // 拷贝内容
        try {
            ContentMongoEO eo = contentMongoService.queryById(contentId);
            // 获取内容
            String content = eo.getContent();
            if (copyFile && !StringUtils.isEmpty(content)) {
                // 新建一个回到函数
                Callback<BaseContentEO> callback = new Callback<BaseContentEO>(baseContentEO);
                // 正则匹配
                Matcher m = PATTERN.matcher(content);
                // 查找
                while (m.find()) {
                    String regex = m.group(1);
                    String mongoId = copyFile(regex, FileCenterEO.Type.Image.toString(), callback);
                    content = content.replaceFirst(regex, mongoId);
                }
                eo.setContent(content);
            }
            eo.setId(baseContentEO.getId());
            contentMongoService.save(eo);
        } catch (Throwable e) {
            logger.error("内容拷贝失败.", e);
        }
    }
}