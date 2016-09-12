package common.utils;

public class StringUtils {
	public static String arrayToQueryString(String[] array){
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
	
	
	public static void main(String args[]){
		String[] aa= {"a","b","c","e","d"};
		System.out.println(arrayToQueryString(aa));
	}
}
