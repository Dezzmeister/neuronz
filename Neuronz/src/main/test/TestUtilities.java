package main.test;

import java.util.function.Supplier;

public class TestUtilities {
	
	public static <R> R timeAndOutput(String description, Supplier<R> task) {
		long beginTime = System.nanoTime();
		R r = task.get();
		long endTime = System.nanoTime();
		
		System.out.println(description + ": \tFinished in " + ((endTime - beginTime)/1000000.0f) + " ms.");
		return r;
	}
	
	public static void repeat(Runnable task, int reps) {
		for (int i = 0; i < reps; i++) {
			task.run();
		}
	}
}
