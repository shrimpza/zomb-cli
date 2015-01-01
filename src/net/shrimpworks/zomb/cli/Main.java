package net.shrimpworks.zomb.cli;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * zomb-cli main entry point.
 * <p>
 * Usage: <pre>jarfile [options] &lt;plugin&gt; &lt;command&gt; [arguments]</pre>
 */
public class Main {

	private static final String argsPattern = "([A-Za-z-_]+ )([A-Za-z-_]+)( .+)?";
	private static final String optionPattern = "--([a-zA-Z0-9-_]+)=(.+)";

	public static void main(String... args) {
		// validate non-options related input (plugin query)
		StringBuilder query = new StringBuilder();
		for (String arg : args) if (!arg.startsWith("--")) query.append(arg).append(" ");
		if (!query.toString().trim().matches(argsPattern)) {
			System.err.printf("Query string invalid: %s%n", query.toString());
			System.exit(1);
		}

		// retrieve user's properties from file
		Path userProperties = Paths.get(String.format("%s%s%s",
													  System.getProperty("user.home"),
													  System.getProperty("file.separator"),
													  ".zomb-cli"));
		Properties properties = new Properties();

		if (Files.exists(userProperties)) {
			try {
				properties.load(Files.newInputStream(userProperties));
			} catch (IOException e) {
				System.err.printf("Could not load user properties: %s%n", e.getMessage());
				System.exit(2);
			}
		}

		// apply properties from the command line on top of user's properties
		properties = parseOptions(properties, args);

		if (properties.containsKey("config")) {
			Path customProperties = Paths.get(properties.getProperty("config"));
			if (!Files.exists(customProperties)) {
				System.err.printf("Config file does not exist: %s%n", properties.getProperty("config"));
				System.exit(3);
			} else {
				try {
					properties.load(Files.newInputStream(customProperties));
				} catch (IOException e) {
					System.err.printf("Could not load custom properties: %s%n", e.getMessage());
					System.exit(2);
				}
			}

		}

		// construct client with gathered properties
		Client client = new Client(properties.getProperty("api-url"), properties.getProperty("api-key"), new ConsoleOut());

		// execute
		try {
			client.execute(System.getProperty("user.name"), query.toString().trim());
		} catch (FileNotFoundException e) {
			System.err.printf("API URL not found: %s%n", e.getMessage());
		} catch (IOException e) {
			System.err.printf("Failed to execute query: %s%n", e.getMessage());
		}
	}

	private static Properties parseOptions(Properties properties, String... args) {
		Properties result = new Properties(properties);

		Pattern pattern = Pattern.compile(optionPattern);

		for (String arg : args) {
			Matcher matcher = pattern.matcher(arg);
			if (matcher.matches()) result.setProperty(matcher.group(1), matcher.group(2));
		}

		return result;
	}

	private static class ConsoleOut implements Client.ResponseOutput {

		@Override
		public void output(Client.Response response) {
			for (String s : response.response()) {
				System.out.println(s);
			}
		}
	}
}
