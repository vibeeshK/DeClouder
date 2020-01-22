package commonTechs;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CustomClassLoader extends ClassLoader {
	/*
	 * Provides a singleton reference to dynamic classloader which loads the classes packaged in given jars
	 */
	private static CustomClassLoader customClassLoader;
	String[] handlerJarNames = null;
	private URLClassLoader myClassLoader = null;	
	public synchronized static Object getInstance(String inClassName, String[] inHandlerJarNames) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		Class<?> cls;
		Object obj = null;
		System.out.println(" @@@ getInstance inClassName= " + inClassName);

		ClassLoader ccl = getCustomClassLoader(inHandlerJarNames).getClassLoader();
		System.out.println(" @@@ getInstance created ccl  = " + ccl);	
		System.out.println(" System.getenv(CLASSPATH) at CustomClassLoader is " + System.getenv("CLASSPATH"));
		System.out.println(" System.getProperty(CLASSPATH) at CustomClassLoader is " + System.getProperty("CLASSPATH"));
		
		cls = Class.forName(inClassName, false, ccl);

		System.out.println(" @@@ getInstance created csl  = " + cls);
		
		obj = cls.newInstance();
			
		return obj;
	}
	private static CustomClassLoader getCustomClassLoader(String[] inHandlerJarNames) throws ClassNotFoundException, IOException {
		System.out.println(" customClassLoader check");
		System.out.println(" aaa customClassLoader = " + customClassLoader);

		if (CustomClassLoader.customClassLoader == null) {
			System.out.println(" customClassLoader is null inHandlerJarNames length is " + inHandlerJarNames.length);
			System.out.println(" customClassLoader is null inHandlerJarNames 0 is " + inHandlerJarNames[0]);
			System.out.println(" customClassLoader is null inHandlerJarNames is " + inHandlerJarNames);
			System.out.println(" customClassLoader is null inHandlerJarNames 1 is " + inHandlerJarNames[1]);
			System.out.println(" customClassLoader is null inHandlerJarNames 2 is " + inHandlerJarNames[2]);
			if (inHandlerJarNames.length > 3) {
				System.out.println(" customClassLoader is null inHandlerJarNames 3 is " + inHandlerJarNames[3]);
			}

			ClassLoader parentClassLoader = CustomClassLoader.class.getClassLoader();
			System.out.println(" parentClassLoader pointer at getCustomClassLoader is " + parentClassLoader);

		    Class swtCls = Class.forName("org.eclipse.swt.events.SelectionAdapter");
			System.out.println(" at getCustomClassLoader SelectionAdapter swtCls = " + swtCls);
			customClassLoader = new CustomClassLoader(parentClassLoader,inHandlerJarNames);
		}
		System.out.println(" bbb customClassLoader = " + customClassLoader);
		return customClassLoader;
	}
	private CustomClassLoader(ClassLoader inParentClassLoader, String[] inHandlerJarNames) throws IOException {
		super(inParentClassLoader);
		handlerJarNames = inHandlerJarNames;
		URL[] urls = new URL[handlerJarNames.length];
		Enumeration<JarEntry> handlersEnum = null;

		System.out.println("handlerJarNames.length = " + handlerJarNames.length);

		for (int jarNameCount = 0; jarNameCount < handlerJarNames.length; jarNameCount++) {
			System.out.println("handlerJarNames[" + jarNameCount + "] : " + handlerJarNames[jarNameCount]);
			JarFile handlersJarFile = new JarFile(handlerJarNames[jarNameCount]);
			System.out.println("urls[" + jarNameCount + "] : " + urls[jarNameCount]);
			handlersEnum = handlersJarFile.entries();
			urls[jarNameCount] = new URL(
					"jar:file:" + handlerJarNames[jarNameCount]
					+ "!/");
			System.out.println("urls[" + jarNameCount + "] : " + urls[jarNameCount]);
		}
		myClassLoader = URLClassLoader
		.newInstance(urls);
	}
	private ClassLoader getClassLoader() {
		return myClassLoader;
	}
}