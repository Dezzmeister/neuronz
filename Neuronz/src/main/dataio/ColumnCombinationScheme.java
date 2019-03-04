package main.dataio;

@FunctionalInterface
public interface ColumnCombinationScheme {
	
	public int combine(int ... rowValues);
}
