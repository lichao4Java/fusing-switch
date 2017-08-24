package com.qding.fusing;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author lichao
 *
 */
public class FusingSwitchValue {

	//0 close
	//1 open
	//2 half-open
	private int fusingSwitchStatus;
	
	//half-open状态下指定的时间内成功次数
	private AtomicInteger halfOpenFusingSwitchSuccessCount = new AtomicInteger();
	//half-open状态下成功次数的有效期
	private long halfOpenFusingSwitchSuccessSecondExpireAt;
	
	
	//close状态下失败次数
	private AtomicInteger openFusingSwitchFaildCount = new AtomicInteger();
	//close状态下失败次数的有效期
	private long openFusingSwitchFaildSecondExpireAt;
	
	//open状态的持续时间
	private long openFusingSwitchSecondExpiredAt;
	
	public FusingSwitchValue() {

	}

	public int getFusingSwitchStatus() {
		return fusingSwitchStatus;
	}
	
	public void setFusingSwitchStatus(int fusingSwitchStatus) {
		this.fusingSwitchStatus = fusingSwitchStatus;
	}

	public AtomicInteger getHalfOpenFusingSwitchSuccessCount() {
		return halfOpenFusingSwitchSuccessCount;
	}

	public void setHalfOpenFusingSwitchSuccessCount(
			AtomicInteger halfOpenFusingSwitchSuccessCount) {
		this.halfOpenFusingSwitchSuccessCount = halfOpenFusingSwitchSuccessCount;
	}

	public long getHalfOpenFusingSwitchSuccessSecondExpireAt() {
		return halfOpenFusingSwitchSuccessSecondExpireAt;
	}

	public void setHalfOpenFusingSwitchSuccessSecondExpireAt(
			long halfOpenFusingSwitchSuccessSecondExpireAt) {
		this.halfOpenFusingSwitchSuccessSecondExpireAt = halfOpenFusingSwitchSuccessSecondExpireAt;
	}

	public AtomicInteger getOpenFusingSwitchFaildCount() {
		return openFusingSwitchFaildCount;
	}

	public void setOpenFusingSwitchFaildCount(
			AtomicInteger openFusingSwitchFaildCount) {
		this.openFusingSwitchFaildCount = openFusingSwitchFaildCount;
	}

	public long getOpenFusingSwitchFaildSecondExpireAt() {
		return openFusingSwitchFaildSecondExpireAt;
	}

	public void setOpenFusingSwitchFaildSecondExpireAt(
			long openFusingSwitchFaildSecondExpireAt) {
		this.openFusingSwitchFaildSecondExpireAt = openFusingSwitchFaildSecondExpireAt;
	}

	public long getOpenFusingSwitchSecondExpiredAt() {
		return openFusingSwitchSecondExpiredAt;
	}
	
	public void setOpenFusingSwitchSecondExpiredAt(
			long openFusingSwitchSecondExpiredAt) {
		this.openFusingSwitchSecondExpiredAt = openFusingSwitchSecondExpiredAt;
	}

	@Override
	public String toString() {
		return "FusingSwitchValue [fusingSwitchStatus=" + fusingSwitchStatus
				+ ", halfOpenFusingSwitchSuccessCount="
				+ halfOpenFusingSwitchSuccessCount
				+ ", halfOpenFusingSwitchSuccessSecondExpireAt="
				+ halfOpenFusingSwitchSuccessSecondExpireAt
				+ ", openFusingSwitchFaildCount=" + openFusingSwitchFaildCount
				+ ", openFusingSwitchFaildSecondExpireAt="
				+ openFusingSwitchFaildSecondExpireAt + ", openFusingSwitchSecondExpiredAt="
				+ openFusingSwitchSecondExpiredAt + "]";
	}
	
}
