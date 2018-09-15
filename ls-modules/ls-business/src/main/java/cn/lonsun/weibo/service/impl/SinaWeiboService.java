package cn.lonsun.weibo.service.impl;

import cn.lonsun.GlobalConfig;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.datacollect.util.CatchImage;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.weibo.entity.*;
import cn.lonsun.weibo.entity.vo.SinaWeiboCommentVO;
import cn.lonsun.weibo.entity.vo.SinaWeiboContentVO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;
import cn.lonsun.weibo.entity.vo.WeiboPagination;
import cn.lonsun.weibo.service.*;
import cn.lonsun.weibo.util.SinaCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weibo4j.*;
import weibo4j.http.ImageItem;
import weibo4j.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-12-9 16:12
 */
@Service
public class SinaWeiboService implements ISinaWeiboService {

    private Long siteId;

    private static GlobalConfig globalConfig = SpringContextHolder.getBean(GlobalConfig.class);

    @Autowired
    private ISinaWeiboContentService sinaWeiboContentService;

    @Autowired
    private ISinaWeiboContentSService sinaWeiboContentSService;

    @Autowired
    private ISinaWeiboContentMService sinaWeiboContentMService;

    @Autowired
    private ISinaWeiboContentFService sinaWeiboContentFService;

    @Autowired
    private ISinaWeiboCommentByMeService sinaWeiboCommentByMeService;

    @Autowired
    private ISinaWeiboCommentToMeService sinaWeiboCommentToMeService;

    @Autowired
    private ISinaWeiboUserInfoService sinaWeiboUserInfoService;

    @Override
    public void syncWeiboDataOnline(Paging paging,Long siteId) throws WeiboException {
        this.siteId = siteId;
        syncWeiboUserOnline(paging,WeiboPageVO.Type.follow.toString());
        syncWeiboContentOnline(paging, WeiboType.all.toString());
        syncWeiboContentOnline(paging, WeiboType.self.toString());
        syncWeiboContentOnline(paging, WeiboType.mention.toString());
        syncWeiboContentOnline(paging, WeiboType.favorite.toString());
        syncWeibCommentOnline(paging, WeiboPageVO.CommentType.byMe.toString());
        syncWeibCommentOnline(paging, WeiboPageVO.CommentType.toMe.toString());
    }

    @Override
    public SinaWeiboUserInfoEO getSelfWeiboInfo() {
        return sinaWeiboUserInfoService.getByUserId(SinaCredential.getUID(LoginPersonUtil.getSiteId()));
    }

    @Override
    public Pagination getPageCurWeibo(WeiboPageVO vo) {
        Pagination page = null;
        String auth = vo.getAuth();
        if(auth.equals(WeiboType.all.toString())) {
            page = sinaWeiboContentService.getPageCurWeibo(vo);
        } else if(auth.equals(WeiboType.self.toString())) {
            page = sinaWeiboContentSService.getPageCurWeibo(vo);
        } else if(auth.equals(WeiboType.mention.toString())) {
            page = sinaWeiboContentMService.getPageCurWeibo(vo);
        } else if(auth.equals(WeiboType.favorite.toString())) {
            page = sinaWeiboContentFService.getPageCurWeibo(vo);
        }
        return page;
    }

    @Override
    public Object removeWeibo(String weiboId) {
        Timeline timeline = new Timeline(SinaCredential.getToken(LoginPersonUtil.getSiteId()));
        try {
            timeline.destroy(weiboId);
            sinaWeiboContentService.delByWeiboId(weiboId);
            sinaWeiboContentSService.delByWeiboId(weiboId);
        } catch (WeiboException e) {
            e.printStackTrace();
            return ResponseData.fail("删除失败!");
        }

        return ResponseData.success("删除成功!");
    }

    @Override
    public void publishWeibo(SinaWeiboContentVO vo) throws Exception {
        this.siteId = LoginPersonUtil.getSiteId();
        Timeline timeline = new Timeline(SinaCredential.getToken(LoginPersonUtil.getSiteId()));
        Status status = null;
        if(AppUtil.isEmpty(vo.getOriginalPic())) {
            status = timeline.updateStatus(vo.getText());
        } else {
            String filepath = null;
            if(vo.getOriginalPic().indexOf(".") != -1){
                filepath = globalConfig.getFileServerNamePath();
            }else{
                filepath = globalConfig.getFileServerPath();
            }
            if(!filepath.contains("http://")) {
                filepath = globalConfig.getCotextUrl() + filepath;
            }
            byte[] picbyte = CatchImage.getImageBytes(filepath);
            ImageItem imageItem = new ImageItem(picbyte);
            status = timeline.uploadStatus(vo.getText(),imageItem);
        }

        sinaWeiboContentService.saveEntity(transferStatus2ContentEO(status));
        sinaWeiboContentSService.saveEntity(transferStatus2ContentSEO(status));
    }

