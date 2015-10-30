import dota.gamedata.MatchDetails;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import java.lang.Thread;

public class getMatchDetails {
	private static BufferedReader reader;
	private static PrintWriter writer;

	public static void setupCtrlC () {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					Thread.sleep(200);
					System.out.println("Shouting down ...");
					//some cleaning up code...
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} finally {
					if (writer != null) {
						writer.close();
					}
					if (reader != null) {
						reader.close();
					}
				}
			}
		});
	}

	public static void main (String[] args) throws IOException {
		reader = new BufferedReader (new FileReader ("MatchList"));
		File file = null;
		writer = null;

		for (int i = 0;; i++) {
			file = new File ("MatchDetails" + i);
			if (!file.exists()) {
				break;
			}
		}

		writer = new FileWriter (file, true);
	}
}