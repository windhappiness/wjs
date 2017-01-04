package com.thinkgem.jeesite.modules.sys.utils;

import java.util.List;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.service.AreaService;

public class AreaUtils {
	
	private static AreaService areaService = SpringContextHolder.getBean(AreaService.class);
	
	/**
	 * 为combo，combobox获取json串
	 * @param parentCode  父区域代码
	 * @return
	 */
	public static String getAreaCombo(String parentCode) {
		List<ComboNode> result  = Lists.newArrayList();
		List<Area> list = areaService.findByParentCode(parentCode);
		for(Area node:list) {
			ComboNode cn = new ComboNode();
			cn.setValue(node.getCode());
			cn.setText(node.getName());
			result.add(cn);
		}
		
		
		return JsonMapper.getInstance().toJson(result).replaceAll("\"", "'");
	}

}
