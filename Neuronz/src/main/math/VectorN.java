package main.math;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import com.aparapi.Range;

import main.math.execution.SigmoidKernel;

public class VectorN implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8904743786193291092L;

	public final float[] values;
	
	public final int SIZE;
	
	public VectorN(final float ... _values) {
		values = _values;
		SIZE = values.length;
	}
	
	public VectorN(int n) {
		this(new float[n]);
	}
	
	public float set(int index, float in) {
		float out = values[index];
		values[index] = in;
		return out;
	}
	
	public float get(int index) {
		return values[index];
	}
	
	public int length() {
		return values.length;
	}
	
	public void fill(DoubleSupplier supplier) {
		for (int i = 0; i < SIZE; i++) {
			values[i] = (float) supplier.getAsDouble();
		}
	}
	
	public VectorN append(float ... vals) {
		VectorN out = new VectorN(values.length + vals.length);
		
		for (int i = 0; i < values.length; i++) {
			out.values[i] = values[i];
		}
		
		for (int i = values.length; i < vals.length; i++) {
			out.values[i] = vals[i - values.length];
		}
		
		return out;
	}
	
	/**
	 * Adds this Vector to another. Ensure that both vectors are the same size.
	 * 
	 * @param vec other Vector
	 * @return sum of this Vector and other Vector
	 */
	public VectorN plus(final VectorN vec) {
		final VectorN out = new VectorN(SIZE);
		
		for (int i = 0; i < SIZE; i++) {
			out.set(i, values[i] + vec.get(i));
		}
		
		return out;
	}
	
	/**
	 * Subtracts this Vector from another. Ensure that both vectors are the same size.
	 * 
	 * @param vec other Vector
	 * @return difference between this Vector and other Vector
	 */
	public VectorN minus(final VectorN vec) {
		final VectorN out = new VectorN(SIZE);
		
		for (int i = 0; i < SIZE; i++) {
			out.set(i, values[i] - vec.get(i));
		}
		
		return out;
	}
	
	public float sum() {
		float sum = 0;
		
		for (float f : values) {
			sum += f;
		}
		
		return sum;
	}
	
	/**
	 * Performs some function on every element of this Vector and returns the resultant
	 * Vector. Does not modify this Vector.
	 * 
	 * @param function function to be applied
	 * @return result of the function on every element of this Vector
	 */
	public VectorN apply(final ToDoubleFunction<Float> function) {
		final VectorN out = new VectorN(SIZE);
		
		for (int i = 0; i < SIZE; i++) {
			out.set(i, (float) function.applyAsDouble(values[i]));
		}
		
		return out;
	}
	
	/**
	 * Performs a sequential, vectorized operation with two supplied vectors and returns a third vector as the result. Ensure that
	 * both vectors are the same size.
	 * 
	 * @param vec0 first operand
	 * @param vec1 second operand
	 * @param operation operation to be performed on each element of the vectors; arguments: (vec0, vec1)
	 * @return resultant vector
	 */
	public static VectorN operate(final VectorN vec0, final VectorN vec1, final BiFunction<Float, Float, Float> operation) {
		final VectorN out = new VectorN(vec0.SIZE);
		
		for (int i = 0; i < vec0.SIZE; i++) {
			out.set(i, operation.apply(vec0.get(i), vec1.get(i)));
		}
		
		return out;
	}
	
	/**
	 * Returns the output of the sigmoid function on this Vector (vectorized). Uses OpenCL,
	 * defaulting to the GPU. Slower than {@link main.math.VectorN#apply <code>apply(Functions::sigmoid)</code>}
	 * 
	 * @return resultant Vector
	 */
	public VectorN applySigmoid() {
		final float[] out = new float[SIZE];
		
		SigmoidKernel.kernel.setInput(values);
		SigmoidKernel.kernel.setOutput(out);
		
		Range range = SigmoidKernel.BEST_DEVICE.createRange(SIZE);		
		SigmoidKernel.kernel.execute(range);
		
		return new VectorN(out);
	}
	
	public VectorN populate(Function<Integer, Float> operator) {
		for (int i = 0; i < values.length; i++) {
			values[i] = operator.apply(i);
		}
		
		return this;
	}
	
	@Override
	public String toString() {
		String out = "(";
		
		for (float value : values) {
			out += value + ",";
		}
		
		return out.substring(0, out.length()-1) + ")";
	}
	
	public static VectorN randomGaussianVector(int length) {
		Random random = new Random();
		
		final float[] vals = new float[length];
		
		for (int i = 0; i < length; i++) {
			vals[i] = (float) random.nextGaussian();
		}
		
		return new VectorN(vals);
	}
	
	/**
	 * Do not use!
	 * 
	 * @param vec0 first vector
	 * @param vec1 second vector
	 * @param operator operation to be performed
	 * @return result
	 */
	@Deprecated
	public static VectorN operateInParallel(VectorN vec0, VectorN vec1, BiFunction<Float, Float, Float> operator) {
		float[] results = new float[vec0.SIZE];
		
		List<Callable<Integer>> fnOperators = new ArrayList<>();
		for (int i = 0; i < results.length; i += 2500) {
			fnOperators.add(new PartialFunctionOperator(vec0.values, vec1.values, results, operator, i, i + 2500));
		}
		ExecutorService executor = Executors.newFixedThreadPool(fnOperators.size());
		
		try {
			executor.invokeAll(fnOperators);
			return new VectorN(results);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static class PartialFunctionOperator implements Callable<Integer> {
		private final float[] vec0;
		private final float[] vec1;
		private final float[] destVec;
		private final BiFunction<Float, Float, Float> operator;
		private final int startIndex;
		private final int endIndex;
		
		public PartialFunctionOperator(final float[] _vec0, final float[] _vec1, final float[] _destVec, final BiFunction<Float, Float, Float> _operator, int _startIndex, int _endIndex) {
			vec0 = _vec0;
			vec1 = _vec1;
			destVec = _destVec;
			operator = _operator;
			startIndex = _startIndex;
			endIndex = _endIndex;
		}
		
		@Override
		public Integer call() {
			for (int i = startIndex; i < endIndex; i++) {
				destVec[i] = operator.apply(vec0[i], vec1[i]);
			}
			
			return 0;
		}
	}
}
