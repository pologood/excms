package cn.lonsun.weibo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import cn.lonsun.core.util.Pagination;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;
import cn.lonsun.weibo.service.ITencentWeiboService;
import cn.lonsun.weibo.util.TencentCredential;

import com.qq.connect.QQConnectException;
import com.qq.connect.api.weibo.Idol;
import com.qq.connect.api.weibo.UserInfo;
import com.qq.connect.api.weibo.Weibo;
import com.qq.connect.javabeans.weibo.FansIdolsBean;
import com.qq.connect.javabeans.weibo.SingleFanIdolBean;

/**
 * @author gu.fei
 * @version 2015-12-9 16:12
 */
@Service
public class TencentWeiboService implements ITencentWeiboService {

    @Override
    public Pagination getPageFans(WeiboPageVO vo) {
        Pagination page = new Pagination();
        UserInfo info = new UserInfo(TencentCredential.getToken(),TencentCredential.getOpenID());
        int size = Integer.valueOf(vo.getPageSize().toString());
        int index = Integer.valueOf(vo.getPageIndex().toString());
        try{
            FansIdolsBean fans = info.getFansList(size,index*size, "mode=0");
            List<SingleFanIdolBean> fanIdolBeans = fans.getFanIdols();
            page.setData(fanIdolBeans);
            page.setPageIndex(vo.getPageIndex());
            page.setPageSize(vo.getPageSize());
            page.setTotal(Long.valueOf(info.getUserInfo().getFansNum()));
        }catch (Exception e) {
        }
        return page;
    }

    @Override
    public Pagination getPageFollows(WeiboPageVO vo) {
        Pagination page = new Pagination();
        if(null == TencentCredential.getToken()) {
            return new Pagination();
        }
        UserInfo info = new UserInfo(TencentCredential.getToken(),TencentCredential.getOpenID());
        int size = Integer.valueOf(vo.getPageSize().toString());
        int index = Integer.valueOf(vo.getPageIndex().toString());
        try{
            FansIdolsBean fans = info.getIdolsList(size,index*size, "mode=0");
            List<SingleFanIdolBean> fanIdolBeans = fans.getFanIdols();
            page.setData(fanIdolBeans);
            page.setPageIndex(vo.getPageIndex());
            page.setPageSize(vo.getPageSize());
            page.setTotal(Long.valueOf(info.getUserInfo().getIdolNum()));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }

    @Override
    public void cancelIdol(String[] openIDs) {
        Idol idol = new Idol(TencentCredential.getToken(),TencentCredential.getOpenID());
        try{
            for(String openID : openIDs) {
                idol.delIdolByOpenID(openID);
            }
        }catch (Exception e) {
        }
    }

    @Override
    public void deleteWeibo(String[] weiboIds) {
        Weibo weibo = new Weibo(TencentCredential.getToken(),TencentCredential.getOpenID());
        try{
            for(String weiboId : weiboIds) {
                weibo.delWeibo(weiboId);
            }
        } catch (QQConnectException e) {
        }
    }

    @Override
    public void publishWeibo(String content) throws Exception {
        Weibo weibo = new Weibo(TencentCredential.getToken(),TencentCredential.getOpenID());
        weibo.addWeibo(content);
    }
}
