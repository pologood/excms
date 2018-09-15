package cn.lonsun.datacollect.util;

import java.util.Collection;

import org.directwebremoting.Browser;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;

/**
 * @author gu.fei
 * @version 2016-1-29 14:21
 */
public class MessagePush {

    public static void send(final String function,final String content) {

        Runnable run = new Runnable() {
            private ScriptBuffer script = new ScriptBuffer();

            public void run() {
                //设置要调用的 js及参数
                script.appendCall(function, content);
                //得到所有ScriptSession
                Collection<ScriptSession> sessions = Browser.getTargetSessions();
                //遍历每一个ScriptSession
                for (ScriptSession scriptSession : sessions) {
                    scriptSession.addScript(script);
                }
            }
        };
        //执行推送
        Browser.withAllSessions(run);
    }
}