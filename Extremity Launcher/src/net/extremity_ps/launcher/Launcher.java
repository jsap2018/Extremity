package net.extremity_ps.launcher;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

public class Launcher 
{
	private static final String CLIENT_URL = "https://github.com/jsap2018/Extremity/raw/master/Extremity2_11.jar";
	private static final String CLIENT_DIR = System.getProperty("user.home") + File.separator + "Extremity"
			+ File.separator + "Client" + File.separator + "client.jar";
	
	public static void main(String[] args)
	{
		retrieveClient();
		launchClient();
	}
	
	private static void retrieveClient()
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
	}
	
	private static void launchClient()
	{
		try 
		{
			Process p = Runtime.getRuntime().exec("java -jar " + CLIENT_DIR);
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(null, "Could run client. Run via command line for more details.",
					"Extremity Launcher", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
