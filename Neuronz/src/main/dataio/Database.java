package main.dataio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
	private final Map<String, List<Integer>> rawData;
	private Connection connection = null;
	
	public Database(String filePath) {
		rawData = new HashMap<String, List<Integer>>();
		connection = load(filePath);
	}
	
	public void loadColumnsAs(String[] columns, String[] aliases, String tableName) {
		
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("select " + formatColumns(columns) + " from " + tableName);
			
			for (int i = 0; i < columns.length; i++) {
				rawData.put(aliases[i], new ArrayList<Integer>());
			}
			
			while (resultSet.next()) {
				
				for (int i = 0; i < rawData.size(); i++) {
					rawData.get(aliases[i]).add(resultSet.getInt(columns[i]));
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String formatColumns(String[] columns) {
		String formatted = columns[0];
		
		for (int i = 1; i < columns.length; i++) {
			formatted += ", " + columns[i];
		}
		
		return formatted;
	}
	
	private Connection load(String path) {
		Connection dbConnection = null;
		
		try {
			dbConnection = DriverManager.getConnection("jdbc:ucanaccess://"+path);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return dbConnection;
	}
}
