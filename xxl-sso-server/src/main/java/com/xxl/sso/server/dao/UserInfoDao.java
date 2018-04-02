package com.xxl.sso.server.dao;

import com.xxl.sso.server.core.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuxueli 2017-08-02 22:34:53
 */
@Mapper
public interface UserInfoDao {

	int insert(@Param("userInfo") UserInfo userInfo);

	int delete(@Param("id") int id);

	int update(@Param("userInfo") UserInfo userInfo);

	List<UserInfo> findAll();

	UserInfo loadById(@Param("id") int id);

	UserInfo findByUsername(@Param("username") String username);

}
