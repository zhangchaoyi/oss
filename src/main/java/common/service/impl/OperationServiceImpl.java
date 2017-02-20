package common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.jfinal.plugin.activerecord.Db;
import common.model.GmRecord;
import common.model.UserFeedback;
import common.service.OperationService;
import common.util.Contants;
import common.util.JsonToMap;

/**
 * 用户运营页接口
 * 表存放在uc服务器上
 * @author chris
 */
public class OperationServiceImpl implements OperationService {
	private static Logger logger = Logger.getLogger(OperationServiceImpl.class);
	/**
	 * 接收玩家反馈,并将其插入到mysql中 使用uc服接受反馈
	 * @param account 帐号id
	 * @param title 标题
	 * @param content 内容
	 * @param server 服务器
	 * @param port 端口
	 * @return true/false
	 */
	public boolean addFeedback(String account, String title, String content, String server, String port) {
		logger.info("params:{"+"account:"+account+",title:"+title+",content:"+content+",server:"+server+",port:"+port+"}");
		boolean succeed = false;
		succeed  = new UserFeedback().use("uc").set("account", account).set("title", title).set("content", content).set("server", server).set("port", port).set("create_time", new Date()).set("reply",0).save();
		logger.info("return:" + succeed);
		return succeed;
	}
	
	/**
	 * 查询用户反馈列表 查询uc服上的反馈数据
	 * @return List<List<String>> 直接填充datatable
	 */
	public List<List<String>> queryFeedback(String startDate, String endDate, String server) {
		logger.info("params:{"+"server:"+server+"}");
		String sql = "select * from user_feedback where DATE_FORMAT(create_time,'%Y-%m-%d') between ? and ? and server in ("+ server +")";
		List<UserFeedback> userFeedback = UserFeedback.dao.use("uc").find(sql, startDate, endDate);
		List<List<String>> data = new ArrayList<List<String>>();
		for(UserFeedback uf : userFeedback){
			String account = uf.getStr("account");
			String content = uf.getStr("content")==null ? "": uf.getStr("content");
			String createTime = uf.getDate("create_time").toString();
			String reply = uf.getInt("reply").toString();
			String id = uf.getInt("id").toString();
			List<String> subList = new ArrayList<String>(Arrays.asList(account,content,createTime,id,reply,id));
			data.add(subList);
		}
		logger.info("data:" + data);
		return data;
	}
	
	/**
	 * 回复反馈成功后根据mysql id 将reply字段修改为1 表示已回复
	 * @param id --mysql row id
	 * @return row id   /0表示失败
	 */
	public int completeReply(int id) {
		logger.info("params:{"+"id"+id+"}");
		String sql = "update user_feedback set reply = 1 where id = ?";
		int succeed = Db.use("uc").update(sql, id);
		logger.info("return:" + succeed);
		return succeed;
	}
	/**
	 * 置邮件发送后的操作记录状态为成功
	 * @param rowId --mysql row id
	 * @return row Id   /0表示失败
	 */
	public int setGmRecordMailToSucceed(int rowId){
		logger.info("params:{"+"id"+rowId+"}");
		String sql = "update gm_record set is_mail_send = 1 where id = ?";
		int succeed = Db.use("uc").update(sql, rowId);
		return succeed;
	}
	
	/**
	 * 根据某个row id 查询反馈信息
	 * @param id --row id
	 */
	public Map<String, String> queryFeedbackById(String id) {
		logger.info("params:{"+"id:"+id+"}");
		String sql = "select * from user_feedback where id = ?";
		UserFeedback uf = UserFeedback.dao.use("uc").findFirst(sql, id);
		Map<String, String> data = new HashMap<String, String>();
		if(uf==null){
			data.put("message", "failed");
			logger.info("<OperationServiceImpl> queryFeedbackById: null" );
			return data;
		}
		String account = uf.getStr("account");
		String content = uf.getStr("content")==null ? "": uf.getStr("content");
		data.put("account",account);
		data.put("content", content);
		logger.info("data:" + data);
		return data;
	}
	
	/**
	 * 插入gm操作
	 * @param account 管理员帐号
	 * @param operation gm指令
	 */
	public int insertGmRecord(String account, String operation, String emailAddress, String type){
		logger.info("params:{"+"account:"+account+",operation:"+operation+",address:"+emailAddress+",type:"+type+"}");
		GmRecord gr = new GmRecord().use("uc").set("account", account).set("operation", operation).set("create_time", new Date()).set("address", emailAddress).set("type",type);
		boolean succeed = gr.save();
		if(!succeed){
			logger.info("insert gm record failed");
		}
		return gr.getInt("id");
	}
	
