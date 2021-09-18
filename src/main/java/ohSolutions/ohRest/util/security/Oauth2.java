package ohSolutions.ohRest.util.security;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import ohSolutions.ohJpo.dao.Jpo;
import ohSolutions.ohJpo.dao.JpoHttpRequest;
import ohSolutions.ohJpo.dao.JpoUtil;
import ohSolutions.ohJpo.dao.Tabla;

public class Oauth2 {
	
	final static Logger logger = LogManager.getLogger(Oauth2.class);
	
	public static String sc_error_notFoundToken = "001_ohRest"; // Oauth2 Do not found token
	public static String sc_error_notRolEnable = "002_ohRest"; // Oauth2 Do not match with the roles access
	
	private Jpo ppo;
	private boolean onFinalize = true;
	private String jpoType; // SQLSERVER - POSTGRESQL
	
	public Oauth2(String source, String propertiesFile) throws Exception {
		this.ppo = new Jpo(source, propertiesFile);
		this.jpoType = JpoUtil.getPropertie(null, "jpo."+source+".type");
	}
	
	public Oauth2(String source, String propertiesFile, Jpo jpo) throws Exception {
		this.ppo = new Jpo(source, propertiesFile);
		
		if(this.ppo.hashConection == jpo.hashConection) {
			this.ppo = jpo;
			this.onFinalize = false;
		}
		this.jpoType = JpoUtil.getPropertie(null, "jpo."+source+".type");
	}
	
	// For login - Input : oauthConfig, user, roles[] | output Dinamic Token
	public String createToken(Map<String, String> oauthConfig, String user, Collection<String> roles) throws Exception {
		
		/*
			{
		      "clientId": "API_APM_INLANDNET",
		      "clientSecret": "33caa750333af31d49d39e9251ecb592",
		      "latitude": "",
		      "longitude": "",
		      "so": "Windows NT 4.0",
		      "browser": "Chrome 77.0.3865.120"
		      "ip_address" : "1.1.1.1"
		    }
		    user : user@company.com
		*/
		
		String clientId = oauthConfig.get("clientId");
		String clientSecret = oauthConfig.get("clientSecret");
		user = user.trim();
		
		// Validating relevant informacion
		if(roles.size()==0) {
			throw new Exception("ohRest Oauth2 Do not fount any roles");
		}
		
		Map<String, Object> client = loginClient(clientId, clientSecret);
		
		if(client == null){
			throw new Exception("ohRest Oauth2 Do not fount client detail");
		} 

		String autenticationId = MD5Util.MD5(clientId+"-"+user); // static token
		
		String tokenId = MD5Util.MD5(clientId+"-"+user+"-"+System.currentTimeMillis()); // dinamic token
		
		/*
		 * contentToken = {
		 *  roles : ["ROL_A", "ROL_B"],
		 *  logins : [
		 *   {
		 *   	ip_address : "",
		 *    	loginDate : date,
		 *    	tokenId : tokenId
		 *   }, ...
		 *  ]
		 * }
		 * */
		String contentToken = "";
		
		// 1.- Check if 'oauth_access_token' exist a row with authentication_id
		Map<String, Object> dToken = getToken("authentication_id", autenticationId);
		
		//Gson TEST = new Gson();
		//System.out.println(TEST.toJson(dToken));
		
		// 2.- If not exist token create the object 'contentToken' and put the roles and the login date
		boolean existAutenticationId;
		
		if(dToken == null || dToken.size()==0) {
			
			JSONObject infoToken = new JSONObject();
			
			infoToken.put("roles", roles);
			
			List<JSONObject> logins = new ArrayList<JSONObject>();
			
			logins.add(getDataLogin(tokenId, oauthConfig.get("ip_address")));
			
			infoToken.put("logins", logins);
			
			contentToken = infoToken.toString();
			
			existAutenticationId = false;
			
		// 3.- If no, load the object token 'contentToken' and replace the roles and add or change the login date
		} else {
			
			//System.out.println(dToken.get("token"));
			
			JSONObject infoToken = new JSONObject(""+dToken.get("token"));

			infoToken.put("roles", roles);
			
			JSONArray logins = infoToken.getJSONArray("logins");
			
			boolean hasToken = false;
			
			for (int i = 0; i < logins.length(); i++) {
				JSONObject login = logins.getJSONObject(i);
				if(login.has("tokenId") && login.get("tokenId").equals(oauthConfig.get("tokenId"))) {
					hasToken = true;
					login.put("loginDate", System.currentTimeMillis());
					break;
				}
			}
			
			if(!hasToken) {
				logins.put(getDataLogin(tokenId, oauthConfig.get("ip_address")));
				infoToken.put("logins", logins);
			}
			
			contentToken = infoToken.toString();

			existAutenticationId = true;
			
		}
		/*
		logger.debug("saveToken");
		logger.debug(existAutenticationId);
		logger.debug(autenticationId);
		logger.debug(contentToken);
		*/
		// 4.- Saving the token
		saveToken(existAutenticationId, autenticationId, clientId, user, clientSecret, contentToken);
		//cleanHistory(autenticationId, oauthConfig.get("ip_address")); pending adding extra informacion to do
		saveHistory(oauthConfig, autenticationId, tokenId);
		
		ppo.commitear();
		if(this.onFinalize) {
			ppo.finalizar();
		}
		
		return tokenId;

	}
	
