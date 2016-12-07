package common.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.Cookie;
import org.apache.log4j.Logger;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;

import common.interceptor.RootInterceptor;
import common.mysql.DbSelector;
import common.service.AdminService;
import common.service.impl.AdminServiceImpl;
import common.utils.EncryptUtils;
import common.utils.JsonToMap;
import common.utils.StringUtils;

@Clear
public class AdminController extends Controller {
	private static Logger logger = Logger.getLogger(AdminController.class);
	private AdminService as = new AdminServiceImpl();
	private Set<String> dbs = DbSelector.getDbs().keySet();

	/**
	 * 没有权限页
	 * 
	 * @author chris
	 * @role 所有
	 */
	@Before(GET.class)
	@ActionKey("/admin/authority/error")
	public void authorityError() {
		renderError(401, "authorityError.html");
	}

	/**
	 * 新增用户页
	 * 
	 * @author chris
	 * @role root
	 */
	@Before({ GET.class, RootInterceptor.class })
	@ActionKey("/admin/createUser")
	public void createUserIndex() {
		render("createUser.html");
	}

	/**
	 * 管理用户页
	 * 
	 * @author chris
	 * @role root
	 */
	@Before({ GET.class, RootInterceptor.class })
	@ActionKey("/admin/manageUsers")
	public void manageUsersIndex() {
		render("userManagement.html");
	}

	/**
	 * 用户个人权限页
	 * 
	 * @author chris
	 * @role all
	 */
	@Before(GET.class)
	@ActionKey("/admin/account")
	public void accountIndex() {
		render("account-permission.html");
	}

	/**
	 * 新增用户接口
	 * 
	 * @author chris
	 * @getParam username 用户名(加密后)
	 * @getParam password 密码(加密后)
	 * @getParam role 角色(加密后)
	 * @getParam key 加密密钥
	 * @return 返回成功/失败信息
	 * @role root
	 */
	@Before({ POST.class, RootInterceptor.class })
	@ActionKey("/api/admin/createUser")
	public void createUser() {
		String username = getPara("username");
		String password = getPara("password");
		String role = getPara("role", "data_guest");
		String selectList = getPara("selectList", "");
		String key = getPara("key");
		Map<String, Object> map = JsonToMap.toMap(selectList);
		Map<String, String> sqlMap = convertToSqlData(map);

		logger.info("params: {" + "username:" + username + ",password:" + password + ",role:" + role + ",key:" + key
				+ ",selectList:" + selectList + "}");
		Map<String, String> data = new HashMap<String, String>();

		try {
			username = EncryptUtils.aesDecrypt(username, key);
			password = EncryptUtils.aesDecrypt(password, key);
			role = EncryptUtils.aesDecrypt(role, key);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception:", e);
		}

		// username 是否已经存在
		boolean exist = as.existUser(username);
		if (exist == true) {
			data.put("message", "exist");
			renderJson(data);
			return;
		}

		logger.info("username:" + username + ",password:" + password + ",role:" + role + ",sqlMap:" + sqlMap);
		logger.info("dbs:" + dbs);

		// 对各个数据库同时进行用户管理,修改完成恢复原数据库
		String originDb = DbSelector.getDbName();
		boolean succeed = false;
		for (String db : dbs) {
			DbSelector.setDbName(db);
			succeed = as.signupUser(username, password, role, sqlMap);
		}
		DbSelector.setDbName(originDb);

		if (succeed == true) {
			logger.info("signup successfully");
			data.put("message", "successfully");
			renderJson(data);
			return;
		}
		logger.info("createUser failed");
		data.put("message", "failed");
		renderJson(data);
	}

	/**
	 * 用户管理接口 由于多个数据源的用户是一致的,只需要查询其中一个
	 * 
	 * @author chris
	 * @return 返回所有用户列表
	 * @role root
	 */
	@Before({ POST.class, RootInterceptor.class })
	@ActionKey("/api/admin/manageUsers")
	public void manageUsers() {
		List<List<String>> data = as.queryUsers();
		logger.info("data:" + data);
		renderJson(data);
	}

