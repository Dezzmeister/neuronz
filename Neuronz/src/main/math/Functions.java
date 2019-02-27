package main.math;

public class Functions {
	
	public static double sigmoid(double x) {
		return 1.0/(1.0 + Math.exp(-x));
	}
	
	public static double sigmoidPrime(double x) {
		return sigmoid(x) * (1- sigmoid(x));
	}
	
	/**
	 * Computes the activation vector (outputs) for a layer of neurons given the previous layer's activation vector, the weight matrix
	 * between the two layers, and the biases of the current layer.
	 * 
	 * @param weights weight relationships between two layers
	 * @param prevActivationVector outputs of previous layer
	 * @param biases biases of current layer
	 * @return outputs of current layer
	 */
	public static VectorN activationVector(final MatrixNN weights, final VectorN prevActivationVector, final VectorN biases) {
		VectorN intermediate = (weights.multiply(prevActivationVector)).plus(biases);
		
		return intermediate.apply(Functions::sigmoid);
	}
	
	public static VectorN getMSEVector(final VectorN expected, final VectorN actual) {
		return VectorN.operate(expected, actual, (e, a) -> 0.5f * (e - a) * (e - a));
	}
	
	public static MatrixNN getDeltas(final VectorN output, final VectorN expected, final VectorN prevOutput, final float learningRate) {
		MatrixNN deltas = new MatrixNN(output.SIZE, prevOutput.SIZE);
		
		deltas.populate((out, prev) -> -(expected.get(out) - output.get(out)) * output.get(out) * (1 - output.get(out)) * prevOutput.get(prev) * learningRate);
		
		return deltas;
	}
}
