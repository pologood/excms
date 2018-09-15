package cn.lonsun.developer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * 
 * 开发模板<br/>
 * 根据数据表创建Entity实体类,支持自动注解
 * @Author taodp
 * @Time 2014年8月16日 上午11:48:12
 */
public class CreateEntity {
  //路径
    private String basePath = System.getProperty("user.dir") + "/src/main/java/";
    //字符名称
    private String charset = "UTF-8";
    //包名称
    private String basePackage = "cn.lonsun";  
    //名路径
    private String packageDir = "";
    //类名称
    private String className;
    //名称
    private String tableName;
    
    //
    private String packageName;
    //写入内容
    private StringBuffer con = new StringBuffer();
    private StringBuffer setAndGetCon = new StringBuffer();
   
    
//    private CreateEntity(){}
    
    public CreateEntity(String packageName,String basePackage){
        
        this.packageName = packageName;
        this.basePackage =  basePackage;
    }
    /**
     * 
     * 批量创建
     * @author
     * @param tableNames
     * @param isMocks
     */
    public void create(String[] tableNames,boolean[] isMocks){
        for (int k=0;k<tableNames.length;k++) {
            create(tableNames[k], isMocks[k]);
        }
    }
    /**
     * 创建EntityEO
     * */
    public void create(String tableName){
        create(tableName, false);
    }
    /***
     * 创建EntityEO
     *
     * @author
     * @param tableName --表物理名称
     * @param isMock -- 是否支持模拟操作
     */
    public void create(String tableName,boolean isMock){
        this.tableName = tableName.toUpperCase();
        
        this.className = getClassName(tableName);
        
        JdbcUtil jdbcUtil = new JdbcUtil();
        List<DsColumn> cols = jdbcUtil.getOracleDsColumnList(tableName);
        
        con.delete(0, con.length());
        setAndGetCon.delete(0, setAndGetCon.length());
        
        //import
        getStart(packageName,cols,isMock);
        
      //创建包目录
        packageDir = (basePath + basePackage + "/" + packageName).replaceAll("\\.", "/");
        File dir = new File(packageDir);
        if(!dir.exists()){
            dir.mkdirs();
        }
        
        //创建EO文件
        System.out.println("物理字段总计:" + cols.size() + "个");
        int idx = 0;
        //忽略字段
        String ignoreStr = "domainid,create_user_id,create_date,update_user_id,update_date,create_organ_id";
        for(DsColumn col:cols){
            //超类公共属性，跳过不添加
            if(ignoreStr.indexOf(col.getName().toLowerCase()) !=-1) continue;
          
            idx++;
            con.append(getProp(col.getName(), col.getRemark(), col.getType(), col.isIsprimarykey()));
            set(col.getName(), col.getType());
            get(col.getName(), col.getType());
        }
        System.out.println("实际可以映射总计:" + idx + "个（忽略" + ignoreStr + "的映射）");
        con.append(setAndGetCon);
        //end
        getEnd();
        //生成文件
        
        make(packageName);
    }

    

    /***
     * 类名称
     * @param tableName
     * @return
     */
    private String getClassName(String tableName){
        String clsName = tableName;
        //如果有下划线，从下划线后一位算起
        if(tableName.indexOf("_") !=-1){
            clsName = tableName.substring(tableName.indexOf("_"), tableName.length());
        }else if(tableName.indexOf("T") !=-1){//单独以T打头
            clsName = tableName.substring(tableName.indexOf("T"), tableName.length());
        }
        //全部转为小写
        clsName = clsName.toLowerCase();
        //首字段大写//如果有多个_下划线时，下划线后首字母大写
        String[] clsNameString = clsName.split("_");
        clsName = "";
        for (int i = 0; i < clsNameString.length; i++) {
            if("".equals(clsNameString[i])) continue;
            //clsName = (new StringBuilder()).append(Character.toLowerCase(clsNameString[i].charAt(0))).append(clsNameString[i].substring(1)).toString();
            clsName +=  clsNameString[i].substring(0, 1).toUpperCase() + clsNameString[i].replaceFirst("\\w", "");
        }
        return clsName + "EO";
    }
    /**
     * 生成字段属性
     * @param column
     * @param remark
     * @return
     */
    private String getProp(String column,String remark,String type,boolean isprimaryKey){
        String prop = "";
        prop += "\r\n";
        if(null !=remark && !"".equals(remark)){
            prop += "\t/** " + remark + " */";
            prop += "\r\n";
        }
        if(isprimaryKey){           
            prop += "\t@Id @GeneratedValue(strategy=GenerationType.AUTO)";
            prop += "\r\n";
        }        
        prop +="\t@Column(name=\"" + column + "\")";
        prop += "\r\n";
        prop += "\t" + getPropPre(type) + " " +getPropName(column) + ";";
        prop += "\r\n";     
        return prop;
    }
    
