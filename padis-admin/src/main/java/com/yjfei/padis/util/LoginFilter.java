package com.yjfei.padis.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

 

public class LoginFilter implements Filter{
	
	private FilterConfig filterConfig ;


	public void destroy() {	
	}


	public void doFilter(ServletRequest serlvetRequest, ServletResponse serlvetResponse,
			FilterChain filterChain) throws IOException, ServletException {
 			HttpServletRequest httpServletRequest = (HttpServletRequest) serlvetRequest ;
 			HttpServletResponse httpServletResponse = (HttpServletResponse)serlvetResponse;
 			HttpSession session = httpServletRequest.getSession();
 			String ok = httpServletRequest.getCookies()[0].getValue();
 			
 			
 			if("ok".equals(ok)){
 				
 			}
 			filterChain.doFilter(serlvetRequest, serlvetResponse);
 	}


	public void init(FilterConfig filerConfig) throws ServletException {
 		this.filterConfig = filerConfig ;
	}
}
