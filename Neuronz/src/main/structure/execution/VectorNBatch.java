package main.structure.execution;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;

import main.math.VectorN;

public class VectorNBatch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4230368937187485963L;
	
	private final VectorN[] inputs;
	private transient int index = 0;
	private Optional<Boolean> isUniform = Optional.empty();
	
	public VectorNBatch(VectorN[] _inputs) {
		inputs = _inputs;
	}
	
	public VectorN getNext() {
		return index < inputs.length ? inputs[index++] : null;
	}
	
	public int length() {
		return inputs.length;
	}
	
	/**
	 * Returns the width of the first element of this <code>VectorNBatch</code>. Does not check to ensure that
	 * the entire batch is uniform, you must do this with {@link main.structure.execution.VectorNBatch#isUniform() isUniform()}.
	 * 
	 * @return width of the batch
	 */
	public int width() {
		return inputs[0].SIZE;
	}
	
	/**
	 * Returns <code>true</code> if all Vectors in the batch have the same size. For efficiency, this function only iterates through all vectors on the first
	 * call. On subsequent calls it returns the result of the first check. For batches generated with {@link main.structure.execution.VectorNBatch#generateBatch(int, int, Function) generateBatch()},
	 * this function always returns <code>true</code>.
	 * 
	 * @return <code>true</code> if the batch if uniform
	 */
	public boolean isUniform() {
		if (!isUniform.isPresent()) {
			int width = inputs[0].SIZE;
			for (VectorN v : inputs) {
				if (v.SIZE != width) {
					isUniform = Optional.of(false);
					return false;
				}
			}
			
			isUniform = Optional.of(true);
			return true;
		} else {
			return isUniform.get();
		}
	}
	
	public void reset() {
		index = 0;
	}
	
	/**
	 * Constructs a uniform <code>VectorNBatch</code> with the given length and width (vector length). The function <code>floatSupplier</code>
	 * is used to populate every {@link main.math.VectorN VectorN}. It accepts the index within the <b>Vector</b> and returns the value to be set
	 * at that index.
	 * 
	 * @param batchLength number of Vectors in the batch
	 * @param vectorLength number of elements in each Vector
	 * @param floatSupplier assigns a value to each element of the Vector
	 * @return new <code>VectorNBatch</code>
	 */
	public static VectorNBatch generateBatch(int batchLength, int vectorLength, Function<Integer, Float> floatSupplier) {
		VectorN[] inputs = new VectorN[batchLength];
		for (int i = 0; i < batchLength; i++) {
			inputs[i] = new VectorN(vectorLength).populate(floatSupplier);
		}
		
		VectorNBatch output = new VectorNBatch(inputs);
		output.isUniform = Optional.of(true);
		return output;
	}
}
