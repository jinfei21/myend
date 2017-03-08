package com.yjfei.padis.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Base64;

import com.yjfei.padis.util.ConfigUtil;

@Slf4j
public class WwwAuthFilter implements Filter {
	private static final String AUTH_PREFIX = "Basic ";

	private String username = "root";

	private String password = "root";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String configFile = filterConfig.getInitParameter("auth-config");

		username = ConfigUtil.getString(configFile, "username", username);
		password = ConfigUtil.getString(configFile, "password", password);

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String authorization = httpRequest.getHeader("authorization");

		if (null != authorization
				&& authorization.length() > AUTH_PREFIX.length()) {
			authorization = authorization.substring(AUTH_PREFIX.length(),
					authorization.length());
			if ((username + ":" + password).equals(new String(Base64
					.decodeBase64(authorization)))) {
				authenticateSuccess(httpResponse);
				chain.doFilter(httpRequest, httpResponse);
			} else {
				needAuthenticate(httpRequest, httpResponse);
			}

		} else {
			needAuthenticate(httpRequest, httpResponse);
		}
	}

	private void authenticateSuccess(final HttpServletResponse response) {
		response.setStatus(200);
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0);
	}

	private void needAuthenticate(final HttpServletRequest request,
			final HttpServletResponse response) {
		response.setStatus(401);
		response.setHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0);
		response.setHeader("WWW-authenticate", AUTH_PREFIX
				+ "Realm=\"Jedisx Amdin Auth\"");
	}

	@Override
	public void destroy() {

	}

}
