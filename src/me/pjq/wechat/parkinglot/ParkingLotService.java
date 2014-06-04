package me.pjq.wechat.parkinglot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
	public static final String LIST_PREPARED = "list_prepared.txt";

	private static ParkingLotService INSTANCE;
	private HashMap<String, ParkingLot> address;
	// md5|disctrict|address|
	// d09134792dc9f79400042e89ee089c8f|黄浦|城隍庙玉龙舫|前两小时免费
	private HashMap<String, String> addressMd5List;

	public ParkingLotService() {
		address = new HashMap<String, ParkingLot>();
		addressMd5List = new HashMap<String, String>();
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
				// parse the address list to md5 and address detail.
				BufferedReader bufferedReader = null;
				try {
					InputStreamReader isr = new InputStreamReader(
							new FileInputStream(LIST_PREPARED), "UTF-8");

					bufferedReader = new BufferedReader(isr);
					String line = null;
					while ((line = bufferedReader.readLine()) != null) {
						int index = line.indexOf("|");
						String md5 = line.substring(0, index);
						String address = line.substring(index + 1);
						Log.i(TAG, " md5 " + md5 + " " + address);
						addressMd5List.put(md5, address);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (bufferedReader != null)
							bufferedReader.close();
					} catch (IOException ex) {
					}
				}

				// parser all the address detail.
				File file = new File(ADDRESS);
				if (file.exists() && file.isDirectory()) {
					File[] files = file.listFiles();
					for (File item : files) {
						String path = item.getAbsolutePath();
						ParkingLot lot = null;
						try {
							Log.i(TAG, "parse " + path);
							lot = parseOneLot(path);
							lot.md5 = item.getName();
							Log.i(TAG, "parse done " + path);

							if (lot.isLocationOK()) {
								address.put(path, lot);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	public static ParkingLot parseOneLot(String fullName) {
		String log = Utils.readFileToString(fullName);
		ParkingLot lot = new ParkingLot(log);

		return lot;
	}

	public String getAddressByMd5(String md5) {
		if (null != addressMd5List) {
			return addressMd5List.get(md5);
		}

		return null;
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

	// x is lat, y is lng
	public String buildLocationResponse(float x, float y) {
		StringBuilder sb = new StringBuilder();
		ArrayList<ParkingLot> finds = findParkingLots(y, x);

		int limit = 10;
		int size = finds.size();
		for (int i = 0; i < size; i++) {
			if (i == limit) {
				break;
			}
			
			ParkingLot lot = finds.get(i);
			String address = getAddressByMd5(lot.md5);
			String[] splits = address.split("\\|");
			if (splits.length >= 2) {
//				sb.append(i + ":" + " distance " + lot.distance + " "
//						+ splits[0] + " " + splits[1] + '\n');
			}

			sb.append((i + 1) + ":" + " " + lot.distance + "m "
					+ address.replaceAll("\\|", " ") + '\n');

		
		}

		sb.append("Total free parking lot " + address.size() + ", find "
				+ limit);

		return sb.toString();
	}

	public static void main(String[] args) {
		String address = "黄浦|城隍庙玉龙舫|前两小时免费";
		String[] splits = address.split("\\|");
		if (splits.length >= 2) {
			System.out.println(splits[0] + splits[1] + '\n');
		}
	}
}
