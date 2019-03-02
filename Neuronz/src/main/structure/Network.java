package main.structure;

import java.io.Serializable;
import java.util.Random;
import java.util.function.Predicate;

import main.math.Functions;
import main.math.MatrixNN;
import main.math.VectorN;

public class Network implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3029795879208794486L;
	
	private final int[] layers;
	private final MatrixNN[] weights;
	private final VectorN[] biases;
	
	public Network(int ... _layers) {
		layers = _layers;
		
		weights = new MatrixNN[layers.length - 1];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = new MatrixNN(layers[i+1], layers[i]);
		}
		
		biases = generateRandomBiases(layers);
	}
	
	private VectorN[] generateRandomBiases(final int[] layers) {
		final VectorN[] tempBiases = new VectorN[layers.length - 1];
		
		Random random = new Random();
		
		for (int row = 0; row < tempBiases.length; row++) {
			tempBiases[row] = new VectorN(layers[row + 1]);
			tempBiases[row].fill(random::nextGaussian);
		}
		
		return tempBiases;
	}
	
	public VectorN feedForward(VectorN inputData) {
		VectorN output = inputData;
		
		for (int i = 0; i < weights.length; i++) {
			output = Functions.activationVector(weights[i], output, biases[i]);
		}
		
		return output;
	}
	
	public VectorN completeEpoch(final VectorN inputData, final VectorN expected, float learningRate) {
		VectorN[] outputs = new VectorN[layers.length];
		outputs[0] = inputData;
		
		for (int i = 1; i < layers.length; i++) {
			outputs[i] = Functions.activationVector(weights[i - 1], outputs[i - 1], biases[i - 1]);
		}
		
		applyDeltas(getBackpropDeltas(outputs, weights, expected, learningRate));
		return outputs[outputs.length - 1];
	}
	
	private MatrixNN[] getBackpropDeltas(final VectorN[] layerOutputs, final MatrixNN[] weights, final VectorN expected, float learningRate) {
		MatrixNN[] deltaMatrices = new MatrixNN[weights.length];
		
		VectorN totalErrorOverOutput = Functions.totalErrorOverOutput(layerOutputs[layerOutputs.length - 1], expected);
		VectorN outputOverNetInput = Functions.outputOverNetInput(layerOutputs[layerOutputs.length - 1]);
		deltaMatrices[layerOutputs.length - 2] = Functions.getOutputLayerDeltas(totalErrorOverOutput, outputOverNetInput, layerOutputs[layerOutputs.length - 2], learningRate);
		
		for (int i = layerOutputs.length - 2; i >= 1; i--) {
			deltaMatrices[i - 1] = Functions.getHiddenLayerDeltas(totalErrorOverOutput, outputOverNetInput, layerOutputs[i], layerOutputs[i - 1], weights[i], weights[i - 1], learningRate);
		}
		
		return deltaMatrices;
	}
	
	private void applyDeltas(final MatrixNN[] deltas) {
		for (int i = 0; i < deltas.length; i++) {
			weights[i] = weights[i].minus(deltas[i]);
		}
	}
	
	public boolean evaluate(VectorN inputData, Predicate<VectorN> successCondition) {
		VectorN output = feedForward(inputData);
		
		return successCondition.test(output);
	}
}
