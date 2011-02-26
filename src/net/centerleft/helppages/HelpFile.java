package net.centerleft.helppages;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpFile {
	
	static LinkedHashMap<String, LinkedHashMap<String, String>> helpCommands = new LinkedHashMap<String, LinkedHashMap<String, String>>();
	static LinkedHashMap<String, String> helpIndex = new LinkedHashMap<String, String>();
	static LinkedHashMap<String, String> helpIndexCommands = new LinkedHashMap<String, String>();
	static HashMap<String, List<String>> helpIndexAccess = new HashMap<String, List<String>>();

	/**
	 * Load the help file text.  If no file found, create default file.
	 */
	public static void load() {

		if( HelpPages.debug ) System.out.println("HelpPages: Starting file load.");
		
		LinkedHashMap<String, String> commands = new LinkedHashMap<String, String>();
		String subHeading = null;
		String[] split = null;
		
		String headingHelp = null;
		String value = null;
		String[] groups = null;
		
		//if the file doesn't exist, create it and put default help in the file
		try {
			if( HelpPages.debug ) System.out.println("HelpPages: First Try.");
			if(PluginProperties.helpFile.createNewFile()) {
				FileOutputStream helpFileOut = new FileOutputStream(PluginProperties.helpFile);
				String[] helpText = {
						"#HelpPages Default Help File\n",
						"#\n",
						"#\n",
						"[Help1, <Page> - Server Commands]\n",
						"/help, <Page> - Shows a list of commands. 7 per page.\n",
						"/playerlist\n",
						"/motd, - Displays the MOTD\n",
						"\n",
						"[;Empty help line]\n",
						"[;]\n",
						"\n",
						"#A help page for the group mods\n",
						"[Help2, <Page> - Commands for movement:mods]\n",
						"/getpos, - Displays your current position.\n",
						"/compass, - Gives you a compass reading.\n",
						";\n",
				};
				for( String line: helpText)
					helpFileOut.write(line.getBytes());
				
				helpFileOut.close();
				System.out.println( "HelpPages: Created Default help.txt file." );
			}
			
			String text = null;
		    Scanner scanner;
			try {
				if( HelpPages.debug ) System.out.println("HelpPages: Secont Try.");
				
				scanner = new Scanner(new FileInputStream(PluginProperties.helpFile));
				helpCommands.clear(); //clear any existing values
				helpIndex.clear();
				helpIndexCommands.clear();
				helpIndexAccess.clear();
				
				
				
				while (scanner.hasNextLine()){
					if( HelpPages.debug ) System.out.println("HelpPages: Reading help file.");
					text = scanner.nextLine();
					  //check if the next line is empty or a comment
					  //ignore comments
					if(!text.startsWith("#") && !text.isEmpty()){
						//found a line of text check for heading
						if(text.startsWith("[")) {
							  
							if (subHeading != null) {
								helpCommands.put(subHeading, new LinkedHashMap<String, String>(commands));
								helpIndex.put(subHeading, headingHelp);
								helpIndexCommands.put(subHeading.toLowerCase(), subHeading);
								List<String> emptyList = new ArrayList<String>();
								for(int i = 0; i < groups.length; i++) {
									// add each of the groups that can see this and each of the ones that inherit it to a list
									if(!helpIndexAccess.containsKey(subHeading.toLowerCase())) {
										helpIndexAccess.put(subHeading.toLowerCase(), emptyList);
									}
									helpIndexAccess.get(subHeading.toLowerCase()).add(groups[i]);								
								}
								commands.clear();
							}
							
							// cut off first [] and split for groups
							split = text.substring(1, (text.length() - 1) ).split(":");
							if (split.length == 2) {
								groups = split[1].split(",");
								
							} else {
								groups = new String[] { "default" };
							}
							
							//then split for help contents
							split = split[0].split(",");
							subHeading = split[0].trim();
				
							if (split.length > 1) {
								headingHelp = split[1].trim();
							} else {
								headingHelp = "";
							}
						} else { //if(text.startsWith("["))
							split = text.split(",");
							if (split.length > 1) {
								value = split[1].trim();
							} else {
								value = "";
							}
							commands.put(split[0].trim(), value);
							
						}
							
					}
				}
				
				helpCommands.put(subHeading, new LinkedHashMap<String, String>(commands));
				helpIndex.put(subHeading, headingHelp);
				helpIndexCommands.put(subHeading.toLowerCase(), subHeading);
				List<String> emptyList = new ArrayList<String>();
				for(int i = 0; i < groups.length; i++) {
					// add each of the groups that can see this and each of the ones that inherit it to a list
					if(!helpIndexAccess.containsKey(subHeading.toLowerCase())) {
						helpIndexAccess.put(subHeading.toLowerCase(), emptyList);
					}
					helpIndexAccess.get(subHeading.toLowerCase()).add(groups[i]);								
				}
				commands.clear();
				
				scanner.close();
			} catch (FileNotFoundException e) {
				System.out.println("HelpPages: Error - Could not read file " + PluginProperties.helpFile.getName());
			}
			
		} catch (IOException e) {
			System.out.println("HelpPages: Error - Could not open file " + PluginProperties.helpFile.getName());
		}
		
	}
	

	/**
	 * Print the help index page
	 * 
	 * @param player
	 * @param split
	 */
	static void printHelpIndex(CommandSender sender, String[] split ) {
        List<String> availableCommands = new ArrayList<String>();
        String outPut = null;
        Integer lineLength = 63; 
        Player player;
        
        for (Entry<String, String> entry : helpIndex.entrySet() ) {
        	boolean hasAccess = false;
			if( HelpPages.checkGroups ) {
				if( sender instanceof Player ) {
					player = (Player)sender;
					Iterator<String> g = helpIndexAccess.get(entry.getKey().toLowerCase()).iterator();
					while(  g.hasNext() ) { 
						String nextGroup = g.next(); 
						if (HelpPages.gmPermissionCheck.inGroup(player.getName(), nextGroup)) { 
							hasAccess = true; 
						} 
					}
				} else {
					hasAccess = true;
				}
			} else {
				hasAccess = true;
			}
        	if (hasAccess || entry.getKey().startsWith(";")) {
        		if (entry.getKey().startsWith(";")) {
            		availableCommands.add(entry.getKey().substring(1) + " " + entry.getValue());
            	} else {
            		availableCommands.add(entry.getKey() + " " + entry.getValue());
            	}
            }
       }

        sender.sendMessage(ChatColor.DARK_AQUA + "Available help categories (Page " + (split.length == 2 ? split[1] : "1") + " of " + (int) Math.ceil((double) availableCommands.size() / (double) 7) + "):");
        sender.sendMessage(ChatColor.DARK_AQUA + "Usage: /help [category] <page> - [] required <> optional");
        if (split.length == 2) {
            try {
                int amount = Integer.parseInt(split[1]);

                if (amount > 0) {
                    amount = (amount - 1) * 7;
                } else {
                    amount = 0;
                }

                for (int i = amount; i < amount + 7; i++) {
                    if (availableCommands.size() > i) {
                    	outPut = ChatColor.RED + availableCommands.get(i);
                    	if(outPut.length() > lineLength) {
                    		sender.sendMessage(outPut.subSequence(0, lineLength).toString());
                    	}
                    	else {
                    		sender.sendMessage(outPut);
                    	}
                    }
                }
            } catch (NumberFormatException ex) {
            	sender.sendMessage(ChatColor.RED + "Not a valid page number.");
            }
        } else {
            for (int i = 0; i < 7; i++) {
                if (availableCommands.size() > i) {
                	outPut = ChatColor.RED + availableCommands.get(i);
                	if(outPut.length() > lineLength) {
                		sender.sendMessage(outPut.subSequence(0, lineLength).toString());
                	}
                	else {
                		sender.sendMessage(outPut);
                	}
                }
            }
        }
		
	}
	
	/**
	 * Print the individual help page.
	 * 
	 * @param player
	 * @param split
	 */
	static void printHelpPage(CommandSender sender, String[] split) {
        List<String> availableCommands = new ArrayList<String>();
        String outPut = null;
        Integer lineLength = 63; 
        
        
        
        for (Entry<String, String> entry : helpCommands.get(split[1]).entrySet() ) {
        	String entryKey = entry.getKey();
        	String[] splitKey = entryKey.split(" ");
        	if(splitKey.length > 1 ) {
        		entryKey = splitKey[0];
        	}
        	
        	if (entry.getKey().startsWith(";")) {
        		availableCommands.add(entry.getKey().substring(1) + " " + entry.getValue());
        	} else {
        		availableCommands.add(entry.getKey() + " " + entry.getValue());
        	}            
       }

        sender.sendMessage(ChatColor.DARK_AQUA + "Available commands (Page " + (split.length == 3 ? split[2] : "1") + " of " + (int) Math.ceil((double) availableCommands.size() / (double) 7) + ") [] = required <> = optional");
        sender.sendMessage(ChatColor.DARK_AQUA + "For help page " + split[1]);
        if (split.length == 3) {
            try {
                int amount = Integer.parseInt(split[2]);

                if (amount > 0) {
                    amount = (amount - 1) * 7;
                } else {
                    amount = 0;
                }

                for (int i = amount; i < amount + 7; i++) {
                    if (availableCommands.size() > i) {
                    	outPut = ChatColor.RED + availableCommands.get(i);
                    	if(outPut.length() > lineLength) {
                    		sender.sendMessage(outPut.subSequence(0, lineLength).toString());
                    	}
                    	else {
                    		sender.sendMessage(outPut);
                    	}
                    }
                }
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Not a valid page number.");
            } 
        } else {
            for (int i = 0; i < 7; i++) {
                if (availableCommands.size() > i) {
                	outPut = ChatColor.RED + availableCommands.get(i);
                	if(outPut.length() > lineLength) {
                		sender.sendMessage(outPut.subSequence(0, lineLength).toString());
                	}
                	else {
                		sender.sendMessage(outPut);
                	}
                }
            }
        }
		
	}
}
