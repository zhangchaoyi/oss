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
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;

import common.interceptor.AuthInterceptor;

@Clear(AuthInterceptor.class)
public class EquipmentController extends Controller{
	@Before(GET.class)
	@ActionKey("/players/equipment")
	public void activePlayer() {
		render("equipment.html");
	}
	
	@Before(POST.class)
	@ActionKey("/api/players/equipment")
	public void queryEquipmentPlayer() {
		String playerTagInfo = getPara("playerTagInfo", "add-players"); 
		
		Map<String, Object> data = new LinkedHashMap<String,Object>();	
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();	
		//保存chart中数据
		Map<String, List<Integer>> seriesMap = new LinkedHashMap<String, List<Integer>>();
		
		switch(playerTagInfo){
		case "add-players":{
			List<Integer> data1 =  Arrays.asList(1,20,0,3,7,10,30,10);
			seriesMap.put("新增玩家", data1);
			break;
		}
		case "active-players":{
			List<Integer> data1 =  Arrays.asList(10,2,10,13,27,0,3,1);		
			seriesMap.put("活跃玩家", data1);
			break;
		}
		case "paid-players":{
			List<Integer> data1 =  Arrays.asList(15,12,10,13,27,1,3,10);
			seriesMap.put("付费玩家", data1);
			break;
		}
	}
		
		Set<String> type = seriesMap.keySet();
		List<String> categories = Arrays.asList("iphone6s","iphone5s","iphone6","nexus5X","nexus6p","小米","一加","诺基亚");
		category.put("机型", categories);	
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		renderJson(data);
	}
	
	@Before(POST.class)
	@ActionKey("/api/players/equipment/details")
	public void queryEquipmentDetails() {
		String playerTagInfo = getPara("playerTagInfo", "add-players"); 
		String detailTagInfo = getPara("detailTagInfo", "resolution");
		
		String categoryName = "";
		switch(playerTagInfo){
			case "add-players":{
				categoryName = "新增玩家";
				break;
			}
			case "active-players":{
				categoryName = "活跃玩家";
				break;
			}
			case "paid-players":{
				categoryName = "付费玩家";
				break;
			}
		}
		
		Map<String, Object> data = new LinkedHashMap<String,Object>();	
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();	
		//保存chart中数据
		Map<String, List<Integer>> seriesMap = new LinkedHashMap<String, List<Integer>>();
		
		switch(detailTagInfo){
			case "resolution":{
				List<String> solution = Arrays.asList("720*960","640*480","480*320","1024*768","1980*1080");
				List<Integer> peopleCount = Arrays.asList(10,20,3,4,8);
				seriesMap.put(categoryName,peopleCount);
				category.put("分辨率", solution);
				break;
			}
			case "operating-system":{
				List<String> OperatingSystem = Arrays.asList("1.1","2.1","-");
				List<Integer> peopleCount = Arrays.asList(50,30,18);
				seriesMap.put(categoryName, peopleCount);
				category.put("操作系统", OperatingSystem);
				break;
			}
			case "network-mode":{
				List<String> networkMode = Arrays.asList("G3","WIFI","OTHER","GPRS","EDGE","UMTS","CDMA","EVDO_0","EVDO_A");
				List<Integer> peopleCount = Arrays.asList(50,330,108,103,201,112,85,65,45);
				seriesMap.put(categoryName, peopleCount);
				category.put("联网方式", networkMode);
				break;			
			}
			case "band-operator":{
				List<String> bandOperator = Arrays.asList("中国电信","中国联通","中国移动","-");
				List<Integer> peopleCount = Arrays.asList(50,30,18,13);
				seriesMap.put(categoryName, peopleCount);
				category.put("宽带运营商", bandOperator);
				break;
			}
			case "mobile-communication-operator":{
				List<String> mobileCommunicationOperator = Arrays.asList("男","女","未知");
				List<Integer> peopleCount = Arrays.asList(50,70,124);
				seriesMap.put(categoryName, peopleCount);
				category.put("移动通信运营商", mobileCommunicationOperator);
				break;
			}
		}
		
		Set<String> type = seriesMap.keySet();
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		renderJson(data);
		
	}
}
