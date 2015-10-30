package dota.gamedata;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.ArrayList;

import java.net.URL;
import java.net.MalformedURLException;

import org.json.JSONObject;
import org.json.JSONArray;


public class MatchDetails {
	private String apiKey;

	public MatchDetails (String apiKey) {
		this.apiKey = apiKey;
	}

	public void setApiKey (String apiKey) {
		this.apiKey = apiKey;
	}

	public String getApiKey () {
		return this.apiKey;
	}

	public String buildURL (int minPlayers, long startAtMatchID, long accountID) {
		String matchURL = Constants.matchHistoryURL;
		matchURL += "?" + Constants.apiKeyString + "=" + apiKey;
		matchURL += "&" + Constants.minPlayers + "=" + minPlayers;
		matchURL += "&" + Constants.accountID + "=" + accountID;
		matchURL += "&" + Constants.startAtMatchID + "=" + startAtMatchID;

		return matchURL;
	}

	public String buildURL (int minPlayers, long startAtMatchID) {
		return buildURL (minPlayers, startAtMatchID, Defaults.accountID);
	}

	public String buildURL (int minPlayers) {
		return buildURL (minPlayers, Defaults.startAtMatchID, Defaults.accountID);
	}

	public String buildURL () {
		return buildURL (Defaults.minPlayers, Defaults.startAtMatchID, Defaults.accountID);
	}

	public String buildMatchURL (long matchID) {
		String matchDetailsURL = Constants.matchDetailsURL;
		matchDetailsURL += "?" + Constants.apiKeyString + "=" + apiKey;
		matchDetailsURL += "&" + Constants.matchID + "=" + matchID;

		return matchDetailsURL;
	}

	public String getDataFromURL (URL url) {
		StringBuilder inputText = new StringBuilder();
		InputStream is = null;
		BufferedReader br;
		String line;

		try {
			is = url.openStream();
			br = new BufferedReader (new InputStreamReader(is));

			while ((line = br.readLine()) != null) {
				inputText.append(line.replaceAll("\\s+", ""));
			}
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ioex) {
				ioex.printStackTrace();
			}
			return inputText.toString();
		}
	}

	public int getNumberOfResults (String jsonText) {
		JSONObject json = new JSONObject(jsonText);
		return json.getJSONObject(Constants.jsonResult)
			.getInt(Constants.jsonNumberOfResults);
	}

	public int getResultsRemaining (String jsonText) {
		JSONObject json = new JSONObject(jsonText);
		return json.getJSONObject(Constants.jsonResult)
			.getInt(Constants.jsonResultsRemaining);
	}

	public ArrayList<Long> getMatchID (String jsonText) {
		JSONObject json = new JSONObject(jsonText);
		JSONArray matches = json.getJSONObject(Constants.jsonResult)
							.getJSONArray(Constants.jsonMatches);

		ArrayList<Long> matchArray = new ArrayList<Long>();

		for (int i = 0; i < matches.length(); i++) {
			matchArray.add(matches.getJSONObject(i).getLong(Constants.jsonMatchID));
		}

		return matchArray;
	}

	public ArrayList<Long> getAccountID (String jsonText) {
		JSONObject json = new JSONObject(jsonText);
		JSONArray matches = json.getJSONObject(Constants.jsonResult)
							.getJSONArray(Constants.jsonMatches);

		ArrayList<Long> accountArray = new ArrayList<Long>();

		for (int i = 0; i < matches.length(); i++) {
			JSONArray players = matches.getJSONObject(i).getJSONArray(Constants.jsonPlayers);
			for (int j = 0; j < players.length(); j++) {
				long accID = players.getJSONObject(j).getLong(Constants.jsonAccountID);
				if (accID != Defaults.noAccount) {
					accountArray.add(accID);
				}
			}
		}

		return accountArray;
	}
}