package common.controllers;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import common.interceptor.AuthInterceptor;
import common.service.RealtimeService;
import common.service.impl.RealtimeServiceImpl;
import common.utils.StringUtils;

@Clear(AuthInterceptor.class)
//@Before(AuthInterceptor.class)
public class RealtimeController extends Controller{
	private RealtimeService realtimeService = new RealtimeServiceImpl();
	@Before(GET.class)
	@ActionKey("/realtime/info")
	public void activePlayer() {
		render("info.html");
	}
	
	@Before(POST.class)
	@ActionKey("/api/realtime/beforedata")
	public void queryBeforeData(){
		Map<String,String> data = realtimeService.queryBeforeData();
		renderJson(data);
	}
	
	@Before(POST.class)
	@ActionKey("/api/realtime/realtimedata")
	public void queryRealtimeData(){
		Map<String,String> data = realtimeService.queryRealtimeData();
		renderJson(data);
	}
	
	@Before(POST.class)
	@ActionKey("/api/realtime/info")
	public void queryRealtimeInfo() {
		String detailTag = getPara("detailTag","rto");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String[] date = getParaValues("startDate[]");
		
		Map<String, Object> data = new LinkedHashMap<String,Object>();
		List<String> categories = Arrays.asList("00:00","02:00","04:00","06:00","08:00","10:00","12:00","14:00","16:00","18:00","20:00","22:00");
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		
		switch(detailTag){
			case "rto":{
				
				break;
			}
			case "equ":{
				break;
			}
			case "adp":{
				break;
			}
			case "pay":{
				break;
			}
		}
		
//		List<Long> list = Arrays.asList(1L,2L,3L,4L,5L,6L,7L,8L,9L,10L,11L,12L);
		for(String s : date){
			Random d = new Random();
			List<Long> list = new ArrayList<Long>();
			for(int i=0;i<12;i++){
				list.add((long)d.nextInt(100));
			}
			seriesMap.put(s, list);	
		}	
		Set<String> type = seriesMap.keySet();
		
		category.put("时间段", categories);
		data.put("category", category);
		data.put("type", type.toArray());
		data.put("data", seriesMap);
		renderJson(data);
	}
}
