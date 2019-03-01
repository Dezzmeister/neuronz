package main.structure;

import java.util.Random;
import java.util.function.Predicate;

import main.math.Functions;
import main.math.MatrixNN;
import main.math.VectorN;

public class Network {
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
	
	public VectorN feedForwardAndLearn(VectorN inputData, VectorN expected, float learningRate) {
		VectorN hiddenOutput = Functions.activationVector(weights[0], inputData, biases[0]);
		VectorN output = Functions.activationVector(weights[1], hiddenOutput, biases[1]);
		
		VectorN totalErrorOverOutput = Functions.totalErrorOverOutput(output, expected);
		VectorN outputOverNetInput = Functions.outputOverNetInput(output);
		
		MatrixNN outputLayerDeltas = Functions.getOutputLayerDeltas(totalErrorOverOutput, outputOverNetInput, hiddenOutput, learningRate);
		MatrixNN hiddenLayerDeltas = Functions.getHiddenLayerDeltas(totalErrorOverOutput, outputOverNetInput, hiddenOutput, inputData, weights[1], weights[0], learningRate);
		weights[1] = weights[1].minus(outputLayerDeltas);
		weights[0] = weights[0].minus(hiddenLayerDeltas);
		
		return output;
	}
	
	public boolean evaluate(VectorN inputData, Predicate<VectorN> successCondition) {
		VectorN output = feedForward(inputData);
		
		return successCondition.test(output);
	}
}
