package com.thinkgem.jeesite.modules.sys.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;



import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.Code;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.service.AreaService;
import com.thinkgem.jeesite.modules.sys.service.CodeService;
import com.thinkgem.jeesite.modules.sys.service.OfficeService;





public class CodeUtils {
	
	public static final String CACHE_DICT = "dicCache";
	public static final String CACHE_DICT_UNIQUE = "dicCacheUNI";
	
	private static CodeService codeService = SpringContextHolder.getBean(CodeService.class);
	private static AreaService areaService = SpringContextHolder.getBean(AreaService.class);
	private static OfficeService officeService = SpringContextHolder.getBean(OfficeService.class);
	
	
	
	
	
	/**
	 * 为combo，combobox获取json串
	 * @param path 字典路径
	 * @return
	 */
	public static String getComboDict(String path) {
		return getComboDict(path,false);
	}
	
	
	/**
	 * 为combo，combobox获取json串
	 * @param path 字典路径
	 * @param showCode 是否显示代码
	 * @return
	 */
	public static String getComboDict(String path,boolean showCode) {
		
		List<ComboNode> result  = Lists.newArrayList();
		List<Code> list = getDictList(path);
		
		if("wj->xk->wj_rad_sxzzmc".equals(path)){
			String aa = "";
		}
		
		for(Code node:list) {
			ComboNode cn = new ComboNode();
			cn.setValue(node.getValue());
			if(showCode) {
				cn.setText(node.getValue()+"-"+node.getName());
			} else {
				cn.setText(node.getName());
			}
			result.add(cn);
		}
		return JsonMapper.getInstance().toJson(result).replaceAll("\"", "'");
	}
	
	/**
	 * 为combo，combobox获取json串（可以联动添加子路径）
	 * @param path 字典路径
	 * @param subpath 子路径
	 * @param showCode 是否显示代码
	 * @return
	 */
	public static String getLinkComboDict(String path,String subpath,boolean showCode) {
		
		List<ComboNode> result  = Lists.newArrayList();
		if(StringUtils.isNotBlank(subpath)){
			path += "->"+subpath;
		}
		List<Code> list = Lists.newArrayList(); 
		if(getDictList(path)!=null&&!getDictList(path).isEmpty()){
			list = getDictList(path);
		}
		
		for(Code node:list) {
			ComboNode cn = new ComboNode();
			cn.setValue(node.getValue());
			if(showCode) {
				cn.setText(node.getValue()+"-"+node.getName());
				cn.setValue(node.getValue());
			} else {
				cn.setText(node.getName());
				cn.setValue(node.getValue());
			}
			result.add(cn);
		}
		return JsonMapper.getInstance().toJson(result).replaceAll("\"", "'");
	}
	
	/**
	 * 获得行政区划选项
	 * @param streeCode 街道Code
	 * @param showShanghai 是否显示上海市
	 * @return
	 */
	public static String getComboDictrict(String areaCode,boolean showShangHai) {
		String path = "wj->xk->wj_districtall";
		if(StringUtils.isNotBlank(areaCode)){
			path += "->"+areaCode;
		}
		List<ComboNode> result  = Lists.newArrayList();
		List<Code> list = getDictList(path);
		for(Code node:list) {
			ComboNode cn = new ComboNode();
			if(!showShangHai) {
				String value = node.getValue();
				if(value .equals("310000")){
					continue;
				}
			}
			cn.setText(node.getName());
			cn.setValue(node.getValue());
			result.add(cn);
		}
		return JsonMapper.getInstance().toJson(result).replaceAll("\"", "'");
	}
	
	
	/**
	 * 根据路径获得节点 先从缓存中找,再从数据库中找
	 * 
	 * @param path
	 *            节点路径(从大类代码开始以"->"连接节点代码 ,例如"GXY->GXYROOT->1->2");
	 *            必须是全路径,对于象性别,证件类型这种平面结构的代码,这没问题,很容易就能给出全路径
	 *            对于象机构代码,行政区划代码这种树形结构的代码,难以给出全路径,此时建议使用getUniqueNode方法
	 * @return
	 */
	public static Code getNode(String path) {
		Code node = (Code)CacheUtils.get(CACHE_DICT, path);
		if(node ==  null) {
			node = codeService.getNode(path,false);
			if(node != null) {
				CacheUtils.put(CACHE_DICT, path, node);
			}
		}
		return node;
	}
	
	
	
