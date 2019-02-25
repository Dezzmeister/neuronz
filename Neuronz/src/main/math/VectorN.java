package main.math;

import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;

import com.aparapi.Range;

import main.math.execution.SigmoidKernel;

public class VectorN {
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
	
	@Override
	public String toString() {
		String out = "(";
		
		for (float value : values) {
			out += value + ",";
		}
		
		return out.substring(0, out.length()-1) + ")";
	}
}
