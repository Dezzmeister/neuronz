package main.test.dataio.mnist;

import main.dataio.mnist.MnistLoader;

public class MnistLoaderTest {
	
	public static void testImageLoader() {
		String imagePath = "data/mnist/train-images.idx3-ubyte";
		MnistLoader.loadImages(imagePath);
	}
}
