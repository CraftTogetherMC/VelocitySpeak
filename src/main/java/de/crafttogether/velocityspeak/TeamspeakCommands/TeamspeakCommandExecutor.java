package de.crafttogether.velocityspeak.TeamspeakCommands;

import java.util.ArrayList;
import java.util.List;

import de.crafttogether.velocityspeak.Configuration.Configuration;
import de.crafttogether.velocityspeak.TeamspeakCommands.internal.CommandList;
import de.crafttogether.velocityspeak.TeamspeakCommands.internal.CommandPm;
import de.crafttogether.velocityspeak.TeamspeakCommands.internal.TeamspeakCommand;

public class TeamspeakCommandExecutor {

	private List<TeamspeakCommand> commands;

	public TeamspeakCommandExecutor() {
		commands = new ArrayList<TeamspeakCommand>();
		if (Configuration.TS_COMMANDS_INTERNAL_LIST.getBoolean()) {
			commands.add(new CommandList());
		}
		if (Configuration.TS_COMMANDS_INTERNAL_PM.getBoolean()) {
			commands.add(new CommandPm());
		}
	}

	public boolean execute(TeamspeakCommandSender sender, String command, String[] args) {
		for (TeamspeakCommand tsc : commands) {
			for (String name : tsc.getNames()) {
				if (name.equalsIgnoreCase(command)) {
					tsc.execute(sender, args);
					return true;
				}
			}
		}

		return false;
	}
}
