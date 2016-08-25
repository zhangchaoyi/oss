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

//@Clear(AuthInterceptor.class)
@Before(AuthInterceptor.class)
public class AddController extends Controller{
	@Before(GET.class)
	@ActionKey("/players/add")
	public void addIndex() {
		render("add.html");
	}
	
	@Before(POST.class)
	@ActionKey("/api/players/add")
	public void queryAddPlayer() {
		String addTagInfo = getPara("addTagInfo", "new-activate");
		
		Map<String, Object> data = new LinkedHashMap<String,Object>();	
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();	
		//保存chart中数据
		Map<String, List<Integer>> seriesMap = new LinkedHashMap<String, List<Integer>>();
		
		switch(addTagInfo){
			case "new-activate":{
				List<Integer> data1 =  Arrays.asList(1,2,10,31,17,0,3,0);
				List<Integer> data2 =  Arrays.asList(0,10,110,110,10,100,50,10);
				List<Integer> data3 =  Arrays.asList(30,20,30,20,30,20,30,20);
				
				seriesMap.put("设备激活", data1);
				seriesMap.put("新增账户", data2);
				seriesMap.put("新增设备", data3);
				break;
			}
			case "players-change-rate":{
				List<Integer> data1 =  Arrays.asList(1,2,10,31,17,0,3,0);
				seriesMap.put("玩家转化率", data1);
				break;
			}
		}
		
		Set<String> type = seriesMap.keySet();
		List<String> categories = Arrays.asList("2016-08-11","2016-08-12","2016-08-13","2016-08-14","2016-08-15","2016-08-16","2016-08-17","2016-08-18");
		category.put("日期", categories);	
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		renderJson(data);
	}

	@Before(POST.class)
	@ActionKey("/api/players/add/detail")
	public void queryaddPlayerDetails() {
		String addDetailTagInfo = getPara("addDetailTagInfo", "first-game-period");		

		Map<String, Object> data = new LinkedHashMap<String,Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		//保存chart中数据
		Map<String, List<Integer>> seriesMap = new LinkedHashMap<String, List<Integer>>();
		
		switch(addDetailTagInfo){
			case "first-game-period":{
				List<String> gamePeriod = Arrays.asList("1~4 s", "5~10 s","11~30 s","31~60 s","1~3 min","3~10 min","10~30 min","30~60 min",">60 min");
				List<Integer> peopleCount = Arrays.asList(10,20,10,3,4,8,5,10,20);
				seriesMap.put("玩家数",peopleCount);
				category.put("首次游戏时长", gamePeriod);
				break;
			}case "subsidiary-account-analyze":{
				List<String> subAccount = Arrays.asList("1","2","3","4","5","6","7","8~10",">10","未统计");
				List<Integer> equipmentCount = Arrays.asList(50,30,18,3,2,5,2,1,1,10);
				seriesMap.put("设备数", equipmentCount);
				category.put("小号分析", subAccount);
				break;
			}case "area":{
				List<String> area = Arrays.asList("广东省","湖南省","广西省","福建省","江西省");
				List<Integer> peopleCount = Arrays.asList(50,30,18,13,21);
				seriesMap.put("新增人数", peopleCount);
				category.put("地区", area);
				break;			
			}case "country":{
				List<String> country = Arrays.asList("中国","美国","朝鲜","马来西亚","日本");
				List<Integer> peopleCount = Arrays.asList(50,30,18,13,21);
				seriesMap.put("新增人数", peopleCount);
				category.put("国家", country);
				break;
			}case "sex":{
				List<String> sex = Arrays.asList("男","女","未知");
				List<Integer> peopleCount = Arrays.asList(50,70,124);
				seriesMap.put("新增人数", peopleCount);
				category.put("性别", sex);
				break;
			}case "age":{
				List<String> age = Arrays.asList("1~15","16~20","21~25","26~30","31~35","36~40","41~45","46~50","51~55","56~60",">60");
				List<Integer> peopleCount = Arrays.asList(50,70,124,113,51,43,12,54,12,16,7);
				seriesMap.put("新增人数", peopleCount);
				category.put("年龄", age);
				break;
			}case "account":{
				List<String> account = Arrays.asList("admin","visitor","未知");
				List<Integer> peopleCount = Arrays.asList(18,13,21);
				seriesMap.put("新增人数", peopleCount);
				category.put("账户类型", account);
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
