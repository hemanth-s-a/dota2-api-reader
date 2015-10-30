import dota.gamedata.MatchDetails;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.lang.Thread;

import java.net.URL;
import java.net.MalformedURLException;

public class GetMatchDetails {
	private static BufferedReader reader = null;
	private static PrintWriter writer = null;

	private static MatchDetails matchDetails;

	private static int count;
	private static int countGames;

	public static void setupCtrlC () {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					Thread.sleep(200);
					System.out.println("Shouting down ...");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					try {
						if (writer != null) {
							writer.close();
						}
						if (reader != null) {
							reader.close();
						}
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		});
	}

	public static void main (String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println ("Usage: java GetMatchDetails <input-file>");
			System.exit(0);
		}

		BufferedReader apiKeyReader = new BufferedReader (new FileReader ("apiKey"));
		matchDetails = new MatchDetails(apiKeyReader.readLine());
		apiKeyReader.close();

		reader = new BufferedReader (new FileReader (args[0]));
		writer = new PrintWriter (new FileWriter (args[0] + ".matchDetails"));

		count = 0;
		countGames = 0;

		String urlString;
		String line;
		while ((line = reader.readLine()) != null) {
			urlString = matchDetails.buildMatchURL (Long.valueOf(line).longValue());
			URL url = null;
			try {
				url = new URL (urlString);
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
			}

			++count;
			String jsonText = matchDetails.getDataFromURL (url);
			if (jsonText.length() > 200) {
				writer.println (jsonText);
				++countGames;
			}
			writer.flush();
			System.out.println (count + "\t" + countGames);
		}
	}
}
