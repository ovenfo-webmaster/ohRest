package ohSolutions.ohRest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ohSolutions.ohJpo.dao.Jpo;
import ohSolutions.ohJpo.dao.Procedure;
import ohSolutions.ohJpo.dao.Tabla;
import ohSolutions.ohRest.util.bean.SendGridConfig;
import ohSolutions.ohRest.util.mail.MailUtil;
import ohSolutions.ohRest.util.security.Oauth2;

/*
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ohSolutions.ohRest.util.bean.SendGridConfig;
import ohSolutions.ohRest.util.mail.MailUtil;
import com.sendgrid.helpers.mail.objects.Attachments;
*/
public class RestTest {
	/*
    @Test
    public void testEmail() throws Exception {
    	
    	String data  = "{\"attach_img_url\": \"https://workflow.inlandservices.com/assets/img/logo_mail_apm.png\",\"attach_img_name\": \"LogoAPM.png\",\"attach_img_id\": \"abc\",\"id\":11520,\"from\":\"hisadmin@inlandservices.com\",\"to\":\"oscar.huertas@inlandservices.com\",\"copy\":\"Consultor.developer9@inlandservices.com\",\"subject\":\"Proceso de nota de crédito Nro XXX - Tarea : CK Manager \\/ Supervisor CS Depot\",\"body\":\"<table border=\\\"0\\\" width=\\\"620\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" align=\\\"center\\\">\\n<tbody>\\n<tr>\\n<td bgcolor=\\\"#004165\\\">\\n<table border=\\\"0\\\" width=\\\"578\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" align=\\\"center\\\">\\n<tbody>\\n<tr>\\n<td height=\\\"16\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td align=\\\"center\\\"><img src=\\\"cid:LogoAPM.png\\\" height=\\\"100px\\\" \\/><\\/td>\\n<\\/tr>\\n<tr>\\n<td height=\\\"16\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td align=\\\"left\\\" bgcolor=\\\"#FFFFFF\\\">\\n<div>\\n<table border=\\\"0\\\" width=\\\"578\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" align=\\\"center\\\">\\n<tbody>\\n<tr>\\n<td colspan=\\\"3\\\" height=\\\"22\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<td width=\\\"498\\\">\\n<div style=\\\"font-family: calibri;\\\"><p><em>Hola.<br \\/><\\/em><\\/p>\\n<p><span style=\\\"color: #000000; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial; display: inline !important; float: none;\\\">Se le informa que se ha iniciado la tarea <strong>\\\"CK Manager \\/ Supervisor CS Depot\\\" Nro 10487<\\/strong> del proceso <strong>\\\"Proceso de nota de crédito\\\" Nro 10277<\\/strong><\\/span><\\/p>\\n<p>Puede acceder al Portal, a trav&eacute;s del link:<\\/p>\\n<ul style=\\\"list-style: none;\\\">\\n<li>URL: <strong>workflow.inlandservices.com<\\/strong><\\/li>\\n<\\/ul>\\n<p>Atte.<br \\/>APM Terminals Inland Services<\\/p><\\/div>\\n<\\/td>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td colspan=\\\"3\\\" height=\\\"22\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<\\/tbody>\\n<\\/table>\\n<\\/div>\\n<\\/td>\\n<\\/tr>\\n<tr>\\n<td height=\\\"16\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td align=\\\"center\\\">\\n<table border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" align=\\\"center\\\">\\n<tbody>\\n<tr>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<td width=\\\"498\\\">\\n<div style=\\\"font-family: arial,Arial,sans-serif; font-size: 11px; color: #999999; line-height: 14px;\\\"><a style=\\\"text-decoration: none; color: #ff6303;\\\" href=\\\"http:\\/\\/containerservices.inlandservices.com\\/apm\\\" target=\\\"_blank\\\" rel=\\\"noopener\\\">Container services<\\/a> | <a style=\\\"text-decoration: none; color: #ff6303;\\\" href=\\\"https:\\/\\/containerservices.inlandservices.com\\/\\\" target=\\\"_blank\\\" rel=\\\"noopener\\\">Portal Inland<\\/a> | <a style=\\\"text-decoration: none; color: #ff6303;\\\" href=\\\"http:\\/\\/www.alconsa.com.pe\\/\\\" target=\\\"_blank\\\" rel=\\\"noopener\\\">Portal Alconsa<\\/a><\\/div>\\n<\\/td>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<\\/tbody>\\n<\\/table>\\n<\\/td>\\n<\\/tr>\\n<tr>\\n<td height=\\\"16\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td align=\\\"left\\\">\\n<table border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" align=\\\"center\\\">\\n<tbody>\\n<tr>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<td width=\\\"498\\\">\\n<div style=\\\"font-family: arial,Arial,sans-serif; font-size: 11px; color: #76838d; line-height: 13px;\\\">&copy; APM Terminals Inland Services<\\/div>\\n<\\/td>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<\\/tbody>\\n<\\/table>\\n<\\/td>\\n<\\/tr>\\n<tr>\\n<td height=\\\"22\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<\\/tbody>\\n<\\/table>\\n<\\/td>\\n<\\/tr>\\n<\\/tbody>\\n<\\/table>\"}";
    	
    	List<Attachments> archivos = new ArrayList<Attachments>();

    	Attachments archivo = new Attachments();
    	
    	archivo.setContent(new RestUtil().fileToString("C:\\Users\\ODH003\\Pictures\\personal.jpg"));
    	archivo.setFilename("data.jpg");
    	
    	archivos.add(archivo);
    	
    	System.out.println(data);
		SendGridConfig 	sgc = new SendGridConfig();
				sgc.setAsync(false);
				sgc.setDataSource("dsinland");
				//sgc.setAttachments(archivos);
		MailUtil.sendMail("" + data, sgc);
    	
    }
	*/
	
