package com.xxl.sso.server.service;

import com.xxl.sso.server.core.model.UserInfo;
import com.xxl.sso.server.dao.UserInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoService {
	
	@Autowired
	private UserInfoDao userInfoDao;

	public List<UserInfo> findAll() {
		return userInfoDao.findAll();
	}
}
