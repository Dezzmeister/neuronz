package main.dataio;
/*
 * class used for interpreting data from database and putting into format useful for AI algorithms
 * */
public class DataInterpreter {
	public final static String[] columns = {"Q1","Q2","Q4A","Q5A","Q5B","Q5C","Q5D","Q5E","Q7","Q19","Q24","Q28","Q34",
			"Q60","Q74","Q75","Q76","Q77","Q78","Q79","Q80","Q81","Q84","Q85","Q86J","Q88"};
	public final static String[] aliases = {"age","sex","not latino","american indian","asian","black","pacific islander",
			"white","cigarettes","cigars","chewing tobacco","e-cigarettes","marijuana","attainability",
			"cig ad internet","cig ad newspaper","cig ad store","cig ad tv","ecig ad internet","ecig ad newspaper",
			"ecig ad store","ecig ad tv","second-hand smoke","second-hand vape","no tobacco at home","condition"};
	//items in which "missing" option IS used: all race related questions (including hispanic/latino), and 
	//"no tobacco at home"
	public static float interpret(int input,String alias) {
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
		}
		else
			return ((float)(input-1))/6;
		
		
	}
	private static int find(String alias) {
		int i = 0;
		for(String s: aliases) {
			if(alias.equals(s))
				return i;
			i++;
		}
		return -1;
	}
}
