package common.controllers.operations;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;

import common.interceptor.GmInterceptor;

@Clear
public class AnnouncementController extends Controller {
	/**
	 * 游戏公告和跑马灯页
	 * @author chris
	 */
	@Before({GET.class,GmInterceptor.class})
	@ActionKey("/operation/announcement")
	public void objectIndex() {
		render("announcement.html");
	}
}
