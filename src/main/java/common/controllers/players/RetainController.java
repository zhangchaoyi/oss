package common.controllers.players;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
import common.service.RetainPlayersService;
import common.service.impl.RetainPlayersServiceImpl;
import common.utils.DateUtils;
import common.utils.StringUtils;

@Clear(AuthInterceptor.class)
//@Before(AuthInterceptor.class)
public class RetainController extends Controller{
	private RetainPlayersService retainPlayersService = new RetainPlayersServiceImpl();
	
	@Before(GET.class)
	@ActionKey("/players/retain")
	public void retain() {
		render("retain.html");
	}
	
	@Before(GET.class)
	@ActionKey("/players/retain-customize")
	public void retainCustomize() {
		render("retain-customize.html");
	}
	
	@Before(GET.class)
	@ActionKey("/players/retain-equipment")
	public void retainEquipment() {
		render("retain-equipment.html");
	}
	
	@Before(POST.class)
	@ActionKey("/api/players/retain")
	public void queryRetain() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		List<String> categories = DateUtils.getDateList(startDate, endDate);
		Map<String, Object> queryData = retainPlayersService.queryRetainUser(categories, icons, startDate, endDate);
		
		Map<String, List<String>> category = new HashMap<String, List<String>>();
		Map<String, Object> addPlayer = new HashMap<String, Object>();
		Map<String, Object> activeDevice = new HashMap<String, Object>();

		//保存chart中数据
		Map<String, Object> seriesMap = new LinkedHashMap<String, Object>();
		seriesMap.put("次日留存率", queryData.get("nDR"));
		seriesMap.put("7日留存率", queryData.get("sDR"));
		seriesMap.put("30日留存率", queryData.get("mR"));
		
		Set<String> type = seriesMap.keySet();
		
		category.put("日期", categories);
		addPlayer.put("新增玩家", queryData.get("add"));
		activeDevice.put("激活设备", queryData.get("activeDevice"));
		
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("addPlayer", addPlayer);
		data.put("activeDevice", activeDevice);
		data.put("data", seriesMap);
		data.put("nDRRateAvg", queryData.get("nDRRateAvg"));
		data.put("sDRRateAvg", queryData.get("sDRRateAvg"));
		data.put("mRRateAvg", queryData.get("mRRateAvg"));
		renderJson(data);
	}
	
	@Before(POST.class)
	@ActionKey("/api/players/retain-customize")
	public void queryRetainCustomize() {
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
	@ActionKey("/api/players/retain-equipment/rate")
	public void queryRetainEquipmentRate() {
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		List<String> categories = DateUtils.getDateList(startDate, endDate);
		Map<String, Object> queryData = retainPlayersService.queryRetainEquipment(categories, icons, startDate, endDate);
		
		Map<String, Object> data = new HashMap<String,Object>();
		Map<String, List<String>> category = new HashMap<String, List<String>>();		
		
		//保存chart中数据
		Map<String, Object> seriesMap = new HashMap<String, Object>();
			
		seriesMap.put("次日留存率", queryData.get("fD"));
		seriesMap.put("7日留存率", queryData.get("sevenD"));
		seriesMap.put("30日留存率", queryData.get("ttD"));
		
		//处理表数据
		List<String> tableHeader = new LinkedList<String>();
		Map<String, Object> tableData = new LinkedHashMap<String, Object>();
		
		tableHeader.addAll(Arrays.asList("首次使用日", "设备数", "第N天后 保留设备"));
		for(int i=1; i<8; i++){
			tableHeader.add("+"+ i + "日");
		}
		tableHeader.add("+14"+"日");
		tableHeader.add("+30"+"日");
		
		tableData.put("addEquipment", queryData.get("addEquipment"));
		tableData.put("fD", queryData.get("fD"));
		tableData.put("sD", queryData.get("sD"));
		tableData.put("tD", queryData.get("tD"));
		tableData.put("fourD", queryData.get("fourD"));
		tableData.put("fifD", queryData.get("fifD"));
		tableData.put("sixD", queryData.get("sixD"));
		tableData.put("sevenD", queryData.get("sevenD"));
		tableData.put("ftD", queryData.get("ftD"));
		tableData.put("ttD", queryData.get("ttD"));
		
		
		Set<String> type = seriesMap.keySet();
		category.put("日期", categories);
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		data.put("tableData", tableData);
		data.put("header", tableHeader);
		renderJson(data);
	}
}
