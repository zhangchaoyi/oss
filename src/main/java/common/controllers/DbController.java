package common.controllers;

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
	 */
	@Before(POST.class)
	@ActionKey("/api/changeDb")
	public void changeDb() {
		String dbName = getPara("db","");
		logger.info("params:{"+"dbName"+dbName+"}");
		if(!dbName.equals("malai") && !dbName.equals("uc")){
			renderText("fail");
			return;
		}
		DbSelector.setDbName(dbName);
		renderText("succeed");
	}
}
