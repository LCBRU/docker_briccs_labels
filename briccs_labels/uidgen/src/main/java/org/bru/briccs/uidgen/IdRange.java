package org.bru.briccs.uidgen;

public class IdRange {
	
	private String pk ;
	
	private String prefix ;
	private long fromValue ;
	private long toValue ;
	private String status ;
	/**
	 * @return the pk
	 */
	public String getPk() {
		return pk;
	}
	/**
	 * @param pk the pk to set
	 */
	public void setPk( String pk) {
		this.pk = pk;
	}
	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}
	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	/**
	 * @return the fromValue
	 */
	public long getFromValue() {
		return fromValue;
	}
	/**
	 * @param fromValue the fromValue to set
	 */
	public void setFromValue(long fromValue) {
		this.fromValue = fromValue;
	}
	/**
	 * @return the toValue
	 */
	public long getToValue() {
		return toValue;
	}
	/**
	 * @param toValue the toValue to set
	 */
	public void setToValue(long toValue) {
		this.toValue = toValue;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus( String status) {
		this.status = status;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "IdRange [prefix=" + prefix + ", status=" + status
				+ ", fromValue=" + fromValue + ", toValue=" + toValue + "]";
	}
	

}