	/**
	 * 删除用户接口
	 * 
	 * @author chris
	 * @getParam users[] 所选删除用户
	 * @role root
	 */
	@Before({ POST.class, RootInterceptor.class })
	@ActionKey("/api/admin/deleteUsers")
	public void deleteUsers() {
		String users = StringUtils.arrayToQueryString(getParaValues("users[]"));
		logger.info("params:{" + "users[]:" + users + "}");
		logger.info("dbs:" + dbs);

		// 对各个数据库同时进行用户管理,修改完成恢复原数据库
		String originDb = DbSelector.getDbName();
		int deleted = 0;
		for (String db : dbs) {
			DbSelector.setDbName(db);
			deleted = as.deleteByUserName(users);
		}
		DbSelector.setDbName(originDb);

		Map<String, String> data = new HashMap<String, String>();
		data.put("message", String.valueOf(deleted));
		logger.info("return:" + deleted);
		renderJson(data);
	}

	/**
	 * 修改用户角色接口
	 * 
	 * @author chris
	 * @getParam username 用户名
	 * @getParam roles[] 所需修改角色
	 * @role root
	 */
	@Before({ POST.class, RootInterceptor.class })
	@ActionKey("/api/admin/changeRole")
	public void changeRole() {
		String username = getPara("username");
		String[] queryRole = getParaValues("roles[]");
		logger.info("params:{" + "username:" + username + ",queryRole:" + queryRole + "}");
		logger.info("dbs:" + dbs);

		Map<String, String> data = new HashMap<String, String>();

		// 对各个数据库同时进行用户管理,修改完成恢复原数据库
		String originDb = DbSelector.getDbName();
		for (String db : dbs) {
			DbSelector.setDbName(db);
			as.changeRoles(username, queryRole);
		}
		DbSelector.setDbName(originDb);

		data.put("message", "successfully");
		logger.info("data:" + data);
		renderJson(data);
	}

	/**
	 * 用户个人帐号权限页 从cookie中获取用户名,cookie不存在则返回空数据
	 * 
	 * @author chris
	 * @role all
	 */
	@Before(POST.class)
	@ActionKey("/api/admin/account")
	public void accountPermission() {
		Cookie cookie = getCookieObject("login");
		String username = "chris";
		if (cookie != null) {
			username = cookie.getValue();
		}
		logger.info("get username from cookie:" + "{" + username + "}");
		List<List<String>> data = new ArrayList<List<String>>();
		if ("".equals(username)) {
			logger.info("cookie 不存在");
			renderJson(data);
			return;
		}
		data = as.queryUsers(username);
		renderJson(data);
	}

