package net.centerleft.helppages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PluginProperties {
	static String folderPath = "plugins/HelpPages/";
	static File folderDir;
	
	static String propertiesPath = folderPath + "HelpPages.properties";
	static File propertiesFile;
	static Properties properties;
	
	static File helpFile;
	static String helpFilePath = folderPath + "help.txt";
	static boolean checkGroups;
	
	static void load() {
		//Setup and check for the plugin folder
		//if not already there create the file
		folderDir = new File(folderPath);
		folderDir.mkdir();
		
		//Setup and check for properties file.
		//if not already there, create a new one.
		propertiesFile = new File(propertiesPath);
		try {
			propertiesFile.createNewFile();
		} catch (IOException e1) {
			System.out.println("HelpPages: Error - Could not create file " + propertiesFile.getName());
		}

		//load the properties file
		properties = new Properties();
		try {
			properties.load(new FileInputStream(propertiesPath));
			
			if(properties.containsKey("help-check-pages")) {
				checkGroups = Boolean.parseBoolean(properties.getProperty("help-check-command"));
			} else {
				properties.setProperty("help-check-pages", "true");
				properties.store(new FileOutputStream(propertiesPath), "HelpPages Properties File");
				checkGroups = true;
			}			

		} catch (FileNotFoundException e) {
			System.out.println("HelpPages: Error - Could not open file " + propertiesFile.getName());
		} catch (IOException e) {
			System.out.println("HelpPages: Error - Could not open file " + propertiesFile.getName());
		}
		
		//load the helpFile
		helpFile = new File(helpFilePath);
		
	}
}
