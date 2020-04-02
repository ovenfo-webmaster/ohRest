package ohSolutions.ohRest.util.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ohSolutions.ohRest.util.bean.ForceDownload;
import ohSolutions.ohRest.util.bean.Response;
import ohSolutions.ohRest.util.ejb.LocalController;
import ohSolutions.ohRest.util.security.Oauth2;

public abstract class BaseController extends HttpServlet {

	final static Logger logger = LogManager.getLogger(BaseController.class);
	
	private static final long serialVersionUID = 1L;
	
	public abstract String prefijoEJB();
	public abstract String paqueteEJB();
	public abstract String datasourceOauth2();
	public abstract String defaultPropertieFile();
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ejecutarControl(request,response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ejecutarControl(request,response);
	}

	private void ejecutarControl(HttpServletRequest request, HttpServletResponse response) throws IOException {

		long time_init = System.currentTimeMillis();
		String ref = (request.getParameter("rnd") != null)?request.getParameter("rnd"):"";
		
		if(request.getParameter("package") != null && request.getParameter("package") != null && request.getParameter("package") != null) {
			ref += " ["+request.getParameter("package")+"."+request.getParameter("class")+"."+request.getParameter("method")+"]";
		}
		
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		//response.setContentType("text/html; charset=UTF-8");
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

		Response objRespuesta = new Response();
				 objRespuesta.isCorrect(false);
				 
		try {
			Object result = ejecutarServicio(request,response);
			if(!(result != null && result.getClass().equals(ForceDownload.class))) {
				objRespuesta.setResult(result);
				objRespuesta.isCorrect(true);
				PrintWriter texto = response.getWriter();
				texto.print(gson.toJson(objRespuesta));
			}
			logger.info("t<"+(System.currentTimeMillis() - time_init)+"> "+ref);
		} catch (Exception e) {
			
			if(e.getMessage() != null && e.getMessage().equals(Oauth2.sc_error_notFoundToken)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
				logger.warn("t<"+(System.currentTimeMillis() - time_init)+"> "+ref);
			} else if(e.getMessage() != null && e.getMessage().equals(Oauth2.sc_error_notRolEnable)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
				logger.warn("t<"+(System.currentTimeMillis() - time_init)+"> "+ref);
			} else {
				objRespuesta.isCorrect(false);
				objRespuesta.setMessage(e.getMessage());
				String message = "t<"+(System.currentTimeMillis() - time_init)+"> "+ref;
				//logger.error(message);
				logger.fatal(message);
				if(e.getCause()!=null){
					//logger.error(e.getCause().getMessage(), e);
					logger.fatal(e.getCause().getMessage(), e);
				} else {
					//logger.error(e.getMessage(), e);
					logger.fatal(e.getMessage(), e);
				}
				PrintWriter texto = response.getWriter();
				texto.print(gson.toJson(objRespuesta));
			}

		}
		
	}
		
	private Object ejecutarServicio(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Object respuesta;
		
		Context ctx = new InitialContext();
		
		LocalController controladorLocal;
		
		String paquete = request.getParameter("package");
		if(paquete !=null && paquete.length()>0){
			paquete = paquete+".";
		} else {
			paquete = "";
		}
		String clase = request.getParameter("class");

		String rutaEJB = prefijoEJB()+clase+paqueteEJB()+paquete+clase;
		try {
			controladorLocal = (LocalController) ctx.lookup(rutaEJB);
		} catch (Exception e) {
			throw new Exception("Paquete y/o Clase incorrecta: "+paquete+clase);
		}
		respuesta = controladorLocal.getRespuesta(request, response, datasourceOauth2(), defaultPropertieFile());
		
		return respuesta;
		
	}
		
}