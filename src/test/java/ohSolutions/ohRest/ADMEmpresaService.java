package ohSolutions.ohRest;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import ohSolutions.ohJpo.dao.Jpo;
import ohSolutions.ohJpo.dao.JpoClass;
import ohSolutions.ohJpo.dao.JpoRequest;
import ohSolutions.ohJpo.dao.Procedure;

@WebServlet(
    name = "ADMEmpresaServiceImp",
    urlPatterns = {"/module/adm/ADMEmpresaServiceImp/*"}
)
@JpoClass(source = "dsinland", oauth2Enable = true)
public class ADMEmpresaService extends Business {
	
	private static final long serialVersionUID = 1L;

	@JpoClass(oauth2Enable = true, method = {JpoRequest.GET})
	public Object gesempresaListarRol(Jpo ppo, HttpServletRequest request) throws Exception {
		Procedure pResult = ppo.procedure("ges.empresa_listar_rol","ADM");
		pResult.input("empresa_id", Jpo.DECIMAL);
		pResult.input("documento", Jpo.STRING);
		pResult.input("razon_social", Jpo.STRING);
		pResult.input("razon_comercial", Jpo.STRING);
		pResult.input("fecha_registro_min", Jpo.DATE);
		pResult.input("fecha_registro_max", Jpo.DATE);
		pResult.input("unidad_negocio_id", Jpo.DECIMAL);
		pResult.input("tipo_documento", Jpo.DECIMAL);
		pResult.input("abreviatura", Jpo.STRING);
		pResult.input("unic_nombre", Jpo.STRING);
		pResult.input("catd_descripcion", Jpo.STRING);
		pResult.input("tipo_rol_id", Jpo.DECIMAL);
		pResult.input("tipo_rol_ids", Jpo.STRING);
		pResult.input("page", Jpo.INTEGER);
		pResult.input("size", Jpo.INTEGER);
		Object ohb_response = pResult.executeL();
		ppo.commit();
		return ohb_response;
	}

}