    @Override
    public Object repostWeibo(String weiboId) {
        this.siteId = LoginPersonUtil.getSiteId();
        Timeline timeline = new Timeline(SinaCredential.getToken(LoginPersonUtil.getSiteId()));
        try {
            Status status = timeline.repost(weiboId);
            sinaWeiboContentService.saveEntity(transferStatus2ContentEO(status));
            SinaWeiboContentEO eo = sinaWeiboContentService.getByWeiboId(weiboId);
            if(eo != null) {
                eo.setRepostsCount(eo.getRepostsCount() + 1);
                sinaWeiboContentService.updateEntity(eo);
            }
            sinaWeiboContentSService.saveEntity(transferStatus2ContentSEO(status));
        } catch (WeiboException e) {
            e.printStackTrace();
            return ResponseData.fail("转发失败!");
        }

        return ResponseData.success("转发成功!");
    }

    @Override
    public Object favoriteWeibo(String tagId, String weiboId) {
        this.siteId = LoginPersonUtil.getSiteId();
        Favorite favorite = new Favorite(SinaCredential.getToken(LoginPersonUtil.getSiteId()));
        try {
            Favorites _favorites = favorite.createFavorites(weiboId);
            SinaWeiboContentFEO eo = transferStatus2ContentFEO(_favorites.getStatus());
            eo.setFavorited("true");
            sinaWeiboContentFService.saveOrUpdateEntity(eo);
        } catch (WeiboException e) {
            e.printStackTrace();
            return ResponseData.fail("收藏失败!");
        }

        return ResponseData.success("收藏成功!");
    }

    @Override
    public Object cancelFavoriteWeibo(String weiboId) {
        this.siteId = LoginPersonUtil.getSiteId();
        Favorite favorite = new Favorite(SinaCredential.getToken(LoginPersonUtil.getSiteId()));
        try {
            favorite.destroyFavorites(weiboId);
            sinaWeiboContentFService.delByWeiboId(weiboId);
        } catch (WeiboException e) {
            e.printStackTrace();
            return ResponseData.fail("取消收藏失败!");
        }

        return ResponseData.success("取消收藏成功!");
    }

    @Override
    public Object getPageComments(WeiboPageVO vo) throws WeiboException {
        WeiboPagination page = null;
        Map<String,Object> map = new HashMap<String, Object>();
        if(vo.getCommentType().equals(CommentType.byWeiboId.toString())) {
            page = new WeiboPagination();
            Comments comments = new Comments(SinaCredential.getToken(LoginPersonUtil.getSiteId()));
            Paging paging = new Paging();
            paging.setPage(Integer.parseInt(vo.getPageIndex().toString()) + 1);
            paging.setCount(vo.getPageSize());
            CommentWapper commentWapper = comments.getCommentById(vo.getWeiboId(),paging,vo.getFilterByAuthor());
            List<Comment> list = commentWapper.getComments();
            List<SinaWeiboCommentVO> commentVOs = new ArrayList<SinaWeiboCommentVO>();
            for(Comment comment : list) {
                SinaWeiboCommentVO commentVO = new SinaWeiboCommentVO();
                Status status = comment.getStatus();
                //设置评论的微博信息
                commentVO.setWeiboId(status.getId());
                commentVO.setWeiboContent(status.getText());
                commentVO.setOriginalPic(status.getOriginalPic());
                commentVO.setThumbnailPic(status.getThumbnailPic());
                commentVO.setBmiddlePic(status.getBmiddlePic());

                //设置用户信息
                User user = comment.getUser();
                commentVO.setUid(user.getId());
                commentVO.setScreenName(user.getScreenName());
                commentVO.setName(user.getName());
                commentVO.setLocation(user.getLocation());
                commentVO.setUrl(user.getUrl());
                commentVO.setProfileImageUrl(user.getProfileImageUrl());
                commentVO.setUserDomain(user.getUserDomain());

                //设置评论信息
                commentVO.setCreatedAt(comment.getCreatedAt());
                commentVO.setId(comment.getId());
                commentVO.setIdstr(comment.getIdstr());
                commentVO.setMid(comment.getMid());
                commentVO.setSource(comment.getSource());
                commentVO.setText(comment.getText());
                commentVOs.add(commentVO);
            }
            page.setData(commentVOs);
            map.put("weiboId",vo.getWeiboId());
            page.setMap(map);
            page.setTotal(commentWapper.getTotalNumber() <= 200 ? commentWapper.getTotalNumber() : 200);
            page.setPageIndex(vo.getPageIndex());
            page.setPageSize(vo.getPageSize());
        } else if(vo.getCommentType().equals(CommentType.byMe.toString())) {
            page = sinaWeiboCommentByMeService.getPageCurComment(vo);
        } else if(vo.getCommentType().equals(CommentType.toMe.toString())) {
            page = sinaWeiboCommentToMeService.getPageCurComment(vo);
        }

        SinaWeiboUserInfoEO selfInfo = sinaWeiboUserInfoService.getByUserId(SinaCredential.getUID(LoginPersonUtil.getSiteId()));
        map.put("selfInfo", selfInfo);
        page.setMap(map);
        return page;
    }

