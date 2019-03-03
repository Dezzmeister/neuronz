package main.test.dataio;

import java.util.ArrayList;
import java.util.List;

import main.dataio.DataInterpreterTest;
import main.dataio.Database;
import main.math.VectorN;
import main.structure.Network;
import main.structure.execution.NetworkEvaluatorFunction;
import main.structure.execution.NetworkPredictorFunction;
import main.structure.execution.NetworkRunner;
import main.structure.execution.VectorNBatch;

public class DatabaseIOTest {
	
	public static void loadAccessDatabaseTest() {
		Database database = new Database("data/drugs/NYTS2017.mdb");
		String[] columns = {
				"Q3", "Q4A"
		};
		
		String[] aliases = {
				"grade", "latino"
		};
		database.loadColumnsAs(columns, aliases, "NYTS2017A");
		database.filterNullsIn("grade");
		//Size without filtering: 17872
		
		System.out.println("latino size: " + database.getColumnData("latino").size());
		System.out.println("grade size: " + database.getColumnData("grade").size());
	}
	
	public static void trainTestNetwork() {
		Database database = new Database("data/drugs/NYTS2017.mdb");
		database.loadColumnsAs(DataInterpreterTest.COLUMNS, DataInterpreterTest.ALIASES, "NYTS2017A");
		database.filterNullsIn(DataInterpreterTest.FILTER_NULLS);
		
		List<Integer> rawOutputs = database.getColumnData("smoked");
		List<Float> outputs = new ArrayList<Float>();
		
		for (int i = 0; i < rawOutputs.size(); i++) {
			outputs.add(DataInterpreterTest.convert("smoked", rawOutputs.get(i)));
		}
		
		VectorNBatch inputs = database.convertAndVectorizeInputs(DataInterpreterTest.INPUTS);
		
		Network network = new Network(3, 16, 1);
		
		NetworkPredictorFunction predictor = (v, i) -> {
			return new VectorN(new float[] {outputs.get(i)});
		};
		
		NetworkEvaluatorFunction evaluator = (e, a, i) -> {
			if ((a.get(0) >= 0.5f && e.get(0) == 1) || (a.get(0) < 0.5f && e.get(0) == 0)) {
				return true;
			} else {
				return false;
			}
		};
		
		NetworkRunner runner = new NetworkRunner(network, inputs).saveBestAs("networks/threeFactorPrototype.ntwk");
		runner.runAndTrain(predictor, evaluator, 1000, 0.01f);
	}
}
