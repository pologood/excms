
package cn.lonsun.process.container;

import cn.lonsun.webservice.processEngine.vo.Button;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程操作按钮容器
 *@date 2014-12-26 11:05  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
public class ButtonsContainer {

    public static Map<String,Button> BUTTONS_MAP = new HashMap<String, Button>();

    static{
        BUTTONS_MAP.put("step_next",new Button("step_next","下一步"));
        BUTTONS_MAP.put("finish",new Button("finish","办理完毕"));
        BUTTONS_MAP.put("complete",new Button("complete","办结"));
        BUTTONS_MAP.put("has_read",new Button("has_read","已阅"));
        BUTTONS_MAP.put("edit_attachment",new Button("edit_attachment","编辑附件"));
        BUTTONS_MAP.put("edit_doc",new Button("edit_doc","编辑正文"));
        BUTTONS_MAP.put("view_processHistLog",new Button("view_processHistLog","查看办理日志"));
        BUTTONS_MAP.put("view_flowchart",new Button("view_flowchart","查看流程图"));
        BUTTONS_MAP.put("view_attachment",new Button("view_attachment","查看附件"));
        BUTTONS_MAP.put("view_doc",new Button("view_doc","查看正文"));
        BUTTONS_MAP.put("register_finish",new Button("register_finish","登记办结"));
        BUTTONS_MAP.put("claim",new Button("claim","办理"));
        BUTTONS_MAP.put("print",new Button("print","打印"));
        BUTTONS_MAP.put("distribution_complete",new Button("distribution_complete","分发办结"));
        BUTTONS_MAP.put("direct_complete",new Button("direct_complete","直接办结"));
        BUTTONS_MAP.put("save_form",new Button("save_form","保存"));
    }
}
