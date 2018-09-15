//package cn.lonsun.rbac.cache;
//
//import net.sf.ehcache.Element;
//import net.sf.ehcache.search.attribute.AttributeExtractor;
//import net.sf.ehcache.search.attribute.AttributeExtractorException;
//import cn.lonsun.webservice.vo.rbac.SimplePersonVO;
//
///**
// * SimplePersonVO缓存查询
// * 
// * @author xujh
// * @version 1.0 2015年4月4日
// * 
// */
//public class SimplePersonFullPyExtractor implements AttributeExtractor {
//
//	/**
//	 * serialVersionUID
//	 */
//	private static final long serialVersionUID = 4468992266092696147L;
//
//	@Override
//	public Object attributeFor(Element element, String attributeName)
//			throws AttributeExtractorException {
//		SimplePersonVO vo = (SimplePersonVO) element.getObjectValue();
//		return vo.getFullPy();
//	}
//
//}
