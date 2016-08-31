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
import common.service.ActivePlayersService;
import common.service.AddPlayersService;
import common.service.impl.ActivePlayersServiceImpl;
import common.service.impl.AddPlayersServiceImpl;
import common.utils.DateUtils;

@Clear(AuthInterceptor.class)
//@Before(AuthInterceptor.class)
public class ActiveController extends Controller{
	private AddPlayersService addPlayersService = new AddPlayersServiceImpl();
	private ActivePlayersService activePlayersService = new ActivePlayersServiceImpl();
	
	@Before(GET.class)
	@ActionKey("/players/active")
	public void activePlayer() {
		render("active.html");
	}
	
	@Before(POST.class)
	@ActionKey("/api/players/active")
	public void queryActivePlayer() {
		String playerTag = getPara("playerTag", "dau");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		
		startDate = startDate + " 00:00:00";
		endDate = endDate + " 23:59:59";
		
		Map<String, Object> data = new LinkedHashMap<String,Object>();	
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();	
		//保存chart中数据
		Map<String, List<Long>> seriesMap = new LinkedHashMap<String, List<Long>>();
		List<String> categories = DateUtils.getDateList(startDate, endDate);
		
		
		switch(playerTag){
			case "dau":{
				List<Long> addPlayers = addPlayersService.queryAddPlayersData(categories, startDate, endDate);
				List<Long> dau =  activePlayersService.queryDau(categories,startDate,endDate);
				activePlayersService.queryActivePlayersInfo(categories,startDate,endDate);			
				seriesMap.put("新增玩家", addPlayers);
				seriesMap.put("DAU", dau);
//				seriesMap.put("付费玩家", data1);
//				seriesMap.put("非付费玩家", data2);
				
				break;
			}
			case "dauwaumau":{
				seriesMap = activePlayersService.queryActivePlayersInfo(categories, startDate, endDate);
				break;
			}
			case "daumau":{
				List<Double> data1 =  Arrays.asList(1.5,1.2,1.0,1.3,2.7,1.0,3.0,1.0);
//				
//				seriesMap.put("DAU/MAU", data1);
				break;
			}
		}
		
		Set<String> type = seriesMap.keySet();
//		List<String> categories = Arrays.asList("2016-08-11","2016-08-12","2016-08-13","2016-08-14","2016-08-15","2016-08-16","2016-08-17","2016-08-18");
		category.put("日期", categories);	
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		renderJson(data);
	}
	
	@Before(POST.class)
	@ActionKey("/api/players/active/details")
	public void queryActiveDetail() {
		String detailTagInfo = getPara("detailTagInfo", "played-days");
		
		Map<String, Object> data = new LinkedHashMap<String,Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		//保存chart中数据
		Map<String, List<Integer>> seriesMap = new LinkedHashMap<String, List<Integer>>();
		
		switch(detailTagInfo){
			case "played-days":{
				List<String> playedDays = Arrays.asList("1 天", "2-3 天","4-7 天","8-14 天","15-30 天","31-90 天","91-180 天","181-365 天","365+ 天");
				List<Integer> peopleCount = Arrays.asList(10,20,10,3,4,8,5,10,20);
				seriesMap.put("人数",peopleCount);
				category.put("已玩天数", playedDays);
				break;
			}
			case "rank":{
				List<String> rank = Arrays.asList("1","10","100");
				List<Integer> peopleCount = Arrays.asList(50,30,18);
				seriesMap.put("人数", peopleCount);
				category.put("等级", rank);
				break;
			}
			case "area":{
				List<String> area = Arrays.asList("广东省","湖南省","广西省","福建省","江西省");
				List<Integer> peopleCount = Arrays.asList(50,30,18,13,21);
				seriesMap.put("人数", peopleCount);
				category.put("地区", area);
				break;			
			}
			case "country":{
				List<String> country = Arrays.asList("中国","美国","朝鲜","马来西亚","日本");
				List<Integer> peopleCount = Arrays.asList(50,30,18,13,21);
				seriesMap.put("人数", peopleCount);
				category.put("国家", country);
				break;
			}
			case "sex":{
				List<String> sex = Arrays.asList("男","女","未知");
				List<Integer> peopleCount = Arrays.asList(50,70,124);
				seriesMap.put("人数", peopleCount);
				category.put("性别", sex);
				break;
			}
			case "age":{
				List<String> age = Arrays.asList("1~15","16~20","21~25","26~30","31~35","36~40","41~45","46~50","51~55","56~60",">60");
				List<Integer> peopleCount = Arrays.asList(50,70,124,113,51,43,12,54,12,16,7);
				seriesMap.put("人数", peopleCount);
				category.put("年龄", age);
				break;
			}
			case "account":{
				List<String> account = Arrays.asList("admin","visitor","未知");
				List<Integer> peopleCount = Arrays.asList(18,13,21);
				seriesMap.put("人数", peopleCount);
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
