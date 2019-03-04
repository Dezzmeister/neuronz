package main.dataio;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/*

 * class used for interpreting data from database and putting into format useful for AI algorithms

 * */

public class DataInterpreter {

	public final static String[] COLUMNS = {"Q1","Q2","Q7","Q19","Q24","Q28","Q34",
			"Q60","Q86J","Q88"};

	public final static String[] ALIASES = {"age","sex","cigarettes","cigars","chewing tobacco","e-cigarettes","marijuana","attainability",
			"no tobacco at home","condition"};
	//items in which "missing" option IS used: all race related questions (including hispanic/latino), and 
	//"no tobacco at home"
	
	public final static String[] FILTER_NULLS = {"age", "sex", "cigarettes", "cigars", "chewing tobacco", "e-cigarettes", "marijuana", "attainability",
			"condition"
	};
	
	public static final String[] INPUTS = {"age","sex","attainability",
			"no tobacco at home","condition"
	};
	
	public static final String[] OUTPUTS = {
			"cigarettes","cigars","chewing tobacco","e-cigarettes","marijuana"
	};
	
	private static final Map<String, Function<Integer, Float>> conversions = new HashMap<String, Function<Integer, Float>>();
	static {
		conversions.put("age", i -> (i - 1)/10.0f);
		conversions.put("sex", i -> i - 1.0f);
		conversions.put("cigarettes", DataInterpreter::p30DDrugUse);
		conversions.put("cigars", DataInterpreter::p30DDrugUse);
		conversions.put("chewing tobacco", DataInterpreter::p30DDrugUse);
		conversions.put("e-cigarettes", DataInterpreter::p30DDrugUse);
		conversions.put("marijuana", i -> (i==2)?1.0f:0.0f);
		conversions.put("attainability", i -> (3 - i)/2.0f);
		conversions.put("no tobacco at home", i -> 1.0f - i);
		conversions.put("condition", i -> 2.0f - i);
	}
	
	private static float p30DDrugUse(int i) {
		return 2.0f - i;
	}
	
	public static float interpret(int input, String alias) {
		return conversions.get(alias).apply(input);
	}

	public static float interpret_old(int input,String alias) {
		if(find(alias)==0) 
			return ((float)(input-1))/10;
		
		if(find(alias)==1) 
			return (float)(input-1);
		
		if(find(alias)==2||find(alias)==24) 
			return (float)(1-input);
		
		if(find(alias)>2&&find(alias)<8)
			return (float)input;
		
		if(find(alias)>7&&find(alias)<12||find(alias)==25)
			return (float)(2-input);
		
		if(find(alias)==12) {
			if(input==1||input==3)
				return 0;
			return 1;
		}

		if(find(alias)==13) 
			return ((float)(3-input))/2;

		if(find(alias)>13&&find(alias)<22) {
			if (input==1)
				return 0;
			return ((float)(input-2))/4;
		} else
			return ((float)(input-1))/6;
	}

	private static int find(String alias) {
		int i = 0;

		for(String s: ALIASES) {
			if(alias.equals(s))
				return i;
			i++;
		}

		return -1;
	}
}