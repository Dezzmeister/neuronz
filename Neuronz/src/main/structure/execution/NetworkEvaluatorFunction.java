package main.structure.execution;

import main.math.VectorN;

@FunctionalInterface
public interface NetworkEvaluatorFunction {
	
	public boolean evaluateIterationSuccess(VectorN expectedOutput, VectorN actualOutput, int index);
}