	/**
	 * 根据路径获得所有子节点 先从缓存中找,再从数据库中找
	 * 
	 * @param path
	 *            节点路径(从大类代码开始以"->"连接节点代码 ,例如"GXY->GXYROOT->1->2"); 必须是全路径
	 * @return
	 */
	public static List<Code> getDictList(String path) {
		Code node = getNode(path);
		if(node==null){
			node = getUniqueNode(path);
		}
		if(node != null) {
			return node.getChilds();
		}
		return null;
	}
	
	/**
	 * 根据路径获得节点 名
	 * @param path
	 * 			       节点路径(从大类代码开始以"->"连接节点代码 ,例如"GXY->GXYROOT->1->2");
	 *            必须是全路径,对于象性别,证件类型这种平面结构的代码,这没问题,很容易就能给出全路径
	 *            对于象机构代码,行政区划代码这种树形结构的代码,难以给出全路径,此时建议使用getUniqueNodeName方法
	 * @return
	 */
	public static String getDictLabel(String path){
		Code node = getNode(path);
		if(node != null) {
			return node.getName();
		}
		return "";
	}
	
	/**
	 * 根据组合码路径获得所对应的节点
	 * 该路径下的直接子节点必须都定义组合码
	 * @param comCodePath 组合码路径(从大类代码开始以"->"连接节点代码 ,例如"GXY->SYM->6");
	 *                    必须是全路径,最后一位必须是组合码
	 * @return
	 */
	public static List<Code> getNodesByComCode(String comCodePath) {
		if(StringUtils.isEmpty(comCodePath)){
			throw new IllegalArgumentException("comCodePath is empty");
		}
		String[] arr = StringUtils.split(comCodePath, Code.PATH_SPERATOR);
		if(arr.length < 2){
			throw new IllegalArgumentException("path is invalid");
		}
		List<Code> result  = new ArrayList<Code>();
		int pos = comCodePath.lastIndexOf(Code.PATH_SPERATOR);
		String parentPath = comCodePath.substring(0,pos);
		String comCodeStr = comCodePath.substring(pos+Code.PATH_SPERATOR.length());
		long comCode = Long.parseLong(comCodeStr);
		List<Code> nodeList = getDictList(parentPath);
		if(nodeList != null && nodeList.size()>0){
			Code node = null;
			for(int i = 0; i < nodeList.size(); i++){
				node = nodeList.get(i);
				if(node.getComCode() == null){
					throw new UnsupportedOperationException("node comcode is null");
				}
				long calcResult = comCode & node.getComCode().longValue();
				if(calcResult == node.getComCode().longValue()){
					result.add(node);
				}
			}
		}
		return result;
	}
	
	/**
	 * 根据组合码路径获得转换后的 名
	 * 该路径下的直接子节点必须都定义组合码
	 * @param comCodePath 组合码路径(从大类代码开始以"->"连接节点代码 ,例如"GXY->SYM->6");
	 *                    必须是全路径,最后一位必须是组合码
	 * @return
	 */
	public static List<String> getNameByComCode(String comCodePath){
		List<String> result  = new ArrayList<String>();
		List<Code> nodes = getNodesByComCode(comCodePath);
		if(nodes != null && nodes.size()>0){
			Code node = null;
			for(int i = 0; i < nodes.size(); i++){
				node = nodes.get(i);
				result.add(node.getName());
			}
		}
		return result;
	}
	
	/**
	 * 根据路径获得代码唯一的节点 先从缓存中找,再从数据库中找
	 * 对于难以给出全路径的情况可以使用此方法
	 * 此时必须注意,要保证字典节点树下的代码唯一
	 * @param path 唯一节点路径 由字典大类代码 +'->'+唯一代码构成 如"COMMON->ORG_CODE->310111712568
	 * @return
	 */
	public static Code getUniqueNode(String path){
		Code node = (Code)CacheUtils.get(CACHE_DICT_UNIQUE, path);
		if(node ==  null) {
			node = codeService.getNode(path,true);
			if(node != null) {
				CacheUtils.put(CACHE_DICT_UNIQUE, path, node);
			}
		}
		return node;
	}
	
