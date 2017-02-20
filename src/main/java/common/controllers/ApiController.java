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
/**
 * 定义api接口
 * @author chris
 */
@Clear
public class ApiController extends Controller{
	private ApiService as = new ApiServiceImpl();
	
	@Before(GET.class)
	@ActionKey("/api/prop/chver")
	public void channels(){
		Map<String, Object> serverChVer = new HashMap<String, Object>();
		Set<String> dbs = DbSelector.getDbs().keySet();
		for(String db : dbs){
			Map<String, Object> qd = as.queryChannelsVersions(db);
			serverChVer.put(db, qd);
		}
		renderJson(serverChVer);
	}	
}
