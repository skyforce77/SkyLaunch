package fr.skyforce77.launcher;

import java.beans.IntrospectionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fr.skyforce77.launcher.Download.updateThread;

@SuppressWarnings("unused")
public class Launcher extends JFrame{
	
	private static final long serialVersionUID = 3706406341754400586L;
	public static String actual;
	public static String downloadlink;
	public static String versionurl = "http://dl.dropboxusercontent.com/u/38885163/TowerMiner/launcher/version.txt";
	public static Launcher instance;

	public static void main(String[] args) {
		instance = new Launcher();
		getActualVersion();
		if(!getLauncher().exists()) {
			Download.update("Telechargement du launcher", "veuillez patienter", new updateThread(){
				@Override
				public void onUpdated(boolean success) {
					if(success) {
						JOptionPane.showMessageDialog(instance, "Telechargement du jeu effectue, vous devez maintenant relancer le jeu","Information",JOptionPane.INFORMATION_MESSAGE);
						System.exit(1);
					} else {
						JOptionPane.showMessageDialog(instance, "Le telechargement a echoue\ncela peut etre causee par une mise a jour du launcher requise.","Information",JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		} else {
			try {
				addURLToSystemClassLoader(getLauncher().toURI().toURL());
				Class<?> cls = ClassLoader.getSystemClassLoader().loadClass("fr.skyforce77.towerminer.Launcher");
				Field fi = cls.getDeclaredField("version");
				String version = (Integer)fi.get(null)+"";
				if(!actual.startsWith(version)) {
					Download.update("Mise a du launcher", "veuillez patienter", new updateThread(){
						@Override
						public void onUpdated(boolean success) {
							if(success) {
								launch();
							} else {
								JOptionPane.showMessageDialog(instance, "Le telechargement a echoue\ncela peut etre causee par une mise a jour du launcher requise.","Information",JOptionPane.ERROR_MESSAGE);
							}
						}
					});
				} else {
					launch();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void launch() {
		try {
			Class<?> cls = ClassLoader.getSystemClassLoader().loadClass("fr.skyforce77.towerminer.Launcher");
			Method main = cls.getDeclaredMethod("main", String[].class);
			main.invoke(cls.newInstance(), new Object[]{new String[0]});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void addURLToSystemClassLoader(URL url) throws IntrospectionException { 
		URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader(); 
		Class<URLClassLoader> classLoaderClass = URLClassLoader.class; 
		try { 
			Method method = classLoaderClass.getDeclaredMethod("addURL", new Class[]{URL.class}); 
			method.setAccessible(true); 
			method.invoke(systemClassLoader, new Object[]{url}); 
		} catch (Throwable t) { 
			t.printStackTrace(); 
			throw new IntrospectionException("Error when adding url to system ClassLoader "); 
		} 
	}
	
	public static String getOS() {
		String OS = System.getProperty("os.name").toUpperCase();
		if (OS.contains("WIN"))
			return "windows";
		else if (OS.contains("MAC"))
			return "mac";
		else if (OS.contains("NUX"))
			return "linux";
		else
			return "wtf";
	}
	
	public static File getLauncher() {
		return new File(getDirectory(), "/launcher/TMLauncher.jar");
	}
	
	public static File getDirectory() {
		String OS = System.getProperty("os.name").toUpperCase();
		if (OS.contains("WIN"))
			return new File(System.getenv("APPDATA"),"/.towerminer");
		else if (OS.contains("MAC"))
			return new File(System.getProperty("user.home") + "/Library/Application "
					+ "Support","/.towerminer");
		else if (OS.contains("NUX"))
			return new File(System.getProperty("user.home"),"/.towerminer");
		return new File(System.getProperty("user.dir"),"/.towerminer");
	}
	
	public static boolean getActualVersion() {
		try {
			BufferedReader out = new BufferedReader(new InputStreamReader(new URL(versionurl).openStream()));
			actual = out.readLine();
			downloadlink = out.readLine();
			out.close();
			return true;
		}catch (Exception e){
			return false;
		}
	}

}
