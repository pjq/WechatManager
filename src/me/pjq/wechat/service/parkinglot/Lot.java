package me.pjq.wechat.service.parkinglot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * <pre>
 * 
 * {
 *     "status":"OK",
 *     "result":{
 *         "location":{
 *             "lng":121.449901,
 *             "lat":31.228283
 *         },
 *         "precise":1,
 *         "confidence":80,
 *         "level":"\u9910\u996e"
 *     }
 * }
 * 
 * </pre>
 * 
 * @author pjq
 * 
 */
public class Lot {
	String status;
	Result result;

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(status);
		if (status.equalsIgnoreCase("OK")) {
			try {
				stringBuilder.append(", " + result.level + ", "
						+ result.location.lng + result.location.lat+'\n');
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return stringBuilder.toString();
	}

	class Result {
		int precise;
		int confidence;
		String level;
		Location location;

		public Result(JsonObject json) {
			precise = json.get("precise").getAsInt();
			confidence = json.get("confidence").getAsInt();
			level = json.get("level").getAsString();
			JsonObject locationJson = json.get("location").getAsJsonObject();
			location = new Location(locationJson);
		}
	}

	class Location {
		float lng;
		float lat;

		public Location(JsonObject json) {
			lng = json.get("lng").getAsFloat();
			lat = json.get("lat").getAsFloat();
		}
	}

	public Lot(JsonObject json) {
		status = json.get("status").getAsString();
		JsonObject resultJson = json.get("result").getAsJsonObject();
		result = new Result(resultJson);
	}

	public Lot(String jsonString) {
		this(new JsonParser().parse(jsonString).getAsJsonObject());
	}

}
