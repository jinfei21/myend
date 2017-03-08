package com.yjfei.padis.monitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.naming.Referenceable;

final public class JdbcWrapperHelper {

	private static final Map<Class<?>, Constructor<?>> PROXY_CACHE = Collections
			.synchronizedMap(new WeakHashMap<Class<?>, Constructor<?>>());

	private JdbcWrapperHelper() {
		super();
	}


	static <T> T createProxy(T object, InvocationHandler invocationHandler,
			List<Class<?>> interfaces) {
		final Class<? extends Object> objectClass = object.getClass();
		Constructor<?> constructor = PROXY_CACHE.get(objectClass);
		if (constructor == null) {
			final Class<?>[] interfacesArray = getObjectInterfaces(objectClass, interfaces);
			constructor = getProxyConstructor(objectClass, interfacesArray);
			if (interfaces == null) {
				PROXY_CACHE.put(objectClass, constructor);
			}
		}
		try {
			return (T) constructor.newInstance(new Object[] { invocationHandler });
		} catch (final Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private static Constructor<?> getProxyConstructor(Class<? extends Object> objectClass,
			Class<?>[] interfacesArray) {
		final ClassLoader classLoader = objectClass.getClassLoader(); // NOPMD
		try {
			final Constructor<?> constructor = Proxy.getProxyClass(classLoader, interfacesArray)
					.getConstructor(new Class[] { InvocationHandler.class });
			constructor.setAccessible(true);
			return constructor;
		} catch (final NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}
	}

	private static Class<?>[] getObjectInterfaces(Class<?> objectClass, List<Class<?>> interfaces) {
		final List<Class<?>> myInterfaces;
		if (interfaces == null) {
			myInterfaces = new ArrayList<Class<?>>(Arrays.asList(objectClass.getInterfaces()));
			Class<?> classe = objectClass.getSuperclass();
			while (classe != null) {
				final Class<?>[] classInterfaces = classe.getInterfaces();
				if (classInterfaces.length > 0) {
					final List<Class<?>> superInterfaces = Arrays.asList(classInterfaces);
					// removeAll d'abord car il ne faut pas de doublon dans la liste
					myInterfaces.removeAll(superInterfaces);
					myInterfaces.addAll(superInterfaces);
				}
				classe = classe.getSuperclass();
			}
			myInterfaces.remove(Referenceable.class);
		} else {
			myInterfaces = interfaces;
		}
		return myInterfaces.toArray(new Class<?>[myInterfaces.size()]);
	}
}

