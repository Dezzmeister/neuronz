package main;

import main.test.TestUtilities;
import main.test.math.ParallelVectorTest;

public class Main {
	
	public static void main(String[] args) {
		TestUtilities.repeat(ParallelVectorTest::addTest, 10);
	}
	
}
