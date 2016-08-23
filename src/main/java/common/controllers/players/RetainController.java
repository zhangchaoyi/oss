package common.controllers.players;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;

import common.interceptor.AuthInterceptor;

@Clear(AuthInterceptor.class)
public class RetainController extends Controller{
	@Before(POST.class)
	@ActionKey("/api/retain")
	public void retain() {
		Map<String, Object> data = new LinkedHashMap<String,Object>();
		
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		Map<String, List<Integer>> addPlayer = new LinkedHashMap<String, List<Integer>>();
		
		//保存chart中数据
		Map<String, List<Integer>> seriesMap = new LinkedHashMap<String, List<Integer>>();
				
//		List<String> data1 =  Arrays.asList("10%","20%","10%","13%","7%","10%","30%","10%");
//		List<String> data2 =  Arrays.asList("10%","10%","10%","10%","10%","10%","10%","10%");
//		List<String> data3 =  Arrays.asList("30%","20%","30%","20%","30%","20%","30%","20%");
		List<Integer> data1 =  Arrays.asList(1,20,0,3,7,10,30,10);
		List<Integer> data2 =  Arrays.asList(10,10,10,10,10,10,10,10);
		List<Integer> data3 =  Arrays.asList(30,20,30,20,30,20,30,20);
		
		seriesMap.put("次日留存率", data1);
		seriesMap.put("7日留存率", data2);
		seriesMap.put("30日留存率", data3);
		
		Set<String> type = seriesMap.keySet();
		List<String> categories = Arrays.asList("2016-08-11","2016-08-12","2016-08-13","2016-08-14","2016-08-15","2016-08-16","2016-08-17","2016-08-18");
		List<Integer> addPlayers = Arrays.asList(10,30,34,5,6,2,13,34);
		
		category.put("日期", categories);
		addPlayer.put("新增玩家", addPlayers);
		
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("addPlayer", addPlayer);
		data.put("data", seriesMap);
		renderJson(data);
	}
}
