package com.lion328.thaifixes.classmap;

public class JSONClassInformation implements IClassInformation {

	private volatile boolean obfuscated = false;
	
	// JSON fields
	private String minecraft_version;
	private String packagename;
	private String obfuscated_classname;
	
	@Override
	public void setObfuscated(boolean obfuscated) {
		this.obfuscated = obfuscated;
	}

	@Override
	public Class<?> getClassObject() throws ClassNotFoundException {
		return null;
	}

	@Override
	public String getMethodName(Class<?> returnType, Class<?>... parametersType) {
		return null;
	}

	@Override
	public String getFieldName(String name) {
		return null;
	}

}
