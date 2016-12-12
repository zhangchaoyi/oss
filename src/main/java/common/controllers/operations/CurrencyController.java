package common.controllers.operations;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.GET;

@Clear
public class CurrencyController extends Controller {
	/**
	 * 货币消耗和获取页
	 * @author chris
	 */
	@Before(GET.class)
	@ActionKey("/operation/currency")
	public void currencyIndex() {
		render("currency-obtain-consume.html");
	}
}
