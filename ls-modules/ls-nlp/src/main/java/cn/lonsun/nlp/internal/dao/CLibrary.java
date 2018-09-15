package cn.lonsun.nlp.internal.dao;

import cn.lonsun.nlp.utils.ReadConfigUtil;
import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/17 15:15
 */
// 定义接口CLibrary，继承自com.sun.jna.Library
public interface CLibrary extends Library {
    // 定义并初始化接口的静态变量，用于加载NLPIR.dll，路径指向文件NLPIR.dll，但不加后缀dll
    CLibrary Instance = (CLibrary) Native.loadLibrary(ReadConfigUtil.getValue("dll_or_so_path"),CLibrary.class);
    // 初始化函数声明：sDataPath是初始化路径地址，包括核心词库和配置文件的路径，encoding为输入字符的编码格式
    int NLPIR_Init(String sDataPath, int encoding, String sLicenceCode);
    // 分词函数声明：sSrc为待分字符串，bPOSTagged=0表示不进行词性标注，bPOSTagged=1表示进行词性标注
    String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);
    //提取关键词函数声明，nMaxKeyLimie表示最多选取的关键词个数，bWeigheOut表示是否显示关键词的权重值
    String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);
    //从文件中提取关键词函数声明，filePath表示待处理文件路径
    String NLPIR_GetFileKeyWords(String filePath, int nMaxKeyLimit, boolean bWeightOut);
    //词频分析
    String NLPIR_WordFreqStat(String sLine);
    //从字符串中获取新词
    String NLPIR_GetNewWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);
    //从TXT文件中获取新词
    String NLPIR_GetFileNewWords(String sTextFile, int nMaxKeyLimit, boolean bWeightOut);
    //添加单条用户词典
    int NLPIR_AddUserWord(String sWord);
    //删除单条用户词典
    int NLPIR_DelUsrWord(String sWord);
    //从TXT文件中导入用户词典
    int NLPIR_ImportUserDict(String sFilename);
    //将用户词典保存至硬盘
    int NLPIR_SaveTheUsrDic();
    //获取一个字符串的指纹值
    long NLPIR_FingerPrint(String sLine);
    //设置要使用的POS map
    int NLPIR_SetPOSmap(int nPOSmap);
    // 获取最后一个错误信息的函数声明
    String NLPIR_GetLastErrorMsg();
    //返回输入段落的词汇数量
    int NLPIR_GetParagraphProcessAWordCount(String sLine);
    //提取摘要
    //sText-文档内容   fSumRate-文档摘要占原文百分比（范围0~1），使用该参数时，把iSumLen置为0
    //iSumLen-用户限定的摘要长度,使用该参数时，把fSumRate置为0
    //bHtmlTagRemove-是否需要对原文进行Html标签的去除，true表示去除	html标签；false表示不去除
    String DS_SingleDoc(String sText, float fSumRate, int iSumLen, boolean bHtmlTagRemove);
    // 退出函数声明
    void NLPIR_Exit();
}
