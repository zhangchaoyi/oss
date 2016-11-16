package common.mysql;

import com.jfinal.core.JFinal;

public class DbSelector {
	public static void setDbName(String db) {
		JFinal.me().getServletContext().setAttribute("db", db);
	}
	public static String getDbName() {
		String db = JFinal.me().getServletContext().getAttribute("db").toString();
		return db;
	}
}
