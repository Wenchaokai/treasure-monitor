package com.best.dao;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * ClassName:BaseDao Description:
 * 
 * @author ChaoKai Wen email:wenchaokai@gmail.com
 * @version
 * @Date 2013-8-27
 */
public abstract class BaseDao {

	private static final Logger logger = LoggerFactory.getLogger(BaseDao.class);

	// @Resource(name = "sqlMapClient")
	// protected SqlMapClient sqlMapClient;
	//
	// @Resource(name = "ecbossSqlMapClient")
	// protected SqlMapClient ecbossSqlMapClient;

	@Value("${best.default.pagesize}")
	public int pageSize;

	protected int delete(String id, Object o, SqlMapClient client) {
		try {
			return client.delete(id, o);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new RuntimeException();
		}
	}

	protected Object insert(String id, Object o, SqlMapClient client) {
		try {
			Object obj = client.insert(id, o);
			return obj;
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new RuntimeException();
		}
	}

	protected int update(String id, Object o, SqlMapClient client) {
		try {
			return client.update(id, o);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
			throw new RuntimeException();
		}
	}

	protected List<?> list(String id, Object o, SqlMapClient client) {
		try {
			return client.queryForList(id, o);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new RuntimeException();
		}
	}

	protected Object object(String id, SqlMapClient client) {
		try {
			return client.queryForObject(id);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new RuntimeException();
		}
	}

	protected Object object(String id, Object o, SqlMapClient client) {
		try {
			return client.queryForObject(id, o);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new RuntimeException();
		}
	}

	protected void startBatch(SqlMapClient client) {
		try {
			client.startBatch();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new RuntimeException();
		}
	}

	protected int executeBatch(SqlMapClient client) {
		try {
			return client.executeBatch();
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new RuntimeException();
		}
	}

	protected void startTransaction(SqlMapClient client) {
		try {
			client.startTransaction();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}

	protected void commitTransaction(SqlMapClient client) {
		try {
			client.commitTransaction();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}

	protected void endTransaction(SqlMapClient client) {
		try {
			client.endTransaction();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}

}
