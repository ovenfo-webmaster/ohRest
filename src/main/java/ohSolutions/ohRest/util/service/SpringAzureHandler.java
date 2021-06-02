package ohSolutions.ohRest.util.service;

import java.lang.reflect.Method;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpResponseMessage.Builder;
import com.microsoft.azure.functions.HttpStatus;

import ohSolutions.ohJpo.dao.Jpo;
import ohSolutions.ohJpo.dao.JpoClass;
import ohSolutions.ohJpo.dao.JpoHttpRequest;
import ohSolutions.ohRest.util.bean.Response;
import ohSolutions.ohRest.util.security.Oauth2;

public abstract class SpringAzureHandler extends AzureSpringBootRequestHandler<Jpo, Object> {

	final static Logger logger = LogManager.getLogger(SpringAzureHandler.class);
	
	public abstract String datasourceOauth2();
	public abstract String defaultPropertieFile();
	
	private String dsOauth2;
	private String propertiesFile;
	
	protected HttpResponseMessage valid(String methodName, HttpRequestMessage<Optional<String>> request, ExecutionContext context) {
		
		// Default values to change per proyect, dsOauth2 is the database security to login, propertiesFile is the file to search the properties
		this.dsOauth2 = datasourceOauth2();
		this.propertiesFile = defaultPropertieFile();
		
		// Default values to work
		//Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
		Response resp = new Response();
		long time_init = System.currentTimeMillis();
		
		String source = null;
		String oauth2Roles = null;
		boolean oauth2Enable = false;

		if(this.getClass().isAnnotationPresent(JpoClass.class)) {
			JpoClass testerInfo = this.getClass().getAnnotation(JpoClass.class);
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
		
		Builder builder = request.createResponseBuilder(HttpStatus.OK);
				builder.header("Content-Type", "application/json");
				builder.body(resp);

		try {
			
			String ref = ((request.getQueryParameters().get("rnd") != null)?request.getQueryParameters().get("rnd"):"") + " ["+request.getUri()+"]";
	        
			Method method = this.getClass().getMethod(methodName, HttpRequestMessage.class, ExecutionContext.class);
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
			
			Object dataJSON = request.getBody().get();
			 
			Jpo ppo = new Jpo(""+dataJSON, source, propertiesFile, null);
			
			JpoHttpRequest jpoRequest = new JpoHttpRequest();
			
			jpoRequest.setDsOauth2(this.dsOauth2);
			jpoRequest.setPropertiesFile(this.propertiesFile);
			
			if(request.getHeaders() != null) {
				for (Entry<String, String> entry : request.getHeaders().entrySet()) {
					jpoRequest.setHeader(entry.getKey(), entry.getValue());
			    }
			}
						
			jpoRequest.setRemoteAddr(request.getUri().getHost());
			
			ppo.setJpoRequest(jpoRequest);
			
			if(oauth2Roles != null || oauth2Enable) {
				if(request.getHeaders().get("authorization") == null) {
				    return builder.status(HttpStatus.UNAUTHORIZED).build();
				} else {
					
					try {
						new Oauth2(this.dsOauth2, propertiesFile, ppo).checkAccess(oauth2Roles, Oauth2.getToken(jpoRequest.getHeader("authorization")), Oauth2.getClientIpAdd(jpoRequest));
					} catch (Exception e) {
						if(e.getMessage() != null && e.getMessage().equals(Oauth2.sc_error_notFoundToken)) {
							logger.fatal("t<"+(System.currentTimeMillis() - time_init)+"> "+ref);
							logger.fatal(e.getMessage());
							return builder.status(HttpStatus.UNAUTHORIZED).build();	// 401
						} else if(e.getMessage() != null && e.getMessage().equals(Oauth2.sc_error_notRolEnable)) {
							logger.fatal("t<"+(System.currentTimeMillis() - time_init)+"> "+ref);
							logger.fatal(e.getMessage());
							return builder.status(HttpStatus.FORBIDDEN).build();	// 403
						}
					}
					
				}
			}
			
			Response resp_fin = (Response) handleRequest(ppo, context);
			resp.setMessage(resp_fin.getMessage());
			resp.setResult(resp_fin.getResult());
			resp.setCorrect(resp_fin.isIsCorrect());
			
		} catch (NoSuchMethodException | SecurityException e) {
			resp.setMessage(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			resp.setMessage(e.getMessage());
			e.printStackTrace();
		}
		
		return builder.build();
		
	}
	
}
