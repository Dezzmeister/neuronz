package main.structure;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class NetworkUtilities {
	
	public static void saveAs(Network network, String fileName) {
		try {
			ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(fileName));
			stream.writeObject(network);
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Network load(String fileName) {
		Network network = null;
		
		try {
			FileInputStream file = new FileInputStream(fileName);
			ObjectInputStream reader = new ObjectInputStream(file);
			network = (Network) reader.readObject();
			reader.close();
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return network;
	}
}
