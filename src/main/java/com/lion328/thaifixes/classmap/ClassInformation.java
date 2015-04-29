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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassInformation {

	// JSON fields
	private String package_name;
	private String class_name;
	private String obfuscated_name;
	private ClassMethod[] methods;
	private ClassField[] fields;

	public Class getClassObject() {
		try {
			return Class.forName(ClassMap.OBFUSCATED ? obfuscated_name : new StringBuilder().append(package_name).append('.').append(class_name).toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getUnobfuscatedClassName() {
		return class_name;
	}
	
	public ClassMethod[] getClassMethods() {
		return methods.clone();
	}
	
	public Method getMethod(String name, Class<?>... params) {
		main_loop: for(ClassMethod cm : methods) if(cm.getMethodName().equals(name)) {
			if(params != null) {
				if(cm.getType().getParametersType().length != params.length) continue;
				for(int i = 0; i < params.length; i++) if(!params[i].getName().equals(cm.getType().getParametersType()[i].getName())) continue main_loop;
			}
			try {
				return cm.getMethod(getClassObject());
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public Field getField(String name) {
		for(ClassField cf : fields) if(cf.field_name.equals(name)) {
			try {
				return cf.getField(getClassObject());
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public static class ClassField {
		
		private String field_name;
		private String obfuscated_name;
		
		public String getField_name() {
			return field_name;
		}
		
		public String getObfuscated_name() {
			return obfuscated_name;
		}
		
		public Field getField(Class clazz) throws NoSuchFieldException {
			return clazz.getDeclaredField(ClassMap.OBFUSCATED ? obfuscated_name : field_name);
		}
	}
}
