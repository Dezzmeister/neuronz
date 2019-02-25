package main.test.math;

import main.math.Functions;
import main.math.MatrixNN;
import main.math.VectorN;
import main.structure.Network;

public class MatrixVectorTest {
	
	public static void matrixVectorMultiplication() {
		float[][] matrixValues = {
				{1, 2, 3},
				{4, 5, 6},
				{7, 8, 9}
		};
		
		MatrixNN matrix = new MatrixNN(matrixValues);
		VectorN vector = new VectorN(2, 1, 3);
		
		VectorN result = matrix.multiply(vector);
		System.out.println(result);
	}
	
	public static void vectorizedFunctionTest() {
		final int SIZE = 150000;
		
		VectorN vector = new VectorN(SIZE);
		for (int i = 0; i < SIZE; i++) {
			vector.set(i, (float)Math.random() * 10);
		}
		
		VectorN result;
		
		System.out.println("Running vectorized sigmoid function on vector of size " + SIZE + "...");
		long beginTime = System.nanoTime();
		result = vector.applySigmoid();
		long endTime = System.nanoTime();
		
		System.out.println("Time (ms): " + ((endTime - beginTime)/1000000.0f));
		
		VectorN result2;
		System.out.println("Running sequential sigmoid function on vector of size " + SIZE + "...");
		beginTime = System.nanoTime();
		result2 = vector.apply(Functions::sigmoid);
		endTime = System.nanoTime();
		System.out.println("Time (ms): " + ((endTime - beginTime)/1000000.0f) + "\n");
		
		vector.set(0, result.get(0));
		vector.set(1, result2.get(0));
	}
	
	public static void repeatedVectorizedFunctionTest() {
		for (int i = 0; i < 10; i++) {
			vectorizedFunctionTest();
		}
	}
	
	public static void rectangularMatrixTest() {
		float[][] matrixValues = {
				{1, 2, 3, 4, 5},
				{9, 8, 7, 6, 5},
				{4, 2, 3, 7, 1}
		};
		
		MatrixNN matrix = new MatrixNN(matrixValues);
		VectorN vector = new VectorN(7, 4, 2, 9, 6);
		
		VectorN result = matrix.multiply(vector);
		System.out.println(result);
	}
	
	public static void randomNetworkTest() {
		Network network = new Network(5,7,3);
		VectorN output = network.feedForward(new VectorN(0f,0.2f,0.7f,0.3f,0.3f));
		System.out.println(output);
	}
}
