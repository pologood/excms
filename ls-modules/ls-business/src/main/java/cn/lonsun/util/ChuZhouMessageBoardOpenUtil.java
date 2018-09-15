package cn.lonsun.util;

import cn.lonsun.system.datadictionary.vo.DataDictVO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by guiyang on 2016/9/222.
 */
public class ChuZhouMessageBoardOpenUtil{

    public static Integer isOpen = 0;
    public static Integer isOpenAcceptance  = 0;
    public static Integer columnId = 0;
    public static Long mayorUnitId = 3046216L;//市长信箱单位号
    public static Long secretaryUnitId = 3080983L;//书记信箱单位号
    public static Long departmentUnitId = 4752930L;//部门信箱单位号
    public static Long mayorColumnId = 2903916L;//市长信箱栏目号
    public static Long secretaryColumnId = 2983442L;//书记信箱栏目号
    public static Long departmentColumnId = 2983504L;//部门信箱栏目号

    private static Properties props = new Properties();

    static{
    }

    static{
        try {
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("is-chuzhou-messageboard.properties"));
            isOpen = props.getProperty("chuzhou.message.board").equals("1")?1:0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static{
        try {
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("is-chuzhou-messageboard.properties"));
            isOpenAcceptance = props.getProperty("chuzhou.acceptance").equals("1")?1:0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}