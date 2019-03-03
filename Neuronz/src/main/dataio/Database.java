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
import java.util.Map.Entry;

import main.math.VectorN;
import main.structure.execution.VectorNBatch;

public class Database {
	private final Map<String, List<Integer>> rawData;
	private Connection connection = null;
	
	public Database(String filePath) {
		rawData = new HashMap<String, List<Integer>>();
		connection = load(filePath);
	}
	
	public List<Integer> getColumnData(String alias) {
		return rawData.get(alias);
	}
	
	/**
	 * Loads and stores column data from the database file specified in the constructor.
	 * 
	 * @param columns database column names
	 * @param aliases aliases for use in code
	 * @param tableName table containing the columns
	 */
	public void loadColumnsAs(String[] columns, String[] aliases, String tableName) {
		
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("select " + formatColumns(columns) + " from " + tableName);
			
			for (int i = 0; i < columns.length; i++) {
				rawData.put(aliases[i], new ArrayList<Integer>());
			}
			
			while (resultSet.next()) {
				
				for (int i = 0; i < rawData.size(); i++) {
					String entry = resultSet.getString(columns[i]);
					
					int value = 0;
					if (entry != null && !entry.contains("*")) {
						value = Integer.parseInt(entry);
					}
					rawData.get(aliases[i]).add(value);
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes an entry from all columns if the entry contains a null value in any of the columns specified in <code>aliases</code>.
	 * 
	 * @param aliases column aliases
	 */
	public void filterNullsIn(String ... aliases) {
		for (String alias : aliases) {
			List<Integer> entries = rawData.get(alias);
			
			for (int i = entries.size() - 1; i >= 0; i--) {
				if (entries.get(i) == 0) {
					removeEntryFromAll(i);
				}
			}
		}
	}
	
	/**
	 * Removes the entry at the specified index from every {@link java.util.List List} in <code>rawData</code>.
	 * 
	 * @param index
	 */
	private void removeEntryFromAll(int index) {
		for (Entry<String, List<Integer>> entry : rawData.entrySet()) {
			entry.getValue().remove(index);
		}
	}
	
	public VectorNBatch convertAndVectorizeInputs(String ... inputAliases) {
		VectorN[] inputs = new VectorN[rawData.get(inputAliases[0]).size()];
		
		for (int i = 0; i < inputs.length; i++) {
			float[] data = new float[inputAliases.length];
			
			for (int j = 0; j < inputAliases.length; j++) {
				data[j] = DataInterpreterTest.convert(inputAliases[j], rawData.get(inputAliases[j]).get(i));
			}
			
			inputs[i] = new VectorN(data);
		}
		
		return new VectorNBatch(inputs);
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
