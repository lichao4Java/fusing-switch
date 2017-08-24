package com.qding.fusing.ext.rmi;

import com.qding.fusing.abstracts.AbstractFusingSwitchProvider;

public class FusingSwitchRMIProvider extends AbstractFusingSwitchProvider{

	private Class<?> clazz;

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FusingSwitchRMIProvider other = (FusingSwitchRMIProvider) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FusingSwitchRPCProvider [clazz=" + clazz + "]";
	}
	
	
}
