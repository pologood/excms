package cn.lonsun.govbbs.util;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.govbbs.internal.service.IBbsFileService;
import org.springframework.core.task.TaskExecutor;

/**
 * Created by zhangchao on 2017/1/10.
 */
public class BbsFilesUtil {

    private static IBbsFileService bbsFileService= SpringContextHolder.getBean("bbsFileService");

    private static TaskExecutor taskExecutor = SpringContextHolder.getBean(TaskExecutor.class);


    public static void updatePostFilesSuffix(final Long postId){
        if(postId != null){
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        bbsFileService.updatePostFilesSuffix(postId);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally{
                    }
                }
            });
        }
    }
}
