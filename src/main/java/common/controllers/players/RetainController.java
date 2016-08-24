package common.controllers.players;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
	
	@Before(POST.class)
	@ActionKey("/api/retain/customize")
	public void customizeRetain() {
		String period = getPara("period","queryDay");
		String playerType = getPara("playerType");
		String gameType = getPara("gameType");
		String times = getPara("times");
		String retentinoType = getPara("retentionType");
		
		String playerParam="";
		String periodParam="";

		switch(period){
			case "queryDay":{
				periodParam = "日";
				break;
			}
			case "queryWeek":{
				periodParam = "周";
				break;
			}
			case "queryMonth":{
				periodParam = "月";
				break;
			}
		}

		Map<String, Object> data = new LinkedHashMap<String,Object>();
		List<String> tableHeader = new LinkedList<String>();
		List<Object> sourceData = new LinkedList<Object>();
		
		tableHeader.addAll(Arrays.asList("首次使用日", "玩家数", "第N"+periodParam+"后 保留玩家"));
		for(int i=1; i<8; i++){
			tableHeader.add("+"+ i + periodParam);
		}
		tableHeader.add("+14"+periodParam);
		tableHeader.add("+30"+periodParam);
		
		for(int i=0; i<10; i++){
			List<String> perData = Arrays.asList("2016-08-1"+i, String.valueOf(i), "0", "0", "0","0","0","0","0","0","0");
			sourceData.add(perData);
		}
		data.put("header", tableHeader);
		data.put("data", sourceData);
		renderJson(data);
	}
	
	@Before(POST.class)
	@ActionKey("/api/retain/equipment/rate")
	public void equipmentRetainRate() {
		Map<String, Object> data = new LinkedHashMap<String,Object>();
		
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		
		//保存chart中数据
		Map<String, List<Integer>> seriesMap = new LinkedHashMap<String, List<Integer>>();
	
		List<Integer> data1 =  Arrays.asList(1,20,0,3,7,10,30,10);
		List<Integer> data2 =  Arrays.asList(10,10,10,10,10,10,10,10);
		List<Integer> data3 =  Arrays.asList(30,20,30,20,30,20,30,20);
		
		seriesMap.put("次日留存率", data1);
		seriesMap.put("7日留存率", data2);
		seriesMap.put("30日留存率", data3);
		
		Set<String> type = seriesMap.keySet();
		List<String> categories = Arrays.asList("2016-08-11","2016-08-12","2016-08-13","2016-08-14","2016-08-15","2016-08-16","2016-08-17","2016-08-18");
		
		category.put("日期", categories);
		
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		renderJson(data);
	}
	
	@Before(POST.class)
	@ActionKey("/api/retain/equipment/detail")
	public void equipmentRetainDetail() {

		Map<String, Object> data = new LinkedHashMap<String,Object>();
		List<String> tableHeader = new LinkedList<String>();
		List<Object> sourceData = new LinkedList<Object>();
		
		tableHeader.addAll(Arrays.asList("首次使用日", "设备数", "第N天后 保留设备"));
		for(int i=1; i<8; i++){
			tableHeader.add("+"+ i + "日");
		}
		tableHeader.add("+14"+"日");
		tableHeader.add("+30"+"日");
		
		for(int i=0; i<10; i++){
			List<String> perData = Arrays.asList("2016-08-1"+i, String.valueOf(i), "0%", "0%", "0%","0%","0%","0%","0%","0%","0%");
			sourceData.add(perData);
		}
		data.put("header", tableHeader);
		data.put("data", sourceData);
		renderJson(data);
	}
}
