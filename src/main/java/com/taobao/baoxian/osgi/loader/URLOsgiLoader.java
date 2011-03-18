package com.taobao.baoxian.osgi.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.taobao.baoxian.osgi.TestInterface;
import com.taobao.baoxian.pojo.OsgiItem;

public class URLOsgiLoader implements OsgiLoader{

	private static Logger logger = Logger.getLogger("com.taobao.baoxian.osgi");

	
	
	public Class<?> load(OsgiItem item) throws MalformedURLException {
		if(item == null || item.getJarpath()==null || "".equals(item.getJarpath()))
			return null;
		//String jarpath = "jar:file:///home/villasy/resources/laoyizhuang.jar!/";
		String jarpath = item.getJarpath();
		jarpath = "jar:file://"+jarpath+"!/";
		OsgiClassLoader classLoader = new OsgiClassLoader(jarpath);
		//加载.class文件
		Class<?> cls = null;
		//String clasName = item.getClassName();
		//提供要加载的类名称
		try {
			cls = classLoader.loadClass("OsgiExtendServiceImpl");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return cls;
	}
	
	private class OsgiClassLoader extends ClassLoader {
		
		private URL url;
		private Hashtable<String, byte[]> resources = new Hashtable<String, byte[]>();
		private Hashtable<String, byte[]> clazz = new Hashtable<String, byte[]>();
		
		public OsgiClassLoader(String url) throws MalformedURLException {
			super(Thread.currentThread().getContextClassLoader());
			this.url = new URL(url);

			String tfsId = url;			
			String rexp = "^http://.*";					// 如果是url开头的地址
			Pattern pattern = Pattern.compile(rexp);
			Matcher m = pattern.matcher(url.toLowerCase());
			//jar包url，某个网址:http://www.addf.dfjlsdj/Test.jar
			if(m.matches()){
				String[] splitUrl = url.split("/");
				tfsId = splitUrl[splitUrl.length-1];
			}
			try {
				JarURLConnection juc = (JarURLConnection)this.url.openConnection();
				JarFile jf = juc.getJarFile();
				JarEntry entry;
				InputStream is;

				Enumeration<JarEntry> entries = jf.entries();

				byte entryBytes[];
				while(entries.hasMoreElements()) {
					entry = entries.nextElement();
					
					if (entry.getName().endsWith(".class")) {
						String fullName = entry.getName().replace("/", ".").replace(".class", "");
						String[] name = fullName.split("\\.");
						String clazzName = name[name.length-1];
						is = jf.getInputStream(entry);
						entryBytes = getResourceAsBytes(is);
						clazz.put(clazzName, entryBytes);
					} else {
						is = jf.getInputStream(entry);
						entryBytes = getResourceAsBytes(is);
						String[] name = entry.getName().split("/");
						String resName = name[name.length-1];
						resources.put(resName, entryBytes);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		
		private byte[] getResourceAsBytes(InputStream jar)throws IOException {
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int size;
			while (jar.available() > 0) {
				size = jar.read(buffer);
				if (size > 0) {
					data.write(buffer, 0, size);
				}
			}
			return data.toByteArray();
		}
	
		public URL getResource(String name){
			if (resources.containsKey(name)) {
				try {
					return new URL(url + "!" + name);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			return super.getResource(name);
		}
		
		public InputStream getResourceAsStream(String name) {
			if (resources.containsKey(name)) {
				return new ByteArrayInputStream((byte[]) resources.get(name));
			}
			return super.getResourceAsStream(name);
		}
	
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			if(clazz.containsKey(name)){
				byte[] bytes = clazz.get(name);
				return defineClass(name, bytes, 0,clazz.get(name).length);
			}
			else
				return super.loadClass(name);
		}
	}
}
