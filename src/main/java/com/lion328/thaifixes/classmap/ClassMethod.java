/*
 * Copyright (c) 2014-2015 Waritnan Sookbuntherng
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.lion328.thaifixes.classmap;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ClassMethod {

	private String method_name;
	private String obfuscated_name;
	private MethodType type;
	
	public String getMethodName() {
		return method_name;
	}
	
	public String getObfuscatedMethodName() {
		return obfuscated_name;
	}
	
	public MethodType getType() {
		return type;
	}
	
	public Method getMethod(Class clazz) throws NoSuchMethodException {
		return clazz.getMethod(ClassMap.OBFUSCATED ? obfuscated_name : method_name, type.realParametersType);
	}
	
	public static class MethodType {
		
		private static Map<String, Class> typeCache = new HashMap<String, Class>();
		
		private String[] parameters_type;
		private String return_type;
		
		private volatile Class[] realParametersType = null;
		
		public MethodType(Class[] paramsType) {
			realParametersType = paramsType;
		}
		
		public Class[] getParametersType() throws ClassNotFoundException {
			if(realParametersType == null) {
				realParametersType = new Class[parameters_type.length];
				for(int i = 0; i < realParametersType.length; i++) realParametersType[i] = parseType(parameters_type[i]);
			}
			return realParametersType.clone();
		}
		
		private Class parseType(String type) throws ClassNotFoundException {
			if(typeCache.containsKey(type)) return typeCache.get(type);
			
			Class clazz = null;
			if(type.startsWith("class:")) {
				clazz = ClassMap.instance.getClassInformation(type.substring(6)).getClassObject();
				if(type.endsWith("[]")) clazz = Array.newInstance(clazz, 0).getClass();
			}
			else clazz = Class.forName(type);
			
			typeCache.put(type, clazz);
			return clazz;
		}
		
		public boolean compare(MethodType t) {
			if(t.realParametersType.length != realParametersType.length) return false;
			for(int i = 0; i < realParametersType.length; i++) if(!realParametersType[i].getName().equals(t.realParametersType[i].getName())) return false;
			return true;
		}

	}
}
