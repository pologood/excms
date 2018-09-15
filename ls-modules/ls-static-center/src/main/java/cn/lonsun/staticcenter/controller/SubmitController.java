package cn.lonsun.staticcenter.controller;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.core.util.PropertiesReader;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.staticcenter.eo.ColumnTreeVO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;

/**
 * @author gu.fei
 * @version 2017-05-04 15:57
 */
@Controller
@RequestMapping(value = "/submit")
public class SubmitController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(SubmitController.class);

    private static final String JSONP = "jsonp";
    private static final String STATUS = "status";
    private static final String DESC = "desc";
    private static final String DATA = "data";
    private static final String SUCESS_STATUS = "1";
    private static final String FAIL_STATUS = "0";

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IColumnConfigService columnConfigService;

    /**
     * 提供给第三方的添加文字新闻的接口
     * @param request
     * @param response
     * @param eo
     */
    @RequestMapping("/addNews")
    public void addNews(HttpServletRequest request, HttpServletResponse response, BaseContentEO eo) {
        String dataType = request.getParameter("dataType");
        logger.info(">>>>>>>>>>>>>>>当前客户端的请求类型：" + dataType + ">>>>>>>>>>>>>>>>>>>");
        String clientip = IpUtil.getIpAddr(request);
        logger.info(">>>>>>>>>>>>>>>当前客户端的ip地址：" + clientip + ">>>>>>>>>>>>>>>>>>>");
        String ips = getAccessIps();
        Map<String,String> map = new HashMap<String,String>();

        //只允许jsonp方式请求
        if(null != dataType && !dataType.equals(JSONP)) {
            map.put(STATUS,FAIL_STATUS);
            map.put(DESC,"该接口只支持dataType返回类型为[" + JSONP + "]格式数据!");
            response(request,response,map);
            return;
        }

        //过滤ip权限，只支持文字新闻类型
        if(null != ips && ips.contains(clientip)) {
            logger.info(">>>>>>>>>>>>>>>当前客户端：" + clientip + " 允许访问>>>>>>>>>>>>>>>>>>>");
            if(null != eo.getTypeCode() && eo.getTypeCode().equals(BaseContentEO.TypeCode.articleNews.toString())){
                Long id = baseContentService.saveArticleNews(eo, eo.getArticle(), null, null, null, null);
                if(null != id) {
                    map.put(STATUS,SUCESS_STATUS);
                    map.put(DESC,"添加新闻成功!");
                }
            } else if(null == eo.getTypeCode()) {
                map.put(STATUS,FAIL_STATUS);
                map.put(DESC,"typeCode参数不能为空!");
            } else {
                map.put(STATUS,FAIL_STATUS);
                map.put(DESC,"不能添加非文字新闻!");
            }
        } else {
            map.put(STATUS,FAIL_STATUS);
            map.put(DESC,"当前IP:[" + clientip + "]禁止访问接口!");
        }

        response(request,response,map);
    }

    /**
     * 提供给第三方的添加文字新闻的接口
     * @param request
     * @param response
     * @param siteIds
     */
    @RequestMapping("/getColumnsJson")
    public void getColumnsJson(HttpServletRequest request, HttpServletResponse response, String siteIds) {
        Map<String,String> map = new HashMap<String,String>();
        //站点ID不能为空
        if(null == siteIds) {
            map.put(STATUS,FAIL_STATUS);
            map.put(DESC,"参数：siteIds不能为空!");
            response(request,response,map);
            return;
        }

        String dataType = request.getParameter("dataType");
        logger.info(">>>>>>>>>>>>>>>当前客户端的请求类型：" + dataType + ">>>>>>>>>>>>>>>>>>>");
        String clientip = IpUtil.getIpAddr(request);
        logger.info(">>>>>>>>>>>>>>>当前客户端的ip地址：" + clientip + ">>>>>>>>>>>>>>>>>>>");
        String ips = getAccessIps();

        //只允许jsonp方式请求
        if(null != dataType && !dataType.equals(JSONP)) {
            map.put(STATUS,FAIL_STATUS);
            map.put(DESC,"该接口只支持dataType返回类型为[" + JSONP + "]格式数据!");
            response(request,response,map);
            return;
        }

        //过滤ip权限，只支持文字新闻类型
        if(null != ips && ips.contains(clientip)) {
            logger.info(">>>>>>>>>>>>>>>当前客户端：" + clientip + " 允许访问>>>>>>>>>>>>>>>>>>>");
            map.put(STATUS,SUCESS_STATUS);
            map.put(DESC,"获取栏目成功!");

            Long[] sites = StringUtils.getArrayWithLong(siteIds,",");
            String[] codes = {BaseContentEO.TypeCode.articleNews.toString()};
            List<ColumnMgrEO> trees = new ArrayList<ColumnMgrEO>();
            for(Long siteId : sites) {
                List<ColumnMgrEO> sub = columnConfigService.getColumns(Arrays.asList(codes), siteId);
                if(null != sub) {
                    trees.addAll(sub);
                }
            }
            if(null != trees && !trees.isEmpty()) {
                List<ColumnTreeVO> treeVOs = new ArrayList<ColumnTreeVO>();
                for(ColumnMgrEO eo : trees) {
                    ColumnTreeVO treeVO = new ColumnTreeVO();
                    treeVO.setColumnId(eo.getIndicatorId());
                    treeVO.setParentId(eo.getParentId());
                    treeVO.setIsParent(eo.getIsParent());
                    treeVO.setType(eo.getType());
                    treeVO.setName(eo.getName());
                    treeVO.setSiteId(eo.getSiteId());
                    treeVOs.add(treeVO);
                }
                JSONArray array = JSONArray.fromObject(treeVOs);
                String jsonstr = array.toString();
                map.put(DATA, jsonstr);
            }
        } else {
            map.put(STATUS,FAIL_STATUS);
            map.put(DESC,"当前IP:[" + clientip + "]禁止访问接口!");
        }

        response(request,response,map);
    }

    /**
     * 获取配置的过滤IP
     * @return
     */
    private String getAccessIps() {
        URL url = this.getClass().getResource("/");
        String path = url.getPath() + "ipaccess-conf.properties";
        //每次调取接口都重新过滤ip
        PropertiesReader reader = PropertiesReader.getInstance(path);
        String ips = reader.getValue("add_news_ips");
        logger.info(">>>>>>>>>>>>>>>当前允许访问的ip地址：" + ips + ">>>>>>>>>>>>>>>>>>>");
        return ips;
    }

    /**
     * 返回结果
     * @param request
     * @param response
     * @param map
     */
    private void response(HttpServletRequest request,HttpServletResponse response,Map<String,String> map) {
        try {
            response.setContentType("text/plain");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setCharacterEncoding("gb2312");
            response.setContentType("text/html; charset=gb2312");
            PrintWriter out = response.getWriter();
            JSONObject resultJSON = JSONObject.fromObject(map); //根据需要拼装json
            String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数
            out.println(jsonpCallback + "(" + resultJSON.toString(1,1) + ")");//返回jsonp格式数据
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
