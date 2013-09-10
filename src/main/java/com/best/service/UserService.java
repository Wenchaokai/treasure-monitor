package com.best.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.exception.MemcachedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.best.dao.UserDao;
import com.best.domain.User;
import com.best.utils.CommonUtils;

/**
 * ClassName:UserService Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-8-27
 */
@Service("userService")
public class UserService extends BaseService {

	@Autowired
	private UserDao userDao;

	public User checkUser(User user) {
		user.setUserPassword(CommonUtils.encoder(user.getUserPassword()));
		user = userDao.checkUser(user);
		if (user != null) {
			// 写进MemcachedClient
			try {
				memcachedClient.addWithNoReply(user.getUserName(), 0, user);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (MemcachedException e) {
				e.printStackTrace();
			}
		}
		return user;
	}

	public int updateUserPassword(Long userId, String oldPassword, String newPassword) {
		oldPassword = CommonUtils.encoder(oldPassword);
		newPassword = CommonUtils.encoder(newPassword);
		int modifyCount = userDao.updateUserPassword(userId, oldPassword, newPassword);
		return modifyCount;
	}

	public List<User> getSearchUsers(String searchName, String searchCount, Integer page) throws TimeoutException,
			InterruptedException, MemcachedException {
		List<User> res = null;
		// 从缓存读取数据
		// String key = "";
		// if (StringUtils.isNotBlank(searchName))
		// key = searchName;
		// key += "-";
		// if (StringUtils.isNotBlank(searchCount))
		// key += searchCount;
		// res = memcachedClient.get(key);
		int start = page * userDao.pageSize;

		res = userDao.findUserByPageSize(searchName, searchCount, start);

		// memcachedClient.add(key, 0, res);

		if (res == null) {
			return new ArrayList<User>();
		}
		// return res.subList(0, res.size() > end ? end : res.size());
		return res;

	}

	public int getTotalSize(String searchName, String searchCount) throws TimeoutException, InterruptedException,
			MemcachedException {
		Integer res = null;
		// String key = "";
		// if (StringUtils.isNotBlank(searchName))
		// key = searchName;
		// key += "-";
		// if (StringUtils.isNotBlank(searchCount))
		// key += searchCount;
		// key += "-TOTALSIZE";
		// res = memcachedClient.get(key);

		res = userDao.findUserTotalSize(searchName, searchCount);
		// memcachedClient.add(key, 0, res);
		return res;
	}

	public User addMember(User user) {
		user.setUserPassword(CommonUtils.encoder(user.getUserPassword()));
		user = (User) userDao.insertUser(user);
		// FIXME 要清除缓存
		return user;
	}

	public void deleteMember(String userIdString) {
		Long userId = Long.parseLong(userIdString);
		userDao.deleteUser(userId);
		// FIXME 清除缓存
	}

	public Integer checkUserCountExisted(String userCount) {
		return userDao.checkUserCountExisted(userCount);
	}

}
