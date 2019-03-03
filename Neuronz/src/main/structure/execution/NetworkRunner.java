package main.structure.execution;

import main.math.VectorN;
import main.structure.Network;
import main.structure.NetworkUtilities;

public class NetworkRunner {
	private final Network network;
	private final VectorNBatch inputData;
	private String fileName;
	private boolean saveBest = false;
	
	public NetworkRunner(final Network _network, final VectorNBatch _inputData) {
		network = _network;
		inputData = _inputData;
	}
	
	public NetworkRunner saveBestAs(String _fileName) {
		saveBest = true;
		fileName = _fileName;
		
		return this;
	}
	
	/**
	 * Runs and trains a Network on the inputs given in the constructor.
	 * 
	 * @param predictor function to return the correct output given the input and its index.
	 * @param evaluator returns true if the network was successful for a given epoch
	 * @param epochPrintInterval specifies how often to start recalculating the success rate
	 * @param learningRate the learning rate
	 */
	public void runAndTrain(NetworkPredictorFunction predictor, NetworkEvaluatorFunction evaluator, int epochPrintInterval, float learningRate) {
		inputData.reset();
		
		VectorN nextInput = null;
		int epochs = 0;
		int bestSuccesses = 0;
		int successes = 0;
		int index = 0;
		while ((nextInput = inputData.getNext()) != null) {
			VectorN expected = predictor.getExpectedOutput(nextInput, index);
			VectorN output = network.completeEpoch(nextInput, expected, learningRate);
			epochs++;
			if (evaluator.evaluateEpochSuccess(expected, output, index)) {
				successes++;
			}
			
			if (epochs == epochPrintInterval) {
				System.out.println("Success rate in " + epochPrintInterval + " epochs: \t" + ((100.0f * successes)/epochs) + "%");
				
				if (saveBest) {
					if (successes > bestSuccesses) {
						bestSuccesses = successes;
						NetworkUtilities.saveAs(network, fileName);
					}
				}
				epochs = 0;
				successes = 0;
			}
			
			index++;
		}
	}
}
