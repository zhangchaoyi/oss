package common.controllers.operations;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;

@Clear
public class ObjectController extends Controller {
	/**
	 * 物品消耗和获取页
	 * @author chris
	 */
	@Before(GET.class)
	@ActionKey("/operation/object")
	public void objectIndex() {
		render("object-obtain-consume.html");
	}
}
