package com.yjfei.padis.monitor;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;

public class MonitorBeanPostProcessor implements BeanPostProcessor,PriorityOrdered{
	
    private int order = LOWEST_PRECEDENCE;
    
    private boolean enabled= true;
    
    private List<BeanPostProcessorWrapper> beanPostProcessorWrappers = Collections.emptyList();


	@Override
	public int getOrder() {
		return 0;
	}
	
    public void setEnabled(boolean enabled){
    	this.enabled= enabled;
    }

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
    	if(enabled){
	        for(BeanPostProcessorWrapper each: beanPostProcessorWrappers){
	            if(each.interest(bean)){
	                return each.wrapBean(bean,beanName);
	            }
	        }
    	}
        return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

    public void setBeanPostProcessorWrappers(List<BeanPostProcessorWrapper> beanPostProcessorWrappers){
        this.beanPostProcessorWrappers = beanPostProcessorWrappers;
    }
}
