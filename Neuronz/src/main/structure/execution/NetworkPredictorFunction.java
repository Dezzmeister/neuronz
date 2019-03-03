package main.structure.execution;

import main.math.VectorN;

@FunctionalInterface
public interface NetworkPredictorFunction {
	
	public VectorN getExpectedOutput(VectorN input, int index);
}
