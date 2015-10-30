/*
 * DownloadDriver: Download and save Match ID and account ID
 * Account ID will be saved along with a flag:
 * 0 indicating that the matches for the account hasn't been pulled in
 * 1 indicating that the matches for the account has been fetched and saved
 */
import dota.gamedata.MatchDetails;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

import java.lang.Thread;

import java.net.URL;
import java.net.MalformedURLException;

import org.json.JSONObject;

public class DownloadDriver {
	private static HashMap<Long, Integer> matchIDMap;
	private static HashMap<Long, Integer> accountIDMap;

	private static MatchDetails matchDetails;

	private static ArrayList<Long> matchList;
	private static ArrayList<Long> accountList;

	private static PrintWriter logWriter;

	private static int count;

	public static void get500MatchesAndAccounts (long accountID) {
		long nextMatchID = 0;
		boolean status = true;

		while (status) {
			logWriter.println ("Entered while\t" + accountID + "\t" + nextMatchID);
			String urlString = matchDetails.buildURL (10, nextMatchID, accountID);
			URL url = null;

			try {
				url = new URL (urlString);
			} catch (MalformedURLException mue) {
				mue.printStackTrace();
			}

			++count;
			String jsonText = matchDetails.getDataFromURL (url);

			if (matchDetails.getNumberOfResults (jsonText) > 0) {
				matchList = matchDetails.getMatchID (jsonText);
				logWriter.println ("Adding matches");
				for (int i = 0; i < matchList.size(); i++) {
					matchIDMap.put (matchList.get(i), new Integer(0));
				}
				logWriter.println ("Done matches");

				accountList = matchDetails.getAccountID (jsonText);
				logWriter.println ("Adding accounts");
				for (int i = 0; i < accountList.size(); i++) {
					if (accountIDMap.get (accountList.get(i)) == null
							|| accountIDMap.get (accountList.get(i)) != 1) {
						accountIDMap.put (accountList.get(i), new Integer(0));
					}
				}
				logWriter.println ("Done accounts");

				nextMatchID = matchList.get(matchList.size() - 1);
			}

			if (matchDetails.getResultsRemaining (jsonText) <= 0) {
				status = false;
			} else if (matchDetails.getResultsRemaining (jsonText) == 1) {
				status = false;
			}
			logWriter.println ("Done while\t" + accountID + "\t" + nextMatchID);
		}
	}

	public static void setupCtrlC () {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				PrintWriter writer = null;

				try {
					Thread.sleep(200);
					System.out.println("Shouting down ...");

					writer = new PrintWriter(new FileWriter("MatchList"));
					Iterator<Long> matchIterator = matchIDMap.keySet().iterator();

					while (matchIterator.hasNext()) {
						writer.println (matchIterator.next().longValue());
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} finally {
					if (writer != null) {
						writer.close();
					}
					if (logWriter != null) {
						logWriter.close();
					}
					System.out.println("Total Calls = " + count);
				}
			}
		});
	}

	public static void readMatchID () {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader("MatchList"));
			String matchLine;

			while ((matchLine = reader.readLine()) != null) {
				matchIDMap.put(new Long(matchLine), new Integer(0));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public static void main (String[] args) throws IOException {
		setupCtrlC();
		matchIDMap = new HashMap<Long, Integer>();
		accountIDMap = new HashMap<Long, Integer>();

		logWriter = new PrintWriter(new FileWriter("getGame"));

		accountIDMap.put(new Long(0), new Integer(0));

		count = 0;

		readMatchID();

		BufferedReader apiKeyReader = new BufferedReader (new FileReader ("apiKey"));
		matchDetails = new MatchDetails(apiKeyReader.readLine());
		apiKeyReader.close();

		String historyURL = matchDetails.buildURL (10);

		//get500MatchesAndAccounts (0);
		System.out.println(matchIDMap.size());

		while (true) {
			//Iterator<Long> accountIterator = accountIDMap.keySet().iterator();
			ArrayList<Long> accountIterator = new ArrayList<Long>();
			accountIterator.addAll (accountIDMap.keySet());

			for (int i = 0; i < accountIterator.size(); i++) {
				long accountID = accountIterator.get(i).longValue();
				// synchronized (accountIterator) {
				// 	accountID = accountIterator.next().longValue();
				// }


				if (accountIDMap.get(new Long(accountID)) != 1) {
					get500MatchesAndAccounts (accountID);
					accountIDMap.put(new Long(accountID), new Integer(1));
				}

				System.out.println (matchIDMap.size());
			}
		}
	}
}