		private JSONObject getDataLogin(String tokenId, String ip_address) {
			JSONObject 	login = new JSONObject();
			login.put("tokenId", tokenId);
			login.put("ip_address", ip_address);
			login.put("loginDate", System.currentTimeMillis());
			return login;
		}
	
	// For closing or logout
	public Object closeToken(String dinamicToken, String ipAddress) throws Exception {
		
		Map<String, Object> dToken = getTokenByDinamic(dinamicToken);
		
		// 1.- Exist token to close
		if(dToken != null && dToken.size()>0) {
			
			// 1.1 Removing ip_address logged in
			JSONObject infoToken = new JSONObject(""+dToken.get("token"));
			
			JSONArray logins = infoToken.getJSONArray("logins");
			
			boolean isUpdateToken = false;
			
			for (int i = 0; i < logins.length(); i++) {
				JSONObject login = logins.getJSONObject(i);
				if(login.has("tokenId") && login.get("tokenId").equals(dinamicToken)) {
					isUpdateToken = true;
					logins.remove(i);
					break;
				}
			}
			infoToken.put("logins", logins);
			
			// 1.2. Updating history and token
			updateHistory(dinamicToken);
			if(isUpdateToken) {
				updateToken(""+dToken.get("authentication_id"), infoToken.toString());
			}

		}
		
		ppo.commitear();
		if(this.onFinalize) {
			ppo.finalizar();
		}
		
		return true;
	}
	
	public Map<String, Object> checkAccess(String roles, String dinamicToken, String ip_address, String service) throws Exception {
		
		Map<String, Object> dToken;
		
		if(service == null) {
			dToken = getTokenByDinamic(dinamicToken);
		} else {
			dToken = getTokenByDinamicService(dinamicToken, service);
		}
		
		if(dToken == null || dToken.size()==0) {
			
			throw new Exception(sc_error_notFoundToken); // Oauth2 Do not found token
			
		} else {
			
			if(roles != null) { // Validating with roles
				// Valid if has the roles to use this method.
				JSONObject infoToken = new JSONObject(""+dToken.get("token"));
				if(!checkRoles(infoToken.getJSONArray("roles").toString(), roles.split(","))) {
					throw new Exception(sc_error_notRolEnable);
				}
			}
			
		}
		
		ppo.commitear();
		if(this.onFinalize) {
			ppo.finalizar();
		}
		
		return dToken;
		
	}
	
	// For checking if has token, and fullfill the roles
	public void checkAccess(String roles, String dinamicToken, String ip_address) throws Exception {
		this.checkAccess(roles, dinamicToken, ip_address, null);
	}
	
	// For check if has token id just
	@Deprecated
	public boolean checkToken(String dinamicToken, String ip_address) throws Exception {
		
		Map<String, Object> dToken = getTokenByDinamic(dinamicToken);
		
		if(dToken == null || dToken.size()==0) {
			throw new Exception("ohRest Oauth2 Do not found token");
		}
		
		ppo.commitear();
		if(this.onFinalize) {
			ppo.finalizar();
		}
		
		return true;
		
	}
	/*
	private void cleanHistory(String autenticationId, String ipAddress) throws Exception {
		
	    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	    Date today = Calendar.getInstance().getTime();      
		
		Tabla token = ppo.tabla("oauth_access_history");
			//token.donde("authentication_id = '"+autenticationId+"' AND ip_address = '"+ipAddress+"' AND logout_date IS NULL");
  	  		token.donde("authentication_id = '"+autenticationId+"' AND logout_date IS NULL");
  	  		token.setData("logout_date", df.format(today));
        	token.editar();
        	  
	}
	 */
	private void updateToken(String staticToken, String infoToken) throws Exception {

		Tabla token = ppo.tabla("oauth_access_token");
        	  token.donde("authentication_id = '"+staticToken+"'");
        	  token.setData("token", setBinary(infoToken));
        	  token.editar();
        	
	}
	
