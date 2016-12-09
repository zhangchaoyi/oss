package common.controllers.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;

import common.interceptor.GmInterceptor;
import common.mysql.DbSelector;
import common.service.OperationService;
import common.service.impl.OperationServiceImpl;
import common.utils.Contants;
@Clear
public class GmRecordController extends Controller {
	private static Logger logger = Logger.getLogger(GmRecordController.class);
	private OperationService os = new OperationServiceImpl();
	
	/**
	 * 操作记录页
	 * @author chris
	 * @role gm
	 */
	@Before({GET.class, GmInterceptor.class})
	@ActionKey("/operation/record")
	public void recordIndex() {
		render("record.html");
	}
	
	/**
	 * 查询gm 操作记录
	 * @author chris
	 */
	@Before(POST.class)
	@ActionKey("/api/operation/record/list")
	public void queryRecrod() {
		String startDate = getPara("startDate", "");
		String endDate = getPara("endDate", "");
		String icons = getPara("icons", "");
		String type = getPara("type", "");
		logger.info("param:{"+",startDate:"+startDate+",endDate:"+endDate+",icos:"+icons+",type:"+type+"}");
		
		if("none".equals(icons)){
			icons = DbSelector.getDbName();
		}
		String address = Contants.getAddressFromIcon(icons);
		List<List<String>> tableData = os.queryGmRecord(startDate, endDate, type, address); 
		renderJson(tableData);
	}
}