    /***
     * 转为规范属性名称
     * @param column
     * @return
     */
    private String formatPropName(String column){
        column = column.toLowerCase();
        String[] clsNameString = column.split("_");
        column = "";
        for (int i = 0; i < clsNameString.length; i++) {
            if("".equals(clsNameString[i])) continue;
            //clsName = (new StringBuilder()).append(Character.toLowerCase(clsNameString[i].charAt(0))).append(clsNameString[i].substring(1)).toString();
            column +=  clsNameString[i].substring(0, 1).toUpperCase() + clsNameString[i].replaceFirst("\\w", "");
        }
        return column;
    }
    /***
     * 获取属性名称
     * @param column
     * @return
     */
    private String getPropName(String column){
        String _col = formatPropName(column);
        return _col.substring(0, 1).toLowerCase() + _col.substring(1);
    }
    
    /***
     * 获取方法名称
     * @param column
     * @return
     */
    private String getMethodName(String column){
        String _col = formatPropName(column);
        return _col.substring(0, 1).toUpperCase() + _col.substring(1);
    }
    /***
     * 获取属性类型
     * @param type
     * @return
     */
    private String getPropPre(String type){
        return "private " + getType(type);
    }
    private String getType(String type){
        String _pre="";
        if(type.toLowerCase().equals("number") || type.toLowerCase().equals("long")){
            _pre += "Long";
        }else if(type.toLowerCase().equals("integer") || type.toLowerCase().equals("int")){
            _pre += "Integer";
        }else if(type.toLowerCase().equals("date") || type.toLowerCase().indexOf("timestamp") !=-1){
            _pre += "Date";
        }else if(type.toLowerCase().equals("blob")){
            _pre += "byte[]";
        }else{
            _pre += "String";
        }
        return _pre;
    }
    /***
     * 处理set方法
     * @param column
     * @param type
     */
    private void set(String column,String type){
        setAndGetCon.append("\r\n");
        setAndGetCon.append("\t");
        String _name = getMethodName(column);
        String _type = getType(type);
        setAndGetCon.append("public " + "void ");
        
        setAndGetCon.append("set" + _name + "(" + _type + " " + _name + "){");
        setAndGetCon.append("\r\n");
        setAndGetCon.append("\t\t" + "this." + getPropName(column) + " = " + _name + ";");
        setAndGetCon.append("\r\n");
        setAndGetCon.append("\t}");
        setAndGetCon.append("\r\n");
        
    }
    /***
     * 处理get
     * @param column
     * @param type
     */
    private void get(String column,String type){
        //BLOB        
        if(type.toLowerCase().equals("blob")){
            setAndGetCon.append("\r\n");
            setAndGetCon.append("\t");
            setAndGetCon.append("@Lob");
            setAndGetCon.append("\r\n");
            setAndGetCon.append("\t");
            setAndGetCon.append("@Basic(fetch=FetchType.LAZY)");
            setAndGetCon.append("\r\n");
            setAndGetCon.append("\t");
            setAndGetCon.append("@Column(name=\"" + column + "\", columnDefinition=\"BLOB\",nullable=true)");
            setAndGetCon.append("\r\n");  
            System.out.println(setAndGetCon);
        }else if(type.toLowerCase().equals("clob")){
            setAndGetCon.append("\r\n");
            setAndGetCon.append("\t");
            setAndGetCon.append("@Lob");
            setAndGetCon.append("\r\n");
            setAndGetCon.append("\t");
            setAndGetCon.append("@Basic(fetch=FetchType.EAGER)");
            setAndGetCon.append("\r\n");
            setAndGetCon.append("\t");
            setAndGetCon.append("@Column(name=\"" + column + "\", columnDefinition=\"CLOB\",nullable=true)");
            setAndGetCon.append("\r\n");          
        }else{
            setAndGetCon.append("\r\n");           
        }
        setAndGetCon.append("\t");
        String _name = getMethodName(column);
        String _type = getType(type);
        setAndGetCon.append("public " + _type + " ");
       
        setAndGetCon.append("get" + _name + "("  + "" +  "){");
        setAndGetCon.append("\r\n");
        
        setAndGetCon.append("\t\t return " + "" + getPropName(column) + ";");
        setAndGetCon.append("\r\n");
        setAndGetCon.append("\t}");
        setAndGetCon.append("\r\n");
        
    }
    /**
     * 生成文件
     */
    private void make(String packageName){
        System.out.println("生成目录：" + packageDir + "/" + this.className +".java");
        File file = new File(packageDir,this.className+".java");
          charset = (null == charset || "".equals(charset)?"UTF-8":charset); //文件编码  ASCII  UTF-16   ISO-8859-1
          OutputStreamWriter os = null;
          try {
              file = new File(file.getAbsolutePath());
              os = new OutputStreamWriter(new FileOutputStream(file), charset);
              os.write(con.toString());
              os.flush();
              os.close();  
          }
          catch (Exception e) {
              e.printStackTrace();
          }
    }
    
