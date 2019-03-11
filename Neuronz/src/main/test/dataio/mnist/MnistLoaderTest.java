package main.test.dataio.mnist;

import main.dataio.mnist.MnistLoader;

public class MnistLoaderTest {
	
	public static void testImageLoader() {
		String imagePath = "data/mnist/train-images.idx3-ubyte";
		MnistLoader.loadImages(imagePath);
		
		String labelPath = "data/mnist/train-labels.idx1-ubyte";
		System.out.println(MnistLoader.loadLabels(labelPath).length);
	}
}