	//@Test
    public void sendgridSincrono() throws Exception {
    	
    	Jpo miJpo = new Jpo();
    	
		Procedure cResult = miJpo.procedure("bpm.tarea_correo_pendiente_listar");
		List<Object> dResp = (List<Object>) cResult.execute();
		
		System.out.println(dResp);
		
		for(var i = 0; i < 2; i++) {
	    	String data  = "{\"attach_img_url\": \"https://workflow.inlandservices.com/assets/img/logo_mail_apm.png\",\"attach_img_name\": \"LogoAPM.png\",\"attach_img_id\": \"abc\",\"id\":11520,\"from\":\"hisadmin@inlandservices.com\",\"to\":\"oscar.huertas@inlandservices.com\",\"copy\":\"Consultor.developer9@inlandservices.com\",\"subject\":\"Proceso de nota de crédito Nro XXX - Tarea : CK Manager \\/ Supervisor CS Depot\",\"body\":\"<table border=\\\"0\\\" width=\\\"620\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" align=\\\"center\\\">\\n<tbody>\\n<tr>\\n<td bgcolor=\\\"#004165\\\">\\n<table border=\\\"0\\\" width=\\\"578\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" align=\\\"center\\\">\\n<tbody>\\n<tr>\\n<td height=\\\"16\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td align=\\\"center\\\"><img src=\\\"cid:LogoAPM.png\\\" height=\\\"100px\\\" \\/><\\/td>\\n<\\/tr>\\n<tr>\\n<td height=\\\"16\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td align=\\\"left\\\" bgcolor=\\\"#FFFFFF\\\">\\n<div>\\n<table border=\\\"0\\\" width=\\\"578\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" align=\\\"center\\\">\\n<tbody>\\n<tr>\\n<td colspan=\\\"3\\\" height=\\\"22\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<td width=\\\"498\\\">\\n<div style=\\\"font-family: calibri;\\\"><p><em>Hola.<br \\/><\\/em><\\/p>\\n<p><span style=\\\"color: #000000; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial; display: inline !important; float: none;\\\">Se le informa que se ha iniciado la tarea <strong>\\\"CK Manager \\/ Supervisor CS Depot\\\" Nro 10487<\\/strong> del proceso <strong>\\\"Proceso de nota de crédito\\\" Nro 10277<\\/strong><\\/span><\\/p>\\n<p>Puede acceder al Portal "+i+", a trav&eacute;s del link:<\\/p>\\n<ul style=\\\"list-style: none;\\\">\\n<li>URL: <strong>workflow.inlandservices.com<\\/strong><\\/li>\\n<\\/ul>\\n<p>Atte.<br \\/>APM Terminals Inland Services<\\/p><\\/div>\\n<\\/td>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td colspan=\\\"3\\\" height=\\\"22\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<\\/tbody>\\n<\\/table>\\n<\\/div>\\n<\\/td>\\n<\\/tr>\\n<tr>\\n<td height=\\\"16\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td align=\\\"center\\\">\\n<table border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" align=\\\"center\\\">\\n<tbody>\\n<tr>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<td width=\\\"498\\\">\\n<div style=\\\"font-family: arial,Arial,sans-serif; font-size: 11px; color: #999999; line-height: 14px;\\\"><a style=\\\"text-decoration: none; color: #ff6303;\\\" href=\\\"http:\\/\\/containerservices.inlandservices.com\\/apm\\\" target=\\\"_blank\\\" rel=\\\"noopener\\\">Container services<\\/a> | <a style=\\\"text-decoration: none; color: #ff6303;\\\" href=\\\"https:\\/\\/containerservices.inlandservices.com\\/\\\" target=\\\"_blank\\\" rel=\\\"noopener\\\">Portal Inland<\\/a> | <a style=\\\"text-decoration: none; color: #ff6303;\\\" href=\\\"http:\\/\\/www.alconsa.com.pe\\/\\\" target=\\\"_blank\\\" rel=\\\"noopener\\\">Portal Alconsa<\\/a><\\/div>\\n<\\/td>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<\\/tbody>\\n<\\/table>\\n<\\/td>\\n<\\/tr>\\n<tr>\\n<td height=\\\"16\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td align=\\\"left\\\">\\n<table border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" align=\\\"center\\\">\\n<tbody>\\n<tr>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<td width=\\\"498\\\">\\n<div style=\\\"font-family: arial,Arial,sans-serif; font-size: 11px; color: #76838d; line-height: 13px;\\\">&copy; APM Terminals Inland Services<\\/div>\\n<\\/td>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<\\/tbody>\\n<\\/table>\\n<\\/td>\\n<\\/tr>\\n<tr>\\n<td height=\\\"22\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<\\/tbody>\\n<\\/table>\\n<\\/td>\\n<\\/tr>\\n<\\/tbody>\\n<\\/table>\"}";
	    	
			SendGridConfig 	sgc = new SendGridConfig();
					//sgc.setAsync(true);
					//sgc.setDataSource("dsinland");
			MailUtil.sendMail("" + data, sgc);
			
			System.out.println("finalizando "+i);
		}
    	
    }
	
