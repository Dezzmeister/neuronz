package main.dataio.mnist;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import main.math.VectorN;
import main.structure.execution.VectorNBatch;

public class MnistLoader {
	
	public static VectorNBatch loadImages(String imagePath) {
		VectorN[] data = null;
		
		try {
			byte[] file = Files.readAllBytes(Paths.get(imagePath));
			int items = concatenateUnsignedBytes(file[4], file[5], file[6], file[7]);
			int rows = concatenateUnsignedBytes(file[8], file[9], file[10], file[11]);
			int cols = concatenateUnsignedBytes(file[12], file[13], file[14], file[15]);
			
			int offset = 16;
			for (int i = 0; i < items; i++) {
				float[] image = new float[rows * cols];
				
				for (int pixel = 0; pixel < rows * cols; pixel++) {
					image[pixel] = unsigned(file[(pixel + offset + (i * rows * cols))])/255.0f;
				}
				
				data[i] = new VectorN(image);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new VectorNBatch(data);
	}
	
	private static int concatenateUnsignedBytes(byte b0, byte b1, byte b2, byte b3) {
		return (unsigned(b0) << 24) | (unsigned(b1) << 16) | (unsigned(b2) << 8) | unsigned(b3);
	}
	
	private static int unsigned(byte b) {
		return b & 0xFF;
	}
}
