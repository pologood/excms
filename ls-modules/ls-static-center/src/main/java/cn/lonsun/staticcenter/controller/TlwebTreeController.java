
package cn.lonsun.staticcenter.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.staticcenter.exproject.tgq.WebInter2016Soap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 铜陵市政府信息公开网 信息公开树信息获取 <br/>
 *
 * @author liuk <br/>
 * @version v1.0 <br/>
 * @date 2016年10月11日 <br/>
 */
@Controller
@RequestMapping("/tlweb/tree")
public class TlwebTreeController extends BaseController {

    private URL url = null;
    private QName qName = null;
    private Service service = null;
    private WebInter2016Soap webInter2016Soap = null;
//    private String unitId = "003101093";//铜官区单位id

    private void initWebService(){
        try {
            url= new URL("http://zwgk.tl.gov.cn/web/WebInter2016.asmx?wsdl");
            qName = new QName("http://tempuri.org/", "WebInter2016");
            service  = Service.create(url,qName);
            webInter2016Soap = service.getPort(WebInter2016Soap.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取组配分类树
     * @param unit_code
     * @return
     */
    @RequestMapping("getColumnTree")
    @ResponseBody
    public Object getColumnTree(String xxfl_code,String unit_code) {
        if(AppUtil.isEmpty(xxfl_code)){//父节点id,不传默认查所有
            xxfl_code = "";//不能为null，否则webservice接口报错
        }
        if(webInter2016Soap==null){
            initWebService();
        }
        String result = webInter2016Soap.getColumnJson(xxfl_code,unit_code);
        List<JSONObject> jsonArray = JSONArray.fromObject(result);
        List<Tree> treelist = new ArrayList<Tree>();

        for(JSONObject obj:jsonArray){
            Tree tree = new Tree();
            String id = obj.getString("xxfl_code");
            tree.setId(id);
            tree.setName(obj.getString("xxfl_name"));
            tree.setOrganId(unit_code);
            tree.setUri("http://zwgk.tl.gov.cn/web/unit.aspx?unit="+unit_code+"&sort=1&xxflid="+id);

            String str1 =id.substring(0,2);
            String str2 =id.substring(2,4);
            String str3 =id.substring(4);

            if(str2.equals("00")){
                tree.setParentId("");
            }else if(!str2.equals("00")&&str3.equals("00")){
                tree.setParentId(str1+"0000");
            }else if(!str3.equals("00")){
                tree.setParentId(str1+str2+"00");
            }
            treelist.add(tree);

        }
        return getObject(treelist);
    }

    /**
     * 获取主题分类树
     * @param unit_code
     * @return
     */
    @RequestMapping("getSubjectTree")
    @ResponseBody
    public Object getSubjectTree(String unit_code) {
        if(webInter2016Soap==null){
            initWebService();
        }
        String result =   webInter2016Soap.getSubjectJson(unit_code);
        List<JSONObject> jsonArray = JSONArray.fromObject(result);
        List<Tree> treelist = new ArrayList<Tree>();

        for(JSONObject obj:jsonArray){
            Tree tree = new Tree();
            String id = obj.getString("subject_id");
            tree.setId(id);
            tree.setName(obj.getString("subject_name"));
            tree.setOrganId(unit_code);
            tree.setParentId("");
            tree.setUri("http://zwgk.tl.gov.cn/web/unit.aspx?unit="+unit_code+"&subjectid="+id);

            treelist.add(tree);
        }
        return getObject(treelist);
    }


    class Tree {
        private String id;
        private String parentId;
        private String organId;
        private String name;
        private String uri;


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOrganId() {
            return organId;
        }

        public void setOrganId(String organId) {
            this.organId = organId;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }
}