	/**
	 * 解析获取的js数据map,返回插入mysql表的数据,Map<type,"000"> 0为无,1为有 按顺序一一对应 实时概况 -- 1 报表
	 * -- 1 玩家分析---6 (新增 活跃 留存 有效 设备分析 生命轨迹) 付费分析---4 (付费数据 付费行为 付费转化 付费排行 付费玩家)
	 * 流失分析--1 在线分析--3 (在线分析 在线习惯 在线人数) 渠道分析--2(渠道分析 渠道短链追踪) 系统分析--5 (道具分析 任务分析
	 * 关卡分析 等级分析 虚拟币分析) 版本分析--1 自定义事件--2 (事件列表 漏斗管理) 运营支持--2(用户反馈 数据报警)
	 * 数据挖掘--2(聚类分析 新玩家价值) 市场分析--1 技术支持--4 (在线参数 实时日志 崩溃分析 用户错误) 管理中心--2(新增用户角色
	 * 用户角色管理) 服务器--4 (马来服 iOS uc 测试)
	 * 
	 * 
	 * @param map
	 * @return
	 */
	private Map<String, String> convertToSqlData(Map<String, Object> map) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("realtime", "0");
		result.put("form", "0");
		result.put("player-analyse", "000000");
		result.put("paid-analyse", "00000");
		result.put("loss", "0");
		result.put("online-analyse", "000");
		result.put("channel-analyse", "00");
		result.put("system-analyse", "00000");
		result.put("version-analyse", "0");
		result.put("custom-event", "00");
		result.put("op-support", "00");
		result.put("data-dig", "00");
		result.put("market-analyse", "0");
		result.put("tech-support", "0000");
		result.put("management-center", "00");
		result.put("server", "0000");
		try {
			for (Map.Entry<String, Object> m : map.entrySet()) {
				@SuppressWarnings("unchecked")
				List<Object> objList = (List<Object>) m.getValue();
				List<String> list = convertObjectToString(objList);
				String key = m.getKey();
				String str = result.get(key);
				char[] ca = str.toCharArray();
				switch (m.getKey()) {
				case "realtime":
					ca[0] = '1';
					break;
				case "form":
					ca[0] = '1';
					break;
				case "player-analyse":
					for (String l : list) {
						switch (l) {
						case "new-players":
							ca[0] = '1';
							break;
						case "active-players":
							ca[1] = '1';
							break;
						case "retain":
							ca[2] = '1';
							break;
						case "effective":
							ca[3] = '1';
							break;
						case "equipment":
							ca[4] = '1';
							break;
						case "circle":
							ca[5] = '1';
							break;
						}
					}
					break;
				case "paid-analyse":
					for (String l : list) {
						switch (l) {
						case "paid-data":
							ca[0] = '1';
							break;
						case "paid-deed":
							ca[1] = '1';
							break;
						case "paid-transform":
							ca[2] = '1';
							break;
						case "paid-rank":
							ca[3] = '1';
							break;
						case "paid-players":
							ca[4] = '1';
							break;	
						}
					}
					break;
				case "loss":
					ca[0] = '1';
					break;
				case "online-analyse":
					for (String l : list) {
						switch (l) {
						case "online-analyse":
							ca[0] = '1';
							break;
						case "online-habits":
							ca[1] = '1';
							break;
						case "online-count":
							ca[2] = '1';
							break;
						}
					}
					break;
				case "channel-analyse":
					for (String l : list) {
						switch (l) {
						case "channel-analyse":
							ca[0] = '1';
							break;
						case "channel-trace":
							ca[1] = '1';
							break;
						}
					}
					break;
				case "system-analyse":
					for (String l : list) {
						switch (l) {
						case "prop-analyse":
							ca[0] = '1';
							break;
						case "task-analyse":
							ca[1] = '1';
							break;
						case "pass-analyse":
							ca[2] = '1';
							break;
						case "rank-analyse":
							ca[3] = '1';
							break;
						case "money-count":
							ca[4] = '1';
							break;
						}
					}
					break;
				case "version-analyse":
					ca[0] = '1';
					break;
				case "custom-event":
					for (String l : list) {
						switch (l) {
						case "event-list":
							ca[0] = '1';
							break;
						case "filter-management":
							ca[1] = '1';
							break;
						}
					}
					break;
				case "op-support":
					for (String l : list) {
						switch (l) {
						case "user-feedback":
							ca[0] = '1';
							break;
						case "data-alert":
							ca[1] = '1';
							break;
						}
					}
					break;
				case "data-dig":
					for (String l : list) {
						switch (l) {
						case "cluster-analyse":
							ca[0] = '1';
							break;
						case "newplayers-value":
							ca[1] = '1';
							break;
						}
					}
					break;
				case "market-analyse":
					ca[0] = '1';
					break;
				case "tech-support":
					for (String l : list) {
						switch (l) {
						case "online-param":
							ca[0] = '1';
							break;
						case "realtime-log":
							ca[1] = '1';
							break;
						case "crash-analyse":
							ca[2] = '1';
							break;
						case "user-mistake":
							ca[3] = '1';
							break;
						}
					}
					break;
				case "management-center":
					for (String l : list) {
						switch (l) {
						case "create-role":
							ca[0] = '1';
							break;
						case "manage-role":
							ca[1] = '1';
							break;
						}
					}
					break;
				case "server":
					for (String l : list) {
						switch (l) {
						case "malai":
							ca[0] = '1';
							break;
						case "iOS":
							ca[1] = '1';
							break;
						case "uc":
							ca[2] = '1';
							break;
						case "test":
							ca[3] = '1';
							break;
						}
					}
					break;
				}
				result.put(key, String.valueOf(ca));
			}
		} catch (Exception e) {
			logger.info("exception", e);
		}
		return result;
	}

	private List<String> convertObjectToString(List<Object> objList) {
		List<String> list = new ArrayList<String>();
		for (Object obj : objList) {
			String str = String.valueOf(obj);
			str = str.replace("\"", "");
			list.add(str);
		}
		return list;
	}

}
