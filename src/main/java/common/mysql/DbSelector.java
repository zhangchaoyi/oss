package common.mysql;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;

/**
 * 保存db的列表
 * @author chris
 * 将当前使用的db 保存在 ServletContext 中,每次进行读取或者修改
 * 将初始化的PropKit的值读取到 私有变量 Map<String, String> dbs 中 
 * dbs为系统配置的数据源,只要程序运行则不会改变,userDbs为某个用户具有权限的数据源,每次登录时需要清空再修改
 */
public class DbSelector {
	private static Logger logger = Logger.getLogger(DbSelector.class);
	private static Map<String, String> dbs = new LinkedHashMap<String, String>();
	private static Map<String, String> userDbs = new LinkedHashMap<String, String>();
	
	public static void setDbName(String db) {
		JFinal.me().getServletContext().setAttribute("db", db);
	}

	public static String getDbName() {
		String db = JFinal.me().getServletContext().getAttribute("db").toString();
		return db;
	}
	
	public static void initDbs(){
		String jdbcList = PropKit.get("jdbcList");
		logger.info("PropKit jdbcList:" + jdbcList);
		if(jdbcList==null){
			logger.info("PropKit load failed,please config the config.txt fill with key-value 'jdbcList' ");
			return;
		}
		String[] dbsSplit = jdbcList.trim().split(":");
		for(String db : dbsSplit){
			dbs.put(db, getDbName(db));
		}
		logger.info("dbs:[" + dbs + "]");
	} 
	
	public static Map<String, String> getDbs(){
		return dbs;
	}
	
	public static String getDbName(String db){
		String dbName = "";
		switch(db){
		case "malai":
			dbName = "马来服";
			break;
		case "uc":
			dbName = "UC服";
			break;
		case "test":
			dbName = "测试服";
			break;
		case "ios":
			dbName = "iOS服";
			break;
		}
		return dbName;
	}
	
	public static Map<String, String> getUserDbs(){
		return userDbs;
	}
	
	/**
	 * 处理用户的数据库列表 四位String数字(0/1) 分别代表 马来服 iOS uc 测试 
	 * 去除userDbs 中在dbs不包含的情況,即取dbs和userDbs的交集
	 * 每次使用setUserDbs 需要清空用户的userDbs 同时设置userDbs的第一个db为当前默认的db
	 * @param dbList
	 * @return
	 */
	public static int setUserDbs(String dbList){
		clearUserDbs();
		if(!dbList.contains("1")){
			logger.info("用户不具有任何数据库权限");
			return 0;
		}
		char[] dbCList = dbList.toCharArray();
		if(dbCList.length!=4){
			logger.info("用户的数据库列表不等于四位");
			return 0;
		}
		for(int i=0;i<dbCList.length;i++){
			if(dbCList[i]=='1'){
				switch(i){
				case 0:
					userDbs.put("malai", getDbName("malai"));
					break;
				case 1:
					userDbs.put("ios", getDbName("ios"));
					break;
				case 2:
					userDbs.put("uc", getDbName("uc"));
					break;
				case 3:
					userDbs.put("test", getDbName("test"));
					break;
				}
			}
		}
		//保留dbs 和 userDbs 的交集
 		Iterator<Map.Entry<String, String>> it = userDbs.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, String> entry = it.next();
			if(!dbs.containsKey(entry.getKey())){
				it.remove();
			}
		}
		
		//设置userDbs第一个key为某用户当前的数据库
		for(Map.Entry<String, String> entry : userDbs.entrySet()){
			setDbName(entry.getKey());
			logger.info("current db:"+entry.getKey());
			break;
		}
		logger.info("after setUserDbs:"+userDbs);
		return 1;
	}
	
	/**
	 * 清空userDbs
	 */
	public static void clearUserDbs(){
		userDbs.clear();
	}
}
