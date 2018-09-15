package cn.lonsun.weibo.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import weibo4j.Account;
import weibo4j.Oauth;
import weibo4j.Timeline;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONObject;
import weibo4j.util.BareBonesBrowserLaunch;

/**
 * @author gu.fei
 * @version 2015-12-10 9:22
 */
public class SinaWeiboUtil {

    private static final String token = "2.00ENhKPGxe5POC4f6c49e7ec0x6qxo";
    public void getRequestToken(){
        Timeline line = new Timeline(token);
        try {
            StatusWapper statusWapper = line.getFriendsTimeline();
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        try {
//            getUID();
//            SinaWeiboUtil util = new SinaWeiboUtil();
//            util.getRequestToken();
            SinaWeiboUtil.createToken();
        }catch (Exception e) {

        }
    }

    public static void createToken() {
        Oauth oauth = new Oauth();
        try {
            BareBonesBrowserLaunch.openURL(oauth.authorize("code"));
            System.out.println(oauth.authorize("code"));
            System.out.print("Hit enter when it's done.[Enter]:");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String code = br.readLine();
            try{
                System.out.println(oauth.getAccessTokenByCode(code));
            } catch (WeiboException e) {
                if(401 == e.getStatusCode()){
                }else{
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {

        }

    }

    /**
     * 获取用户UID
     * @return
     */
    private static String getUID() {
        Account account = new Account(token);
        String uid = null;
        try {
            JSONObject jsonObj = account.getUid();
            uid = jsonObj.getString("uid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uid;
    }
}
