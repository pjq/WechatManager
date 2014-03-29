package me.pjq.wechat.service;

import me.pjq.util.Log;

import java.io.*;
import java.util.HashMap;

import org.apache.http.util.TextUtils;

/**
 * Created by pengjianqing on 2/14/14.
 */
public class DictionService {
	private static final String TAG = DictionService.class.getSimpleName();

	private static final String WORDS_PATH = "./word.list";

	private static DictionService instance;

	HashMap<String, String> dictions;

	public DictionService() {
		if (null == dictions) {
			parser();
			new WordListUpdater().updateFromServer();
		}
	}

	public void updateFromServer() {
		WordListUpdater wordListUpdater = new WordListUpdater();
		wordListUpdater.updateFromServer();
	}

	public static DictionService getInstance() {
		if (null == instance) {
			instance = new DictionService();
		}

		return instance;
	}

	public HashMap<String, String> getDictions() {
		return dictions;
	}

	public String getValue(String value) {
		String result = value;

		if (null != dictions) {
			Log.i(TAG, "getValue, dictions size = " + dictions.size()
					+ ", value = " + value);
		}

		if (null != dictions && dictions.containsKey(value)) {
			result = dictions.get(value);
			result = result.replace(" ", "");
		}

		return result;
	}

	public String convert(String input) {
		if (TextUtils.isEmpty(input)) {
			return input;
		}

		char[] inputs = input.toCharArray();
		StringBuilder stringBuilder = new StringBuilder();
		for (char ch : inputs) {
			String newCh = getValue(String.valueOf(ch));
			stringBuilder.append(newCh);
		}

		return stringBuilder.toString();
	}

	public void parser() {
		ServiceProvider.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				long start = System.currentTimeMillis();
				HashMap<String, String> hashMap = null;
				String path = WORDS_PATH;
				try {
					InputStream inputStream = null;
					if (WordListUpdater.isWordListCached()) {
						String cachePath = WordListUpdater
								.getWordlistCachePath();
						Log.i(TAG, cachePath + " is already cached locally.");
						inputStream = new FileInputStream(new File(cachePath));
					} else {
						inputStream = new FileInputStream(new File(path));
					}

					InputStreamReader isr = new InputStreamReader(inputStream,
							"UTF-8");
					BufferedReader rd = new BufferedReader(isr);
					String line = "";

					hashMap = new HashMap<String, String>();
					while ((line = rd.readLine()) != null) {
						// Log.i(TAG, line);
						if (line.endsWith(";")) {
							line = line.substring(0, line.length() - 1);
						}
						String[] values = line.split(" ");
						if (null == values || values.length < 2) {
							continue;
						}
						String key = values[0];
						String value = line.substring(1);
						//Log.i(TAG, "put key = " + key + ", value = " + value);
						hashMap.put(key, value);
					}

					isr.close();
					rd.close();
					inputStream.close();
					inputStream = null;
				} catch (IOException e) {
					e.printStackTrace();
				}

				long end = System.currentTimeMillis();
				Log.i(TAG, "parse the word.list, use time " + (end - start));
				Log.i(TAG, "total words count " + hashMap.size());

				dictions = hashMap;
			}
		});
	}
}
