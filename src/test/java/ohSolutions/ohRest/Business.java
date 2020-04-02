package ohSolutions.ohRest;

import ohSolutions.ohRest.util.service.MainService;

public class Business extends MainService {
	
	private static final long serialVersionUID = 1L;

	@Override
	public String datasourceOauth2() {
		return "dsoauth2";
	}

	@Override
	public String defaultPropertieFile() {
		return null;
	}
	
}
