package cn.lonsun;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;
import com.mongodb.*;
import org.apache.commons.lang3.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 更新信息公开分类ids
 * Created by zhangchao on 2016/10/10.
 */
public class ImportDatatest {

    private MongoClient mongoClient = null;// 建立连接
    private DB get_db_credit = null;

    private Map<String, Long> classRelMap;

    private String mongo_siteId = "53fee67c9a05c2383dc182d2";// 滁州信息公开网站点id


    private String mongo_top_organId = "53fee67d9a05c2383dc182d9";

    private JdbcUtils jdbcUtils;

    private int c = 1 ;


    @Test
    public void updateXXGKFiles(){
        JdbcUtils jdbc =  getOracleDB();
        DBCollection attachC = getDB().getCollection("site_attach");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("removed", Boolean.FALSE);
        map.put("site_id", mongo_siteId);
        DBObject ref = new BasicDBObject(map);
        DBCursor cursor = attachC.find(ref);
        List<DBObject> objects = cursor.toArray();
        System.out.println(objects.size());
        String sql2="";
        DBCollection content = getDB().getCollection("openness_content");
        if(objects != null && objects.size() > 0){
            for (int k=0;k<objects.size();k++) {
                DBObject d = objects.get(k);
                String module_id = AppUtil.isEmpty(d.get("module_id")) ? "" : d.get("module_id").toString();
                String real_name = AppUtil.isEmpty(d.get("real_name")) ? "" : d.get("real_name").toString();
                String saved_name = AppUtil.isEmpty(d.get("saved_name")) ? "" : d.get("saved_name").toString();
                String file_size = AppUtil.isEmpty(d.get("file_size")) ? "" : d.get("file_size").toString();
                System.out.println("_real_name:"+real_name+"_saved_name:"+saved_name+"_file_size:"+file_size);
                if(!StringUtils.isEmpty(module_id)){
                    DBObject d1 = content.findOne(new ObjectId(module_id));
                    if (null != d1) {// 附件
                        String indexNUm = AppUtil.isEmpty(d1.get("serial_number")) ? "" : d1.get("serial_number").toString();
                        System.out.println("_indexNUm:" + indexNUm);
                        if (!StringUtils.isEmpty(indexNUm)) {
                            try {
                                sql2 = "update CMS_BASE_CONTENT t set t.attach_saved_name = '"+saved_name+"',t.attach_real_name = '"+real_name+"',t.attach_size = '"+file_size+"' where t.id" +
                                        " in (select c.content_id from CMS_PUBLIC_CONTENT c where c.index_num = '"+indexNUm+"')";
                                jdbcUtils.executeUpdate(sql2, null);
                                System.out.println("已更新" + c);
                                c++;
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    //    @Test
    public void updateXXGKCLassIds(){
        JdbcUtils jdbc =  getOracleDB();
        String sql="select id,parent_id,code_ from CMS_PUBLIC_CLASS  where record_status = 'Normal'";
        List<Object> list = jdbcUtils.excuteQuery(sql, null);
        Map<String,ClassVO> classMongoMap = new HashMap<String, ClassVO>();
        for (int i = 0, l = list.size(); i < l; i++) {
            Map<String, Object> map = (HashMap<String, Object>) list.get(i);
            BigDecimal id = (BigDecimal) map.get("ID");
            BigDecimal parent_id = (BigDecimal) map.get("PARENT_ID");
            String code_ = (String) map.get("CODE_");
            ClassVO vo = new ClassVO();
            vo.setId(id.longValue());
            vo.setParentId(parent_id.longValue());
            vo.setMongoId(code_);
            classMongoMap.put(code_,vo);
        }
        List<MgTree>  organs= getMgOrgans();
        Map<String, Object> map = null;
        String sql2 = "";
        DBCollection collectioncontent = getDB().getCollection("openness_content");
        for(int j=0;j<organs.size();j++){
            System.out.println("当前导入单位到----------------------------"+j);
            map = new HashMap<String, Object>();
            map.clear();
            map.put("site_id", mongo_siteId);
            map.put("removed", Boolean.FALSE);
            map.put("branch_id", organs.get(j).getId());
            DBObject ref = new BasicDBObject(map);
            DBCursor cursor = collectioncontent.find(ref);
            List<DBObject> objects = cursor.toArray();
            System.out.println(objects.size());
            if(objects != null && objects.size() > 0){
                for (int k=0;k<objects.size();k++) {
                    DBObject d = objects.get(k);
                    System.out.println("-"+k);
                    try{
                        String classIds="";
                        String parentClassIds="";
                        Object obj = d.get("topic_id");
                        if (null != obj) {
                            if (obj instanceof BasicDBObject) {
                                BasicDBObject o = (BasicDBObject) obj;
                                if (classMongoMap.containsKey(o.toString())) {
                                    ClassVO e = classMongoMap.get(o.toString());
                                    classIds = e.getId() + "";
                                    parentClassIds = e.getParentId() + "";
                                }
                            } else if (obj instanceof BasicDBList) {
                                BasicDBList o = (BasicDBList) obj;
                                List<Long> arr = new ArrayList<Long>();
                                List<Long> parentIds = new ArrayList<Long>();
                                for (int i = 0; i < o.size(); i++) {
                                    String str = o.get(i).toString();
                                    if (classMongoMap.containsKey(str)) {
                                        ClassVO e = classMongoMap.get(str);
                                        arr.add(e.getId());
                                        if (!parentIds.contains(e.getParentId())) {
                                            parentIds.add(e.getParentId());
                                        }
                                    }
                                }
                                classIds = StringUtils.join(arr.toArray(), ",");
                                parentClassIds = StringUtils.join(parentIds.toArray(), ",");
                            }
                            String indexNUm = AppUtil.isEmpty(d.get("serial_number")) ? "" : d.get("serial_number").toString();
                            if(!StringUtils.isEmpty(indexNUm) && !StringUtils.isEmpty(classIds)){
                                sql2 = "update CMS_PUBLIC_CONTENT t set t.class_ids='"+classIds+"',t.parent_class_ids='"+parentClassIds+"' where t.index_num = '"+indexNUm+"'";
                                jdbcUtils.executeUpdate(sql2,null);
                                System.out.println("已更新"+c);
                                c++;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    class ClassVO {
        private Long id;
        private Long parentId;
        private String mongoId;

        public String getMongoId() {
            return mongoId;
        }

        public void setMongoId(String mongoId) {
            this.mongoId = mongoId;
        }

        public Long getParentId() {
            return parentId;
        }

        public void setParentId(Long parentId) {
            this.parentId = parentId;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    /**
     * 构建ex6tree
     */
    class MgTree {
        private String id;
        private String parentId;
        private String ssSiteId;
        private String branchId;//MG单位id
        private String count;//MG单位id
        private String name;
        private String type;
        private String code;// 编码
        private Boolean isParent = Boolean.FALSE;// 是否是父栏目
        private String ssPath;
        private Long ssNo;
        private String ssBmbh;

        public String getSsBmbh() {
            return ssBmbh;
        }

        public String getSsSiteId() {
            return ssSiteId;
        }

        public void setSsSiteId(String ssSiteId) {
            this.ssSiteId = ssSiteId;
        }

        public void setSsBmbh(String ssBmbh) {
            this.ssBmbh = ssBmbh;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getSsNo() {
            return ssNo;
        }

        public void setSsNo(Long ssNo) {
            this.ssNo = ssNo;
        }

        public String getSsPath() {
            return ssPath;
        }

        public void setSsPath(String ssPath) {
            this.ssPath = ssPath;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Boolean getIsParent() {
            return isParent;
        }

        public void setIsParent(Boolean parent) {
            isParent = parent;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getBranchId() {
            return branchId;
        }

        public void setBranchId(String branchId) {
            this.branchId = branchId;
        }
    }
    private Map<String, Object> getParamsMap(){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("site_id", mongo_siteId);
        map.put("status", Boolean.TRUE);
        map.put("removed",Boolean.FALSE);
        return map;
    }

    private List<MgTree> getMgOrgans() {
        List<MgTree> listTree = new ArrayList<MgTree>();
        //        BasicDBList values = new BasicDBList();
//        values.add(new BasicDBObject("parent_id", "53e9a8509a05c275776a17ff"));
//        values.add(new BasicDBObject("parent_id", "53e9a8789a05c2dc78ac81f7"));
//        values.add(new BasicDBObject("parent_id", "53e9a8979a05c27577967e47"));
//        values.add(new BasicDBObject("parent_id", "54226df15d21fb3452cbad8b"));
//        ref.put("$or", values);
        DBObject ref = new  BasicDBObject();
        ref.putAll(getParamsMap());
        ref.put("parent_id",mongo_top_organId);
        DBCollection collection = getDB().getCollection("site_branch");
        DBCursor cursor = collection.find(ref).sort(new BasicDBObject("sort",-1));
        List<DBObject> list = cursor.toArray();
        if(list != null && list .size() >0){
            for (DBObject d : list) {
                String _id = d.get("_id").toString();
                MgTree tree= new MgTree();
                tree.setId(_id);
                tree.setParentId(d.get("parent_id").toString());
                tree.setName(d.get("name").toString());

                //查询子节点
                DBObject refChild = new  BasicDBObject();
                refChild.putAll(getParamsMap());
                refChild.put("parent_id",_id);
                DBCursor cursorChild = collection.find(refChild).sort(new BasicDBObject("sort",-1));
                List<DBObject> listChild = cursorChild.toArray();
                if(listChild != null && listChild .size() >0){
                    tree.setIsParent(true);
                    for (DBObject child : listChild) {
                        MgTree treeChild= new MgTree();
                        treeChild.setId(child.get("_id").toString());
                        treeChild.setParentId(child.get("parent_id").toString());
                        treeChild.setName(child.get("name").toString());
                        treeChild.setIsParent(true);
                        listTree.add(treeChild);
                    }
                }
                if(!tree.getIsParent()){
                    tree.setIsParent(true);
                    listTree.add(tree);
                }
            }
        }
        return listTree;
    }

    private DB getDB() {
        if (null == get_db_credit) {
            try {
                //内网
//                mongoClient = new MongoClient("10.0.0.227", 27017);
                mongoClient = new MongoClient("61.191.61.136", 22717);// 服务器地址
//                mongoClient = new MongoClient("192.168.0.12", 27017);
                get_db_credit = mongoClient.getDB("mgold");// 数据库名
            } catch (Exception e) {
                throw new BaseRunTimeException("数据源获取错误！");
            }
        }
        return get_db_credit;
    }

    public JdbcUtils getOracleDB() {
        if(jdbcUtils == null){
            jdbcUtils = JdbcUtils.getInstance();
            jdbcUtils.setDRIVER("oracle.jdbc.OracleDriver");
            jdbcUtils.setURLSTR("jdbc:oracle:thin:@60.175.156.2:26021:MINGGUANGEX8");
            jdbcUtils.setUSERNAME("MINGGUANGEX8");
            jdbcUtils.setUSERPASSWORD("12345678");
        }
        return jdbcUtils;
    }
}
