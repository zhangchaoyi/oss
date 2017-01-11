package common.controllers;

import java.util.Map;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;

import common.config.PropertiesConfigs;

@Clear
public class ApiController extends Controller{
	@Before(GET.class)
	@ActionKey("/api/prop/channels")
	public void channels(){
		Map<String, String> channels = PropertiesConfigs.getChannels();
		renderJson(channels);
	}
}
