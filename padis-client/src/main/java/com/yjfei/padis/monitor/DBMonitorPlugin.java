package com.yjfei.padis.monitor;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.transaction.SpringManagedTransaction;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

@Intercepts(value = {
		@Signature(type = Executor.class, method = "update", args = {
				MappedStatement.class, Object.class }),
		@Signature(type = Executor.class, method = "query", args = {
				MappedStatement.class, Object.class, RowBounds.class,
				ResultHandler.class, CacheKey.class, BoundSql.class }),
		@Signature(type = Executor.class, method = "query", args = {
				MappedStatement.class, Object.class, RowBounds.class,
				ResultHandler.class }) })
public class DBMonitorPlugin implements Interceptor {

	// 缓存，提高性能
	private static final Map<String, String> sqlURLCache = new ConcurrentReferenceHashMap<String, String>(
			256);

	private static final String EMPTY_CONNECTION = "jdbc:mysql://unknown:3306/%s?useUnicode=true";

	private Executor target;

	private String dbName;

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		MappedStatement mappedStatement = (MappedStatement) invocation
				.getArgs()[0];
		// 得到类名，方法
		String[] strArr = mappedStatement.getId().split("\\.");
		String methodName = strArr[strArr.length - 2] + "."
				+ strArr[strArr.length - 1];

		Transaction t = Cat.newTransaction("SQL", methodName);

		// 获取SQL类型
		SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
		Cat.logEvent("SQL.Method", sqlCommandType.name().toLowerCase());

		String s = this.getSQLDatabase();
		Cat.logEvent("SQL.Database", s);

		Object returnObj = null;
		try {
			returnObj = invocation.proceed();
			t.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			t.setStatus(e);
			Cat.logError(e);
		} finally {
			t.complete();
		}

		return returnObj;
	}

	private String getSqlURL() {
		javax.sql.DataSource dataSource = this.getDataSource();
		if (dataSource != null) {
			try {

				Connection con = dataSource.getConnection();
				String url = con.getMetaData().getURL();
				con.close();
				return url;
			} catch (Throwable t) {
				Cat.logError(t);
			}

		}
		return null;
	}

	private String getSQLDatabase() {
		if (dbName == null) {
			dbName = "DEFAULT";
		}
		String url = sqlURLCache.get(dbName);
		if (url != null) {
			return url;
		}

		url = this.getSqlURL();
		if (url == null) {
			url = String.format(EMPTY_CONNECTION, dbName);
		}
		sqlURLCache.put(dbName, url);
		return url;
	}

	private javax.sql.DataSource getDataSource() {
		org.apache.ibatis.transaction.Transaction transaction = this.target
				.getTransaction();
		if (transaction == null) {
			Cat.logError(String.format(
					"Could not find transaction on target [%s]", this.target),
					null);
			return null;
		}
		if (transaction instanceof SpringManagedTransaction) {
			String fieldName = "dataSource";
			Field field = ReflectionUtils.findField(transaction.getClass(),
					fieldName, javax.sql.DataSource.class);

			if (field == null) {
				Cat.logError(
						String.format(
								"Could not find field [%s] of type [%s] on target [%s]",
								fieldName, javax.sql.DataSource.class,
								this.target), null);
				return null;
			}

			ReflectionUtils.makeAccessible(field);
			javax.sql.DataSource dataSource = (javax.sql.DataSource) ReflectionUtils
					.getField(field, transaction);
			return dataSource;
		}

		Cat.logError(String.format(
				"the transaction is not SpringManagedTransaction:%s",
				transaction.getClass().toString()), null);

		return null;
	}

	@Override
	public Object plugin(Object target) {

		if (target instanceof Executor) {
			this.target = (Executor) target;
			return Plugin.wrap(target, this);
		}
		return target;
	}

	@Override
	public void setProperties(Properties properties) {

	}
}
