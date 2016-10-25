package common.controllers.online;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;

import common.interceptor.AuthInterceptor;

@Clear(AuthInterceptor.class)
public class HabitsController extends Controller{
	
	@Before(GET.class)
	@ActionKey("/online/habits")
	public void analyse() {
		render("habits.html");
	}
}
