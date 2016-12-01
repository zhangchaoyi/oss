package common.controllers.online;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;

@Clear
public class CountController extends Controller{
	private static Logger logger = Logger.getLogger(CountController.class);
	
	/**
	 * 在线分析页
	 * @author chris
	 * @role data_guest
	 */
	@Before(GET.class)
	@ActionKey("/online/count")
	public void count() {
		render("count.html");
	}

}
