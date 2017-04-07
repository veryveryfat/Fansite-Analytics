import java.util.Date;

public class HourRecord implements Comparable<HourRecord>{
	private Date startTime;
	private int count;
	
	/**
	 * 
	 * @param start
	 */
	public HourRecord(Date start) {
		this.startTime = start;
		this.count = 0;
	}
	
	/**
	 * 
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
