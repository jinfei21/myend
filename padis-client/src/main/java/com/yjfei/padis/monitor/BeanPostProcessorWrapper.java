package com.yjfei.padis.monitor;
public interface BeanPostProcessorWrapper {

    boolean interest(Object bean);

    Object wrapBean(Object bean, String beanName);
}