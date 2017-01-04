package com.thinkgem.jeesite.modules.sys.dao;

import java.util.List;

import com.thinkgem.jeesite.common.persistence.TreeDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.sys.entity.Code;

@MyBatisDao
public interface CodeDao extends TreeDao<Code> {
	
	public List<Code> findByParent(Code entity);
	
	public List<Code> findTopList(Code entity);
	
	public List<Code> findForCheckCode(Code entity);
	
	public List<Code> findForCheckComCode(Code entity);
	
	public Code findByValueWithParent(Code entity);
	
	public Code findUniqueValueByParent(Code entity);
}
