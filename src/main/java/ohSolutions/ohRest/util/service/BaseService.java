package ohSolutions.ohRest.util.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ohSolutions.ohJpo.dao.Jpo;
import ohSolutions.ohJpo.dao.JpoClass;
import ohSolutions.ohRest.util.bean.ForceDownload;
import ohSolutions.ohRest.util.ejb.LocalController;
import ohSolutions.ohRest.util.security.Oauth2;

public abstract class BaseService implements LocalController {
	
	final static Logger logger = LogManager.getLogger(BaseService.class);
	
	private String dsOauth2;
	private String propertiesFile;
	
	@Override
	public Object getRespuesta(HttpServletRequest request, HttpServletResponse response, String dsOauth2, String propertiesFile) throws Exception {
		return this.getRespuesta(request, response, dsOauth2, propertiesFile, null, null);
	}
	
	@Override
	public Object getRespuesta(HttpServletRequest request, HttpServletResponse response, String dsOauth2, String propertiesFile, String methodName, Jpo parentJpo) throws Exception {
		
		this.dsOauth2 = dsOauth2;
		this.propertiesFile = propertiesFile;
		
		java.lang.reflect.Method method;
		String finalMethod = (methodName!= null)?methodName:request.getParameter("method");
		boolean comesWithResp = false;
		
		try {
			method = this.getClass().getMethod(finalMethod, Jpo.class, HttpServletRequest.class);
		} catch (SecurityException e) {
			throw new Exception("ohRest BaseService Try to access a protected method");
		} catch (NoSuchMethodException e) {
			
			try {
				method = this.getClass().getMethod(finalMethod, Jpo.class, HttpServletRequest.class, HttpServletResponse.class);
				comesWithResp = true;
			} catch (SecurityException eu) {
				throw new Exception("ohRest BaseService Try to access a protected method");
			} catch (NoSuchMethodException eu) {
				throw new Exception("ohRest BaseService Check the method name: "+finalMethod); 
			} catch (UnsupportedOperationException eu) {
				throw new Exception("ohRest BaseService Incorrect Method"); 
			}

		} catch (UnsupportedOperationException e) {
			throw new Exception("ohRest BaseService Incorrect Method"); 
		}
		String source = null;
		String oauth2Roles = null;
		boolean oauth2Enable = false;
		
		logger.debug("method.isAnnotationPresent(JpoClass.class)");
		logger.debug(method.isAnnotationPresent(JpoClass.class));
		if(method.isAnnotationPresent(JpoClass.class)) {
			JpoClass testerInfo = (JpoClass) method.getAnnotation(JpoClass.class);
			source = testerInfo.source();
			if(testerInfo.oauth2Roles().length()>0) {
				oauth2Roles = testerInfo.oauth2Roles();
			}
			oauth2Enable = testerInfo.oauth2Enable();
			logger.debug(source);
			logger.debug(oauth2Roles);
			logger.debug(oauth2Enable);
		} 

		logger.debug("this.getClass().isAnnotationPresent(JpoClass.class)");
		logger.debug(this.getClass().isAnnotationPresent(JpoClass.class));
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
			logger.debug(source);
			logger.debug(oauth2Roles);
			logger.debug(oauth2Enable);
		}
		
		if(oauth2Roles != null || oauth2Enable) {
			if(request.getHeader("Authorization") == null) {
				throw new Exception("ohRest BaseService Authorization header is required");
			} else {
				new Oauth2(this.dsOauth2, propertiesFile).checkAccess(oauth2Roles, getToken(request), getClientIpAddress(request));
			}
		}
		
		Jpo jpo;
		if(parentJpo != null) {
			jpo= new Jpo(parentJpo, source, propertiesFile);
		} else {
			jpo= new Jpo(request, source, propertiesFile);
		}
		 
		try {
			return (comesWithResp)?method.invoke(this, jpo, request, response):method.invoke(this, jpo, request);
		} catch (IllegalArgumentException e) {
			throw new Exception("ohRest BaseService Incorrect arguments Method");
		} catch (IllegalAccessException e) {
			throw new Exception("ohRest BaseService Ilegal access method");
		} catch (InvocationTargetException e) {
			jpo.rollback();
			e.getCause().printStackTrace();
			throw new Exception(e.getCause().getMessage());
		} catch (Exception e) {
			jpo.rollback();
			throw new Exception(e.getCause().getMessage());
		} finally {
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
	    	logger.debug(ip);
	    	return ip;
	    } else {
	    	String ip = new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
	    	int findSep = ip.indexOf(":");
	    	if(findSep>=0) {
	    		ip = ip.substring(0, findSep);
	    	}
	    	logger.debug(ip);
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
	
	protected boolean oauth2TokenCheck(HttpServletRequest request) throws Exception {
		return new Oauth2(this.dsOauth2, propertiesFile).checkToken(getToken(request), getClientIpAddress(request));
	}
	
	public String getDsOauth() {
		return this.dsOauth2;
	}
	
	public Object downloadFile(HttpServletResponse response, String name , String source) throws IOException {

		File file = new File(source);
		
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + name);
		response.setHeader("Content-Length", String.valueOf(file.length()));
		
		FileInputStream fileIn = new FileInputStream(file);
		
		ServletOutputStream out = response.getOutputStream();
  
		byte[] outputByte = new byte[4096];
  
		while (fileIn.read(outputByte, 0, 4096) != -1){
			out.write(outputByte, 0, 4096);
		}
		fileIn.close();
		out.flush();
		out.close();
		
		return new ForceDownload();
		
	}
	
	public Object getSharedMethod(HttpServletRequest request, HttpServletResponse response, Jpo jpo, String project, String pPackage, String pClass, String pMethod) throws Exception {
		Context ctx = new InitialContext();
		LocalController controladorLocal;
    	logger.debug("java:global/"+project+"/"+pClass+"!com.apm.business.service."+pPackage+"."+pClass);
		controladorLocal = (LocalController) ctx.lookup("java:global/"+project+"/"+pClass+"!com.apm.business.service."+pPackage+"."+pClass);
		return controladorLocal.getRespuesta(request, response, this.getDsOauth(), null, pMethod, jpo);
	}
	
}