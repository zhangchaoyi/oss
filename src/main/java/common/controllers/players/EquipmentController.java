package common.controllers.players;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;

import common.interceptor.AuthInterceptor;
import common.model.DeviceInfo;
import common.service.EquipmentAnalyzeService;
import common.service.impl.EquipmentAnalyzeServiceImpl;
import common.utils.StringUtils;

//@Clear(AuthInterceptor.class)
@Before(AuthInterceptor.class)
public class EquipmentController extends Controller {
	private static Logger logger = Logger.getLogger(EquipmentController.class);
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
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
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
			List<DeviceInfo> addPlayersEquipment = equipmentAnalyzeService.queryAddPlayersEquipment(icons, startDate, endDate);
			for (DeviceInfo di : addPlayersEquipment) {
				String model = di.getStr("model");
				Long count = di.getLong("count");
				categories.add((model==null||"".equals(model))? "-" : model);
				equipmentsCount.add(count==null? 0 : count);
			}

			seriesMap.put("新增玩家", equipmentsCount);
			break;
		}
		case "active-players": {
			List<Long> equipmentsCount = new ArrayList<Long>();
			List<DeviceInfo> activePlayersEquipment = equipmentAnalyzeService.queryActivePlayersEquipment(icons, startDate,
					endDate);
			for (DeviceInfo di : activePlayersEquipment) {
				String model = di.getStr("model");
				Long count = di.getLong("count");
				categories.add((model==null||"".equals(model))? "-" : model);
				equipmentsCount.add(count==null? 0 : count);
			}

			seriesMap.put("活跃玩家", equipmentsCount);
			break;
		}
		case "paid-players": {
			List<Long> equipmentsCount = new ArrayList<Long>();
			List<DeviceInfo> paidPlayersEquipment = equipmentAnalyzeService.queryPaidPlayersEquipment(icons, startDate, endDate);
			
			for (DeviceInfo di : paidPlayersEquipment) {
				String model = di.getStr("model");
				Long count = di.getLong("count");
				categories.add((model==null||"".equals(model))? "-" : model);
				equipmentsCount.add(count==null? 0 : count);
			}
			seriesMap.put("付费玩家", equipmentsCount);
			break;
		}
		}

		Set<String> type = seriesMap.keySet();
		category.put("机型", categories);
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		logger.debug("<EquipmentController> queryEquipmentPlayer:" + data);
		renderJson(data);
	}

	@Before(POST.class)
	@ActionKey("/api/players/equipment/details")
	public void queryEquipmentDetails() {
		String playerTagInfo = getPara("playerTagInfo", "add-players");
		String detailTagInfo = getPara("detailTagInfo", "resolution");
		String icons = StringUtils.arrayToQueryString(getParaValues("icon[]"));
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
						List<DeviceInfo> resolution = equipmentAnalyzeService.queryAddPlayersEquipmentResolution(icons, startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for (DeviceInfo di : resolution) {
							String reso = di.getStr("resolution");
							Long count = di.getLong("count");
							categories.add((reso==null||"".equals(reso))? "-":reso);
							peopleCount.add(count==null? 0L:count);
						}
						seriesMap.put(categoryName, peopleCount);
						category.put("分辨率", categories);
						break;
					}
					case "operating-system": {
						List<DeviceInfo> os = equipmentAnalyzeService.queryAddPlayersEquipmentOsVersion(icons, startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for (DeviceInfo di : os) {
							String osV = di.getStr("os_version");
							Long count = di.getLong("count");
							categories.add((osV==null||"".equals(osV))? "-" : osV);
							peopleCount.add(count==null? 0L:count);
						}
						seriesMap.put(categoryName, peopleCount);
						category.put("操作系统", categories);
						break;
					}
					case "network-mode": {
						List<DeviceInfo> net = equipmentAnalyzeService.queryAddPlayersEquipmentNet(icons, startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for (DeviceInfo di : net) {
							String netStr = di.getStr("net");
							Long count = di.getLong("count");
							categories.add((netStr==null||"".equals(netStr))? "-" : netStr);
							peopleCount.add(count==null? 0L:count);
						}
						seriesMap.put(categoryName, peopleCount);
						category.put("联网方式", categories);
						break;
					}
					case "band-operator": {
						List<DeviceInfo> bandOperator = equipmentAnalyzeService.queryAddPlayersEquipmentBandOperator(icons, startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for (DeviceInfo di : bandOperator) {
							String carrier = di.getStr("carrier");
							Long count = di.getLong("count");
							categories.add((carrier==null||"".equals(carrier))? "-" : carrier);
							peopleCount.add(count==null? 0L:count);
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
						List<DeviceInfo> resolution = equipmentAnalyzeService.queryActivePlayersEquipmentResolution(icons, startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for(DeviceInfo di : resolution){
							String reso = di.getStr("resolution");
							Long count = di.getLong("count");
							categories.add((reso==null||"".equals(reso))? "-":reso);
							peopleCount.add(count==null? 0L:count);
						}
						seriesMap.put(categoryName,peopleCount);
						category.put("分辨率", categories);
						break;
					}
					case "operating-system":{
						List<DeviceInfo> os = equipmentAnalyzeService.queryActivePlayersEquipmentOsVersion(icons, startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for(DeviceInfo di : os){
							String osV = di.getStr("os_version");
							Long count = di.getLong("count");
							categories.add((osV==null||"".equals(osV))? "-" : osV);
							peopleCount.add(count==null? 0L:count);
						}
						seriesMap.put(categoryName, peopleCount);
						category.put("操作系统", categories);
						break;
					}
					case "network-mode":{
						List<DeviceInfo> net = equipmentAnalyzeService.queryActivePlayersEquipmentNet(icons, startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for(DeviceInfo di : net){
							String netStr = di.getStr("net");
							Long count = di.getLong("count");
							categories.add((netStr==null||"".equals(netStr))? "-" : netStr);
							peopleCount.add(count==null? 0L:count);
						}
						seriesMap.put(categoryName, peopleCount);
						category.put("联网方式", categories);
						break;			
					}
					case "band-operator":{
						List<DeviceInfo> bandOperator = equipmentAnalyzeService.queryActivePlayersEquipmentBandOperator(icons, startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for(DeviceInfo di : bandOperator){
							String carrier = di.getStr("carrier");
							Long count = di.getLong("count");
							categories.add((carrier==null||"".equals(carrier))? "-" : carrier);
							peopleCount.add(count==null? 0L:count);
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
				switch(detailTagInfo){
					case "resolution":{
						List<DeviceInfo> resolution = equipmentAnalyzeService.queryPaidPlayersEquipmentResolution(icons, startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for(DeviceInfo di : resolution){
							String reso = di.getStr("resolution");
							Long count = di.getLong("count");
							categories.add((reso==null||"".equals(reso))? "-":reso);
							peopleCount.add(count==null? 0L:count);
						}
						seriesMap.put(categoryName,peopleCount);
						category.put("分辨率", categories);
						break;
					}
					case "operating-system":{
						List<DeviceInfo> os = equipmentAnalyzeService.queryPaidPlayersEquipmentOsVersion(icons, startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for(DeviceInfo di : os){
							String osV = di.getStr("os_version");
							Long count = di.getLong("count");
							categories.add((osV==null||"".equals(osV))? "-" : osV);
							peopleCount.add(count==null? 0L:count);
						}
						seriesMap.put(categoryName, peopleCount);
						category.put("操作系统", categories);
						break;
					}
					case "network-mode":{
						List<DeviceInfo> net = equipmentAnalyzeService.queryPaidPlayersEquipmentNet(icons, startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for(DeviceInfo di : net){
							String netStr = di.getStr("net");
							Long count = di.getLong("count");
							categories.add((netStr==null||"".equals(netStr))? "-" : netStr);
							peopleCount.add(count==null? 0L:count);
						}
						seriesMap.put(categoryName, peopleCount);
						category.put("联网方式", categories);
						break;			
					}
					case "band-operator":{
						List<DeviceInfo> bandOperator = equipmentAnalyzeService.queryPaidPlayersEquipmentBandOperator(icons, startDate, endDate);
						List<String> categories = new ArrayList<String>();
						List<Long> peopleCount = new ArrayList<Long>();
						for(DeviceInfo di : bandOperator){
							String carrier = di.getStr("carrier");
							Long count = di.getLong("count");
							categories.add((carrier==null||"".equals(carrier))? "-" : carrier);
							peopleCount.add(count==null? 0L:count);
						}
						seriesMap.put(categoryName, peopleCount);
						category.put("宽带运营商", categories);
						break;
					}
				}
				break;
			}
		}

		Set<String> type = seriesMap.keySet();
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("data", seriesMap);
		logger.debug("<EquipmentController> queryEquipmentDetails:" + data);
		renderJson(data);

	}
}
