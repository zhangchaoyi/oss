package common.mysql;

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
 */
public class DbSelector {
	private static Logger logger = Logger.getLogger(DbSelector.class);
	private static Map<String, String> dbs = new LinkedHashMap<String, String>();

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
			dbName = "IOS服";
			break;
		}
		return dbName;
	}
}
