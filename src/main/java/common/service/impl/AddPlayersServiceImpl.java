package common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.model.CreateRole;
import common.model.DeviceInfo;
import common.service.AddPlayersService;

public class AddPlayersServiceImpl implements AddPlayersService {
	// 整理数据 源数据可能存在缺失 如2016-08-01当天的数据可能返回null,则数据列表长度 和 时间列表长度 不能一一对应
	public List<Long> queryAddPlayersData(List<String> categories, String startDate, String endDate) {
		String addPlayersSql = "select DATE_FORMAT(create_time,'%Y-%m-%d') date,count(*) count from create_role where create_time >= ? and create_time <= ? group by DATE_FORMAT(create_time,'%Y-%m-%d')";
		List<CreateRole> addPlayersSource = CreateRole.dao.find(addPlayersSql, startDate, endDate);

		List<Long> data = new ArrayList<Long>();
		Map<String, Long> sort = new TreeMap<String, Long>();
		// 将日期作为Map的key保证查询出来的数据不会缺失
		for (String category : categories) {
			sort.put(category, 0L);
		}
		for (CreateRole cr : addPlayersSource) {
			sort.put(cr.getStr("date"), cr.getLong("count"));
		}
		for (Map.Entry<String, Long> entry : sort.entrySet()) {
			data.add(entry.getValue());
		}
		return data;
	}

	//查询设备激活的信息
	public List<Long> queryDeviceInfoData(List<String> categories, String startDate, String endDate) {
		String activateEquipmentSql = "select DATE_FORMAT(create_time,'%Y-%m-%d') date,count(*) count from device_info where create_time >= ? and create_time <= ? group by DATE_FORMAT(create_time,'%Y-%m-%d')";
		List<DeviceInfo> activateEquipmentSource = DeviceInfo.dao.find(activateEquipmentSql, startDate, endDate);
		return dealQueryDeviceInfoData(activateEquipmentSource, categories);
	}

	//查询新增设备的信息
	public List<Long> queryAddEquipmentData(List<String> categories, String startDate, String endDate) {
		String addEquipmentSql = "select count(B.openudid) count,DATE_FORMAT(create_time,'%Y-%m-%d') date from (select openudid from device_info where create_time >= ? and create_time <= ? ) A left join (select openudid,min(create_time) create_time from create_role where create_time >= ? and create_time <= ? group by openudid) B on A.openudid = B.openudid where B.openudid is not null group by DATE_FORMAT(create_time,'%Y-%m-%d')";
		List<DeviceInfo> addEquipmentSource = DeviceInfo.dao.find(addEquipmentSql, startDate, endDate, startDate,
				endDate);
		return dealQueryDeviceInfoData(addEquipmentSource, categories);
	}

	//处理查询后的中间数据
	private List<Long> dealQueryDeviceInfoData(List<DeviceInfo> source, List<String> categories) {
		List<Long> data = new ArrayList<Long>();
		Map<String, Long> sort = new TreeMap<String, Long>();
		// 将日期作为Map的key保证查询出来的数据不会缺失
		for (String category : categories) {
			sort.put(category, 0L);
		}
		for (DeviceInfo cr : source) {
			sort.put(cr.getStr("date"), cr.getLong("count"));
		}
		for (Map.Entry<String, Long> entry : sort.entrySet()) {
			data.add(entry.getValue());
		}
		return data;
	}

	//计算玩家转化率百分比 返回List<Long> 兼容格式
	public List<Long> dealQueryPlayersChangeRate(List<Long> activateEquipment, List<Long> addEquipment) {
		List<Long> data = new ArrayList<Long>();
		for (int i = 0; i < activateEquipment.size(); i++) {
			if (activateEquipment.get(i) == 0) {
				data.add(0L);
				continue;
			}
			double result = (double) addEquipment.get(i) / (double) activateEquipment.get(i);
			BigDecimal bg = new BigDecimal(result);
			double percent = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			percent *= 100;
			data.add(Double.valueOf(percent).longValue());
		}
		return data;
	}
}