	/**
	 * 根据路径获得代码唯一的节点 名先从缓存中找,再从数据库中
	 * 对于难以给出全路径的情况可以使用此方法
	 * 此时必须注意,要保证字典节点树下的代码唯一
	 * @param path 唯一节点路径 由字典大类代码 +'->'+唯一代码构成 如"COMMON->ORG_CODE->310111712568
	 * @return
	 */
	public static String getUniqueNodeName(String path) {
		Code node = getUniqueNode(path);
		if(node != null){
			return node.getName();
		}
		return "";
	}
	
	/**
	 * 获取行政区划树
	 * @return json
	 */
	public static String getAreaTree() {
		
		List<Area> list = areaService.findAll();
		Map<String,Area> areaCodeMap = Maps.newHashMap();
		Map<String,Area> areaIdMap = Maps.newHashMap();
		Map<String,List<Area>> childCodeMap = Maps.newHashMap();
		for(Area a :list) {
			areaCodeMap.put(a.getCode(), a);
			areaIdMap.put(a.getId(), a);
			childCodeMap.put(a.getCode(), new ArrayList<Area>());
		}
		
		for(Area a :list) {
			if(a.getParent() != null && StringUtils.isNotBlank(a.getParent().getId())) {
				if(areaIdMap.containsKey(a.getParent().getId())) {
					Area parent = areaIdMap.get(a.getParent().getId());
					List<Area> childList = childCodeMap.get(parent.getCode());
					childList.add(a);
				}
			}
		}
		
		
		Area a = areaCodeMap.get("100000");
		SimpleTreeNode  root = new SimpleTreeNode();
		root.setText(a.getName());
		root.setId(a.getCode());
		
		List<SimpleTreeNode> childStList = recurChildArea(areaIdMap,childCodeMap,a);
		
		
		root.setChildren(childStList);	
		
		List<SimpleTreeNode> resultList = new ArrayList<SimpleTreeNode>();
		resultList.add(root);
		
		String result = JsonMapper.getInstance().toJson(resultList).replaceAll("\"", "'");
		return result;
	}
	
	/**
	 * 获取组织机构树
	 * @return json
	 */
	public static String getOfficeTree() {
		List<Office> list = officeService.findAll();
		Map<String,Office> officeCodeMap = Maps.newHashMap();
		Map<String,Office> officeIdMap = Maps.newHashMap();
		Map<String,List<Office>> childCodeMap = Maps.newHashMap();
		for(Office o :list) {
			officeCodeMap.put(o.getCode(), o);
			officeIdMap.put(o.getId(), o);
			childCodeMap.put(o.getCode(), new ArrayList<Office>());
		}
		
		for(Office o :list) {
			if(o.getParent() != null && StringUtils.isNotBlank(o.getParent().getId())) {
				if(officeIdMap.containsKey(o.getParent().getId())) {
					Office parent = officeIdMap.get(o.getParent().getId());
					List<Office> childList = childCodeMap.get(parent.getCode());
					childList.add(o);
				}
			}
		}
		
		List<SimpleTreeNode> resultList = new ArrayList<SimpleTreeNode>();
		for(Office o :list) {
			if(o.getParent() == null || StringUtils.isBlank(o.getParent().getId()) || "0".equals(o.getParent().getId())) {
				SimpleTreeNode  root = new SimpleTreeNode();
				root.setText(o.getName());
				root.setId(o.getCode());
				List<SimpleTreeNode> childStList = recurChildOffice(officeIdMap,childCodeMap,o);
				root.setChildren(childStList);	
				resultList.add(root);
				
			}
		}
		String result = JsonMapper.getInstance().toJson(resultList).replaceAll("\"", "'");
		return result;
	}
	
	
	/**
	 * 递归处理行政区划
	 * @param areaIdMap
	 * @param childCodeMap
	 * @param a
	 * @return
	 */
	private static List<SimpleTreeNode> recurChildArea(Map<String,Area> areaIdMap,Map<String,List<Area>> childCodeMap,Area a) {
		List<SimpleTreeNode> childStList = Lists.newArrayList();
		List<Area> childList = childCodeMap.get(a.getCode());
		if(childList.size()>0) {
			for(Area child: childList) {
				SimpleTreeNode st = new SimpleTreeNode();
				st.setId(child.getCode());
				st.setText(child.getName());
				
				
				List<SimpleTreeNode> list = recurChildArea(areaIdMap,childCodeMap,child);
				st.setChildren(list);
				
				childStList.add(st);
			}
		}
		return childStList;
	}
	
	
	/**
	 * 递归处理组织机构
	 * @param officeIdMap
	 * @param childCodeMap
	 * @param a
	 * @return
	 */
	private static List<SimpleTreeNode> recurChildOffice(Map<String,Office> officeIdMap,Map<String,List<Office>> childCodeMap,Office a) {
		List<SimpleTreeNode> childStList = Lists.newArrayList();
		List<Office> childList = childCodeMap.get(a.getCode());
		if(childList.size()>0) {
			for(Office child: childList) {
				SimpleTreeNode st = new SimpleTreeNode();
				st.setId(child.getCode());
				st.setText(child.getName());
				
				
				List<SimpleTreeNode> list = recurChildOffice(officeIdMap,childCodeMap,child);
				st.setChildren(list);
				
				childStList.add(st);
			}
		}
		return childStList;
	}
	
