package ohSolutions.ohRest.util.controller;

import java.io.IOException;

//import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

//@WebFilter(filterName = "ohHeaderFilter", urlPatterns = {"/*"})
public class HeaderFilter implements Filter {
 
  //private final static Logger log = Logger.getLogger(FiltroAcceso.class.getName() );

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
	  if (response instanceof HttpServletResponse) {
		  HttpServletResponse http = (HttpServletResponse) response;
		  http.addHeader("Access-Control-Allow-Origin", "*");
		  http.addHeader("Access-Control-Allow-Credentials", "true");
		  http.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
		  http.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, jpoNoMappingBody");
	  } 
	  chain.doFilter(request, response);
  }
	
	@Override
	public void destroy() {
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
}