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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.lion328.thaifixes.ThaiFixesCore;

public class ClassMap {
	
	public static final boolean OBFUSCATED = false;

	public static ClassMap instance = new ClassMap();
	
	public static void load() throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		File base = null;
		try {
			base = new File(ClassLoader.getSystemClassLoader().getResource("assets/thaifixes/classmap").toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
		
		File[] files = base.listFiles();
		Gson gson = new Gson();
		for(File f : files) if(f.isFile()) if(f.getName().endsWith(".json")) ClassMap.instance.registerClassInformation(gson.fromJson(new FileReader(f), ClassInformation.class));
	}
	
	private Map<String, ClassInformation> infos = new HashMap<String, ClassInformation>();
	
	public ClassInformation getClassInformation(String name) {
		return infos.get(name);
	}
	
	public void registerClassInformation(ClassInformation info) {
		infos.put(info.getClassObject().getName(), info);
	}
}
