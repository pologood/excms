package cn.lonsun.site.template.util;

import cn.lonsun.core.util.ResultVO;

/**
 * @author gu.fei
 * @version 2015-6-16 8:57
 */
public class ResponseData {

    /*
    * 默认成功返回
    * */
    public static Object success() {
        ResultVO vo = new ResultVO();
        vo.setStatus(1);
        vo.setDesc("操作成功!");
        return vo;
    }

    /*
    * 默认成功失败
    * */
    public static Object fail() {
        ResultVO vo = new ResultVO();
        vo.setStatus(1);
        vo.setDesc("操作失败!");
        return vo;
    }

    /*
    * 自定义返回描述信息
    * */
    public static Object success(String desc) {
        ResultVO vo = new ResultVO();
        vo.setStatus(1);
        vo.setDesc(desc);
        return vo;
    }

    /*
   * 自定义返回描述信息
   * */
    public static Object success(Object data,String desc) {
        ResultVO vo = new ResultVO();
        vo.setData(data);
        vo.setStatus(1);
        vo.setDesc(desc);
        return vo;
    }

    /*
    * 自定义失败描述信息
    * */
    public static Object fail(String desc) {
        ResultVO vo = new ResultVO();
        vo.setStatus(0);
        vo.setDesc(desc);
        return vo;
    }
}