	private boolean checkRoles(String rolesInToken, String[] roles) {
		for(int i = 0; i < roles.length; i++) {
			if(rolesInToken.contains(roles[i])) {
				return true;
			}
		}
		return false;
	}
	
	private void saveHistory(Map<String, String> oauthConfig, String autenticationId, String tokenId) throws Exception {
		
        Tabla history = ppo.tabla("oauth_access_history");
        
        history.setData("authentication_id", autenticationId);
        if(oauthConfig.get("latitude").length()!=0) {
        	history.setData("latitude", oauthConfig.get("latitude"));
        }
        if(oauthConfig.get("longitude").length()!=0) {
        	history.setData("longitude", oauthConfig.get("longitude"));
        }
        history.setData("so", oauthConfig.get("so"));
        history.setData("browser", oauthConfig.get("browser"));
        history.setData("ip_address", oauthConfig.get("ip_address"));
        history.setData("token_id", tokenId);
        
        history.registrar();
       
	}
	
		private String setBinary(String token) {
	        if(jpoType.equals(Jpo.TYPE_POSTGRESQL)) {
	        	return "'decode('"+token+"'::text, 'escape')";
	        }
	        if(jpoType.equals(Jpo.TYPE_SQLSERVER)) {
	        	return "'CONVERT(varbinary(MAX), '"+token+"')";
	        }
			return null;
		}
		
		private String getBinary(String token) {
	        if(jpoType.equals(Jpo.TYPE_POSTGRESQL)) {
	        	return "encode("+token+"::BYTEA, 'escape')";
	        }
	        if(jpoType.equals(Jpo.TYPE_SQLSERVER)) {
	        	return "CONVERT(VARCHAR(MAX), "+token+")";
	        }
			return null;
		}
	
	private boolean updateHistory(String dinamicTokenId) throws Exception {
		
	    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	    Date today = Calendar.getInstance().getTime();      
		
        Tabla history = ppo.tabla("oauth_access_history");
	          history.donde("token_id = '"+dinamicTokenId+"'");
	          history.setData("logout_date", df.format(today));
	          history.editar();
        
        return true;

	}
	
