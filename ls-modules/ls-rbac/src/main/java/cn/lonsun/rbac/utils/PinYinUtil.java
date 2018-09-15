package cn.lonsun.rbac.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import cn.lonsun.core.base.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 拼音工具
 * 
 * @author xujh
 * 
 */
public class PinYinUtil {

	private static Logger log = LoggerFactory.getLogger(PinYinUtil.class);


	/**
	 * 获取汉字串拼音首字母，英文字符不变
	 * 
	 * @param chinese 汉字串
	 * @return 汉语拼音首字母
	 */
	public static String cn2FirstSpell(String chinese) {
		if(StringUtils.isEmpty(chinese)){
			return chinese;
		}
		//去除特殊字符
		chinese = StringUtils.replaceSpecialCharacter(chinese);
		if(StringUtils.isEmpty(chinese)){
			return chinese;
		}
		StringBuffer pybf = new StringBuffer();
		char[] arr = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > 128) {
				try {
					String[] _t = PinyinHelper.toHanyuPinyinStringArray(arr[i],
							defaultFormat);
					if (_t != null) {
						pybf.append(_t[0].charAt(0));
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pybf.append(arr[i]);
			}
		}
		return pybf.toString().replaceAll("\\W", "").trim();
	}

	/**
	 * 获取拼音的首字母
	 * @param pinyin 拼音
	 * @return 汉语拼音首字母
	 */
	public static String getFirstSpell(String[] pinyin) {
		StringBuilder sb = new StringBuilder(pinyin.length);
		for(String p : pinyin){
			if(StringUtils.isEmpty(p)){
				continue;
			}
			sb.append(p.charAt(0));
		}
		return sb.toString();
	}
	/**
	 * 获取拼音的首字母
	 * @param pinyin 拼音
	 * @return 汉语拼音首字母
	 */
	public static String getFirstSpell(List<String> pinyin) {
		StringBuilder sb = new StringBuilder(pinyin.size());
		for(String p : pinyin){
			if(StringUtils.isEmpty(p)){
				continue;
			}
			sb.append(p.charAt(0));
		}
		return sb.toString();
	}

	/**
	 * 获取拼音的首字母
	 * @param pinyin 拼音
	 * @return 汉语拼音首字母
	 */
	public static String[] getFirstSpellArr(List<String> pinyin) {
		String[] sb = new String[pinyin.size()];
		for(int i = 0; i < pinyin.size();i++){
			if(StringUtils.isEmpty(pinyin.get(i))){
				sb[i] = "";
				continue;
			}
			sb[i] = String.valueOf(pinyin.get(i).charAt(0));
		}
		return sb;
	}

	/**
	 * 获取汉字串拼音，英文字符不变
	 * 
	 * @param chinese
	 *            汉字串
	 * @return 汉语拼音
	 */
	public static String cn2Spell(String chinese) {
		if(StringUtils.isEmpty(chinese)){
			return chinese;
		}
		//去除特殊字符
		chinese = StringUtils.replaceSpecialCharacter(chinese);
		if(StringUtils.isEmpty(chinese)){
			return chinese;
		}
		//去除所有空格
		chinese.replaceAll(" ", "");
		StringBuffer pybf = new StringBuffer();
		char[] arr = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > 128) {
				try {
					pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i],
							defaultFormat)[0]);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				} catch (NullPointerException e){
					log.error("拼音转换失败：{}[{}] -> {}", chinese, i, e.getMessage());
					//如果输入全角的数字等，那么会抛出空指针一场，此处直接忽略处理
				}
			} else {
				pybf.append(arr[i]);
			}
		}
		return pybf.toString();
	}

	/**
	 * 获取汉字串拼音，英文字符不变
	 *
	 * @param chinese
	 *            汉字串
	 * @return 汉语拼音
	 */
	public static List<String>  cn2SpellList(String chinese) {
		if(StringUtils.isEmpty(chinese)){
			return Collections.emptyList();
		}
		//去除特殊字符
		chinese = StringUtils.replaceSpecialCharacter(chinese);
		if(StringUtils.isEmpty(chinese)){
			return Collections.emptyList();
		}
		//去除所有空格
		chinese.replaceAll(" ", "");
		List<String> result = new ArrayList<String>();
		char[] arr = chinese.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > 128) {
				try {
					result.add(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
					log.error("拼音转换失败：{}[{}] -> {}", chinese, i, e.getMessage());
				} catch (NullPointerException e){
					log.error("chinese [ {} ] convert error with char [{}] -> {}", chinese, arr[i], e.getMessage());
					//如果输入全角的数字等，那么会抛出空指针一场，此处直接忽略处理
				}
			} else {
				result.add(String.valueOf(arr[i]));
			}
		}
		return result;
	}
	public static void main(String[] args){
		System.out.println(PinYinUtil.cn2SpellList("信息公开"));

	}
}