    @Override
    public Object commentWeibo(String weiboId, String content) {
        this.siteId = LoginPersonUtil.getSiteId();
        Comments comments = new Comments(SinaCredential.getToken(LoginPersonUtil.getSiteId()));
        try {
            Comment cmt = comments.createComment(content, weiboId);
            SinaWeiboCommentByMeEO eo = transferComment2CommentByMeEO(cmt);
            sinaWeiboCommentByMeService.saveEntity(eo);
        } catch (WeiboException e) {
            e.printStackTrace();
            return ResponseData.fail("评论失败!");
        }
        return ResponseData.success("评论成功!");
    }

    @Override
    public Object commentWeibo(String weiboId, String content, boolean commentOrgin) {
        this.siteId = LoginPersonUtil.getSiteId();
        if(!commentOrgin || AppUtil.isEmpty(commentOrgin)) {
            return commentWeibo(weiboId,content);
        }
        Comments comments = new Comments(SinaCredential.getToken(LoginPersonUtil.getSiteId()));
        try {
            Comment cmt = comments.createComment(content, weiboId, 1);
            SinaWeiboCommentByMeEO eo = transferComment2CommentByMeEO(cmt);
            sinaWeiboCommentByMeService.saveEntity(eo);
        } catch (WeiboException e) {
            e.printStackTrace();
            return ResponseData.fail("评论失败!");
        }
        return ResponseData.success("评论成功!");
    }

    @Override
    public Object replyComment(String cid, String weiboId, String comment) {
        this.siteId = LoginPersonUtil.getSiteId();
        Comments comments = new Comments(SinaCredential.getToken(LoginPersonUtil.getSiteId()));
        try {
            Comment cmt = comments.replyComment(cid, weiboId, comment);
            SinaWeiboCommentByMeEO eo = transferComment2CommentByMeEO(cmt);
            sinaWeiboCommentByMeService.saveEntity(eo);
        } catch (WeiboException e) {
            e.printStackTrace();
            return ResponseData.fail("回复失败!");
        }
        return ResponseData.success("回复成功!");
    }

    @Override
    public Object removeReplyComment(String cid,String commentType) {
        this.siteId = LoginPersonUtil.getSiteId();
        Comments comments = new Comments(SinaCredential.getToken(LoginPersonUtil.getSiteId()));
        try {
            comments.destoryCommentBatch(cid);
            if(commentType.equals(CommentType.byMe.toString())) {
                sinaWeiboCommentByMeService.delByCommentId(cid);
            } else if(commentType.equals(CommentType.toMe.toString())) {
                sinaWeiboCommentToMeService.delByCommentId(cid);
            }
        } catch (WeiboException e) {
            e.printStackTrace();
            return ResponseData.fail("删除失败!");
        }
        return ResponseData.success("删除评论成功!");
    }

    @Override
    public Object getPageFollows(WeiboPageVO vo) {
        return sinaWeiboUserInfoService.getPageCurUser(vo);
    }

