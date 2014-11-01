package com.lion328.thaifixes.nmod;

public enum ThaiFixesFontStyle {

	UNICODE("unicode", "default"), MCPX("mcpx"), DISABLE("disable");
	
	private String[] s;
	
	ThaiFixesFontStyle(String... s) {
		this.s = s;
	}
	
	@Override
	public String toString() {
		return s[0];
	}
	
	public String[] toStringArray() {
		return s.clone();
	}
	
	public boolean compare(String g) {
		for(String h : s) if(h.equalsIgnoreCase(g)) return true;
		return false;
	}
}
