package main.test.dataio.passwords;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import main.dataio.PlaintextDataset;
import main.math.VectorN;
import main.structure.Network;
import main.structure.NetworkUtilities;
import main.structure.execution.NetworkEvaluatorFunction;
import main.structure.execution.NetworkPredictorFunction;
import main.structure.execution.NetworkRunner;
import main.structure.execution.VectorNBatch;

public class PasswordTest {
	private static final int PASSWORD_LENGTH = 10;
	
	public static void passwordTest() {		
		Network network = new Network(PASSWORD_LENGTH, 1000, 2);
		//Network network = NetworkUtilities.load("networks/passwords/password-predictor.ntwk");
		PlaintextDataset passwordDataset = null;
		
		try {
			passwordDataset = new PlaintextDataset("data/passwords/password-dataset.txt");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		final List<String> trimmedPasswords = passwordDataset.entries.parallelStream().map(s -> s.length() > PASSWORD_LENGTH ? s.substring(0, PASSWORD_LENGTH) : s).collect(Collectors.toList());
		final List<Boolean> expectedResults = new ArrayList<Boolean>();
		final List<VectorN> allInputs = new ArrayList<VectorN>();
		
		float f = 0;
		
		for (int i = 0; i < trimmedPasswords.size(); i++) {
			f = (float)Math.random();
			
			if (f < 0.2f) {
				allInputs.add(makeVector(getNormalCharacters(PASSWORD_LENGTH)));
				expectedResults.add(false);
			}
			
			String password = trimmedPasswords.get(i);
			
			while (password.length() < PASSWORD_LENGTH) {
				password += (char)0;
			}
			
			allInputs.add(makeVector(password));
			expectedResults.add(true);
		}
		
		NetworkPredictorFunction predictor = (in, i) -> {
			if (expectedResults.get(i)) {
				return new VectorN(1, 0);
			} else {
				return new VectorN(0, 1);
			}
		};
		
		NetworkEvaluatorFunction evaluator = (e, a, i) -> {
			return (e.get(0) == 1 && a.get(0) > a.get(1)) || (e.get(1) == 1 && a.get(1) > a.get(0));
		};
		
		VectorNBatch inputBatch = new VectorNBatch(allInputs.toArray(new VectorN[allInputs.size()]));
		
		NetworkRunner runner = new NetworkRunner(network, inputBatch).saveBestAs("networks/passwords/password-predictor-1000-layer.ntwk");
		runner.runAndTrain(predictor, evaluator, 2000, 10);
	}
	
	private static VectorN makeVector(final String in) {
		float[] values = new float[in.length()];
		
		for (int i = 0; i < in.length(); i++) {
			values[i] = in.charAt(i)/(float)255;
		}
		
		return new VectorN(values);
	}
	
	private static final Random RANDOM = new Random();
	
	private static String getNormalCharacters(int maxLength) {
		int length = -1;
		
		while (length < 4 || length > maxLength) {
			length = (int)(((RANDOM.nextGaussian() + 3)/3) * maxLength);
		}
		
		String out = "";
		
		float f = 0;
		for (int i = 0; i < length; i++) {
			f = (float) Math.random();
			
			if (f < 0.1f) {
				out += (char)(Math.random() * 10 + 48);
			} else if (f < 0.3f) {
				out += (char)(Math.random() * 26 + 65);
			} else {
				out += (char)(Math.random() * 26 + 97);
			}
		}
		
		while (out.length() < maxLength) {
			out += (char)0;
		}
		
		return out;
	}
}
