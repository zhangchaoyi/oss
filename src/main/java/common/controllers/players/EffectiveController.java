package common.controllers.players;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.ext.interceptor.GET;
import common.interceptor.AuthInterceptor;

/**
 * 目前有效玩家页为假数据
 * @author chris
 *
 */
//@Clear(AuthInterceptor.class)
@Before(AuthInterceptor.class)
public class EffectiveController extends Controller {
	private static Logger logger = Logger.getLogger(EffectiveController.class);
	@Before(GET.class)
	@ActionKey("/players/effective")
	public void effective() {
		render("effective.html");
	}
	
	@Before(GET.class)
	@ActionKey("/players/effective-distributed")
	public void effectiveDistributed() {
		render("effective-distributed.html");
	}
	
	@Before(POST.class)
	@ActionKey("/api/players/effective")
	public void queryEffectiveData() {
		String tagDataInfo = getPara("tagDataInfo","add-players");
		Map<String, Object> data = new LinkedHashMap<String,Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		Map<String, List<Integer>> seriesMap = new LinkedHashMap<String, List<Integer>>();
		
		List<Integer> dataAdd1 =  Arrays.asList(0,1,2,3,4,5,6,7);
		List<Integer> dataAdd2 =  Arrays.asList(7,6,5,4,3,2,1,0);
		List<Integer> dataAdd3 =  Arrays.asList(2,2,2,2,2,2,2,2);
		
		List<Integer> dataAct1 =  Arrays.asList(0,1,0,20,0,0,10,0);
		List<Integer> dataAct2 =  Arrays.asList(0,0,4,0,5,0,0,20);
		List<Integer> dataAct3 =  Arrays.asList(0,0,8,0,6,9,0,10);
		
		if(tagDataInfo.equals("add-players")){
			seriesMap.put("日有效新增玩家", dataAdd1);
			seriesMap.put("7日有效新增玩家", dataAdd2);
			seriesMap.put("30日有效新增玩家", dataAdd3);
		}else{
			seriesMap.put("日有效活跃玩家", dataAct1);
			seriesMap.put("7日有效活跃玩家", dataAct2);
			seriesMap.put("30日有效活跃玩家", dataAct3);
		}
		Set<String> type = seriesMap.keySet();
		List<String> categories = Arrays.asList("2016-08-11","2016-08-12","2016-08-13","2016-08-14","2016-08-15","2016-08-16","2016-08-17","2016-08-18");

		category.put("日期", categories);
		
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("serie", seriesMap);
		logger.debug("<EffectiveController> queryEffectiveData:" + data);
		renderJson(data);
	}
	
	@Before(POST.class)
	@ActionKey("/api/players/effective-distributed")
	public void queryDistributedData() {
		String tagDataInfo = getPara("tagDataInfo");
		String subTagDataInfo = getPara("subTagDataInfo");
		
		Map<String, Object> data = new LinkedHashMap<String,Object>();
		Map<String, List<String>> category = new LinkedHashMap<String, List<String>>();
		Map<String, List<Integer>> seriesMap = new LinkedHashMap<String, List<Integer>>();
		
		List<String> categories =null;
		List<Integer> dataAdd = null;
		List<Integer> dataAct = null;
		
		
		switch(tagDataInfo){
			case "area":{
				categories = Arrays.asList("广东省","广西省","湖南省");
				dataAdd = Arrays.asList(5,6,7);
				dataAct = Arrays.asList(1,2,3);
				category.put("地区", categories);
				break;
			}
			case "country":{
				categories = Arrays.asList("中国","美国","日本");
				dataAdd = Arrays.asList(5,6,7);
				dataAct = Arrays.asList(1,2,3);
				category.put("国家", categories);
				break;
			}
			case "sex":{
				categories = Arrays.asList("男","女","未知");
				dataAdd = Arrays.asList(5,6,7);
				dataAct = Arrays.asList(1,2,3);
				category.put("性别", categories);
				break;
			}
			case "age":{
				categories = Arrays.asList("1-15","16-20","21-25","26-30","31-35","36-40","41-45","46-50","51-55","56-60",">60");
				dataAdd =  Arrays.asList(1,5,6,0,0,8,0,6,9,0,10);
				dataAct =  Arrays.asList(6,9,1,5,6,0,0,8,0,0,10);
				category.put("年龄", categories);
				break;
			}
			case "account":{
				categories = Arrays.asList("admin","visitor");
				dataAdd =  Arrays.asList(5,6);
				dataAct =  Arrays.asList(7,8);
				category.put("账户类型", categories);
				break;
			}
		
		}
		
		if(subTagDataInfo.equals("add-players")){
			seriesMap.put("新增玩家", dataAdd);
		}else{
			seriesMap.put("活跃玩家", dataAct);
		}
		Set<String> type = seriesMap.keySet();
		data.put("type", type.toArray());
		data.put("category", category);
		data.put("serie", seriesMap);
		logger.debug("<EffectiveController> queryDistributedData:" + data);
		renderJson(data);
	}
}
