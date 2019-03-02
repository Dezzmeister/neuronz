package main.structure.execution;

import main.math.VectorN;
import main.structure.Network;

public class NetworkRunner {
	private final Network network;
	private final VectorNBatch inputData;
	
	public NetworkRunner(final Network _network, final VectorNBatch _inputData) {
		network = _network;
		inputData = _inputData;
	}
	
	public void runAndTrain(NetworkPredictorFunction predictor, NetworkEvaluatorFunction evaluator, int epochPrintInterval, float learningRate) {
		inputData.reset();
		
		VectorN nextInput = null;
		int epochs = 0;
		int successes = 0;
		while ((nextInput = inputData.getNext()) != null) {
			VectorN expected = predictor.getExpectedOutput(nextInput);
			VectorN output = network.completeEpoch(nextInput, expected, learningRate);
			epochs++;
			if (evaluator.evaluateEpochSuccess(expected, output)) {
				successes++;
			}
			
			if (epochs == epochPrintInterval) {
				System.out.println("Success rate in " + epochPrintInterval + " epochs: \t" + ((100.0f * successes)/epochs) + "%");
				epochs = 0;
				successes = 0;
			}
		}
	}
}
