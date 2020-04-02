package ohSolutions.ohRest.util.service;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ohSolutions.ohJpo.dao.Jpo;
import ohSolutions.ohRest.util.ejb.LocalLoaderController;

public abstract class BaseLoaderService implements LocalLoaderController {
		
	@Override
	public void getRespuesta(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		java.lang.reflect.Method method;
		try {
			method = this.getClass().getMethod(request.getParameter("metodo"), Jpo.class, HttpServletRequest.class, HttpServletResponse.class);
		} catch (SecurityException e) {
			throw new Exception("Acceso a un metodo protegido");
		} catch (NoSuchMethodException e) {
			throw new Exception("Verifique el nombre del método: "+request.getParameter("metodo")); 
		} catch (UnsupportedOperationException e) {
			throw new Exception("Metodo incorrecto"); 
		}
		Jpo jpo = new Jpo(request,false);
		try {
			method.invoke(this, jpo, request, response) ;
		} catch (IllegalArgumentException e) {
			throw new Exception("Metodo Argumentos incorrectos");
		} catch (IllegalAccessException e) {
			throw new Exception("Metodo Acceso protegido");
		} catch (InvocationTargetException e) {
			e.getCause().printStackTrace();
			throw new Exception(e.getCause().getMessage());
		} finally {
			jpo.finalizar();
		}
		
	}

}