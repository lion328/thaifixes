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

package com.lion328.thaifixes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.minecraft.client.Minecraft;

public class ThaiFixesConfiguration {
	
	private static Properties prop = new Properties();
	private static ThaiFixesFontStyle fontStyle;
	
	public static void loadConfig(File basepath) {
		fontStyle = ThaiFixesFontStyle.UNICODE;
		try {
			File cfg = new File(basepath, "/config/ThaiFixes.cfg");
			if(!cfg.exists()) {
				cfg.createNewFile();
				InputStream in = ThaiFixesConfiguration.class.getResourceAsStream("/assets/thaifixes/config/ThaiFixes.cfg");
				FileOutputStream out = new FileOutputStream(cfg);
				byte[] buffer = new byte[1024];
				int readBytes;
				while((readBytes = in.read(buffer)) != -1)
					out.write(buffer, 0, readBytes);
				out.close();
				in.close();
			}
			FileInputStream in = new FileInputStream(cfg);
			prop.load(in);
			in.close();
			if(prop.containsKey("font.style")) {
				for(ThaiFixesFontStyle style : ThaiFixesFontStyle.values())
					if(style.compare(prop.getProperty("font.style"))) {
						fontStyle = style;
						break;
					}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ThaiFixesFontStyle getFontStyle() {
		return fontStyle;
	}

	public static void load() {}
}
