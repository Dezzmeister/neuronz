package main.test.dataio.mnist;

import main.dataio.mnist.MnistLoader;
import main.math.VectorN;
import main.structure.Network;
import main.structure.execution.NetworkEvaluatorFunction;
import main.structure.execution.NetworkPredictorFunction;
import main.structure.execution.NetworkRunner;
import main.structure.execution.VectorNBatch;

public class MnistNetworkTest {
	
	public static void testMNIST() {
		VectorNBatch inputs = MnistLoader.loadImages("data/mnist/train-images.idx3-ubyte");
		byte[] labels = MnistLoader.loadLabels("data/mnist/train-labels.idx1-ubyte");
		Network network = null;
		
		if (inputs.isUniform()) {
			network = new Network(inputs.width(), 100, 10);
		} else {
			System.out.println("Input batch is not uniform!");
		}
		
		NetworkPredictorFunction predictor = (in, i) -> {
			VectorN expected = new VectorN(10);
			
			byte digit = labels[i];
			expected.set(digit, 1);
			
			for (int j = 0; j < 10; j++) {
				if (j != digit) {
					expected.set(j, 0);
				}
			}
			
			return expected;
		};
		
		NetworkEvaluatorFunction evaluator = (e, a, index) -> {
			int greatestExpectedDigit = -2;
			int greatestActualDigit = -1;
			
			float greatestValue = 0;
			for (int i = 0; i < 10; i++) {
				
				if (a.get(i) > greatestValue) {
					greatestActualDigit = i;
				}
				
				if (e.get(i) == 1) {
					greatestExpectedDigit = i;
				}
			}
			
			return greatestExpectedDigit == greatestActualDigit;
		};
		
		NetworkRunner runner = new NetworkRunner(network, inputs).saveBestAs("networks/mnist/handwritten-char-recognizer-100.ntwk");
		runner.runAndTrain(predictor, evaluator, 1000, 20000.0f);
	}
}
