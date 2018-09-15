package cn.lonsun.dbimport.service;

import cn.lonsun.core.exception.BaseRunTimeException;
import org.apache.poi.ss.formula.functions.T;

import java.util.*;

/**
 * 数据导入
 * @author zhongjun
 */
public interface IImportService {

    public static Map<String, Queue<String>> importMsg = new HashMap<String, Queue<String>>();

    /**
     * 做数据准备
     * @return
     */
    public Object doBefore();

    public Object doImport();

    public Object afterImport(String taskId);

    /**
     * 设置备份
     * @param t
     * @param <T>
     * @return
     */
    public List doBackUp();

    /**
     * 获取导入进度
     * @param taskId
     * @return
     */
    public Collection<String> getProcess(String taskId);

    /**
     * 备份还原
     * @param task_id
     * @throws BaseRunTimeException
     */
    public void revert(Long task_id) throws BaseRunTimeException;

    /**
     * 重新加载配置文件
     */
    public void reloadConfig();

    /**
     * 终止导入
     * @param taskId
     * @return
     */
    public boolean stopImport(String taskId);

}
