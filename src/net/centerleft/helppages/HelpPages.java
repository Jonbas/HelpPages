package net.centerleft.helppages;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class HelpPages extends JavaPlugin {
	final static boolean debug = false;

	public void onEnable() {
		
		PluginProperties.load();
		HelpFile.load();
		
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " is enabled!");

	}

	public void onDisable() {

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version "
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
		if (args.length == 2) {
			try { // check to see if this was a /help # format command
				Integer.parseInt(args[0]);  
				String[] temp = new String[2];
				temp[0] = "help";
				temp[1] = args[0];
				HelpFile.printHelpIndex(sender, args);

			} catch (NumberFormatException ex) {
				boolean hasAccess = false;
				// TODO Add permissions plugin access
				// check if player's group is in groups listed for that
				// entry
				/*
				 * for( Iterator<String> g = HelpFile.helpIndexAccess.get
				 * (split[1].toLowerCase()).iterator(); g.hasNext();) { String
				 * nextGroup = g.next(); if (player.isInGroup(nextGroup.trim()))
				 * { hasAccess = true; } }
				 */hasAccess = true;

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
				// TODO Add permissions support
				/*
				 * // check if player's group is in groups listed for that //
				 * entry for (Iterator<String> g = HelpFile.helpIndexAccess.get(
				 * split[1].toLowerCase()).iterator(); g.hasNext();) { String
				 * nextGroup = g.next(); if (player.isInGroup(nextGroup.trim()))
				 * { hasAccess = true; } }
				 */hasAccess = true;
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
