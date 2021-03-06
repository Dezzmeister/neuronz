package main.math;

import java.io.Serializable;
import java.util.Random;
import java.util.function.BiFunction;

public class MatrixNN implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8632376030217345391L;
	
	private final float[][] values;
	public final int ROWS;
	public final int COLS;
	
	public MatrixNN(final float[][] _values) {
		values = _values;
		
		ROWS = values.length;
		COLS = values[0].length;
	}
	
	public MatrixNN(int rows, int cols) {
		this(randomGaussianArray(rows, cols));
	}
	
	public float get(int row, int col) {
		return values[row][col];
	}
	
	public VectorN multiply(final VectorN vec) {
		if (vec.SIZE != COLS) {
			throw new IllegalArgumentException("Vector size must match Matrix columns!");
		}
		
		final VectorN out = new VectorN(ROWS);
		
		for (int i = 0; i < ROWS; i++) {
			out.set(i, singleRowResult(i, vec));
		}
		
		return out;
	}
	
	public MatrixNN plus(MatrixNN matrix) {
		MatrixNN result = new MatrixNN(ROWS, COLS);
		
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				result.values[row][col] = values[row][col] + matrix.values[row][col];
			}
		}
		
		return result;
	}
	
	public MatrixNN minus(MatrixNN matrix) {
		MatrixNN result = new MatrixNN(ROWS, COLS);
		
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				result.values[row][col] = values[row][col] - matrix.values[row][col];
			}
		}
		
		return result;
	}
	
	private float singleRowResult(int row, final VectorN vec) {
		float sum = 0;
		final float[] rowValues = values[row];
		
		for (int i = 0; i < COLS; i++) {
			sum += (rowValues[i] * vec.get(i));
		}
		
		return sum;
	}
	
	private static float[][] randomGaussianArray(int rows, int cols) {
		Random random = new Random();
		
		final float[][] vals = new float[rows][cols];
		
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				vals[row][col] = (float) random.nextGaussian();
			}
		}
		
		return vals;
	}
	
	public MatrixNN populate(BiFunction<Integer, Integer, Float> operator) {
		
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				values[row][col] = operator.apply(row, col);
			}
		}
		
		return this;
	}
	
	@Override
	public String toString() {
		String out = "";
		
		for (int row = 0; row < ROWS; row++) {
			out += "[";
			
			for (int col = 0; col < COLS; col++) {
				out += values[row][col] + "\t";
			}
			out = out.substring(0, out.length() - 1);
			out += "]\n";
		}
		
		return out;
	}
}