	//@Test
    public void sendgridSincronoLlamada() throws Exception {
    	
    	Jpo miJpo = new Jpo();
    	
		Procedure cResult = miJpo.procedure("bpm.tarea_correo_pendiente_listar");
		List<Object> dResp = (List<Object>) cResult.execute();
		
		System.out.println("--->Correos pendientes a enviar v5 - "+dResp.size());
		
		for(int i = 0; i < dResp.size(); i++) {
			
			Map<String, Integer> dElemento = (Map<String, Integer>) dResp.get(i);
			
			Procedure pEmail = miJpo.procedure("bpm.tarea_correo_pendiente_enviar");
			pEmail.input("usuario_id", "1", Jpo.INTEGER);
			pEmail.input("instancia_tarea_id", ""+dElemento.get("instancia_tarea_id"), Jpo.INTEGER);
		
			List<Object> rEmail = (List<Object>) pEmail.executeL(true); // [1, , {"id":155910}
			System.out.println(rEmail);
			//miJpo.commit();
	    	String data  = "{\"attach_img_url\": \"https://workflow.inlandservices.com/assets/img/logo_mail_apm.png\",\"attach_img_name\": \"LogoAPM.png\",\"attach_img_id\": \"abc\",\"id\":11520,\"from\":\"hisadmin@inlandservices.com\",\"to\":\"oscar.huertas@inlandservices.com\",\"copy\":\"Consultor.developer9@inlandservices.com\",\"subject\":\"Proceso de nota de crédito Nro XXX - Tarea : CK Manager \\/ Supervisor CS Depot\",\"body\":\"<table border=\\\"0\\\" width=\\\"620\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" align=\\\"center\\\">\\n<tbody>\\n<tr>\\n<td bgcolor=\\\"#004165\\\">\\n<table border=\\\"0\\\" width=\\\"578\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" align=\\\"center\\\">\\n<tbody>\\n<tr>\\n<td height=\\\"16\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td align=\\\"center\\\"><img src=\\\"cid:LogoAPM.png\\\" height=\\\"100px\\\" \\/><\\/td>\\n<\\/tr>\\n<tr>\\n<td height=\\\"16\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td align=\\\"left\\\" bgcolor=\\\"#FFFFFF\\\">\\n<div>\\n<table border=\\\"0\\\" width=\\\"578\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" align=\\\"center\\\">\\n<tbody>\\n<tr>\\n<td colspan=\\\"3\\\" height=\\\"22\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<td width=\\\"498\\\">\\n<div style=\\\"font-family: calibri;\\\"><p><em>Hola.<br \\/><\\/em><\\/p>\\n<p><span style=\\\"color: #000000; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif; font-size: medium; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: normal; orphans: 2; text-align: start; text-indent: 0px; text-transform: none; white-space: normal; widows: 2; word-spacing: 0px; -webkit-text-stroke-width: 0px; text-decoration-style: initial; text-decoration-color: initial; display: inline !important; float: none;\\\">Se le informa que se ha iniciado la tarea <strong>\\\"CK Manager \\/ Supervisor CS Depot\\\" Nro 10487<\\/strong> del proceso <strong>\\\"Proceso de nota de crédito\\\" Nro 10277<\\/strong><\\/span><\\/p>\\n<p>Puede acceder al Portal "+i+", a trav&eacute;s del link:<\\/p>\\n<ul style=\\\"list-style: none;\\\">\\n<li>URL: <strong>workflow.inlandservices.com<\\/strong><\\/li>\\n<\\/ul>\\n<p>Atte.<br \\/>APM Terminals Inland Services<\\/p><\\/div>\\n<\\/td>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td colspan=\\\"3\\\" height=\\\"22\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<\\/tbody>\\n<\\/table>\\n<\\/div>\\n<\\/td>\\n<\\/tr>\\n<tr>\\n<td height=\\\"16\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td align=\\\"center\\\">\\n<table border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" align=\\\"center\\\">\\n<tbody>\\n<tr>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<td width=\\\"498\\\">\\n<div style=\\\"font-family: arial,Arial,sans-serif; font-size: 11px; color: #999999; line-height: 14px;\\\"><a style=\\\"text-decoration: none; color: #ff6303;\\\" href=\\\"http:\\/\\/containerservices.inlandservices.com\\/apm\\\" target=\\\"_blank\\\" rel=\\\"noopener\\\">Container services<\\/a> | <a style=\\\"text-decoration: none; color: #ff6303;\\\" href=\\\"https:\\/\\/containerservices.inlandservices.com\\/\\\" target=\\\"_blank\\\" rel=\\\"noopener\\\">Portal Inland<\\/a> | <a style=\\\"text-decoration: none; color: #ff6303;\\\" href=\\\"http:\\/\\/www.alconsa.com.pe\\/\\\" target=\\\"_blank\\\" rel=\\\"noopener\\\">Portal Alconsa<\\/a><\\/div>\\n<\\/td>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<\\/tbody>\\n<\\/table>\\n<\\/td>\\n<\\/tr>\\n<tr>\\n<td height=\\\"16\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<tr>\\n<td align=\\\"left\\\">\\n<table border=\\\"0\\\" cellspacing=\\\"0\\\" cellpadding=\\\"0\\\" align=\\\"center\\\">\\n<tbody>\\n<tr>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<td width=\\\"498\\\">\\n<div style=\\\"font-family: arial,Arial,sans-serif; font-size: 11px; color: #76838d; line-height: 13px;\\\">&copy; APM Terminals Inland Services<\\/div>\\n<\\/td>\\n<td width=\\\"40\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<\\/tbody>\\n<\\/table>\\n<\\/td>\\n<\\/tr>\\n<tr>\\n<td height=\\\"22\\\">&nbsp;<\\/td>\\n<\\/tr>\\n<\\/tbody>\\n<\\/table>\\n<\\/td>\\n<\\/tr>\\n<\\/tbody>\\n<\\/table>\"}";
	    	
	    	System.out.println(data);
	    	
			if(rEmail.get(0).equals(1)) {
				System.out.println("Notificacion via correo en "+dElemento.get("instancia_tarea_id"));
				
				System.out.println(rEmail.get(2));
				
				SendGridConfig 	sgc = new SendGridConfig();
					//sgc.setJpo(miJpo);
					sgc.setAsync(true);
						//sgc.setDataSource("dsinland");
				//MailUtil.sendMail("" + data, sgc);
				MailUtil.sendMail("" + rEmail.get(2), sgc);
				
			} else {
				System.out.println("Error en notificacion via correo en "+dElemento.get("instancia_tarea_id"));
				System.out.println(rEmail.get(1));
				
			}
			
		}
		
		miJpo.commit();
		miJpo.finalizar();
    	
    }
    
