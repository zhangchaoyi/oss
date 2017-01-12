package common.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import common.mysql.DbSelector;
import common.service.ApiService;
import common.service.impl.ApiServiceImpl;

@Clear
public class ApiController extends Controller{
	private ApiService as = new ApiServiceImpl();
	
	@Before(GET.class)
	@ActionKey("/api/prop/channels")
	public void channels(){
		Map<String, Object> serverChannels = new HashMap<String, Object>();
		Set<String> dbs = DbSelector.getDbs().keySet();
		for(String db : dbs){
			Map<String, String> qc = as.queryChannels(db);
			if(!qc.isEmpty()){
				serverChannels.put(db, qc);
			}
		}
		renderJson(serverChannels);
	}
}