    @Override
    public Object cancelFollow(String uid) {
        Friendships friendships = new Friendships(SinaCredential.getToken(LoginPersonUtil.getSiteId()));
        try {
            friendships.destroyFriendshipsById(uid);
        } catch (WeiboException e) {
            e.printStackTrace();
            return ResponseData.fail("取消关注失败!");
        }
        return ResponseData.success("取消关注成功!");
    }

    /**
     * 同步微博内同信息
     * @param paging
     * @param auth
     * @throws WeiboException
     */
    @Override
    public void syncWeiboContentOnline(Paging paging,String auth) throws WeiboException{
        Timeline timeline = new Timeline(SinaCredential.getToken(LoginPersonUtil.getSiteId()));

        if(auth.equals(WeiboType.all.toString())) {
            StatusWapper wapper = timeline.getFriendsTimeline(0, 0, paging);
            List<Status> statuses = wapper.getStatuses();
            List<SinaWeiboContentEO> _contentVOs = new ArrayList<SinaWeiboContentEO>();
            for(Status status : statuses) {
                SinaWeiboContentEO contentVO = transferStatus2ContentEO(status);
                _contentVOs.add(contentVO);
            }
            sinaWeiboContentService.saveOrUpdateEntities(_contentVOs);
        } else if(auth.equals(WeiboType.self.toString())) {

            StatusWapper wapper = timeline.getUserTimelineByUid(SinaCredential.getUID(LoginPersonUtil.getSiteId()), paging, 0, 0);
            List<Status> statuses = wapper.getStatuses();
            List<SinaWeiboContentSEO> _contentVOs = new ArrayList<SinaWeiboContentSEO>();
            for(Status status : statuses) {
                SinaWeiboContentSEO contentVO = transferStatus2ContentSEO(status);
                _contentVOs.add(contentVO);
            }
            sinaWeiboContentSService.saveOrUpdateEntities(_contentVOs);
        } else if(auth.equals(WeiboType.mention.toString())) {
            StatusWapper wapper = timeline.getMentions(paging, 0, 0, 0);
            List<Status> statuses = wapper.getStatuses();
            List<SinaWeiboContentMEO> _contentVOs = new ArrayList<SinaWeiboContentMEO>();
            for(Status status : statuses) {
                SinaWeiboContentMEO _contentVO = transferStatus2ContentMEO(status);
                _contentVOs.add(_contentVO);
            }
            sinaWeiboContentMService.saveOrUpdateEntities(_contentVOs);
        } else if(auth.equals(WeiboType.favorite.toString())) {
            Favorite favorite = new Favorite(SinaCredential.getToken(LoginPersonUtil.getSiteId()));
            List<Status> statuses = new ArrayList<Status>();
            List<SinaWeiboContentFEO> _contentVOs = new ArrayList<SinaWeiboContentFEO>();
            List<Favorites> list = favorite.getFavorites(paging);
            for(Favorites favorites : list) {
                statuses.add(favorites.getStatus());
            }
            for(Status status : statuses) {
                SinaWeiboContentFEO contentVO = transferStatus2ContentFEO(status);
                _contentVOs.add(contentVO);
            }
            sinaWeiboContentFService.saveOrUpdateEntities(_contentVOs);
        }
    }

    @Override
    public void syncWeibCommentOnline(Paging paging,String type) throws WeiboException {
        Comments comments = new Comments(SinaCredential.getToken(LoginPersonUtil.getSiteId()));
        if(type.equals(CommentType.byMe.toString())) {
            CommentWapper wapper = comments.getCommentByMe(paging, 0);
            List<Comment> commentList = wapper.getComments();
            List<SinaWeiboCommentByMeEO> byMeEOs = new ArrayList<SinaWeiboCommentByMeEO>();
            for(Comment comment : commentList) {
                byMeEOs.add(transferComment2CommentByMeEO(comment));
            }
            sinaWeiboCommentByMeService.saveOrUpdateEntities(byMeEOs);
        } else if(type.equals(CommentType.toMe.toString())) {
            CommentWapper wapper = comments.getCommentToMe(paging, 0, 0);
            List<Comment> commentList = wapper.getComments();
            List<SinaWeiboCommentToMeEO> toMeEOs = new ArrayList<SinaWeiboCommentToMeEO>();
            for(Comment comment : commentList) {
                toMeEOs.add(transferComment2CommentToMeEO(comment));
            }
            sinaWeiboCommentToMeService.saveOrUpdateEntities(toMeEOs);
        }
    }

