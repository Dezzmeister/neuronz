package main.test.network;

import main.math.VectorN;
import main.structure.Network;

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
}
 