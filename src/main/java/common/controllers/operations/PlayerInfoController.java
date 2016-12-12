package common.controllers.operations;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;

@Clear
public class PlayerInfoController extends Controller {
	/**
	 * 角色当前信息页
	 * @author chris
	 */
	@Before(GET.class)
	@ActionKey("/operation/playerInfo")
	public void objectIndex() {
		render("playerInfo.html");
	}
}
