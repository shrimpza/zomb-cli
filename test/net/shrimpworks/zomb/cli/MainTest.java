package net.shrimpworks.zomb.cli;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import net.jadler.Jadler;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MainTest {

	private static final int jadlerPort = 9080;

	private Path tmpConfig;

	@Before
	public void setup() throws IOException {
		this.tmpConfig = Files.createTempFile("zomb-cli", ".conf");

		Jadler.initJadlerListeningOn(jadlerPort);
	}

	@After
	public void teardown() throws IOException {
		Files.deleteIfExists(tmpConfig);
	}

	@Test
	public void mainTest() throws IOException {
		final String key = "mykey";
		final String user = System.getProperty("user.name");
		final String query = "hello lol";

		// set properties for mock server client and key
		Properties props = new Properties();
		props.setProperty("api-key", key);
		props.setProperty("api-url", String.format("http://localhost:%d/", jadlerPort));
		props.store(Files.newOutputStream(tmpConfig), "test properties");

		// configure mock request/response
		Jadler.onRequest()
			  .havingMethodEqualTo("POST")
			  .havingPathEqualTo("/")
			  .havingBody(new BaseMatcher<String>() {
				  @Override
				  public boolean matches(Object o) {
					  if (!(o instanceof String)) throw new IllegalArgumentException("o is not a String");

					  JsonObject j = JsonObject.readFrom(o.toString());

					  return j.get("key").asString().equals(key)
							 && j.get("user").asString().equals(user)
							 && j.get("query").asString().equals(query);
				  }

				  @Override
				  public void describeTo(Description description) {
					  // wat
				  }
			  })
			  .respond()
			  .withContentType("application/json")
			  .withStatus(200)
			  .withBody(
					  new JsonObject()
							  .add("plugin", "hello")
							  .add("user", user)
							  .add("query", query)
							  .add("response", new JsonArray()
									  .add("hello"))
							  .add("image", "")
							  .toString()
			  );

		// set output to custom implementation, so we may inspect it
		TestOutput out = new TestOutput();
		System.setOut(new PrintStream(out));

		Main.main("--config=" + tmpConfig.toString(), "hello", "lol");

		assertEquals(1, out.output.size());
		assertEquals("hello", out.output.get(0));
	}

	private static class TestOutput extends OutputStream {

		private final List<String> output;
		private final StringBuilder sb;

		public TestOutput() {
			this.output = new ArrayList<>();
			this.sb = new StringBuilder();
		}

		@Override
		public void write(int i) throws IOException {
			if (i == 10) {
				output.add(sb.toString());
				sb.setLength(0);
			} else {
				sb.append((char)i);
			}
		}
	}
}
