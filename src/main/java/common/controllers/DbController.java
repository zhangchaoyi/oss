package common.controllers;

import java.util.Map;

import org.apache.log4j.Logger;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import common.mysql.DbSelector;

@Clear
public class DbController extends Controller{
	private static Logger logger = Logger.getLogger(DbController.class);
	
	/**
	 * 选择db
	 * 需要进行权限校验
	 * @author chris
	 */
	@Before(POST.class)
	@ActionKey("/api/changeDb")
	public void changeDb() {
		String dbName = getPara("db","");
		logger.info("params:{"+"dbName"+dbName+"}");
		if(!dbName.equals("malai") && !dbName.equals("uc") && !dbName.equals("test") && !dbName.equals("ios")){
			renderText("failed");
			return;
		}
		//校验数据库权限
		Map<String,String> dbs = DbSelector.getUserDbs();
		if(!dbs.containsKey(dbName)){
			renderText("failed");
			return;
		}
		DbSelector.setDbName(dbName);
		renderText("succeed");
	}
}
