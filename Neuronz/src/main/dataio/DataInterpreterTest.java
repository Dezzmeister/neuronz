package main.dataio;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DataInterpreterTest {
	public static final String[] COLUMNS = {
			"Q1",
			"Q2",
			"Q7",
			"Q11"
	};
	
	public static final String[] ALIASES = {
			"age",	
			"sex",	
			"tried",
			"smoked"
	};
	
	public static final String[] INPUTS = {
			"age",
			"sex",
			"tried"
	};
	
	public static final String[] FILTER_NULLS = {
			"age",
			"sex",
			"tried",
			"smoked"
	};
	
	private static final Map<String, Function<Integer, Float>> converter = new HashMap<>();
	static {
		converter.put("age", DataInterpreterTest::convertRange10);
		converter.put("sex", DataInterpreterTest::convertInverted2);
		converter.put("tried", DataInterpreterTest::convertInverted2);
		converter.put("smoked", DataInterpreterTest::oneValueSwitch);
	}
	
	public static float convert(String alias, int value) {
		return converter.get(alias).apply(value);
	}
	
	private static float convertRange10(int value) {
		return (value - 1)/10.0f;
	}
	
	private static float convertInverted2(int value) {
		return 2 - value;
	}
	
	private static float oneValueSwitch(int value) {
		if (value == 1) {
			return 0;
		} else {
			return 1;
		}
	}
}
