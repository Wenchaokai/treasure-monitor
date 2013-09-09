package com.best.service;

import java.util.List;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.exception.MemcachedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.best.dao.CustomerDao;
import com.best.domain.Customer;

/**
 * ClassName:CustomerService Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-9-6
 */
@Service("customerService")
public class CustomerService extends BaseService {
	@Autowired
	private CustomerDao customerDao;

	public static final String ALL_CUSTOMER_KEY = "ALL_CUSTOMER_KEY";

	public List<Customer> getAllCustomer() {
		List<Customer> res = null;
		try {
			res = memcachedClient.get(ALL_CUSTOMER_KEY);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		if (null != res)
			return res;
		res = customerDao.getAllCustomer();
		try {
			memcachedClient.set(ALL_CUSTOMER_KEY, 0, res);
		} catch (TimeoutException e) {
		} catch (InterruptedException e) {
		} catch (MemcachedException e) {
		}
		return res;
	}
}
