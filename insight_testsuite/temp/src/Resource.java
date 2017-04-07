import java.util.Comparator;

public class Resource {
	private String path;
	private int bandWidth;
	private int accessCount = 0;

	public Resource(String path, int bandWidth) {
		this.path = path;
		this.bandWidth = bandWidth;
		this.accessCount = 1;
	}
	
	/**
	 * 
	 */
	public void addAccessCount() {
		accessCount++;
	}

	public static class Comparators {
		/**
		 * 
		 */
		public static Comparator<Resource> consumption = new Comparator<Resource> () {
			@Override
			public int compare(Resource o1, Resource o2) {
				long consumption1 =  o1.bandWidth * o1.accessCount;
				long consumption2 =  o2.bandWidth * o2.accessCount;
				if ((int)(consumption2 - consumption1) != 0) {
					return (int)(consumption2 - consumption1);
				}
				return o1.path.compareTo(o2.path);
			} 	
		};
	}
	
	public String getPath() {
		return path;
	}

	public int getBandWidth() {
		return bandWidth;
	}

	public int getAccessCount() {
		return accessCount;
	}
	

}
