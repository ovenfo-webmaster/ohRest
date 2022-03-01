package ohSolutions.ohRest.util.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ohSolutions.ohJpo.dao.Jpo;
import ohSolutions.ohJpo.dao.JpoClass;
import ohSolutions.ohJpo.dao.JpoRequest;
import ohSolutions.ohRest.util.bean.ForceDownload;
import ohSolutions.ohRest.util.bean.Response;
import ohSolutions.ohRest.util.security.Oauth2;

public abstract class MainService extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	public static String sc_error_notMethod = "003_ohRest"; // Method Not Allowed
	
	final static Logger logger = LogManager.getLogger(MainService.class);
	
	public abstract String datasourceOauth2();
	public abstract String defaultPropertieFile();
	
	private String dsOauth2;
	private String propertiesFile;
	private JpoRequest currentMethod;
	//private Jpo jpo;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		this.currentMethod = JpoRequest.GET;
		ejecutarControl(request,response);
	}
  
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		this.currentMethod = JpoRequest.POST;
		ejecutarControl(request,response);
	}
	
	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		this.currentMethod = JpoRequest.PUT;
		ejecutarControl(request,response);
	}
	
	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		this.currentMethod = JpoRequest.DELETE;
		ejecutarControl(request,response);
	}

	private void ejecutarControl(HttpServletRequest request, HttpServletResponse response) throws IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
		
		long time_init = System.currentTimeMillis();
		
		String method = null;
		//System.out.println(request.getPathInfo());
		if(request.getPathInfo() != null && request.getPathInfo().length()>1) {
			String[] methods = request.getPathInfo().split("/");
			
			if(methods.length == 2) {
				method = methods[1];
			}
		}

		String ref = ((request.getParameter("rnd") != null)?request.getParameter("rnd"):"") + " ["+request.getServletPath()+"/"+method+"]";
		Response objRespuesta = new Response();
				 objRespuesta.setCorrect(false);
				 
		if(method == null) {
			objRespuesta.setMessage("ohRest MainService Incorrect Method");
			String message = "t<"+(System.currentTimeMillis() - time_init)+"> "+ref;
			logger.fatal(message);
			PrintWriter texto = response.getWriter();
			texto.print(gson.toJson(objRespuesta));
		} else {
			try {
				Object result = ejecutarServicio(request, response, method);
				if(!(result != null && result.getClass().equals(ForceDownload.class))) {
					objRespuesta.setResult(result);
					objRespuesta.setCorrect(true);
					PrintWriter texto = response.getWriter();
					texto.print(gson.toJson(objRespuesta));
				}
				logger.info("t<"+(System.currentTimeMillis() - time_init)+"> "+ref);
			} catch (Exception e) {
				if(e.getMessage() != null && e.getMessage().equals(Oauth2.sc_error_notFoundToken)) {
					logger.fatal("t<"+(System.currentTimeMillis() - time_init)+"> "+ref);
					logger.fatal(e.getMessage());
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
				} else if(e.getMessage() != null && e.getMessage().equals(Oauth2.sc_error_notRolEnable)) {
					logger.fatal("t<"+(System.currentTimeMillis() - time_init)+"> "+ref);
					logger.fatal(e.getMessage());
					response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
				} else if(e.getMessage() != null && e.getMessage().equals(sc_error_notMethod)) {
					logger.fatal("t<"+(System.currentTimeMillis() - time_init)+"> "+ref);
					logger.fatal(e.getMessage());
					response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED); // 405
				} else {
					objRespuesta.setCorrect(false);
					objRespuesta.setMessage(e.getMessage());
					String message = "t<"+(System.currentTimeMillis() - time_init)+"> "+ref;
					logger.fatal(message);
					if(e.getCause()!=null){
						logger.fatal(e.getCause().getMessage(), e);
					} else {
						logger.fatal(e.getMessage(), e);
					}
					PrintWriter texto = response.getWriter();
					texto.print(gson.toJson(objRespuesta));
				}
			}
		}

	}
	
	private Object ejecutarServicio(HttpServletRequest request, HttpServletResponse response, String methodName) throws Exception {
		
		this.dsOauth2 = datasourceOauth2();
		this.propertiesFile = defaultPropertieFile();
		
		java.lang.reflect.Method method;
		
		boolean comesWithResp = false;
		
		try {
			method = this.getClass().getMethod(methodName, Jpo.class, HttpServletRequest.class);
		} catch (SecurityException e) {
			throw new Exception("ohRest MainService Try to access a protected method");
		} catch (NoSuchMethodException e) {
			
			try {
				method = this.getClass().getMethod(methodName, Jpo.class, HttpServletRequest.class, HttpServletResponse.class);
				comesWithResp = true;
			} catch (SecurityException eu) {
				throw new Exception("ohRest MainService Try to access a protected method");
			} catch (NoSuchMethodException eu) {
				throw new Exception("ohRest MainService Check the method name: "+methodName); 
			} catch (UnsupportedOperationException eu) {
				throw new Exception("ohRest MainService Incorrect Method"); 
			}

		} catch (UnsupportedOperationException e) {
			throw new Exception("ohRest MainService Incorrect Method"); 
		}
		String source = null;
		String oauth2Roles = null;
		boolean oauth2Enable = false;
		
		if(method.isAnnotationPresent(JpoClass.class)) {
			JpoClass testerInfo = (JpoClass) method.getAnnotation(JpoClass.class);
			source = testerInfo.source();
			if(testerInfo.oauth2Roles().length()>0) {
				oauth2Roles = testerInfo.oauth2Roles();
			}
			oauth2Enable = testerInfo.oauth2Enable();
			
			boolean isMethod = false;
			JpoRequest[] methods = testerInfo.method();
			for(int i = 0; i < methods.length; i++) {
				if(this.currentMethod.equals(methods[i])){
					isMethod = true;
					break;
				}
			}
			
			if(!isMethod) {
				throw new Exception(sc_error_notMethod); 
			}
		} 

		if(this.getClass().isAnnotationPresent(JpoClass.class)) {
			JpoClass testerInfo = (JpoClass) this.getClass().getAnnotation(JpoClass.class);
			if(source == null || source.length()==0) {
				source = testerInfo.source();
			}
			if(oauth2Roles == null || oauth2Roles.length()==0) {
				if(testerInfo.oauth2Roles().length()>0) {
					oauth2Roles = testerInfo.oauth2Roles();
				}
			}
			if(!oauth2Enable) {
				oauth2Enable = testerInfo.oauth2Enable();
			}
		}
		
		Jpo jpo = new Jpo(request, source, propertiesFile);
		
		if(oauth2Roles != null || oauth2Enable) {
			if(request.getHeader("Authorization") == null) {
				throw new Exception("ohRest MainService Authorization header is required");
			} else {
				new Oauth2(this.dsOauth2, propertiesFile, jpo).checkAccess(oauth2Roles, getToken(request), getClientIpAddress(request));
			}
		}
		
		try {
			return (comesWithResp)?method.invoke(this, jpo, request, response):method.invoke(this, jpo, request);
		} catch (IllegalArgumentException e) {
			throw new Exception("ohRest MainService Incorrect arguments Method");
		} catch (IllegalAccessException e) {
			throw new Exception("ohRest MainService Ilegal access method");
		} catch (InvocationTargetException e) {
			jpo.rollback();
			e.getCause().printStackTrace();
			throw new Exception(e.getCause().getMessage());
		} catch (Exception e) {
			jpo.rollback();
			throw new Exception(e.getCause().getMessage());
		} finally {
			//System.out.println("Finalizando JPO");
			jpo.finalizar();
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
	
	private String getToken(HttpServletRequest request) {
		String auto = request.getHeader("Authorization");
		return auto.substring(7);
	}
	
	protected String oauth2Token(Map<String, String> oauthConfig, String user, Collection<String> roles, HttpServletRequest request) throws Exception {
		oauthConfig.put("ip_address", getClientIpAddress(request));
		return new Oauth2(this.dsOauth2, propertiesFile).createToken(oauthConfig, user, roles);
	}
	
	protected Object oauth2TokenOut(HttpServletRequest request) throws Exception {
		return new Oauth2(this.dsOauth2, propertiesFile).closeToken(getToken(request), getClientIpAddress(request));
	}
	
	@SuppressWarnings("deprecation")
	protected boolean oauth2TokenCheck(HttpServletRequest request) throws Exception {
		return new Oauth2(this.dsOauth2, propertiesFile).checkToken(getToken(request), getClientIpAddress(request));
	}
	
	public String getDsOauth() {
		return this.dsOauth2;
	}
	
}
