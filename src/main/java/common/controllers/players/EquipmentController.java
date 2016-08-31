package common.controllers.players;

import java.util.ArrayList;
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
import common.model.DeviceInfo;
import common.service.EquipmentAnalyzeService;
import common.service.impl.EquipmentAnalyzeServiceImpl;

@Clear(AuthInterceptor.class)
public class EquipmentController extends Controller {
	private EquipmentAnalyzeService equipmentAnalyzeService = new EquipmentAnalyzeServiceImpl();

	@Before(GET.class)
	@ActionKey("/players/equipment")
	public void activePlayer() {
		render("equipment.html");
	}

	@Before(POST.class)
	@ActionKey("/api/players/equipment")
	public void queryEquipmentPlayer() {
		String playerTagInfo = getPara("playerTagInfo", "add-players");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		startDate = startDate + " 00:00:00";
		endDate = endDate + " 23:59:59";

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		// 保存chart中数据
		Map<String, List<Long>> seriesMap = new LinkedHashMap<String, List<Long>>();
		List<String> categories = new ArrayList<String>();

		switch (playerTagInfo) {
		case "add-players": {
			List<Long> equipmentsCount = new ArrayList<Long>();
			List<DeviceInfo> addPlayersEquipment = equipmentAnalyzeService.queryaddPlayersEquipment(startDate, endDate);
			for (DeviceInfo di : addPlayersEquipment) {
				categories.add(di.getStr("model"));
				equipmentsCount.add(di.getLong("count"));
			}

			seriesMap.put("新增玩家", equipmentsCount);
			break;
		}
		case "active-players": {
			List<Long> equipmentsCount = new ArrayList<Long>();
			List<DeviceInfo> activePlayersEquipment = equipmentAnalyzeService.queryaddPlayersEquipment(startDate,
					endDate);
			for (DeviceInfo di : activePlayersEquipment) {
				categories.add(di.getStr("model"));
				equipmentsCount.add(di.getLong("count"));
			}

			seriesMap.put("活跃玩家", equipmentsCount);
			break;
		}
		case "paid-players": {
			// List<Integer> data1 = Arrays.asList(15,12,10,13,27,1,3,10);
			// seriesMap.put("付费玩家", data1);
			// break;
		}
		}

		Set<String> type = seriesMap.keySet();
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
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		startDate = startDate + " 00:00:00";
		endDate = endDate + " 23:59:59";

		Map<String, Object> data = new LinkedHashMap<String, Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		// 保存chart中数据
		Map<String, List<Long>> seriesMap = new LinkedHashMap<String, List<Long>>();

		String categoryName = "";
		switch (playerTagInfo) {
			case "add-players": {
				categoryName = "新增玩家";
				switch (detailTagInfo) {
					case "resolution": {
						List<DeviceInfo> resolution = equipmentAnalyzeService.queryAddPlayersEquipmentResolution(startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for (DeviceInfo di : resolution) {
							categories.add(di.getStr("resolution"));
							peopleCount.add(di.getLong("count"));
						}
						seriesMap.put(categoryName, peopleCount);
						category.put("分辨率", categories);
						break;
					}
					case "operating-system": {
						List<DeviceInfo> os = equipmentAnalyzeService.queryAddPlayersEquipmentOs(startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for (DeviceInfo di : os) {
							categories.add(di.getStr("os"));
							peopleCount.add(di.getLong("count"));
						}
						seriesMap.put(categoryName, peopleCount);
						category.put("操作系统", categories);
						break;
					}
					case "network-mode": {
						List<DeviceInfo> net = equipmentAnalyzeService.queryAddPlayersEquipmentNet(startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for (DeviceInfo di : net) {
							categories.add(di.getStr("net"));
							peopleCount.add(di.getLong("count"));
						}
						seriesMap.put(categoryName, peopleCount);
						category.put("联网方式", categories);
						break;
					}
					case "band-operator": {
						List<DeviceInfo> bandOperator = equipmentAnalyzeService.queryAddPlayersEquipmentBandOperator(startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for (DeviceInfo di : bandOperator) {
							categories.add(di.getStr("carrier"));
							peopleCount.add(di.getLong("count"));
						}
						seriesMap.put(categoryName, peopleCount);
						category.put("宽带运营商", categories);
						break;
					}
				}
				break;
			}
			case "active-players": {
				categoryName = "活跃玩家";
				switch(detailTagInfo){
					case "resolution":{
						List<DeviceInfo> resolution = equipmentAnalyzeService.queryActivePlayersEquipmentResolution(startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for(DeviceInfo di : resolution){
							categories.add(di.getStr("resolution"));
							peopleCount.add(di.getLong("count"));
						}
						seriesMap.put(categoryName,peopleCount);
						category.put("分辨率", categories);
						break;
					}
					case "operating-system":{
						List<DeviceInfo> os = equipmentAnalyzeService.queryActivePlayersEquipmentOs(startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for(DeviceInfo di : os){
							categories.add(di.getStr("os"));
							peopleCount.add(di.getLong("count"));
						}
						seriesMap.put(categoryName, peopleCount);
						category.put("操作系统", categories);
						break;
					}
					case "network-mode":{
						List<DeviceInfo> net = equipmentAnalyzeService.queryActivePlayersEquipmentNet(startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for(DeviceInfo di : net){
							categories.add(di.getStr("net"));
							peopleCount.add(di.getLong("count"));
						}
						seriesMap.put(categoryName, peopleCount);
						category.put("联网方式", categories);
						break;			
					}
					case "band-operator":{
						List<DeviceInfo> bandOperator = equipmentAnalyzeService.queryActivePlayersEquipmentBandOperator(startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for(DeviceInfo di : bandOperator){
							categories.add(di.getStr("carrier"));
							peopleCount.add(di.getLong("count"));
						}
						seriesMap.put(categoryName, peopleCount);
						category.put("宽带运营商", categories);
						break;
					}
				}
				break;
			}
			case "paid-players": {
				categoryName = "付费玩家";
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
