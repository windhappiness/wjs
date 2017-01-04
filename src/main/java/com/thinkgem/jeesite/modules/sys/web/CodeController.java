package com.thinkgem.jeesite.modules.sys.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.entity.Code;
import com.thinkgem.jeesite.modules.sys.service.CodeService;




/**
 * 字典Controller
 * @author code
 * @version 2015-07-26
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/code")
public class CodeController extends BaseController {
	
	@Autowired
	private CodeService codeService;
	
	@RequiresUser
	@RequestMapping(value = {"list", ""})
	public String index() {
		return "modules/sys/codeList";
	}
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "loadlist")
	public List<Code> loadlist(@RequestParam(required=false) String id) {
		List<Code> result = codeService.findByParent(id);
		//System.out.println("++++++++++++"+result.size());
		return result;
	}
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "loadDicNode/{id}")
	public Code loadDicNode(@PathVariable String id) {
		return codeService.loadDicNode(id);
	}
	
	@RequiresUser
	@RequestMapping(value = "checkNodeCode")
	@ResponseBody
	public Map<String,Integer> checkNodeCode(@RequestParam(required=false) String id,@RequestParam(required=true) String code,@RequestParam String parentId) {
		boolean flag = codeService.checkDicNodeCode(id, code, parentId);
		Map<String,Integer> result = new HashMap<String,Integer>();
		if(flag) {
			result.put("result", 0);
		} else {
			result.put("result", 1);
		}
		return result;
	}
	
	@RequiresUser
	@RequestMapping(value = "checkNodeComCode")
	@ResponseBody
	public Map<String,Integer> checkNodeComCode(@RequestParam(required=false) String id,@RequestParam(required=true) Long comCode,@RequestParam String parentId) {
		boolean flag = codeService.checkDicNodeComCode(id, comCode,parentId);
		Map<String,Integer> result = new HashMap<String,Integer>();
		if(flag) {
			result.put("result", 0);
		} else {
			result.put("result", 1);
		}
		return result;
	}
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "saveNode")
	public String saveNode(Code bo) {
		int flag = codeService.saveNode(bo);
		Map<String,Integer> result = new HashMap<String,Integer>();
		result.put("result", flag);
		return JsonMapper.getInstance().toJson(result);
	}
	
	@RequiresUser
	@ResponseBody
	@RequestMapping(value = "removeNode/{nodeId}")
	public String removeNode(@PathVariable String nodeId) {
		codeService.removeDicNode(nodeId);
		Map<String,Integer> result = new HashMap<String,Integer>();
		result.put("result", 0);
		return JsonMapper.getInstance().toJson(result);
	}
	
	
	/**
	 * 
	 
	 * @param lazy 是否赖加载
	 * @param 父节点id，当懒加载时有效
	 * @param path 字典路径
	 * @return
	 */
	@RequiresUser
	@RequestMapping(value = "tree")
	@ResponseBody
	public List<Code> tree(@RequestParam(required=true) String path,
			@RequestParam(required=false) boolean lazy,@RequestParam(required=false) String id) {
		if(lazy){
			return codeService.lazyTree(path,id);
		} else {
			return codeService.tree(path);
		}
		
	}

}