    //@Test
    public void testoauth() throws Exception {
    	
    	System.out.println("Oauth2.getToken");
    	
    	String _bearer = Oauth2.getToken("Bearer");
    	System.out.println(_bearer);
    	
    	Map<String, Object> _data = this.getTokenByDinamic(_bearer);
    
    	System.out.println("Map<String, Object> getTokenByDinamic");
    	System.out.println(_data);
    	
    }
    
	@SuppressWarnings("unchecked")
	private Map<String, Object> getTokenByDinamic(String dinamicTokenId) throws Exception {
		
		
        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLSERVER");
        config.put("url", "pilotodbqa.database.windows.net");
        config.put("db", "maersk-apmtis-sqlserver-inland-dev");
        config.put("username", "adminsqlqa");
        config.put("password", "H${:3Q]pE7X&N7JW");
        
		System.out.println("--> Inicio");
		
        Jpo miJpo = new Jpo(config);
        
        if(dinamicTokenId != null && dinamicTokenId.trim().length() > 0) {
        	
            Tabla token = miJpo.tabla("oauth_access_history ACD INNER JOIN oauth_access_token ACT ON ACT.authentication_id = ACD.authentication_id");
            token.donde("ACD.token_id = '"+dinamicTokenId+"' AND logout_date IS NULL");
            
        	return (Map<String, Object>) token.obtener(this.getBinary("ACT.token")+" AS token, ACT.authentication_id");
        } else {
        	return null;
        }
	}
	
