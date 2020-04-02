package ohSolutions.ohRest.util.ejb;

import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Local
public interface LocalLoaderController {
	
	public void getRespuesta(HttpServletRequest request, HttpServletResponse response) throws Exception;

}