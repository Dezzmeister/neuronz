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
}
