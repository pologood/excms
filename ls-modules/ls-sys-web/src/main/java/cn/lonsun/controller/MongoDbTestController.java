package cn.lonsun.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.entity.MongoDbTest;
import cn.lonsun.mongodb.service.MongoDbTestService;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;


@Controller
@RequestMapping(value = "mongo")
public class MongoDbTestController {

	@Autowired
	private  MongoDbTestService mongoDbTestService;
	
	@Autowired
	protected MongoTemplate mongoTemplate;
	
	@Autowired
    private IMongoDbFileServer mongoDbFileServer;
	
	@RequestMapping("/upload")  
	public void upload(HttpServletRequest request ,HttpServletResponse response){
		try{
			List<File> files= new ArrayList<File>();
			files.add(new File("D:\\啊啊啊1.docx"));
			files.add(new File("D:\\啊啊啊2.docx"));
			
			GridFS gridFS= new GridFS(mongoTemplate.getDb(),"fs");
			GridFSFile file = null;
			for(int i=0;i<files.size();i++){
				File f  = files.get(0);
				if (f.isFile()) {
					file = gridFS.createFile(f);
					file.put("filename", "啊啊啊"+i+".docx");
					file.put("caseId", 12);
					file.put("createDate", new Date());
					file.put("contentType", "docx");
					file.save();
					System.out.println(file.getId());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	@RequestMapping("/list")  
	private void showList(HttpServletRequest request ,HttpServletResponse response){
		try{
			GridFS gridFS= new GridFS(mongoTemplate.getDb());
			
			
			DBObject query=new BasicDBObject("caseId", 12);
			List<GridFSDBFile> gridFSDBFileList = gridFS.find(query);
			for(GridFSDBFile gf:gridFSDBFileList){
				System.out.println(gf.get("caseId"));
				System.out.println(gf.getFilename());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 文件删除
	 * @param request
	 * @param response
	 */
	@RequestMapping("/delete1")  
	private void delete(HttpServletRequest request ,HttpServletResponse response){
		try{
			GridFS gridFS= new GridFS(mongoTemplate.getDb());
			DBObject query=new BasicDBObject("caseId", 12);
			gridFS.remove(query);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	@RequestMapping("/save")  
	public void save(){  
		MongoDbTest mongo = new  MongoDbTest();
		mongo.setId("1111");
		mongo.setName("张益达1");
		mongo.setTime(new Date());
		mongoDbTestService.saveMongoDbTest(mongo);  

	}  

	@RequestMapping("/find")  
	public void find(){  
		MongoDbTest mongo=mongoDbTestService.findMongoDbTest("张益达1");  
		System.out.println(mongo.getName());
	}  
	
	@RequestMapping("/delete")  
	public void delete(){  
		mongoDbTestService.deleteMongoDbTest("张益达");
	}  
	
	/**
	 * 文件上传
	 * @param request
	 * @param response
	 */
	@RequestMapping("/downLoad")  
	public void downLoad(HttpServletRequest request ,HttpServletResponse response){
		try{
			mongoDbFileServer.downloadFile(response, "562de7f98ee3e646ee75ee23", null);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
