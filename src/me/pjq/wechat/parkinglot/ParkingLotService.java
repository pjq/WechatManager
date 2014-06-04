package me.pjq.wechat.parkinglot;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import me.pjq.util.Log;
import me.pjq.util.Utils;
import me.pjq.wechat.service.ServiceProvider;

public class ParkingLotService {
	private static final String TAG = ParkingLotService.class.getSimpleName();
	public static final String ADDRESS = "address";

	private static ParkingLotService INSTANCE;
	private HashMap<String, ParkingLot> address;

	public ParkingLotService() {
		address = new HashMap<String, ParkingLot>();
		initFreeParkingLotList();
	}

	public static ParkingLotService getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new ParkingLotService();
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
						ParkingLot lot = null;
						try {
							Log.i(TAG, "parse " + path);
							lot = parseOneLot(path);
							lot.detail = path;

							if (lot.isLocationOK()) {
								address.put(path, lot);
							}

						} catch (Exception e) {
						}
					}
				}
			}
		});
	}

	public static ParkingLot parseOneLot(String fileName) {
		String log = Utils.readFileToString(fileName);
		ParkingLot lot = new ParkingLot(log);

		return lot;
	}

	public ArrayList<ParkingLot> findParkingLots(double lng, double lat) {
		ArrayList<ParkingLot> lots = new ArrayList<ParkingLot>();

		Iterator<Entry<String, ParkingLot>> iterator = address.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Entry<String, ParkingLot> item = iterator.next();
			String key = item.getKey();
			ParkingLot value = item.getValue();
			value.distance = Distance.getDistance(lng, lat,
					value.result.location.lng, value.result.location.lat);

			lots.add(value);
		}

		// Sorting
		Collections.sort(lots, new Comparator<ParkingLot>() {
			@Override
			public int compare(ParkingLot lot1, ParkingLot lot2) {

				return (int) (lot1.distance - lot2.distance);
			}
		});

		return lots;
	}

	//x is lat, y is lng
	public String buildLocationResponse(float x, float y) {
		StringBuilder sb = new StringBuilder();
		ArrayList<ParkingLot> finds = findParkingLots(y, x);

		int size = finds.size();
		for (int i = 0; i < size; i++) {
			ParkingLot lot = finds.get(i);
			sb.append(i + ":" + " distance " + lot.distance+'\n');

			if (i == 5) {
				break;
			}
		}

		sb.append("Total free parking lot " + address.size() + ", find "
				+ finds.size());

		return sb.toString();
	}
}
