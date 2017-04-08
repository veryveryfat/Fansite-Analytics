import java.util.Date;

/**
 * Hourly record.
 * @author Y.L
 *
 */
public class HourRecord implements Comparable<HourRecord>{
	private Date startTime;
	private int count;
	
	/**
	 * Construct with the start time of this hour.
	 * @param start
	 */
	public HourRecord(Date start) {
		this.startTime = start;
		this.count = 0;
	}
	
	/**
	 * Add count of access in this hour.
	 */
	public void addCount(){
		count++;
	}
	

	@Override
	public int compareTo(HourRecord o) {
		if (count - o.count == 0){
			return (int)(o.getStartTime().getTime() - startTime.getTime());
		}
		return count - o.count;
	}
	
	
	public Date getStartTime() {
		return startTime;
	}

	public int getCount() {
		return count;
	}

}
