package net.extremity_ps.launcher;

import java.io.File;
import java.io.IOException;
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
		try 
		{
			System.out.println("Attempting to launch a new process...");
			Process p = Runtime.getRuntime().exec("java -Xmx512m -jar " + CLIENT_DIR);
			
			try
			{
			p.waitFor();
			} 
			catch (InterruptedException e)
			{
				// never going to happen
			}
			
			if(p.exitValue() > 0)
			{
				System.err.println("Failed to launch new process.");
				launchReflection();
			}
		}
		catch(IOException e)
		{
			System.err.println("Failed to launch new process.");
			e.printStackTrace();
			launchReflection();
		}
	}
	
	private static void launchReflection() 
	{
		try
		{
			System.out.println("Attempting to launch via reflection...");
			URL[] urls = new URL[] { client.toURI().toURL() };
			ClassLoader cl = new URLClassLoader(urls);
			Class<?> clazz = cl.loadClass("org.client.Client");
			Method method = clazz.getMethod("main", String[].class);
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
}
