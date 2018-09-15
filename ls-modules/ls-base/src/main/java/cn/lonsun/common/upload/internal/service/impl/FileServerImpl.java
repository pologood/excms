package cn.lonsun.common.upload.internal.service.impl;

import cn.lonsun.common.upload.csource.fastdfs.*;
import cn.lonsun.common.upload.fileManager.FileManager;
import cn.lonsun.common.upload.internal.service.IFileServer;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * 	文件服务器类
 *
 * 	@author
 */
public class FileServerImpl implements IFileServer {

    private TrackerClient trackerClient;
    private TrackerServer trackerServer;
    private StorageServer storageServer;
    private StorageClient1 storageClient;

    private int ts = 0;
    private int ss = 0;

    public TrackerServer getTrackerServer(TrackerClient trackerClient) {
        for (int i = 0; i < 2; i++) {
            try {
                trackerServer = trackerClient.getConnection();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(trackerServer!=null) {
                    break;
                }
            }
        }
//        ts ++;
//        System.out.println("connect time out : "+ts);
//
//        try {
//            trackerServer = trackerClient.getConnection();
//
//            if(ts==3) {
//                throw new BaseRunTimeException(TipsMode.Message.toString(), "连接超时！");
//            }
//        } catch (BaseRunTimeException e) {
//
//            return trackerServer;
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            getTrackerServer(trackerClient);
//        }
//
//        if(trackerServer==null) {
//            getTrackerServer(trackerClient);
//        }
        return trackerServer;
    }

    public StorageServer getStoreStorage(TrackerServer trackerServer) {
        for (int i = 0; i < 2; i++) {
            try {
                storageServer = trackerClient.getStoreStorage(trackerServer);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(storageServer!=null) {
                    break;
                }
            }
        }
//        ss ++;
//        try {
//            storageServer = trackerClient.getStoreStorage(trackerServer);
//            if(ss==3) {
//                throw new BaseRunTimeException(TipsMode.Message.toString(), "连接超时！");
//            }
//        } catch (BaseRunTimeException e) {
//
//            return storageServer;
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            getStoreStorage(trackerServer);
//        }
//
//        if(storageServer==null) {
//            getStoreStorage(trackerServer);
//        }
        return storageServer;
    }

	/**
	 * 
	 */
	public FileServerImpl() {
        try {
            trackerClient = new TrackerClient();
            trackerServer = getTrackerServer(trackerClient);
//            trackerServer = trackerClient.getConnection();
            if (trackerServer == null) {
                System.out.println("getConnection return null");
            }
//            ProtoCommon.activeTest(trackerServer.getSocket());  // 设置长连接
//            storageServer = trackerClient.getStoreStorage(trackerServer);
            storageServer = getStoreStorage(trackerServer);
            if (storageServer == null) {
                System.out.println("getStoreStorage return null");
            }
            storageClient = new StorageClient1(trackerServer, storageServer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Message.toString(), "获取文件服务失败！");
        }
    }

    public FileServerImpl(StorageClient1 storageClient) {
        this.storageClient = storageClient;
    }


    public String uploadMultipartFile(MultipartFile file) throws IOException,
            Exception {
        return uploadMultipartFile(file, getFileExtName(file.getOriginalFilename()));
    }

	public String uploadMultipartFile(MultipartFile file, String suffix) throws IOException,
			Exception {
        return storageClient.upload_file1(null, file.getSize(),
                new UploadStream(file.getInputStream(), file.getSize()), suffix, null);

//		byte[] fileBuff = getFileBuffer(file);
//		return send(fileBuff, suffix);
	}

    public String uploadFile(File file) throws IOException, Exception {
        return uploadFile(file, getFileExtName(file.getName()));
    }

    public String uploadFile(File file, String suffix) throws IOException,
			Exception {
        String path = null;
        FileInputStream fis = new FileInputStream(file);
        path = storageClient.upload_file1(null, file.length(),
                new UploadStream(fis, file.length()), suffix, null);
        fis.close();
        return path;
//		byte[] fileBuff = getFileBuffer(file);
//		return send(fileBuff, suffix);
	}

	public String uploadFile(byte[] fileBuff, String suffix)
			throws IOException, Exception {
		return send(fileBuff, suffix);
	}

    /**
     * 上传文件
     *
     * @param in     二进制数组
     * @param suffix 文件扩展名 ，如(jpg,txt)
     * @return
     */
    @Override
    public String uploadFile(InputStream in, String suffix) throws IOException, Exception {
        String path = null;
        int length = in.available();
        path = storageClient.upload_file1(null, length,new UploadStream(in, length), suffix, null);
        return path;
    }

    private String send(byte[] fileBuff, String fileExtName)
			throws IOException, Exception {
		String upPath = null;
		try {
			upPath = storageClient.upload_file1(fileBuff, fileExtName, null);
		} catch (Exception e) {
			e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Message.toString(), "文件上传失败！");
		}finally {
            if(trackerServer!=null) {
                try {
                    trackerServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    trackerServer = null;
                }
            }
            if(storageServer!=null) {
                try {
                    storageServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    storageServer = null;
                }
            }
        }
		return upPath;
	}

	private String getFileExtName(String name) {
		String extName = null;
		if (name != null && name.contains(".")) {
			extName = name.substring(name.lastIndexOf(".") + 1);
		}
		return extName;
	}

	private byte[] getFileBuffer(File file) {
		byte[] fileByte = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			fileByte = new byte[fis.available()];
			fis.read(fileByte);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileByte;
	}

	@Override
	public boolean deleteFile(String fileId) throws IOException, Exception {
		boolean result=false;
		try {
			result=storageClient.delete_file1(fileId)==0?true:false;
		} catch (Exception e) {
			e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Message.toString(), "文件删除失败！");
		}
		return result;
	}

	@Override
	public byte[] getFileByID(String fileId) throws IOException,Exception{
		byte[] result=null;
		try {
			result=storageClient.download_file1(fileId);
		} catch (Exception e) {
			e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Message.toString(), "文件下载失败！");
		}
		return result;
	}

    @Override
    public int downloadByID(String fileId, BufferedOutputStream out) throws IOException, Exception {
        int result = 0;
        try {
            result=storageClient.download_file1(fileId, new  DownloadStream(out));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Message.toString(), "文件下载失败！");
        }
        return result;
    }

    @Override
    public FileInfo getFileInfoByID(String fileId) throws IOException, Exception {

         try {
             return storageClient.query_file_info1(fileId);
         } catch (IOException e) {
             e.printStackTrace();
         } catch (Exception e) {
             e.printStackTrace();
         }
        return null;
    }

    @Override
    public byte[] getFileByHttp(String path) throws Exception {
        if(null == path || "".equals(path)){
            return new byte[0];
        }
        String downloadIP = FileManager.getDownloadIP();
       // String url = downloadIP + "/" + path;
        String url=path;
        InputStream in = null;
        GetMethod httpGet = null;
        try{
            HttpClient client = new HttpClient();
            httpGet = new GetMethod(url);
            int status = client.executeMethod(httpGet);
            if(HttpStatus.SC_OK == status){
                in = httpGet.getResponseBodyAsStream();
                if(null != in && in.available() > 0){
                    byte[] bytes = inStream2byte(in);
                    return bytes;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null != in){
                in.close();
            }
            if(null != httpGet){
                httpGet.releaseConnection();
            }
        }
        return new byte[0];
    }

    public static final byte[] inStream2byte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 4096)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

}