    @Override
    public void syncWeiboUserOnline(Paging paging, String type) throws WeiboException {
        Friendships friendships = new Friendships(SinaCredential.getToken(LoginPersonUtil.getSiteId()));
        Map<String, String> map = new HashMap<String, String>();
        map.put("uid",SinaCredential.getUID(LoginPersonUtil.getSiteId()));
        map.put("count",String.valueOf(paging.getCount()));
        map.put("cursor",String.valueOf(paging.getPage()));
        UserWapper userWapper = friendships.getFriends(map);
        List<User> users = userWapper.getUsers();
        List<SinaWeiboUserInfoEO> sinaUsers = new ArrayList<SinaWeiboUserInfoEO>();
        for(User user : users) {
            SinaWeiboUserInfoEO infoEO = transferUser2WeiboUserEO(user);
            infoEO.setAuth(WeiboPageVO.Type.follow.toString());
            sinaUsers.add(transferUser2WeiboUserEO(user));
        }
        Users self = new Users(SinaCredential.getToken(LoginPersonUtil.getSiteId()));
        User user = self.showUserById(SinaCredential.getUID(LoginPersonUtil.getSiteId()));
        SinaWeiboUserInfoEO selfEO = transferUser2WeiboUserEO(user);
        selfEO.setAuth(WeiboPageVO.Type.self.toString());
        sinaUsers.add(selfEO);
        sinaWeiboUserInfoService.saveOrUpdateEntities(sinaUsers);
    }

    private SinaWeiboContentEO transferStatus2ContentEO(Status status) {
        SinaWeiboContentEO contentVO = new SinaWeiboContentEO();
        User user = status.getUser();
        if(null != user) {
            contentVO.setUserId(user.getId());
            contentVO.setScreenName(user.getScreenName());
            contentVO.setName(user.getName());
            contentVO.setProvince(user.getProvince());
            contentVO.setCity(user.getCity());
            contentVO.setLocation(user.getLocation());
            contentVO.setDescription(user.getDescription());
            contentVO.setUrl(user.getUrl());
            contentVO.setProfileImageUrl(user.getProfileImageUrl());
            contentVO.setUserDomain(user.getUserDomain());
            contentVO.setGender(user.getGender());
            contentVO.setFollowersCount(user.getFollowersCount());
            contentVO.setFriendsCount(user.getFriendsCount());
            contentVO.setStatusesCount(user.getStatusesCount());
            contentVO.setFavouritesCount(user.getFavouritesCount());
            contentVO.setCreatedAtUser(user.getCreatedAt());
            contentVO.setVerified(user.isVerified() ? "true" : "false");
            contentVO.setVerifiedType(user.getVerifiedType());
            contentVO.setAllowAllActMsg(user.isAllowAllActMsg() ? "true" : "false");
            contentVO.setAllowAllComment(user.isAllowAllComment() ? "true" : "false");
            contentVO.setWeihao(user.getWeihao());
        }

        //设置此微博ID
        contentVO.setWeiboId(status.getId());
        contentVO.setMid(status.getMid());
        contentVO.setCreatedAtWeibo(status.getCreatedAt());
        contentVO.setCommentsCount(status.getCommentsCount());
        contentVO.setFavorited(status.isFavorited() ? "true" : "false");
        contentVO.setRepostsCount(status.getRepostsCount());

        contentVO.setAuth(WeiboType.all.toString());
        if(null != status.getRetweetedStatus()) {
            status = status.getRetweetedStatus();
            contentVO.setIsRetweeted("true");
        }
        Source source = status.getSource();

        if(null != source) {
            contentVO.setSourceName(source.getName());
            contentVO.setRelationShip(source.getRelationship());
            contentVO.setSourceUrl(source.getUrl());
        }

        contentVO.setText(status.getText());
        contentVO.setOriginalPic(status.getOriginalPic());
        contentVO.setThumbnailPic(status.getThumbnailPic());
        contentVO.setBmiddlePic(status.getBmiddlePic());
        contentVO.setGeo(status.getGeo());
        contentVO.setSiteId(this.siteId);
        return contentVO;
    }

