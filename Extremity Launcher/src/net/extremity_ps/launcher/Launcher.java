package net.extremity_ps.launcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

public class Launcher 
{
	private static final String CLIENT_URL = "https://www.dropbox.com/s/g382wgddwxbwvx8/Extremity.jar?dl=1";
	private static final String CLIENT_DIR = System.getProperty("user.home") + File.separator + "Extremity"
			+ File.separator + "Client" + File.separator + "client.jar";
	
	private static File client;
	
	public static void main(String[] args)
	{
		updateClient();
		launchNewProcess();
	}
	
	private static void updateClient()
	{
		try 
		{
			FileUtils.copyURLToFile(new URL(CLIENT_URL), new File(CLIENT_DIR));
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(null, "Could not retrieve client. Run via command line for more details.",
					"Extremity Launcher", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(-1);
		}
		
		client = new File(CLIENT_DIR);
		
	}

	private static void launchNewProcess()
	{
		Timer timer = new Timer();
		System.out.println("Attempting to launch new process...");
		 try 
		 {
			ProcessBuilder pb = new ProcessBuilder("java", "-jar", CLIENT_DIR);
			pb.redirectError();
			pb.inheritIO();
			Process process = pb.start();
			timer.start();
			int exitCode = process.waitFor();
			if (exitCode != 0) 
			{
				throw new IOException("Exit code: " + exitCode);
			}
		} 
		 catch (Exception ex)
		 {
			System.err.println("New process launch failed.");
			ex.printStackTrace();
			if(timer.attemptNewLaunch)
			{
				launchReflection();
			}
		}
	}
	
	private static Field[] fields;
	private static Method[] methods;
	private static ClassLoader cl;
	
	private static void launchReflection() 
	{
		try
		{
			System.out.println("Attempting to launch via reflection...");
			URL[] urls = new URL[] { client.toURI().toURL() };
			cl = new URLClassLoader(urls);
			Class<?> clazz = cl.loadClass("org.client.Client");
			Method method = clazz.getMethod("main", String[].class);
			fields = clazz.getDeclaredFields();
			methods = clazz.getDeclaredMethods();
			
			for (int i = 0; i < fields.length; i++) 
			{
				fields[i].setAccessible(true);
			}

			for (int i = 0; i < methods.length; i++) 
			{
				methods[i].setAccessible(true);
			}
			
			method.invoke(null, (Object) new String[] {});
		} 
		catch (MalformedURLException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | ClassNotFoundException e) 
		{
			System.err.println("Launch via reflection failed.");
			JOptionPane.showMessageDialog(null, "Could not run client. Try running client manually located in Extremity/Client.",
					"Extremity Launcher", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
    public static Field getField(String s) {
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals(s)) {
                //   System.out.println("FOUND FIELD "+fields[i].toGenericString());
                return fields[i];
            }
        }
        return null;
    }
			
    public static Method getMethod(String s) {
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(s)) {
                return methods[i];
            }
        }
        return null;
    }
	
	private void addBank()
	{
		int x = getField("ab").getInt(cl.getClass())
	}
}

class Timer extends Thread 
{
	public boolean attemptNewLaunch = true;

	@Override
	public void run()
	{
		try 
		{
			Thread.sleep(180000);
			attemptNewLaunch = false;
		} 
		catch (InterruptedException e)			
		{
			// not really important
		}
	}
}
