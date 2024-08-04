package makePropertyandXml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class run {
	
	
	public static void main(String[] args) {
		setConnectionInfo();
		setSQLInfo();
		
	}
	
	
	public static void setConnectionInfo() {
		Properties prop = new Properties();
		File path = new File("sources/");
		
		if(!path.exists()) {
			path.mkdirs();
		}
		
		prop.setProperty("driver", "oracle.jdbc.driver.OracleDriver");
		prop.setProperty("URL", "jdbc:oracle:thin:@192.168.219.49:1521:xe");
		prop.setProperty("ID", "C##JDBC");
		prop.setProperty("password", "JDBC");
		try {
			prop.store(new FileOutputStream("sources/connInfo.properties"), "Oracle Connection Info");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		};
	}
	
	public static void setSQLInfo() {
		Properties prop = new Properties();
		File path = new File("sources/");
		
		if(!path.exists()) {
			path.mkdirs();
		}
		
		prop.setProperty("selectAll", "SELECT * FROM CLIENT");
		prop.setProperty("selectTarget", "SELECT * FROM CLIENT WHERE NICKNAME = ?");
		
		try {
			prop.storeToXML(new FileOutputStream("sources/SQLInfo.xml"), "Oracle SQL Info");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		};
	}

}
