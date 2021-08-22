package com.example.bps;

public class GlobalHeader {

	public static final int LINK_TYPE_80211 = 127;

	private int magic;
	private int linkType;
	
	public int getMagic() {
		return magic;
	}

	public void setMagic(int magic) {
		this.magic = magic;
	}

    public int getLinkType() {
        return linkType;
    }

    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }

	public GlobalHeader() {}

	@Override
	public String toString() {
		return "GlobalHeader{" +
				"magic=" + magic +
				", linkType=" + linkType +
				'}';
	}
}
