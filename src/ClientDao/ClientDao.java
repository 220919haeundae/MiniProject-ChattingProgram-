package ClientDao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import Template.OracleTemplate;

public class ClientDao {
	String sql = null;
	Properties prop = new Properties();
	ResultSet rset = null;
	
	public ClientDao() {
		try {
			prop.loadFromXML(new FileInputStream("sources/SQLInfo.xml"));
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<Client> selectClient() {
		ArrayList<Client> list = null;
		sql = prop.getProperty("selectAll");
		
		try {
			PreparedStatement pstmt = OracleTemplate.getConnection().prepareStatement(sql);
			rset = pstmt.executeQuery();
			while(rset.next()) {
				// list.add(new Client(rset.getString("NICKNAME"), ...));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public int searchClient(String nickName) {
		int result = 0;
		sql = prop.getProperty("selectTarget");
		
		try {
			PreparedStatement pstmt = OracleTemplate.getConnection().prepareStatement(sql);
			pstmt.setString(1, nickName);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				result += 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public int searchClient(String nickName, String password) {
		int result = 0;
		sql = prop.getProperty("selectTarget2");
		
		try {
			PreparedStatement pstmt = OracleTemplate.getConnection().prepareStatement(sql);
			pstmt.setString(1, nickName);
			pstmt.setString(2, password);
			rset = pstmt.executeQuery();
			if (rset.next()) {
				result += 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public int insertClient(Client c) {
		int result = 0;
		String sql = prop.getProperty("insertClient");
		
		try {
			PreparedStatement pstmt = OracleTemplate.getConnection().prepareStatement(sql);
			pstmt.setString(1, c.nickName);
			pstmt.setString(2, c.password);
			
			result = pstmt.executeUpdate();
			
			if(result >0) {
				OracleTemplate.commit();
			} else {
				OracleTemplate.rollback();
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return result;
	}
	
}
