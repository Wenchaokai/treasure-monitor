package com.best.service;

import net.rubyeye.xmemcached.MemcachedClient;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * ClassName:BaseService Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-1
 */
public class BaseService {

	@Autowired
	protected MemcachedClient memcachedClient;
}