    private SinaWeiboContentSEO transferStatus2ContentSEO(Status status) {
        SinaWeiboContentSEO contentVO = new SinaWeiboContentSEO();
        User user = status.getUser();
        if(null != user) {
            contentVO.setUserId(user.getId());
            contentVO.setScreenName(user.getScreenName());
            contentVO.setName(user.getName());
            contentVO.setProvince(user.getProvince());
            contentVO.setCity(user.getCity());
            contentVO.setLocation(user.getLocation());
            contentVO.setDescription(user.getDescription());
            contentVO.setUrl(user.getUrl());
            contentVO.setProfileImageUrl(user.getProfileImageUrl());
            contentVO.setUserDomain(user.getUserDomain());
            contentVO.setGender(user.getGender());
            contentVO.setFollowersCount(user.getFollowersCount());
            contentVO.setFriendsCount(user.getFriendsCount());
            contentVO.setStatusesCount(user.getStatusesCount());
            contentVO.setFavouritesCount(user.getFavouritesCount());
            contentVO.setCreatedAtUser(user.getCreatedAt());
            contentVO.setVerified(user.isVerified() ? "true" : "false");
            contentVO.setVerifiedType(user.getVerifiedType());
            contentVO.setAllowAllActMsg(user.isAllowAllActMsg() ? "true" : "false");
            contentVO.setAllowAllComment(user.isAllowAllComment() ? "true" : "false");
            contentVO.setWeihao(user.getWeihao());
        }

        //设置此微博ID
        contentVO.setWeiboId(status.getId());
        contentVO.setMid(status.getMid());
        contentVO.setCreatedAtWeibo(status.getCreatedAt());
        contentVO.setCommentsCount(status.getCommentsCount());
        contentVO.setFavorited(status.isFavorited() ? "true" : "false");
        contentVO.setRepostsCount(status.getRepostsCount());
        contentVO.setAuth(WeiboType.self.toString());

        if(null != status.getRetweetedStatus()) {
            status = status.getRetweetedStatus();
            contentVO.setIsRetweeted("true");
        }
        Source source = status.getSource();

        if(null != source) {
            contentVO.setSourceName(source.getName());
            contentVO.setRelationShip(source.getRelationship());
            contentVO.setSourceUrl(source.getUrl());
        }

        contentVO.setText(status.getText());
        contentVO.setOriginalPic(status.getOriginalPic());
        contentVO.setThumbnailPic(status.getThumbnailPic());
        contentVO.setBmiddlePic(status.getBmiddlePic());
        contentVO.setGeo(status.getGeo());
        contentVO.setSiteId(this.siteId);
        return contentVO;
    }

    private SinaWeiboContentMEO transferStatus2ContentMEO(Status status) {
        SinaWeiboContentMEO contentVO = new SinaWeiboContentMEO();
        User user = status.getUser();
        if(null != user) {
            contentVO.setUserId(user.getId());
            contentVO.setScreenName(user.getScreenName());
            contentVO.setName(user.getName());
            contentVO.setProvince(user.getProvince());
            contentVO.setCity(user.getCity());
            contentVO.setLocation(user.getLocation());
            contentVO.setDescription(user.getDescription());
            contentVO.setUrl(user.getUrl());
            contentVO.setProfileImageUrl(user.getProfileImageUrl());
            contentVO.setUserDomain(user.getUserDomain());
            contentVO.setGender(user.getGender());
            contentVO.setFollowersCount(user.getFollowersCount());
            contentVO.setFriendsCount(user.getFriendsCount());
            contentVO.setStatusesCount(user.getStatusesCount());
            contentVO.setFavouritesCount(user.getFavouritesCount());
            contentVO.setCreatedAtUser(user.getCreatedAt());
            contentVO.setVerified(user.isVerified() ? "true" : "false");
            contentVO.setVerifiedType(user.getVerifiedType());
            contentVO.setAllowAllActMsg(user.isAllowAllActMsg() ? "true" : "false");
            contentVO.setAllowAllComment(user.isAllowAllComment() ? "true" : "false");
            contentVO.setWeihao(user.getWeihao());
        }

        //设置此微博ID
        contentVO.setWeiboId(status.getId());
        contentVO.setMid(status.getMid());
        contentVO.setCreatedAtWeibo(status.getCreatedAt());
        contentVO.setCommentsCount(status.getCommentsCount());
        contentVO.setFavorited(status.isFavorited() ? "true" : "false");
        contentVO.setRepostsCount(status.getRepostsCount());

        contentVO.setAuth(WeiboType.mention.toString());
        if(null != status.getRetweetedStatus()) {
            status = status.getRetweetedStatus();
            contentVO.setIsRetweeted("true");
        }
        Source source = status.getSource();

        if(null != source) {
            contentVO.setSourceName(source.getName());
            contentVO.setRelationShip(source.getRelationship());
            contentVO.setSourceUrl(source.getUrl());
        }

        contentVO.setText(status.getText());
        contentVO.setOriginalPic(status.getOriginalPic());
        contentVO.setThumbnailPic(status.getThumbnailPic());
        contentVO.setBmiddlePic(status.getBmiddlePic());
        contentVO.setGeo(status.getGeo());
        contentVO.setSiteId(this.siteId);
        return contentVO;
    }

