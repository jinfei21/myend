package com.yjfei.padis.monitor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;

@Slf4j
public class DataSourceCollector implements BeanPostProcessorWrapper,ServletContextAware{

    private List<String> excludedDatasources;
    
    private Map<String,JdbcWrapper> jdbcWrapperMapper= new HashMap<String, JdbcWrapper>();
    
    private ServletContext servletContext;
    
	@Override
	public boolean interest(Object bean) {
		return (bean instanceof DataSource) || (bean instanceof JndiObjectFactoryBean);
	}
	
    /**
     * 不需要监控的数据源
     * @param excludedDatasources
     */
    public void setExcludedDatasources(String excludedDatasources) {
        if(!StringUtils.isEmpty(excludedDatasources)) {
            this.excludedDatasources = Arrays.asList(excludedDatasources.split(","));
        }
    }
	
    private boolean isExcludedDataSource(String beanName) {
        if (excludedDatasources != null && excludedDatasources.contains(beanName)) {
            log.info("Spring datasource excluded: " + beanName);
            return true;
        }
        return false;
    }

	@Override
	public Object wrapBean(Object bean, String beanName) {
		if(bean instanceof DataSource){
            if (isExcludedDataSource(beanName) || beanName.matches("\\(inner bean\\).+")) {
                return bean;
            }
            
            if(!jdbcWrapperMapper.containsKey(beanName)){
	            final DataSource dataSource = (DataSource) bean;
	            JdbcWrapper jdbcWrapper= new JdbcWrapper(servletContext);
	            jdbcWrapperMapper.put(beanName, jdbcWrapper);
	            
	            final DataSource result = jdbcWrapper.createDataSourceProxy(beanName,dataSource);
	            log.info("Spring datasource wrapped: " + beanName);
	            return result;
	
            }
		}else if(bean instanceof JndiObjectFactoryBean){
            if (isExcludedDataSource(beanName)) {
                return bean;
            }
            final Object result = createProxy(bean, beanName);
            log.info("Spring JNDI factory wrapped: " + beanName);
            return result;
		}
		return bean;
	}

    private Object createProxy(final Object bean, final String beanName) {
        final InvocationHandler invocationHandler = new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object result = method.invoke(bean, args);
                if (result instanceof DataSource) {
                    JdbcWrapper jdbcWrapper= new JdbcWrapper(servletContext);
                    jdbcWrapperMapper.put(beanName,jdbcWrapper);
                    result = jdbcWrapper.createDataSourceProxy(beanName,(DataSource) result);
                }
                return result;
            }
        };
        return JdbcWrapper.createProxy(bean, invocationHandler);
    }
	@Override
	public void setServletContext(ServletContext servletContext) {
		 this.servletContext= servletContext;
	}

}
