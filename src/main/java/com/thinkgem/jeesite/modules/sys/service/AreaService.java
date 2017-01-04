/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.service.TreeService;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 区域Service
 * @author ThinkGem
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class AreaService extends TreeService<AreaDao, Area> {

	public List<Area> findAll(){
		return UserUtils.getAreaList();
	}
	
	public List<Area> findByParentCode(String parentCode){
		Area qr = new Area();
		qr.setCode(parentCode);
		return dao.findByParentCode(qr);
	}

	@Transactional(readOnly = false)
	public void save(Area area) {
		super.save(area);
		UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
	}
	
	@Transactional(readOnly = false)
	public void delete(Area area) {
		super.delete(area);
		UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
	}
	
	/**
	 * 按代码查询区域
	 * @param code
	 * @return
	 */
	public Area getAreaByCode(String code) {
		Area qr = new Area();
		qr.setCode(code);
		return dao.getAreaByCode(qr);
	}
	
	/**
	 * 按父id集合，查找所有的祖先
	 * @param parentIdArr
	 * @return
	 */
	public List<Area> findAllParent(String[] parentIdArr) {
		return dao.findAllParent(parentIdArr);
		
	}

}
