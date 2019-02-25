package main.structure;

import java.util.Random;

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
	
	public VectorN feedForward(VectorN inputs) {
		VectorN output = inputs;
		
		for (int i = 0; i < weights.length; i++) {
			output = Functions.activationVector(weights[i], output, biases[i]);
		}
		
		return output;
	}
}
