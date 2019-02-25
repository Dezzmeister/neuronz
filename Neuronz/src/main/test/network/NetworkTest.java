package main.test.network;

import main.math.VectorN;
import main.structure.Network;

public class NetworkTest {
	
	public static void randomNetworkTest() {
		Network network = new Network(5,7,3);
		VectorN output = network.feedForward(new VectorN(0f,0.2f,0.7f,0.3f,0.3f));
		System.out.println(output);
	}
}
