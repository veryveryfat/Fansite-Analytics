import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * 
 * @author veryfat
 *
 */

public class ProcessLog {
	private static SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");
	
	public static void main(String[] argv) throws IOException, ParseException {
		String INPUT = argv[0];
		String HOSTS = argv[1];
		String RESOURCES = argv[2];
		String HOURS = argv[3];
		String BLOCKED = argv[4];
					
		BufferedReader bf = new BufferedReader(new FileReader(INPUT));
		BufferedWriter out = new BufferedWriter(new FileWriter(HOSTS));
		BufferedWriter out2 = new BufferedWriter(new FileWriter(RESOURCES));
		BufferedWriter out3 = new BufferedWriter(new FileWriter(HOURS));
		BufferedWriter out4 = new BufferedWriter(new FileWriter(BLOCKED));

		Map<String, Host> hostMap = new HashMap<>();
		Map<String, Resource> resourceMap = new HashMap<>();
		
		LinkedList<HourRecord> hourRecords = new LinkedList<>();
		PriorityQueue<HourRecord> buiestRecord = new PriorityQueue<>();
		
		String line = bf.readLine();
		String timezone = line.split("\\s+")[4].replaceFirst("\\]", "");
		Date startTime = format.parse(line.split("\\s+")[3].replaceFirst("\\[", ""));
		hourRecords.add(new HourRecord(startTime));
		
		Date currentTime = new Date();
		
		while(line != null) {
			try {
				
				/**
				 *  Parse the line of log
				 */
				String[] lineSplit = line.split("\\s+");
				String hostName = lineSplit[0];
				Date time = format.parse(lineSplit[3].replaceFirst("\\[", ""));
				String resourcePath = lineSplit[6].replace("\"", "");
				
	//			if (lineSplit.length != 10 && lineSplit.length != 9) {
	//				System.out.println(lineSplit.length + " " + line);
	//			}
				
				if (time.getDate() != currentTime.getDate()) {
					currentTime = time;
					System.out.println("Current Process to: " + lineSplit[3].replaceFirst("\\[", ""));
				}
				
				/**
				 * Add new host or update the attempt times
				 */
				Host host;			
				if (!hostMap.containsKey(hostName)) {
					host = new Host(hostName, 1);
					hostMap.put(hostName, host);
				} else {
					host = hostMap.get(hostName);
					host.addAttemptCount();
				}
				
				/**
				 * Add new resources with bandwidth or update the access times
				 */
				Resource resource;
				if (!resourceMap.containsKey(resourcePath)) {				
					int bandWidth;
					if(lineSplit[lineSplit.length - 1].equals("-")) {
						bandWidth = 0;
					} else {
						bandWidth = Integer.parseInt(lineSplit[lineSplit.length - 1]);
					}
					resource = new Resource(resourcePath, bandWidth);
					resourceMap.put(resourcePath, resource);
				} else {
					resource = resourceMap.get(resourcePath);
					resource.addAccessCount();
				}
				
				/**
				 * Adjust block status and judge if current attempt should be blocked
				 */
				if (resourcePath.equals("/login")) {
					if (lineSplit[lineSplit.length - 2].equals("200")) {
						host.checkStatus(time, true);
					} else {
						host.checkStatus(time, false);
					}
				}
				
				if (host.shouldBlock(time)) {
					out4.write(line);
					out4.write("\n");
				}
					
				/**
				 * Maintain the access count record of recent 60 minutes and update it.
				 * Maintain the record of top 10 buiest hours which end before 60 minutes.
				 */				
				while (time.getTime() - hourRecords.getLast().getStartTime().getTime() != 0) {
					Date last = hourRecords.getLast().getStartTime();
					Date newStart = new Date(last.getTime() + 1000);
					hourRecords.addLast(new HourRecord(newStart));
				}				
				while(time.getTime() - hourRecords.getFirst().getStartTime().getTime() > 60 * 60 * 1000) {
					addToQueue(buiestRecord, hourRecords.removeFirst(), 10);
				}				
				for(HourRecord record : hourRecords) {
					record.addCount();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(line);
			}
			
			line = bf.readLine();
		}
		
		outputHosts(hostMap, out);
		outputResources(resourceMap, out2);
		
		for (HourRecord record : hourRecords) {
			addToQueue(buiestRecord, record, 10);
		}
		outputBusyHours(buiestRecord, timezone, out3);
			
		
		//System.out.println(hostMap.size());
		//System.out.println(resourceMap.size());

//		System.out.println(hosts.size());
//		System.out.println(resources.size());
		
		bf.close();
		out.close();
		out2.close();
		out3.close();
		out4.close();
		
	}
	
	
	/**
	 * Add 
	 * Keep only top 10 busiest record using min heap
	 * @param pq
	 * @param record
	 * @param limit
	 * @throws ParseException
	 */
	public static void addToQueue(PriorityQueue<HourRecord> pq, HourRecord record, int limit) throws ParseException {
		Date tmp = format.parse("13/Jul/1995:09:48:56");
		if(record.getStartTime().getTime() - tmp.getTime() == 0) {
			System.out.println("test time:" + record.getCount());
			System.out.println("test peek:" + pq.peek().getCount());
		}
		if (pq.size() < limit) {
			pq.add(record);
		}else {
			if (record.getCount() > pq.peek().getCount()) {
				pq.poll();
				pq.offer(record);
			}
		}
	}
	

	public static void outputHosts(Map<String, Host> hostMap, BufferedWriter out) throws IOException {
		List<Host> hosts = new ArrayList<>(hostMap.values());
		Collections.sort(hosts, Host.Comparators.attempt);
		
		for (int i = 0; i < Math.min(hosts.size(), 10); i++) {
			//System.out.println(hosts.get(i).getHostName());
			//System.out.println(hosts.get(i).getAttemptCount());
			out.write(hosts.get(i).getHostName());
			out.write(",");
			out.write(Integer.toString(hosts.get(i).getAttemptCount()));
			out.write("\n");
		}
	}
	
	public static void outputResources(Map<String, Resource> resourceMap, BufferedWriter out) throws IOException {
		List<Resource> resources = new ArrayList<>(resourceMap.values());
		Collections.sort(resources, Resource.Comparators.consumption);
		
		for (int i = 0; i < Math.min(resources.size(), 10); i++) {
			//System.out.println(resources.get(i).getPath());
			out.write(resources.get(i).getPath());
			out.write("\n");
		}
		
	}
	
	public static void outputBusyHours(PriorityQueue<HourRecord> buiestRecord, String timezone, BufferedWriter out) throws IOException {
		List<HourRecord> outputBuiestHours = new ArrayList<>();
		while(buiestRecord.size() != 0) {
			HourRecord record = buiestRecord.poll();
			//System.out.println(record.getCount());	
			outputBuiestHours.add(record);			
		}
		
		for (int i = outputBuiestHours.size() - 1; i >= 0; i--) {
			HourRecord record = outputBuiestHours.get(i);
			//System.out.println(format.format(record.getStartTime()));
			out.write(format.format(record.getStartTime()).toString());
			out.write(" " + timezone + ",");
			out.write(Integer.toString(record.getCount()));
			if (i != 0) {
				out.write("\n");
			}			
			//System.out.println(record.getCount());			
		}
	}
	
}