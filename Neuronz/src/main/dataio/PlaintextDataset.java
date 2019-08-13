package main.dataio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Contains {@link String} entries.
 *
 * @author Joe Desmond
 */
public class PlaintextDataset {
	/**
	 * List of entries
	 */
	public final List<String> entries;
	
	/**
	 * Loads 1 or more text files containing entries (1 entry on each line).
	 * 
	 * @param datasetPaths paths to datasets
	 * @throws IOException if there is a problem loading the files
	 */
	public PlaintextDataset(final String ... datasetPaths) throws IOException {
		if (datasetPaths[0] == null) {
			entries = null;
			throw new NullPointerException("datasetPaths cannot be null!");
		}
		entries = Files.readAllLines(Paths.get(datasetPaths[0]));
		for (int i = 1; i < datasetPaths.length; i++) {
			entries.addAll(Files.readAllLines(Paths.get(datasetPaths[i])));
		}
	}
}
