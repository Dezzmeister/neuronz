package main.structure.execution;

import java.io.Serializable;
import java.util.function.Function;

import main.math.VectorN;

public class VectorNBatch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4230368937187485963L;
	
	private final VectorN[] inputs;
	private transient int index = 0;
	
	public VectorNBatch(VectorN[] _inputs) {
		inputs = _inputs;
	}
	
	public VectorN getNext() {
		return index < inputs.length ? inputs[index++] : null;
	}
	
	public void reset() {
		index = 0;
	}
	
	public static VectorNBatch generateBatch(int batchLength, int vectorLength, Function<Integer, Float> floatSupplier) {
		VectorN[] inputs = new VectorN[batchLength];
		for (int i = 0; i < batchLength; i++) {
			inputs[i] = new VectorN(vectorLength).populate(floatSupplier);
		}
		
		return new VectorNBatch(inputs);
	}
}
