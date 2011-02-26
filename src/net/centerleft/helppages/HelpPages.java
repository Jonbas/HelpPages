package net.centerleft.helppages;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.DataHolder;
import com.nijiko.permissions.PermissionHandler;

public class HelpPages extends JavaPlugin {
	final static boolean debug = false;
	static GroupManager groupManager;
	static PermissionHandler gmPermissionCheck; 
	static boolean checkGroups = false;

	public void onEnable() {
		
		//Load the GroupManager Plugins
		Plugin p = this.getServer().getPluginManager().getPlugin("GroupManager");
        if (p != null) {
            if (!p.isEnabled()) {
                this.getServer().getPluginManager().enablePlugin(p);
            }
            GroupManager gm = (GroupManager) p;
            groupManager = gm;
            gmPermissionCheck = gm.getPermissionHandler();
            System.out.println("HelpPages: GroupManager found.");
            checkGroups = true;
          
        } else {
        	System.out.println("HelpPages: GroupManager not found.");
        	checkGroups = false;
        	
        }
        
       
		PluginProperties.load();
		HelpFile.load();
		
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + ": Version "
				+ pdfFile.getVersion() + " is enabled!");

	}

	public void onDisable() {

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + ": Version "
				+ pdfFile.getVersion() + " is disabled!");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		String[] trimmedArgs = args;
		String commandName = command.getName().toLowerCase();

		if (commandName.equals("help")) {
			return performHelp(sender, trimmedArgs);
		}

		return false;
	}

	private boolean performHelp(CommandSender sender, String[] args) {
		//TODO Clean this up.  Reformat to cut out repetition
		Player player;
		if (args.length == 2) {
			try { // check to see if this was a /help # format command
				Integer.parseInt(args[0]);  
				String[] temp = new String[2];
				temp[0] = "help";
				temp[1] = args[0];
				HelpFile.printHelpIndex(sender, args);

			} catch (NumberFormatException ex) {
				boolean hasAccess = false;
				if( checkGroups ) {
					if( sender instanceof Player ) {
						player = (Player)sender;
						Iterator<String> g = HelpFile.helpIndexAccess.get(args[0].toLowerCase()).iterator();
						while(  g.hasNext() ) { 
							String nextGroup = g.next(); 
							if (gmPermissionCheck.inGroup(player.getName(), nextGroup)) { 
								hasAccess = true; 
							} 
						}
					} else {
						hasAccess = true;
					}
				} else {
					hasAccess = true;
				}
				if (hasAccess && HelpFile.helpIndexCommands.containsKey(args[0].toLowerCase())) {
					args[0] = HelpFile.helpIndexCommands.get(args[0].toLowerCase());
					String temp = "help " + args[0] + " " + args[1];
					HelpFile.printHelpPage(sender, temp.split(" "));
				} else {
					HelpFile.printHelpIndex(sender, "help".split(" "));
				}
			}

		} else if (args.length == 1) {
			// check to see if the user can look at that help file
			try { // check to see if this was a /help # command
				if (debug) sender.sendMessage( ChatColor.AQUA + "Entered args.length == 1");
				Integer.parseInt(args[0]);
				String[] temp = new String[2];
				temp[0] = "help";
				temp[1] = args[0];
				HelpFile.printHelpIndex(sender, temp);

			} catch (NumberFormatException ex) {
				if (debug) sender.sendMessage( ChatColor.AQUA + "Entered exception because of not #");
				
				boolean hasAccess = false;
				if( checkGroups ) {
					if( sender instanceof Player ) {
						player = (Player)sender;
						Iterator<String> g = HelpFile.helpIndexAccess.get(args[0].toLowerCase()).iterator();
						while(  g.hasNext() ) { 
							String nextGroup = g.next(); 
							if (gmPermissionCheck.inGroup(player.getName(), nextGroup)) { 
								hasAccess = true; 
							} 
						}
					} else {
						hasAccess = true;
					}
				} else {
					hasAccess = true;
				}
				 if (debug) sender.sendMessage( ChatColor.AQUA + "Testing content of args[0]: " + args[0]);
				if (hasAccess && HelpFile.helpIndexCommands.containsKey(args[0].toLowerCase())) {
					String temp = "help " + HelpFile.helpIndexCommands.get(args[0].toLowerCase());
					if (debug) sender.sendMessage( ChatColor.AQUA + "Sending temp to help");
					HelpFile.printHelpPage(sender, temp.split(" "));
				} else {
					if (debug) sender.sendMessage( ChatColor.AQUA + "Sending empty to help");
					HelpFile.printHelpIndex(sender, "help".split(" "));
				}
			}
		} else {
			HelpFile.printHelpIndex(sender, " ".split(" "));
		}

		return true;
	}

}