	private String getBinary(String token) {
		return "CONVERT(VARCHAR(MAX), "+token+")";
	}
	
    @SuppressWarnings("unchecked")
    //@Test
    public void oauthtestservice() throws Exception {
    	
        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLSERVER");
        config.put("url", "pilotodbqa.database.windows.net");
        config.put("db", "maersk-apmtis-sqlserver-inland-dev");
        config.put("username", "adminsqlqa");
        config.put("password", "H${:3Q]pE7X&N7JW");
        
        Jpo miJpo = new Jpo(config);
        
		Tabla tservicio = miJpo.tabla("seg.servicio");
    		tservicio.donde("nombre LIKE 'aDMUsuarioService.segusuarioValidar'");
    	
    	Map<String, Object> respuesta = (Map<String, Object>) tservicio.obtener("indicador_protegido");
    	
    	System.out.println(respuesta);
    	System.out.println(respuesta.get("indicador_protegido"));
    	System.out.println(respuesta.get("indicador_protegido").equals("1"));
    	
    }
    
    @SuppressWarnings("unchecked")
    //@Test
    public void oauthtestservicemenu() throws Exception {
    	
        Map<String, String> config = new HashMap<String, String>();

        config.put("type", "SQLSERVER");
        config.put("url", "pilotodbqa.database.windows.net");
        config.put("db", "maersk-apmtis-sqlserver-inland-dev");
        config.put("username", "adminsqlqa");
        config.put("password", "H${:3Q]pE7X&N7JW");
        
        Jpo miJpo = new Jpo(config);
        
        Tabla tServiceAccess = miJpo.tabla("seg.usuario_rol USR (NOLOCK)"
        		+ "	INNER JOIN seg.rol_menu RME (NOLOCK) ON RME.rol_id = USR.rol_id"
        		+ "	INNER JOIN seg.menu MEN (NOLOCK) ON MEN.menu_id = RME.menu_id"
        		+ "	INNER JOIN seg.menu_servicio MSE (NOLOCK) ON MSE.menu_id = RME.menu_id"
        		+ "	INNER JOIN seg.servicio SER (NOLOCK) ON SER.servicio_id = MSE.servicio_id");
        tServiceAccess.donde("USR.usuario_id = '1' AND SER.nombre LIKE 'aDMUsuarioService.segusuarisoRegistrarNuevo'");

        Map<String, Object> dServiceAccess = (Map<String, Object>) tServiceAccess.obtener("DISTINCT SER.servicio_id AS servicio_id");
    	
        System.out.println(dServiceAccess);
        
        if(dServiceAccess == null || dServiceAccess.size()==0) {
        	System.out.println("null");
        } else {
        	System.out.println("ok");
        }
        
    }

}