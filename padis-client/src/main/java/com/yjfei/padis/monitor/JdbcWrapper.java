package com.yjfei.padis.monitor;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

@Slf4j
public class JdbcWrapper {

	private ServletContext servletContext;

	private boolean jboss;
	private boolean glassfish;
	private boolean weblogic;
	private boolean jonas;

	// 缓存，提高性能
	private static final Map<String, String> sqlURLCache = new ConcurrentHashMap<String, String>(
			256);

	private static final String EMPTY_CONNECTION = "jdbc:mysql://unknown:3306/%s?useUnicode=true";
	
	private final static Pattern pattern= Pattern.compile("(?i)[a-z]+");

	JdbcWrapper(ServletContext servletContext) {
		this.servletContext = servletContext;
		initServletContext(servletContext);
	}

	void initServletContext(ServletContext context) {
		assert context != null;
		this.servletContext = context;
		final String serverInfo = servletContext==null?"":servletContext.getServerInfo();
		jboss = serverInfo.contains("JBoss") || serverInfo.contains("WildFly");
		glassfish = serverInfo.contains("GlassFish")
				|| serverInfo.contains("Sun Java System Application Server");
		weblogic = serverInfo.contains("WebLogic");
		jonas = System.getProperty("jonas.name") != null;
	}

	/**
	 * 拉取数据源的配置属性
	 * 
	 * @param name
	 * @param dataSource
	 */
	private void pullDataSourceConfigProperties(String name,
			DataSource dataSource) {
		try {

			Connection con = dataSource.getConnection();
			String url = con.getMetaData().getURL();
			con.close();
			sqlURLCache.put(name, url);
		} catch (Throwable t) {
			log.error("get url error!", t);
		}
	}