	/**
	 * 获取树
	 * @return json
	 */
	public  static String getCodeTree(String path) {
		
		List<SimpleTreeNode> resultList = new ArrayList<SimpleTreeNode>();
		
		Code root = codeService.getNode(path, false);
		List<Code> list = codeService.findChildsByPath(path);
		
		Map<String,Code> codeCodeMap = Maps.newHashMap();
		Map<String,Code> codeIdMap = Maps.newHashMap();
		Map<String,List<Code>> childIdMap = Maps.newHashMap();
		for(Code a :list) {
			codeCodeMap.put(a.getValue(), a);
			codeIdMap.put(a.getId(), a);
			childIdMap.put(a.getId(), new ArrayList<Code>());
		}
		
		for(Code a :list) {
			if(a.getParent() != null && StringUtils.isNotBlank(a.getParent().getId())) {
				if(codeIdMap.containsKey(a.getParent().getId())) {
					Code parent = codeIdMap.get(a.getParent().getId());
					List<Code> childList = childIdMap.get(parent.getId());
					childList.add(a);
				}
			}
		}
		for(Code a :list) {
			if(a.getParent() != null && StringUtils.isNotBlank(a.getParent().getId()) && a.getParent().getId().equals(root.getId())) {
				SimpleTreeNode st = new SimpleTreeNode();
				st.setId(a.getValue());
				st.setText(a.getName());
				
				List<SimpleTreeNode> childStList = recurChildCode(codeIdMap,childIdMap,a);
				
				st.setChildren(childStList);
				
				resultList.add(st);
			}
		}
		
		String result = JsonMapper.getInstance().toJson(resultList).replaceAll("\"", "'");
		
		//System.out.println("------------------"+result);
		
		
		return result;
		
	}
	
	
	/**
	 * 递归处理字典代码
	 * @param areaIdMap
	 * @param childCodeMap
	 * @param a
	 * @return
	 */
	private static List<SimpleTreeNode> recurChildCode(Map<String,Code> codeIdMap,Map<String,List<Code>> childIdMap,Code a) {
		List<SimpleTreeNode> childStList = Lists.newArrayList();
		List<Code> childList = childIdMap.get(a.getId());
		if(childList.size()>0) {
			for(Code child: childList) {
				SimpleTreeNode st = new SimpleTreeNode();
				st.setId(child.getValue());
				st.setText(child.getName());
				
				
				List<SimpleTreeNode> list = recurChildCode(codeIdMap,childIdMap,child);
				st.setChildren(list);
				
				childStList.add(st);
			}
		}
		return childStList;
	}
	
