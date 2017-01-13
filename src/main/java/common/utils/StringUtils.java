package common.utils;

import org.apache.log4j.Logger;

public class StringUtils {
	private static Logger logger = Logger.getLogger(StringUtils.class);
	//用于拼接sql   --icon  --account
	public static String arrayToQueryString(String[] array){
		if(array==null){
			logger.info("array is null");
			return "";
		}
		String str = "";
		for(int i=0;i<array.length;i++){
			if(i+1==array.length){
				str+= "'" + array[i] + "'";
				break;
			}
			str += "'" + array[i] + "',";
		}
		
		return str;
	}
	
	//避免被实例化
	private StringUtils(){}
	
	public static void main(String args[]){
		String[] aa= {"a","b","c","e","d"};
		System.out.println("----"+arrayToQueryString(aa));
	}
}
