import java.util.Comparator;
import java.util.Date;

/**
 * Host information.
 * @author Y.L
 *
 */
public class Host {
	private String hostName;
	private int attemptCount;

	private Date firstFailedTime;
	private int faliureCount;

	private Date blockStartTime;
	private boolean isBlocked;

	/**
	 * 
	 * @param hostName
	 */
	public Host(String hostName) {
		this.hostName = hostName;
		this.attemptCount = 1;
		this.firstFailedTime = null;
		this.blockStartTime = null;
		this.isBlocked = false;
		this.faliureCount = 0;
	}

	/**
	 * Add one to the attemp time of this host.
	 */
	public void addAttemptCount() {
		attemptCount++;
	}

	/**
	 * Update the count of login failure in 20 seconds.
	 * 
	 * @param Time
	 *            Time of input record.
	 * @param isSuccess
	 *            Boolean indicates if the login attempt is succeed.
	 */
	public void checkStatus(Date time, boolean isSuccess) {
		if (!isBlocked) {
			if (isSuccess) {
				firstFailedTime = null;
				faliureCount = 0;
			} else {
				if (firstFailedTime == null) {
					firstFailedTime = time;
				} else {
					long span = (time.getTime() - firstFailedTime.getTime()) / 1000;
					if (span < 20) {
						faliureCount++;
					} else {
						firstFailedTime = time;
						faliureCount = 0;
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param time
	 * @return
	 */
	public boolean shouldBlock(Date time) {
		if (isBlocked) {
			long span = (time.getTime() - blockStartTime.getTime()) / 1000;
			if (span > 300) {
				this.isBlocked = false;
				blockStartTime = null;
			}
		} else {
			if (faliureCount == 3) {
				blockStartTime = time;
				faliureCount = 0;
				isBlocked = true;
			}
		}
		return isBlocked;
	}

	public static class Comparators {
		/**
		 * 
		 */
		public static Comparator<Host> attempt = new Comparator<Host>() {
			@Override
			public int compare(Host o1, Host o2) {
				if (o2.attemptCount - o1.attemptCount != 0) {
					return o2.attemptCount - o1.attemptCount;
				}
				return o1.hostName.compareTo(o2.hostName);
			}
		};
	}

	public String getHostName() {
		return hostName;
	}

	public int getAttemptCount() {
		return attemptCount;
	}

}
