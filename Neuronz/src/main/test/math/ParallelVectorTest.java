package main.test.math;

import static main.test.TestUtilities.*;

import main.math.VectorN;

public class ParallelVectorTest {
	
	public static void addTest() {
		int size = 5000;
		
		VectorN vec0 = VectorN.randomGaussianVector(size);
		VectorN vec1 = VectorN.randomGaussianVector(size);
		VectorN vec2 = timeAndOutput("Parallel vector addition with size " + size, () -> VectorN.operateInParallel(vec0, vec1, (v0, v1) -> v0 + v1));
		
		VectorN vec3 = timeAndOutput("Sequential vector addition with size " + size, () -> vec0.plus(vec1));
	}
}
