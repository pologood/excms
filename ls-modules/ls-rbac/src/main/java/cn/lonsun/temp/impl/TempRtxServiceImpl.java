package cn.lonsun.temp.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.ldap.vo.OrganNodeVO;
import cn.lonsun.ldap.vo.PersonNodeVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.facade.ILdapFacadeService;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.temp.ITempRtxService;

@Service("tempRtxService")
public class TempRtxServiceImpl implements ITempRtxService{
	@Autowired
	private ILdapFacadeService ldapFacadeService;
//	@Autowired
//	private IRTXClient rtxClient;
//
//	
//	@Autowired
//	private IBigAntClient bigAntClient;

	@Autowired
	private IOrganService organService;

	int personCount = 0;
	int organCount = 0;
	String nameCode="";
	@Override
	public void addRoot(Long organId,String organName,int sortNum){
//		rtxClient.saveDept(organId.longValue(),organName,null,sortNum);
	}
	@Override
	public void update(Long organId,Map<String,String> mapDomain){
		List<Object> objects = ldapFacadeService.getSubNodes(organId);
		//if(organCount>20)return;
		if(objects!=null&&objects.size()>0){
			int i=1;
			for(Object object:objects){
				if(object instanceof OrganNodeVO){
					organCount++;
					OrganNodeVO organ = (OrganNodeVO)object;
					this.getallname(organ.getParentId(),"");
//					//name code编码
//					System.out.println("单位:"+organ.getName()+"，编码是："+nameCode);
					/*if(organ.getParentId()==null){
						nameCode=organ.getName();
					}else{
						nameCode=nameCode+"\\"+organ.getName();
					}*/
					//System.out.println("organ.getParentId()===" + organ.getParentId() + "-----------" + nameCode);
					saveDept(organ,nameCode);
//					bigAntClient.addDept(nameCode, organ.getSortNum().intValue(), null);
					update(organ.getOrganId(),mapDomain);
					System.out.println("已同步"+organCount+"个组织");
				}else{
					personCount++;
					PersonNodeVO person = (PersonNodeVO)object;
					if(i==1){
						this.getallname(person.getOrganId(),"");
						i++;
					}
					/*if(organ.getParentId()==null){
						nameCode=organ.getName();
					}else{
						nameCode=nameCode+"\\"+organ.getName();
					}*/
					//name code编码
					System.out.println("用户:"+person.getName()+"，的编码是："+nameCode);

					//					//					处理用户密码
					//					if(!StringUtils.isEmpty(person.getUid())){
					//						String password=mapDomain.get(person.getUid());
					//						if(password!=null){
					//							person.setPassword(password);
					//						}else{
					//							person.setPassword("1234");
					//						}
					//						System.out.println("用户密码："+person.getPassword());
					//					}
					// 非兼职用户才添加
					if(!person.getIsPluralistic()){
						syncUser4Contact(person,nameCode);
						System.out.println("已同步"+personCount+"个用户");
					}
				}
			}
		}

	}

	private void getallname(Long parentId,String name) {
		if(parentId!=null){
			OrganEO o=organService.getEntity(OrganEO.class, parentId);
			if(StringUtils.isEmpty(name)){
				name=o.getName();
			}else{
				name=o.getName()+"\\"+name;
			}
			getallname(o.getParentId(),name);
		}else{
			nameCode=name;
		}
	}

	private void saveDept(OrganNodeVO organ) {
		String organParentId = organ.getParentId()==null?"0":organ.getParentId().toString();
		String organId = organ.getOrganId()==null?"0":organ.getOrganId().toString();
		String organName = organ.getName();

		//rtxClient.saveDept(organ.getId().longValue(),organ.getName(),organ.getParentId(),organ.getSortNum().intValue());
	}
	private void saveDept(OrganNodeVO organ,String parentName) {
		String organParentId = organ.getParentId()==null?"0":organ.getParentId().toString();
		String organId = organ.getOrganId()==null?"0":organ.getOrganId().toString();
		String organName = organ.getName();
		System.out.println(organName + "--->" + parentName);
		/*String btnParentName = "";
		if(!organName.equals(parentName)){
			btnParentName = parentName.substring(0,parentName.length()-(organName.length()+1));
		}*/
//		rtxClient.saveDept(organName,parentName);
		//rtxClient.saveDept(organ.getId().longValue(),organ.getName(),organ.getParentId(),organ.getSortNum().intValue());
	}
	private void syncUser4Contact(PersonNodeVO person,String nameCode) {
		if(null == person || null == person.getUid())return;
		//String organId = person.getOrganId()==null?"0":person.getOrganId().toString();
//		person.setPassword("81dc9bdb52d04dc20036dbd8313ed055");
//		bigAntClient.saveUser(nameCode, person.getUid(), person.getName(), person.getPassword(), person.getSortNum().intValue(), person.getMail(),
//				person.getMobile(), person.getOfficePhone());
//		rtxClient.saveUser(person.getUid(),person.getName(),"0",person.getPassword(),
//				person.getOfficePhone(),person.getMobile(),person.getMail(),nameCode,person.getSortNum()==null?0:person.getSortNum().intValue());
		/*rtxService.syncUser4Contact(person.getUid()==null?"0":person.getUid(), person.getName(),
				"0",person.getPassword(),organId, person.getOfficePhone(),
				person.getMobile(), person.getMail());*/
	}
}
