package ohSolutions.ohRest.util.ejb;

import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ohSolutions.ohJpo.dao.Jpo;

@Local
public interface LocalController {
	
	public Object getRespuesta(HttpServletRequest request, HttpServletResponse response, String dsOauth2, String propertiesFile) throws Exception;
	
	public Object getRespuesta(HttpServletRequest request, HttpServletResponse response, String dsOauth2, String propertiesFile, String methodName, Jpo jpo) throws Exception;

}