package main.test.network;

import main.math.VectorN;
import main.structure.Network;
import main.structure.NetworkUtilities;

public class NetworkTest {
	
	public static void randomNetworkTest() {
		Network network = new Network(5,7,3);
		VectorN output = network.feedForward(new VectorN(0f,0.2f,0.7f,0.3f,0.3f));
		System.out.println(output);
	}
	
	public static void randomNetworkLearningTest() {
		Network network = new Network(5, 7, 3);
		VectorN input = new VectorN(0.1f, 0.67f, 0.4f, 0.8f, 0.48f);
		VectorN expected = new VectorN(0.4f, 0.6f, 0.5f);
		float learningRate = 0.1f;

		for (int i = 0; i < 10000; i++) {
			System.out.println(network.feedForwardAndLearn(input, expected, learningRate));
		}
	}
	
	/**
	 * Trains a neural network to perform a bitwise operation
	 */
	public static void bitwiseNetworkLearningTest() {
		Network network = new Network(2, 4, 2);
		float learningRate = 0.1f;
		float epochs = 10000;
		for (int j = 0; j < 10; j++) {
			int correct = 0;
			for (int i = 0; i < epochs; i++) {
				int rand0 = (int)(Math.random() * 2);
				int rand1 = (int)(Math.random() * 2);
				int expectedResult = rand0 | rand1;
				
				VectorN expected = new VectorN(expectedResult == 1 ? 1 : 0, expectedResult == 0 ? 1 : 0);
				VectorN input = new VectorN(rand0, rand1);
				
				VectorN output = network.feedForwardAndLearn(input, expected, learningRate);
				
				int realResult = (output.get(0) > output.get(1)) ? 1 : 0;
				
				if (realResult == expectedResult) {
					correct += 1;
				}
			}
			System.out.println("Percent correct in last 10000 epochs: " + ((100.0f * correct)/10000) + "%");
		}
		NetworkUtilities.saveAs(network, "networks/bitwise-network.ntwk");
	}
	
	public static void savedBitwiseNetworkLearningTest() {
		Network network = NetworkUtilities.load("networks/bitwise-network.ntwk");
		float learningRate = 0.1f;
		float epochs = 10000;
		for (int j = 0; j < 10; j++) {
			int correct = 0;
			for (int i = 0; i < epochs; i++) {
				int rand0 = (int)(Math.random() * 2);
				int rand1 = (int)(Math.random() * 2);
				int expectedResult = rand0 | rand1;
				
				VectorN expected = new VectorN(expectedResult == 1 ? 1 : 0, expectedResult == 0 ? 1 : 0);
				VectorN input = new VectorN(rand0, rand1);
				
				VectorN output = network.feedForwardAndLearn(input, expected, learningRate);
				
				int realResult = (output.get(0) > output.get(1)) ? 1 : 0;
				
				if (realResult == expectedResult) {
					correct += 1;
				}
			}
			System.out.println("Percent correct in last 10000 epochs: " + ((100.0f * correct)/10000) + "%");
		}
	}
}
 