    /***
     * 复杂类型，需要导入其它包
     * @param cols
     */
    private void getExtImport(List<DsColumn> cols){
        boolean hasDate = false;
        boolean hasClob = false;
        boolean hasBlob = false;
        
        for(DsColumn col:cols){
            if(col.getType().toLowerCase().equals("blob")){
                hasBlob = true;                
            }else if(col.getType().toLowerCase().equals("clob")){
                hasClob = true;
            }else if(col.getType().toLowerCase().equals("date") || col.getType().toLowerCase().indexOf("timestamp") !=-1){
                hasDate = true;
            }
            
        }
        if(hasDate){
            con.append("import java.util.Date;").append("\r\n");
        }
        if(hasBlob){
            con.append("import javax.persistence.Lob;").append("\r\n");
            con.append("import javax.persistence.Basic;").append("\r\n");
            con.append("import javax.persistence.FetchType;").append("\r\n");
        }
        if(hasClob){
            con.append("import javax.persistence.Lob;").append("\r\n");
            con.append("import javax.persistence.Basic;").append("\r\n");
            con.append("import javax.persistence.FetchType;").append("\r\n");
        }
           
        
        
    }
    
    private void getStart(String packageName,List<DsColumn> cols,boolean isMock){
        con.append("package " + (basePackage + "." + packageName).replaceAll("/", "\\."));
        con.append(";");
        con.append("\r\n").append("\r\n");        
        con.append("import java.io.Serializable;").append("\r\n");
        con.append("import javax.persistence.Column;").append("\r\n");
        con.append("import javax.persistence.Entity;").append("\r\n");
        con.append("import javax.persistence.GeneratedValue;").append("\r\n");
        con.append("import javax.persistence.GenerationType;").append("\r\n");
        con.append("import javax.persistence.Id;").append("\r\n");
        con.append("import javax.persistence.Table;").append("\r\n");
        if(isMock){
            con.append("import cn.lonsun.core.base.entity.AMockEntity;").append("\r\n").append("\r\n");
        }else{
            con.append("import cn.lonsun.core.base.entity.ABaseEntity;").append("\r\n").append("\r\n");
        }
           
        //import ext
        getExtImport(cols);
        con.append("@Entity").append("\r\n");
        con.append("@Table(name=\"" + tableName + "\")").append("\r\n");
        if(isMock){
            con.append("public class " +  className + " extends AMockEntity implements Serializable").append("{").append("\r\n");
        }else{
            con.append("public class " +  className + " extends ABaseEntity implements Serializable").append("{").append("\r\n");  
        }
        con.append("\r\n");
        con.append("\t" + "private static final long serialVersionUID = 1L;").append("\r\n");       
        
        
    }
    
    private void getEnd(){
        con.append("\r\n").append("}").append("\r\n");
    }
    
}