    private SinaWeiboContentFEO transferStatus2ContentFEO(Status status) {
        SinaWeiboContentFEO contentVO = new SinaWeiboContentFEO();
        User user = status.getUser();
        if(null != user) {
            contentVO.setUserId(user.getId());
            contentVO.setScreenName(user.getScreenName());
            contentVO.setName(user.getName());
            contentVO.setProvince(user.getProvince());
            contentVO.setCity(user.getCity());
            contentVO.setLocation(user.getLocation());
            contentVO.setDescription(user.getDescription());
            contentVO.setUrl(user.getUrl());
            contentVO.setProfileImageUrl(user.getProfileImageUrl());
            contentVO.setUserDomain(user.getUserDomain());
            contentVO.setGender(user.getGender());
            contentVO.setFollowersCount(user.getFollowersCount());
            contentVO.setFriendsCount(user.getFriendsCount());
            contentVO.setStatusesCount(user.getStatusesCount());
            contentVO.setFavouritesCount(user.getFavouritesCount());
            contentVO.setCreatedAtUser(user.getCreatedAt());
            contentVO.setVerified(user.isVerified() ? "true" : "false");
            contentVO.setVerifiedType(user.getVerifiedType());
            contentVO.setAllowAllActMsg(user.isAllowAllActMsg() ? "true" : "false");
            contentVO.setAllowAllComment(user.isAllowAllComment() ? "true" : "false");
            contentVO.setWeihao(user.getWeihao());
        }

        //设置此微博ID
        contentVO.setWeiboId(status.getId());
        contentVO.setMid(status.getMid());
        contentVO.setCreatedAtWeibo(status.getCreatedAt());
        contentVO.setCommentsCount(status.getCommentsCount());
        contentVO.setFavorited(status.isFavorited() ? "true" : "false");
        contentVO.setRepostsCount(status.getRepostsCount());

        contentVO.setAuth(WeiboType.favorite.toString());
        if(null != status.getRetweetedStatus()) {
            status = status.getRetweetedStatus();
            contentVO.setIsRetweeted("true");
        }
        Source source = status.getSource();

        if(null != source) {
            contentVO.setSourceName(source.getName());
            contentVO.setRelationShip(source.getRelationship());
            contentVO.setSourceUrl(source.getUrl());
        }

        contentVO.setText(status.getText());
        contentVO.setOriginalPic(status.getOriginalPic());
        contentVO.setThumbnailPic(status.getThumbnailPic());
        contentVO.setBmiddlePic(status.getBmiddlePic());
        contentVO.setGeo(status.getGeo());
        contentVO.setSiteId(this.siteId);
        return contentVO;
    }