	/**
	 * 查询gm操作
	 * @param type  操作类型
	 * @param address  针对的服务器地址
	 * @return 帐号/时间/标题/内容/附件/操作人
	 */
	public List<List<String>> queryGmRecord(String startDate, String endDate, String type, String address) {
		String sql = "select account,operation,create_time,is_mail_send from gm_record where DATE_FORMAT(create_time,'%Y-%m-%d') between ? and ? and type = ? and address = ?";
		List<GmRecord> gmRecord = GmRecord.dao.use("uc").find(sql, startDate, endDate, type, address);
		List<List<String>> data = new ArrayList<List<String>>();
		try{
			for(GmRecord gr : gmRecord){
				String account = gr.getStr("account");
				String operation = gr.getStr("operation");
				Date createTime = gr.getDate("create_time");
				int isMailSend = gr.getInt("is_mail_send");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//解析操作
				Map<String, String> emailDetail = getEmailDetail(operation);
				if(emailDetail.containsKey("errCode")){
					logger.info(emailDetail.get("errCode"));
					continue;
				}
				List<String> subList = new ArrayList<String>();
				//帐号-时间-标题-内容-附件-等级-状态-操作人
				String emailAccount = emailDetail.get("account");
				emailAccount = "*".equals(emailAccount)?"全服":emailAccount;
				subList.add(emailAccount);
				subList.add(sdf.format(createTime));
				subList.add(emailDetail.get("title"));
				subList.add(emailDetail.get("content"));
				subList.add(emailDetail.get("attachment"));
				subList.add(emailDetail.get("level"));
				subList.add(isMailSend==1?"成功":"未知");
				subList.add(account);
				data.add(subList);
			}
		}catch(Exception e){
			logger.info("转换operation JSON格式出错", e);
		}
		
		return data;
	}
	
	//邮件发送的帐号 标题 正文 附件
	private Map<String, String> getEmailDetail(String operation) {
		Map<String, String> data = new HashMap<String, String>();
		
		//解析gm指令
		Map<String, Object> map = JsonToMap.toMap(operation); 
		//获取parms列表
		@SuppressWarnings("unchecked")
		List<Object> objList = (List<Object>)map.get("parms");
		//获取发送的帐号
		if(objList.size()!=4&&objList.size()!=5){
			data.put("errCode", "格式不满足要求");
			return data;
		}
		for(int i=0;i<objList.size();i++){
			switch(i){
			case 0:
				data.put("account", String.valueOf(objList.get(i)).replace("\"", ""));
				break;
			case 1:
				data.put("title", String.valueOf(objList.get(i)).replace("\"", ""));
				break;
			case 2:
				data.put("content", String.valueOf(objList.get(i)).replace("\"", ""));
				break;
			case 3:
				@SuppressWarnings("unchecked")
				List<Object> props = (List<Object>)objList.get(i);
				String attachment = getAttachment(props);
				data.put("attachment", attachment);
				break;
			case 4:
				Map<String, Object> level = JsonToMap.toMap(String.valueOf(objList.get(i)));
				if(!level.isEmpty()){
					@SuppressWarnings("unchecked")
					List<Object> levelList = (List<Object>)level.get("parms");
					String low = String.valueOf(levelList.get(0)).replace("\"", "");
					String high = String.valueOf(levelList.get(1)).replace("\"", "");
					data.put("level", low+"-"+high);
				}else{
					data.put("level", "-");
				}
				break;
			}
			if(!data.containsKey("level")){
				data.put("level", "-");
			}
		}
		return data;
	}
	
	/**
	 * @param props [{obj_id="obj_1", num=1}, {obj_id="obj_2", num=1}, {obj_id="obj_3", num=1,"param_list":[parseInt(num),1]}]
	 * json中的字段可能会携带"" 因此需要进行去除"
	 * @return
	 */
	private String getAttachment(List<Object> props){
		String attachment = "";
		for(Object obj : props){
			Map<String, Object> propMap = JsonToMap.toMap(String.valueOf(obj));
			String objId = String.valueOf(propMap.get("obj_id"));
			int num = Integer.parseInt(propMap.get("num").toString());
			if(propMap.containsKey("param_list")){
				@SuppressWarnings("unchecked")
				List<Object> paramList = (List<Object>)propMap.get("param_list");
				if(Contants.HEROID.equals(objId.replace("\"", ""))){
					String heroName = Contants.getPropName(String.valueOf(paramList.get(0)));
					String level = String.valueOf(paramList.get(1));
					attachment += heroName+"*"+level+"阶"+" ";
					continue;
				}
			}
			String propName = Contants.getPropName(objId);
			if(propName==null){
				logger.info("propName is null"+ objId);
			}
			attachment += propName+"*"+num+" ";
		}
		return attachment;
	}
	
	
}