	private void saveToken(boolean existAutenticationId, String authentication_id, String clientId, String user, String clientSecret, String contentToken) throws Exception {
		
        Tabla token = ppo.tabla("oauth_access_token");
        
        	token.setData("token_id", null);
        	token.setData("token", setBinary(contentToken));
	        token.setData("user_name", user);
	        token.setData("client_id", clientId);
	        token.setData("authentication", null);
	        token.setData("refresh_token", null);
        
        if(existAutenticationId) {
        	token.donde("authentication_id = '"+authentication_id+"'");
        	token.editar();
        } else {
        	token.setData("authentication_id", authentication_id);
        	token.registrar();
        }
        
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getToken(String tokenSearch, String authentication_id) throws Exception {
		
        Tabla token = ppo.tabla("oauth_access_token");
        
        token.donde(tokenSearch+" = '"+authentication_id+"'");
        
        return (Map<String, Object>) token.obtener(this.getBinary("token")+" AS token, authentication_id");
        
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getTokenByDinamic(String dinamicTokenId) throws Exception {
		
        if(dinamicTokenId != null && dinamicTokenId.trim().length() > 0) {
        	
            Tabla token = ppo.tabla("oauth_access_history ACD INNER JOIN oauth_access_token ACT ON ACT.authentication_id = ACD.authentication_id");
            token.donde("ACD.token_id = '"+dinamicTokenId+"' AND logout_date IS NULL");
        	
        	return (Map<String, Object>) token.obtener(this.getBinary("ACT.token")+" AS token, ACT.authentication_id");
        } else {
        	return null;
        }
                
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> getTokenByDinamicService(String dinamicTokenId, String service) throws Exception {
		
        if(dinamicTokenId != null && dinamicTokenId.trim().length() > 0) {
        	
            Tabla token = ppo.tabla("oauth_access_history ACD INNER JOIN oauth_access_token ACT ON ACT.authentication_id = ACD.authentication_id");
            token.donde("ACD.token_id = '"+dinamicTokenId+"' AND logout_date IS NULL");
        	
            Map<String, Object> dToken = (Map<String, Object>) token.obtener(this.getBinary("ACT.token")+" AS token, ACT.authentication_id, ACT.user_name AS usuario_id");
        	
        	if(dToken == null || dToken.size()==0) {
        		
        		return null;
        		
        	} else {
        		
        		String usuario_id = (String) dToken.get("usuario_id");
        		
        		Tabla tservicio = ppo.tabla("seg.servicio");
	        		  tservicio.donde("nombre LIKE '"+service+"'");
	        	
	        	Map<String, Object> indicador_protegido = (Map<String, Object>) tservicio.obtener("indicador_protegido");
        		
        		if (indicador_protegido.get("indicador_protegido") != null && indicador_protegido.get("indicador_protegido").equals("1")) {
        			
                    Tabla tServiceAccess = ppo.tabla("seg.usuario_rol USR (NOLOCK)"
                    		+ "	INNER JOIN seg.rol_menu RME (NOLOCK) ON RME.rol_id = USR.rol_id"
                    		+ "	INNER JOIN seg.menu MEN (NOLOCK) ON MEN.menu_id = RME.menu_id"
                    		+ "	INNER JOIN seg.menu_servicio MSE (NOLOCK) ON MSE.menu_id = RME.menu_id"
                    		+ "	INNER JOIN seg.servicio SER (NOLOCK) ON SER.servicio_id = MSE.servicio_id");
                    tServiceAccess.donde("USR.usuario_id = '"+usuario_id+"' AND SER.nombre LIKE '"+service+"'");

                    Map<String, Object> dServiceAccess = (Map<String, Object>) tServiceAccess.obtener("DISTINCT SER.servicio_id AS servicio_id");
                	
                    if(dServiceAccess == null || dServiceAccess.size()==0) {
                    	
                    	return null;
                    	
                    } else {
                    	
                    	return dToken;
                    	
                    }
        			
        		} else {
        			return dToken;
        		}
        		
        	}
        	
        } else {
        	return null;
        }

	}
	
	// Obtain the correct configuration from oauth_client_details
	@SuppressWarnings("unchecked")
	private Map<String, Object> loginClient(String clientId, String clientSecret) throws Exception {
        Tabla client = ppo.tabla("oauth_client_details");
        client.donde("client_id = '"+clientId+"' AND client_secret = '"+clientSecret+"'");
        return (Map<String, Object>) client.obtener("access_token_validity");
	}
	
	public String preCreateToken(Map<String, String> oauthConfig, String user, Collection<String> roles, HttpServletRequest request) throws Exception {
		oauthConfig.put("ip_address", getClientIpAddress(request));
		return this.createToken(oauthConfig, user, roles);
	}
	
	public String preCreateToken(Map<String, String> oauthConfig, String user, Collection<String> roles, JpoHttpRequest request) throws Exception {
		oauthConfig.put("ip_address", getClientIpAdd(request));
		return this.createToken(oauthConfig, user, roles);
	}
	
	public static String getClientIpAddress(HttpServletRequest request) throws UnknownHostException {
		
		JpoHttpRequest jpoRequest = new JpoHttpRequest();

		jpoRequest.setHeader("x-forwarded-for", request.getHeader("X-Forwarded-For"));
		jpoRequest.setRemoteAddr(request.getRemoteAddr());
		
		return getClientIpAdd(jpoRequest);
		
	}
	
	public static String getClientIpAdd(JpoHttpRequest request) throws UnknownHostException {
	    String xForwardedForHeader = request.getHeader("x-forwarded-for");
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
	
	public Object preCloseToken(HttpServletRequest request) throws Exception {
		return closeToken(getToken(request), getClientIpAddress(request));
	}
	
	public Object preCloseToken(JpoHttpRequest request) throws Exception {
		return closeToken(getToken(request.getHeader("authorization")), getClientIpAdd(request));
	}
	
	public static String getToken(HttpServletRequest request) {
		return getToken(request.getHeader("Authorization"));
	}
	
	public static String getToken(String authorization) {
		return authorization != null && authorization.trim().length()>6 ? authorization.substring(7) : null;
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<String> getRoles(Object roles) {
		
		List<Object> strRoles = (List<Object>) roles;
		
		Collection<String> elements = new ArrayList<String>();
		
		if(strRoles != null) {
			Map<Object, Boolean> hasRol = new HashMap<Object, Boolean>();
			for(int i = 0; i < strRoles.size(); i++) {
				List<Object> attributes = (List<Object>) strRoles.get(i);
				if(hasRol.get(attributes.get(0)) == null) {
					hasRol.put(attributes.get(0), true);
					elements.add("" + attributes.get(0));
				}
			}
		}
		
		return elements;
		
	}
	
}
