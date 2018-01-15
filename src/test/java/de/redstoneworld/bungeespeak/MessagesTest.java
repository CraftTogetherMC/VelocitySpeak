package de.redstoneworld.bungeespeak;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.redstoneworld.bungeespeak.Configuration.Messages;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.junit.*;

public class MessagesTest {

	private Configuration defaults;

	public MessagesTest() {
		defaults = new Configuration();
		try {
			InputStreamReader in = new InputStreamReader(getResource("locale.yml"));
			defaults = ConfigurationProvider.getProvider(YamlConfiguration.class).load(in);
		} catch (NullPointerException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void checkAllPathsValid() {
		Pattern p = Pattern.compile("(?:[\\w\\-]+" + Pattern.quote(".")
				+ ")*[\\w\\-]+");
		for (Messages m : Messages.values()) {
			if (!p.matcher(m.getConfigPath()).matches()) {
				fail(m.name() + "'s path did not comply to the path naming rules.");
			}
		}
	}

	@Test
	public void checkAllDefaultValues() {
		for (Messages m : Messages.values()) {
			if (m.getDefaultValue() == null) {
				fail(m.name() + "(" + m.getConfigPath() + ") did not have a default value assigned.");
			} else if (!m.getDefaultValue().equals(defaults.get(m.getConfigPath()))) {
				fail(m.name() + "(" + m.getConfigPath() + ") default values did not match.");
			}
		}
	}

	@Test
	public void checkAllValuesExist() {
		List<String> keys = getAllKeys(defaults);

		for (Messages m : Messages.values()) {
			if (!keys.remove(m.getConfigPath())) {
				fail(m.getConfigPath() + " did not have a value set in the default file.");
			}
		}

		for (String key : keys) {
			fail(key + " was set in the default file, but not in the config.");
		}
	}

	private List<String> getAllKeys(net.md_5.bungee.config.Configuration c) {
		List<String> keys = new ArrayList<String>();
		for (String s : c.getKeys()){
			if (c.get(s) instanceof Map) {
				for (String key : getAllKeys(c.getSection(s))) {
					keys.add(s + "." + key);
				}
			} else {
				keys.add(s);
			}
		}
		return keys;
	}

	private InputStream getResource(String filename) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		if (filename == null) {
			throw new IllegalArgumentException("Filename cannot be null");
		}

		try {
			URL url = loader.getResource(filename);
			if (url == null) {
				return null;
			}

			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			return connection.getInputStream();
		} catch (IOException ex) {
			return null;
		}
	}
}