	/**
	 * 按行政区划代码查找所有祖先
	 * @param code
	 * @return
	 */
	public static List<Area> getAreaChainByCode(String code) {
		List<Area> result = Lists.newArrayList();
		Area area = areaService.getAreaByCode(code);
		if(StringUtils.isNotBlank(area.getParentIds())) {
			String[] arr = area.getParentIds().split(",");
			List<Area> pList = areaService.findAllParent(arr);
			result.addAll(pList);
		}
		
		
		result.add(area);
		return result;
	}
	
	
	/**
	 * 按行政区划代码查找所有祖先,构造完整行政区划名
	 * @param code
	 * @return
	 */
	public static String getAreaFullNameByCode(String code) {
		List<Area> list = getAreaChainByCode(code);
		if(list != null && list.size()>0) {
			StringBuffer result = new StringBuffer();
			for(Area area:list) {
				result.append(area.getName());
			}
			return result.toString();
		}
		return null;
	}
	
	public static String getOrgNameByCode(String code){
		Office params = new Office();
		params.setCode(code);
		List<Office> offices = officeService.getOrgNameByCode(params);
		if(offices != null){
			if(offices.size()==1)
				return offices.get(0).getName();
			if(offices.size()>1){
				throw new RuntimeException("根据传入的code“"+code+"”值查询出了"+offices.size()+"条值，请检查字典项！");
			}
		}
		return "";
	}

	/**
	 * 多选转码
	 * @param path 为转码路径
	 * @param codes为要转的代码 ","分隔
	 * @return
	 */
	public static String getMultiNodeName(String path,String codes){
		String result = "";
		if(codes !=null && codes.length()>0){
			String codeArr[] = codes.split(",");
			for (int i = 0; i < codeArr.length; i++) {
				String code = codeArr[i];
				if(StringUtils.isNotBlank(code)){
					if(!path.endsWith("->")) path+="->";
					if(null != CodeUtils.getUniqueNodeName(""+path+""+code) && "" != CodeUtils.getUniqueNodeName(""+path+""+code)){
						result += CodeUtils.getUniqueNodeName(""+path+""+code);
						result += ",";
					}
				}
			}
			if(StringUtils.isNotBlank(result)){
				result = result.substring(0, result.length()-1);
			}
		}else{
			result += "";
		}
		return result;
	}
	
	/**
	 * 多选转树
	 * @param path 为转码路径
	 * @param codes为要转的代码 ","分隔
	 * @param separator 分隔符
	 * @return json
	 */
	public static String getMultiCodeTree(String path,String codes,String separator) {
		
		List<SimpleTreeNode> resultList = new ArrayList<SimpleTreeNode>();
		
		Code root = codeService.getNode(path, false);
		
		List<Code> list = new ArrayList<Code>();
		if(StringUtils.isBlank(separator)){
			separator = ",";
		}
		
		String[] codeArr = codes.split(separator);
		if(!path.endsWith("->")) path+="->";
		for (String value : codeArr) {
			Code code = getUniqueNode(path+value);
			list.add(code);
		}
		
		Map<String,Code> codeCodeMap = Maps.newHashMap();
		Map<String,Code> codeIdMap = Maps.newHashMap();
		Map<String,List<Code>> childIdMap = Maps.newHashMap();
		for(Code a :list) {
			codeCodeMap.put(a.getValue(), a);
			codeIdMap.put(a.getId(), a);
			childIdMap.put(a.getId(), new ArrayList<Code>());
		}
		
		for(Code a :list) {
			if(a.getParent() != null && StringUtils.isNotBlank(a.getParent().getId())) {
				if(codeIdMap.containsKey(a.getParent().getId())) {
					Code parent = codeIdMap.get(a.getParent().getId());
					List<Code> childList = childIdMap.get(parent.getId());
					childList.add(a);
				}
			}
		}
		for(Code a :list) {
			if(a.getParent() != null && StringUtils.isNotBlank(a.getParent().getId()) && a.getParent().getId().equals(root.getId())) {
				SimpleTreeNode st = new SimpleTreeNode();
				st.setId(a.getValue());
				st.setText(a.getName());
				
				List<SimpleTreeNode> childStList = recurChildCode(codeIdMap,childIdMap,a);
				
				st.setChildren(childStList);
				
				resultList.add(st);
			}
		}
		String result = JsonMapper.getInstance().toJson(resultList).replaceAll("\"", "'");
		
		//System.out.println("------------------"+result);
		return result;
	}
	
}
