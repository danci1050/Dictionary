package sample;

import java.io.File;
import java.io.IOException;

/**
 * A class used to load the JavaFX Runtime modules after launching the program
 *
 * There's probably a better way to do this, but everything else I tried failed. I did not test this on a Windows system,
 * but I don't see why it would not.
 */
public class JARLoader {
	public static void main(String[] args) {
		String path = new JARLoader().getPath();
		String jarName = new JARLoader().getJarName();
		String folderSeparator = System.getProperty("file.separator");

		ProcessBuilder pb = new ProcessBuilder("java", "--module-path", path, "--add-modules", "javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.web,javafx.media", "-cp", path + folderSeparator + jarName, "sample.Main");
		File log = new File(path + folderSeparator + "log.txt");
		pb.redirectErrorStream(true);
		pb.redirectOutput(ProcessBuilder.Redirect.appendTo(log));
		try {
			Process p = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getPath(){
		String absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
		absolutePath = absolutePath.replaceAll("%20"," "); // Surely need to do this here
		return absolutePath;
	}

	public String getJarName(){
		String absolutePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		absolutePath = absolutePath.substring(absolutePath.lastIndexOf("/"));
		absolutePath = absolutePath.replaceAll("%20"," "); // Surely need to do this here
		return absolutePath;
	}

}