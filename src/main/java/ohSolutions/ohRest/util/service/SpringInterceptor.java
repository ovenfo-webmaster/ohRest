package ohSolutions.ohRest.util.service;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import ohSolutions.ohJpo.dao.Jpo;
import ohSolutions.ohJpo.dao.JpoClass;
import ohSolutions.ohRest.util.security.Oauth2;

public class SpringInterceptor implements HandlerInterceptor {
	
	final static Logger logger = LogManager.getLogger(SpringInterceptor.class);
	
	private String dsOauth2;
	private String propertiesFile;
	//private Jpo ppo;
	
	@Autowired
	public SpringInterceptor(String dsOauth2, String propertiesFile) {
       this.dsOauth2 = dsOauth2;
       this.propertiesFile = propertiesFile;
	}
	
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		
        HttpSession session = request.getSession();
        session.setAttribute("dsOauth2", this.dsOauth2);
        session.setAttribute("propertiesFile", this.propertiesFile);
        
		long time_init = System.currentTimeMillis();
		
		
        HandlerMethod handlerMethod;
        try {

        	handlerMethod = (HandlerMethod) handler;
			Method method = handlerMethod.getMethod();
			
			String ref = ((request.getParameter("rnd") != null)?request.getParameter("rnd"):"") + " ["+request.getServletPath()+"/"+method.getName()+"]";
        
			String source = null;
			String oauth2Roles = null;
			boolean oauth2Enable = false;

			if(method.getDeclaringClass().isAnnotationPresent(JpoClass.class)) {
				JpoClass testerInfo = (JpoClass) method.getDeclaringClass().getAnnotation(JpoClass.class);
				String mySource = testerInfo.source();
				String sources = testerInfo.oauth2Roles();
				boolean enable = testerInfo.oauth2Enable();
				
				if(mySource != null && mySource.length()>0) {
					source = mySource;
				}
				if(sources != null && sources.length()>0) {
					oauth2Roles = sources;
				}
				oauth2Enable = enable;
			}
			
			if(method.isAnnotationPresent(JpoClass.class)) {
				JpoClass testerInfo = (JpoClass) method.getAnnotation(JpoClass.class);
				
				String mySource = testerInfo.source();
				String sources = testerInfo.oauth2Roles();
				boolean enable = testerInfo.oauth2Enable();
				
				if(mySource != null && mySource.length()>0) {
					source = mySource;
				}
				if(sources != null && sources.length()>0) {
					oauth2Roles = sources;
				}
				oauth2Enable = enable;
			} 

			Jpo ppo = new Jpo(request, source, propertiesFile);
			
			Map<String, Object> result = null;
			
			if(oauth2Roles != null || oauth2Enable) {
				if(request.getHeader("Authorization") == null) {
					//response.getWriter().write("something");
				    response.setStatus(HttpStatus.SC_UNAUTHORIZED);
					return false;
				} else {
					
					try {
						result = new Oauth2(this.dsOauth2, propertiesFile, ppo).checkAccess(oauth2Roles, Oauth2.getToken(request), getClientIpAddress(request), request.getServletPath());
					} catch (Exception e) {
						if(e.getMessage() != null && e.getMessage().equals(Oauth2.sc_error_notFoundToken)) {
							logger.fatal("t<"+(System.currentTimeMillis() - time_init)+"> "+ref);
							logger.fatal(e.getMessage());
							response.setStatus(HttpStatus.SC_UNAUTHORIZED); // 401
							return false;
						} else if(e.getMessage() != null && e.getMessage().equals(Oauth2.sc_error_notRolEnable)) {
							logger.fatal("t<"+(System.currentTimeMillis() - time_init)+"> "+ref);
							logger.fatal(e.getMessage());
							response.setStatus(HttpStatus.SC_FORBIDDEN); // 403
							return false;
						} else {
							e.printStackTrace();
							response.setStatus(500); // 403
							return false;
						}
					}
					
				}
			}
			
			if(result != null) {
				ppo.setData("_AUTH_USER", "usuario_id", ""+result.get("usuario_id"));
			}

			request.setAttribute("jpo", ppo);
        	
        	//request.setAttribute("jpo", new Jpo());
	 	   
        } catch (ClassCastException e) {
        	//e.printStackTrace();
        	//throw new Exception("ohRest MainService Incorrect Method"); 
        }

			/*
			
	        Map<String, String> config = new HashMap<String, String>();
	
	        config.put("type", "SQLSERVER");
	        config.put("url", "10.10.10.51");
	        config.put("db", "Inland");
	        config.put("username", "sa");
	        config.put("password", "Pr0t0c0l0");
	    	
	        this.ppo = new Jpo(config);
	        
	        request.setAttribute("jpo", this.ppo);
	        
	        */

        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    	HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
    
    @Override
    public void postHandle( HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    	Jpo ppo = (Jpo) request.getAttribute("jpo");
        if(ppo != null) {
        	ppo.finalizar();
        }
    }
	
	public static String getClientIpAddress(HttpServletRequest request) throws UnknownHostException {
	    String xForwardedForHeader = request.getHeader("X-Forwarded-For");
    	logger.debug(xForwardedForHeader);
    	if (xForwardedForHeader == null || "".equals(xForwardedForHeader)) {
	    	String ip = request.getRemoteAddr();
	    	if (ip.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
			    InetAddress inetAddress = InetAddress.getLocalHost();
			    String ipAddress = inetAddress.getHostAddress();
			    ip = ipAddress;
			}
	    	return ip;
	    } else {
	    	String ip = new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
	    	int findSep = ip.indexOf(":");
	    	if(findSep>=0) {
	    		ip = ip.substring(0, findSep);
	    	}
	        return ip;
	    }
	}
	
}
