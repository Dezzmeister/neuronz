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
		VectorN net = (weights.multiply(prevActivationVector)).plus(biases);
		
		return net.apply(Functions::sigmoid);
	}
	
	public static VectorN getMSEVector(final VectorN expected, final VectorN actual) {
		return VectorN.operate(expected, actual, (e, a) -> 0.5f * (e - a) * (e - a));
	}
	
	//TODO: Remove this
	public static MatrixNN getDeltasOld(final VectorN output, final VectorN expected, final VectorN prevOutput, float learningRate) {
		MatrixNN deltas = new MatrixNN(output.SIZE, prevOutput.SIZE);
		
		deltas.populate((out, prev) -> -(expected.get(out) - output.get(out)) * output.get(out) * (1 - output.get(out)) * prevOutput.get(prev) * learningRate);
		
		return deltas;
	}
	
	public static MatrixNN getOutputLayerDeltas(final VectorN totalErrorOverOutput, final VectorN outputOverNetInput, final VectorN prevOutput, float learningRate) {
		MatrixNN deltas = new MatrixNN(totalErrorOverOutput.SIZE, prevOutput.SIZE);
		
		deltas.populate((out, prev) -> totalErrorOverOutput.get(out) * outputOverNetInput.get(out) * prevOutput.get(prev) * learningRate);
		
		return deltas;
	}
	
	public static MatrixNN getHiddenLayerDeltas(final VectorN totalErrorOverOutput, final VectorN outputOverNetInput, VectorN hiddenOutput, VectorN inputs, MatrixNN outputWeights, MatrixNN hiddenWeights, float learningRate) {
		MatrixNN deltas = new MatrixNN(hiddenWeights.ROWS, hiddenWeights.COLS);
		
		VectorN[] outputErrorOverHiddenOutput = new VectorN[hiddenOutput.SIZE];
		float[] totalErrorOverHiddenOutput = new float[hiddenOutput.SIZE];
		
		for (int i = 0; i < hiddenOutput.SIZE; i++) {
			final int index = i;
			outputErrorOverHiddenOutput[i] = new VectorN(outputWeights.ROWS).populate(j -> totalErrorOverOutput.get(j) * outputOverNetInput.get(j) * outputWeights.get(j, index));
			totalErrorOverHiddenOutput[i] = outputErrorOverHiddenOutput[i].sum();
		}
		
		VectorN hiddenOutOverNet = outputOverNetInput(hiddenOutput);
		
		deltas.populate((out, prev) -> totalErrorOverHiddenOutput[out] * hiddenOutOverNet.get(out) * inputs.get(prev) * learningRate);
		
		return deltas;
	}
	
	/**
	 * Returns the partial derivatives of the total error with respect to each output.
	 * 
	 * @param output real neuron outputs
	 * @param expected	expected neuron outputs
	 * @return partial derivatives
	 */
	public static VectorN totalErrorOverOutput(final VectorN output, final VectorN expected) {
		return output.minus(expected);
	}
	
	/**
	 * Returns the derivatives of each output with respect to its net input.
	 * 
	 * @param output real neuron outputs
	 * @return derivatives
	 */
	public static VectorN outputOverNetInput(final VectorN output) {
		return output.apply(Functions::sigmoidPrime);
	}
}