	/**
	 * 创建代理数据源对象
	 * 
	 * @param name
	 * @param dataSource
	 * @return
	 */
	DataSource createDataSourceProxy(final String name, final DataSource dataSource) {
		assert dataSource != null;
		pullDataSourceConfigProperties(name, dataSource);
		final InvocationHandler invocationHandler = new AbstractInvocationHandler<DataSource>(
				dataSource) {
			private static final long serialVersionUID = 1L;

			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				Object result = method.invoke(dataSource, args);
				if (result instanceof Connection) {
					result = createConnectionProxy((Connection) result,name);
				}
				return result;
			}
		};
		return createProxy(dataSource, invocationHandler);
	}

	/**
	 * 创建代理Connection对象
	 * 
	 * @param connection
	 * @return
	 */
	Connection createConnectionProxy(Connection connection,String dbname) {
		assert connection != null;
		final ConnectionInvocationHandler invocationHandler = new ConnectionInvocationHandler(
				connection,dbname);
		final Connection result;
		if (jonas) {
			result = createProxy(connection, invocationHandler,
					Arrays.asList(new Class<?>[] { Connection.class }));
		} else {
			result = createProxy(connection, invocationHandler);
		}
		return result;
	}

	/**
	 * 创建Statement|PreparedStatement对象
	 * 
	 * @param query
	 * @param statement
	 * @return
	 */
	Statement createStatementProxy(String query, Statement statement,String dbname) {
		assert statement != null;
		final InvocationHandler invocationHandler = new StatementInvocationHandler(
				query, statement,dbname);
		return createProxy(statement, invocationHandler);
	}

	static <T> T createProxy(T object, InvocationHandler invocationHandler) {
		return createProxy(object, invocationHandler, null);
	}
	
    private static boolean isProxyAlready(Object object) {
        return Proxy.isProxyClass(object.getClass()) && Proxy.getInvocationHandler(object).getClass().getName().equals(DelegatingInvocationHandler.class.getName());
    }

	static <T> T createProxy(T object, InvocationHandler invocationHandler,
			List<Class<?>> interfaces) {
		if (isProxyAlready(object)) {
			return object;
		}
		InvocationHandler ih = new DelegatingInvocationHandler(invocationHandler);
		return JdbcWrapperHelper.createProxy(object, ih, interfaces);
	}

	boolean isEqualsMethod(Object methodName, Object[] args) {
		return "equals" == methodName && args != null && args.length == 1; // NOPMD
	}

	boolean isHashCodeMethod(Object methodName, Object[] args) {
		return "hashCode" == methodName && (args == null || args.length == 0);
	}
	
    Object doExecute(String requestName, Statement statement, Method method, Object[] args,String dbname)
            throws IllegalAccessException, InvocationTargetException {
        assert requestName != null;
        assert statement != null;
        assert method != null;

        if (requestName.startsWith("explain ")) {
      
             return method.invoke(statement, args);
    
        }

        // 获取SQL类型
        Matcher m = pattern.matcher(requestName);
    	String sqlType = "other";
    	if(m.find()){
    		sqlType = m.group().toLowerCase();
    	}

    	//获取类名方法名
        Transaction t = Cat.newTransaction("SQL", sqlType);
		Cat.logEvent("SQL.Method", sqlType);
		
		//获取数据库
		String url = this.sqlURLCache.get(dbname);
		if(url == null){
			url = EMPTY_CONNECTION;
		}
		
		Cat.logEvent("SQL.Database", url);
        Object returnObj = null;
        try {
        	
        	returnObj = method.invoke(statement, args);
            t.setStatus(Transaction.SUCCESS);
        } catch (final InvocationTargetException e) {
        	Cat.logError(requestName,e);
            throw e;
        } finally {            
        	t.complete();
        	return returnObj;
        }
    }

	private abstract static class AbstractInvocationHandler<T> implements
			InvocationHandler, Serializable {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("all")
		private final T proxiedObject;

		AbstractInvocationHandler(T proxiedObject) {
			super();
			this.proxiedObject = proxiedObject;
		}

		T getProxiedObject() {
			return proxiedObject;
		}
	}

	/**
	 * Connection InvocationHandler
	 */
	private class ConnectionInvocationHandler implements InvocationHandler {
		private final Connection connection;
		private boolean alreadyClosed;
		private String dbname;
		ConnectionInvocationHandler(Connection connection,String dbname) {
			super();
			assert connection != null;
			this.connection = connection;
			this.dbname = dbname;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			final String methodName = method.getName();
			if (isEqualsMethod(methodName, args)) {
				return areConnectionsEquals(args[0]);
			} else if (isHashCodeMethod(methodName, args)) {
				return connection.hashCode();
			}
			try {
				Object result = method.invoke(connection, args);
				if (result instanceof Statement) {
					final String requestName;
					if ("prepareStatement".equals(methodName)
							|| "prepareCall".equals(methodName)) {
						requestName = (String) args[0];
					} else {
						requestName = null;
					}
					result = createStatementProxy(requestName,
							(Statement) result,dbname);
				}
				return result;
			} finally {
				if ("close".equals(methodName) && !alreadyClosed) {
					alreadyClosed = true;
				}
			}
		}

		private boolean areConnectionsEquals(Object object) {
			if (Proxy.isProxyClass(object.getClass())) {
				final InvocationHandler invocationHandler = Proxy
						.getInvocationHandler(object);
				if (invocationHandler instanceof DelegatingInvocationHandler) {
					final DelegatingInvocationHandler d = (DelegatingInvocationHandler) invocationHandler;
					if (d.getDelegate() instanceof ConnectionInvocationHandler) {
						final ConnectionInvocationHandler c = (ConnectionInvocationHandler) d
								.getDelegate();
						return connection.equals(c.connection);
					}
				}
			}
			return connection.equals(object);
		}
	}
	
	
    private static class DelegatingInvocationHandler implements InvocationHandler, Serializable {
        private static final long serialVersionUID = 7515240588169084785L;
        @SuppressWarnings("all")
        private final InvocationHandler delegate;

        DelegatingInvocationHandler(InvocationHandler delegate) {
            super();
            this.delegate = delegate;
        }

        InvocationHandler getDelegate() {
            return delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                return delegate.invoke(proxy, method, args);
            } catch (final InvocationTargetException e) {
                if (e.getTargetException() != null) {
                    throw e.getTargetException();
                }
                throw e;
            }
        }
    }
    
    /**
     * Statement | PreparedStatement InvocationHandler
     */
    private class StatementInvocationHandler implements InvocationHandler {
        private String requestName;
        private final Statement statement;
        private String dbname;
        StatementInvocationHandler(String query, Statement statement,String dbname) {
            super();
            assert statement != null;

            this.requestName = query;
            this.statement = statement;
            this.dbname = dbname;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            final String methodName = method.getName();
            if (isEqualsMethod(methodName, args)) {
                return statement.equals(args[0]);
            } else if (isHashCodeMethod(methodName, args)) {
                return statement.hashCode();
            } else if (methodName.startsWith("execute")) {
                if (isFirstArgAString(args)) {
                    requestName = (String) args[0];
                }
                requestName = String.valueOf(requestName);
                return doExecute(requestName, statement, method, args,dbname);
            } else if ("addBatch".equals(methodName) && isFirstArgAString(args)) {
                requestName = (String) args[0];
            }
            return method.invoke(statement, args);
        }

        private boolean isFirstArgAString(Object[] args) {
            return args != null && args.length > 0 && args[0] instanceof String;
        }
    }

}
