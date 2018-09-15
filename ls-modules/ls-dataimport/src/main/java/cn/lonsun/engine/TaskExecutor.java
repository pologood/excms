package cn.lonsun.engine;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.ThreadUtil;
import cn.lonsun.engine.vo.CommandParamVO;
import cn.lonsun.engine.vo.ImportResultVO;
import cn.lonsun.util.HibernateHandler;
import cn.lonsun.util.HibernateSessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncListenableTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 命令执行器,用来执行导入命令
 * @author zhongjun
 */
public class TaskExecutor {

    private  static final Logger log = LoggerFactory.getLogger("dataImport");

    /**
     * 每个导入任务最大线程执行数
     */
    private static final int importMaxThreadSize = 10;

    private static final AsyncListenableTaskExecutor taskExecutor = SpringContextHolder.getBean("taskExecutor");

    private static final DataImportCommand dataImportCommand = SpringContextHolder.getBean("dataImportCommand");

    public static <E> List<E> getData(CommandParamVO commandParam){
        return dataImportCommand.getData(commandParam);
    }

    /**
     * 异步导入（多线程导入，无需等待执行完）
     */
    public static Long execute(final CallBack callBack, final CommandParamVO... commandParams){
        if(commandParams == null || commandParams.length == 0){
            return null;
        }
        final Long taskId = saveTask(commandParams);
        final Map<String, Object> sessionInfo = ThreadUtil.session.get();
        taskExecutor.submit(new Callable<Long>() {
            @Override
            public Long call() {
                // 将session数据绑定到线程
                ThreadUtil.set(sessionInfo);
                Long result = HibernateSessionUtil.execute(new HibernateHandler<Long>() {
                    @Override
                    public Long execute() throws Throwable {
                        return executeSync(callBack, taskId, commandParams);
                    }
                    @Override
                    public Long complete(Long result, Throwable exception) {
                        log.debug("异步导入完成，返回结果： {}", result);
                        return result;
                    }
                });
                return result;
            }
        });
        return taskId;
    }

    /**
     * 异步执行
     */
    public static <P> Future<P> execute(Callable<P> callable){
        return taskExecutor.submit(callable);
    }

    /**
     * 同步导入（等待所有任务执行完）
     */
    public static Long executeSync(final CallBack callBack, Long taskId, CommandParamVO... commandParams){
        if(commandParams == null || commandParams.length == 0){
            return null;
        }
        if(taskId == null){
            taskId = saveTask(commandParams);
        }
        List<Future<ImportResultVO>> tasks = new ArrayList<Future<ImportResultVO>>();
        for(CommandParamVO c : commandParams){
            c.setTaskId(taskId);
            tasks.add(dataImportCommand.executeAsync(c));
            if(tasks.size() >= importMaxThreadSize){
                waitTaskFinish(callBack, tasks);
            }
        }
        waitTaskFinish(callBack, tasks);
        return taskId;
    }

    /**
     * 等待线程执行结束
     * @param callBack
     * @param tasks
     */
    private static void waitTaskFinish(CallBack callBack, List<Future<ImportResultVO>> tasks) {
        try {
            for(Future<ImportResultVO> task : tasks){
                ImportResultVO result = task.get();
                if(callBack != null){
                    callBack.setItemImportSuccess(result);
                }
            }
            tasks.clear();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("导入异常: {}", e.getCause());
        }
    }

    /**
     * 终止导入
     */
    public static void cancelImportTask(Long taskId){
        dataImportCommand.stop(taskId);
    }

    public static Long saveTask(CommandParamVO... commandParams){
        if(commandParams == null || commandParams.length == 0){
            return 0l;
        }
        //TODO 保存任务
        return null;
    }
    public static Long saveTask(List<CommandParamVO> commandParams){
        if(commandParams == null || commandParams.isEmpty()){
            return 0l;
        }
        //TODO 保存任务
        return null;
    }

    /**
     * 回调方法
     * @author zhongjun
     */
    public interface CallBack{
        /**
         * 每执行完一个最小单元的导入操作后调用，如 信息公开主动公开：每导入一个单位的一个目录的内容就调用一次该方法，用来保存导入日志或重置配置
         * @param result
         */
         void setItemImportSuccess(ImportResultVO result);
    }


}
