package me.pjq.wechat.service.parkinglot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import me.pjq.util.Log;
import me.pjq.util.Utils;
import me.pjq.wechat.service.ServiceProvider;

public class ParkingLot {
	private static final String TAG = ParkingLot.class.getSimpleName();
	public static final String ADDRESS = "address";

	private static ParkingLot INSTANCE;
	private HashMap<String, Lot> address;

	public ParkingLot() {
		address = new HashMap<String, Lot>();
		initFreeParkingLotList();
	}

	public static ParkingLot getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new ParkingLot();
		}

		return INSTANCE;
	}

	public void initFreeParkingLotList() {
		ServiceProvider.getExecutorService().execute(new Runnable() {

			@Override
			public void run() {
				File file = new File(ADDRESS);
				if (file.exists() && file.isDirectory()) {
					File[] files = file.listFiles();
					for (File item : files) {
						String path = item.getAbsolutePath();
						Lot lot = null;
						try {
							Log.i(TAG, "parse " + path);
							lot = parseOneLot(path);
							address.put(path, lot);
						} catch (Exception e) {
						}
					}
				}
			}
		});
	}

	public static Lot parseOneLot(String fileName) {
		String log = Utils.readFileToString(fileName);
		Lot lot = new Lot(log);

		return lot;
	}

	public HashMap<String, Lot> findParkingLots(float lng, float lat) {
		ArrayList<Lot> lots = new ArrayList<Lot>();
		HashMap<String, Lot> finds = new HashMap<String, Lot>();

		Iterator<Entry<String, Lot>> iterator = address.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Lot> item = iterator.next();
			String key = item.getKey();
			Lot value = item.getValue();
			if (Utils.randomBoolean()) {
				lots.add(value);
				finds.put(key, value);
			}
		}

		return finds;
	}

	public String buildLocationResponse(float x, float y) {
		StringBuilder sb = new StringBuilder();
		HashMap<String, Lot> finds = findParkingLots(x, y);
		Iterator<Entry<String, Lot>> iterator = finds.entrySet().iterator();

		int i = 0;
		while (iterator.hasNext()) {
			i++;
			Entry<String, Lot> item = iterator.next();
			String key = item.getKey();
			Lot value = item.getValue();

			sb.append(i + ":" + key + '\n');
		}

		sb.append("Total free parking lot " + address.size() + ", find "
				+ finds.size());

		return sb.toString();
	}
}
