package de.redstoneworld.bungeespeak.TeamspeakCommands.internal;

import de.redstoneworld.bungeespeak.TeamspeakCommands.TeamspeakCommandSender;

public abstract class TeamspeakCommand {

	private final String[] names;

	protected TeamspeakCommand(String firstName, String... otherNames) {
		if (firstName == null || firstName.isEmpty()) {
			throw new IllegalArgumentException("A Command did not have a name specified.");
		}

		if (otherNames == null) {
			names = new String[] {firstName};
		} else {
			names = new String[otherNames.length + 1];
			names[0] = firstName;
			System.arraycopy(otherNames, 0, names, 1, otherNames.length);
		}
	}

	public String getName() {
		return names[0];
	}

	public String[] getNames() {
		return names;
	}

	public abstract void execute(TeamspeakCommandSender sender, String[] args);
}
