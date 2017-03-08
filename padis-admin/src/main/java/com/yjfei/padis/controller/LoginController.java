package com.yjfei.padis.controller;


/*
@Controller
@RequestMapping(value = "/login")
@Slf4j
public class LoginController {
	
	@Resource(name = "umService")
	private UMService umService;
	
	
	@RequestMapping(value = "/authorize", method = RequestMethod.GET)
	@ResponseBody
	public Result login(@RequestParam(value = "name") String name,@RequestParam(value = "passwd") String passwd,ServletRequest serlvetRequest, ServletResponse serlvetResponse){
		Result result = new Result();
		
		try{
		HttpServletRequest httpServletRequest = (HttpServletRequest) serlvetRequest ;
		HttpServletResponse httpServletResponse = (HttpServletResponse)serlvetResponse;
		HttpSession session = httpServletRequest.getSession();
		
		if(umService.authenticate(name, passwd)){
			result.setSuccess(true);
			result.setResult(name);
			httpServletRequest.getCookies()[0].setValue("ok");
			
			String url = httpServletResponse.encodeRedirectURL(httpServletRequest.getRequestURL().toString()) ;
				//throw new NotLoginException(url);
			httpServletResponse.sendRedirect("login.html");
		}else{
			result.setSuccess(false);
			httpServletRequest.getCookies()[0].setValue("false");
		}
		
		}catch(Throwable t){
			log.error("login fail!", t);
		}
		
		return result;
	}

}
*/