    private SinaWeiboCommentByMeEO transferComment2CommentByMeEO(Comment comment) {
        SinaWeiboCommentByMeEO eo = new SinaWeiboCommentByMeEO();
        eo.setCommentId(comment.getId() + "");
        eo.setCreatedAtComment(comment.getCreatedAt());
        eo.setCommentText(comment.getText());

        User user = comment.getUser();
        eo.setUserId(user.getId());
        eo.setScreenName(user.getScreenName());
        eo.setName(user.getName());
        eo.setUrl(user.getUrl());
        eo.setProfileImageUrl(user.getProfileImageUrl());
        eo.setUserDomain(user.getUserDomain());
        eo.setGender(user.getGender());

        Comment replycomment = comment.getReplycomment();
        if(null != replycomment) {
            eo.setRepCommentId(replycomment.getId() + "");
            eo.setRepCreatedAtComment(replycomment.getCreatedAt());
            eo.setRepCommentText(replycomment.getText());

            User repUser = comment.getUser();
            eo.setRepUserId(repUser.getId());
            eo.setRepScreenName(repUser.getScreenName());
            eo.setRepName(repUser.getName());
            eo.setRepUrl(repUser.getUrl());
            eo.setRepProfileImageUrl(repUser.getProfileImageUrl());
            eo.setRepUserDomain(repUser.getUserDomain());
            eo.setRepGender(repUser.getGender());
        }

        Status status = comment.getStatus();
        eo.setWeiboId(status.getId());
        eo.setText(status.getText());
        eo.setThumbnailPic(status.getThumbnailPic());
        eo.setBmiddlePic(status.getBmiddlePic());
        eo.setOrginalPic(status.getOriginalPic());
        eo.setIsRetweeted(AppUtil.isEmpty(status.getRetweetedStatus()) ? "false" : "true");
        eo.setSiteId(this.siteId);
        return eo;
    }

    private SinaWeiboCommentToMeEO transferComment2CommentToMeEO(Comment comment) {
        SinaWeiboCommentToMeEO eo = new SinaWeiboCommentToMeEO();
        eo.setCommentId(comment.getId() + "");
        eo.setCreatedAtComment(comment.getCreatedAt());
        eo.setCommentText(comment.getText());

        User user = comment.getUser();
        eo.setUserId(user.getId());
        eo.setScreenName(user.getScreenName());
        eo.setName(user.getName());
        eo.setUrl(user.getUrl());
        eo.setProfileImageUrl(user.getProfileImageUrl());
        eo.setUserDomain(user.getUserDomain());
        eo.setGender(user.getGender());

        Comment replycomment = comment.getReplycomment();
        if(null != replycomment) {
            eo.setRepCommentId(replycomment.getId() + "");
            eo.setRepCreatedAtComment(replycomment.getCreatedAt());
            eo.setRepCommentText(replycomment.getText());

            User repUser = comment.getUser();
            eo.setRepUserId(repUser.getId());
            eo.setRepScreenName(repUser.getScreenName());
            eo.setRepName(repUser.getName());
            eo.setRepUrl(repUser.getUrl());
            eo.setRepProfileImageUrl(repUser.getProfileImageUrl());
            eo.setRepUserDomain(repUser.getUserDomain());
            eo.setRepGender(repUser.getGender());
        }

        Status status = comment.getStatus();
        eo.setWeiboId(status.getId());
        eo.setText(status.getText());
        eo.setThumbnailPic(status.getThumbnailPic());
        eo.setBmiddlePic(status.getBmiddlePic());
        eo.setOrginalPic(status.getOriginalPic());
        eo.setIsRetweeted(AppUtil.isEmpty(status.getRetweetedStatus()) ? "false" : "true");
        eo.setSiteId(this.siteId);
        return eo;
    }

    private SinaWeiboUserInfoEO transferUser2WeiboUserEO(User user) {
        SinaWeiboUserInfoEO eo = new SinaWeiboUserInfoEO();
        eo.setUserId(user.getId());
        eo.setScreenName(user.getScreenName());
        eo.setName(user.getName());
        eo.setProvince(user.getProvince());
        eo.setCity(user.getCity());
        eo.setLocation(user.getLocation());
        eo.setDescription(user.getDescription());
        eo.setUrl(user.getUrl());
        eo.setProfileImageUrl(user.getProfileImageUrl());
        eo.setUserDomain(user.getUserDomain());
        eo.setGender(user.getGender());
        eo.setFollowersCount(user.getFollowersCount());
        eo.setFriendsCount(user.getFriendsCount());
        eo.setStatusesCount(user.getStatusesCount());
        eo.setFavouritesCount(user.getFavouritesCount());
        eo.setCreatedAtUser(user.getCreatedAt());
        eo.setVerified(user.isVerified() ? "true" : "false");
        eo.setVerifiedType(user.getVerifiedType());
        eo.setAllowAllActMsg(user.isAllowAllActMsg() ? "true" : "false");
        eo.setAllowAllComment(user.isAllowAllComment() ? "true" : "false");
        eo.setWeihao(user.getWeihao());
        eo.setSiteId(this.siteId);
        return eo;
    }
}
