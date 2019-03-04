package main.test.dataio;

import java.util.ArrayList;
import java.util.List;

import main.dataio.DataInterpreter;
import main.dataio.DataInterpreterTest;
import main.dataio.Database;
import main.math.VectorN;
import main.structure.Network;
import main.structure.NetworkUtilities;
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
		
		@SuppressWarnings("deprecation")
		VectorNBatch inputs = database.convertAndVectorizeInputsDataInterpreterTest(DataInterpreterTest.INPUTS);
		
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
		runner.runAndTrain(predictor, evaluator, 1000, 0.1f);
	}
	
	//"cigarettes","cigars","chewing tobacco","e-cigarettes","marijuana"
	public static void trainDrugUsePredictor() {
		Database database = new Database("data/drugs/NYTS2017.mdb");
		database.loadColumnsAs(DataInterpreter.COLUMNS, DataInterpreter.ALIASES, "NYTS2017A");
		database.filterNullsIn(DataInterpreter.FILTER_NULLS);
		
		List<List<Float>> outputs = new ArrayList<List<Float>>();
		
		for (int i = 0; i < DataInterpreter.OUTPUTS.length; i++) {
			List<Integer> rawValues = database.getColumnData(DataInterpreter.OUTPUTS[i]);
			List<Float> converted = new ArrayList<Float>();
			
			for (int j = 0; j < database.length(); j++) {
				float val = DataInterpreter.interpret(rawValues.get(j), DataInterpreter.OUTPUTS[i]);
				converted.add(val);
			}
			outputs.add(converted);
		}
		
		VectorNBatch inputs = database.convertAndVectorizeInputsDataInterpreter(DataInterpreter.INPUTS);
		
		Network network = new Network(DataInterpreter.INPUTS.length, 10, 2);
		
		NetworkPredictorFunction predictor = (v, index) -> {
			float e0 = 0;
			float e1 = 0;
			
			for (int i = 0; i < DataInterpreter.OUTPUTS.length; i++) {
				float val = outputs.get(i).get(index);
				
				if (i != DataInterpreter.OUTPUTS.length - 1) {
					e0 = (e0 == 0) ? val : 1;
				} else {
					e1 = (e1 == 0) ? val : 1;
				}
			}
			
			return new VectorN(e0, e1);
		};
		
		final float limit = 0.005f;
		NetworkEvaluatorFunction evaluator = (e, a, i) -> {
			boolean nicotene = a.get(0) >= limit;
			boolean marijuana = a.get(1) >= limit;
			
			boolean nicoteneExpected = e.get(0) == 1;
			boolean marijuanaExpected = e.get(1) == 1;
			
			if ((nicotene && nicoteneExpected) || (marijuana && marijuanaExpected)) {
				return true;
			}
			return false;
		};
		
		NetworkRunner runner = new NetworkRunner(network, inputs).saveBestAs("networks/drugUsePredictor.ntwk");
		runner.runAndTrain(predictor, evaluator, 1000, 0.1f);
	}
}
