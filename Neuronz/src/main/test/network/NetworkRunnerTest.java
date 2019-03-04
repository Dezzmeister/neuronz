package main.test.network;

import main.math.VectorN;
import main.structure.Network;
import main.structure.NetworkUtilities;
import main.structure.execution.NetworkEvaluatorFunction;
import main.structure.execution.NetworkPredictorFunction;
import main.structure.execution.NetworkRunner;
import main.structure.execution.VectorNBatch;

public class NetworkRunnerTest {
	
	public static void bitwiseWithVectorBatch() {
		Network network = new Network(4, 12, 2);
		
		int batchLength = 100000;
		float learningRate = 0.1f;
		
		VectorNBatch inputBatch = VectorNBatch.generateBatch(batchLength, 4, (index) -> (float)(int)(Math.random() * 2));
		
		NetworkPredictorFunction predictor = (v, i) -> {
			int result = ((int)v.get(0) & (int)v.get(1)) ^ (~(int)v.get(2) | (int)v.get(3));
			
			int e0 = 0;
			int e1 = 1;
			
			if (result == 1) {
				e0 = 1;
				e1 = 0;
			}
			
			return new VectorN(e0, e1);
		};
		
		NetworkEvaluatorFunction evaluator = (e, a, i) -> {
			if (a.get(0) > a.get(1)) {
				return e.get(0) == 1;
			} else {
				return e.get(1) == 1;
			}
		};
		
		NetworkRunner runner = new NetworkRunner(network, inputBatch);
		
		runner.runAndTrain(predictor, evaluator, 10000, learningRate);
		NetworkUtilities.saveAs(network, "networks/more-complicated-bitwise-network.ntwk");
	}
	
	public static void mathTest() {
		Network network = new Network(4, 4, 2);
		
		int setLength = 2000000;
		float learningRate = 0.1f;
		
		VectorNBatch inputSet = VectorNBatch.generateBatch(setLength, 4, i -> (float)(int)(Math.random() * 2));
		
		NetworkPredictorFunction predictor = (v, i) -> {
			int result = ((int)v.get(0) ^ (int)v.get(1) << 1) | ((int)v.get(2) & (int)v.get(3));
			int d0 = result >> 1;
			int d1 = (result & 1) >> 2;
			
			return new VectorN(d0, d1);
		};
		
		NetworkEvaluatorFunction evaluator = (e, a, i) -> {
			int d0 = a.get(0) > 0.5f ? 1 : 0;
			int d1 = a.get(1) > 0.5f ? 1 : 0;
			return (e.get(0) == d0 && e.get(1) == d1);
		};
		
		NetworkRunner runner = new NetworkRunner(network, inputSet).saveBestAs("networks/mathtest0.ntwk");
		
		runner.runAndTrain(predictor, evaluator, 10000, learningRate);
		System.out.println(runner.getLatestOutput());
	}
}
