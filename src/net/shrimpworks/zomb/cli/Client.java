package net.shrimpworks.zomb.cli;

import java.io.IOException;

import com.eclipsesource.json.JsonObject;
import net.shrimpworks.zomb.cli.common.HttpClient;

public class Client {

	private final String url;
	private final String key;
	private final ResponseOutput output;

	private final HttpClient client;

	public Client(String url, String key, ResponseOutput output) {
		this.url = url;
		this.key = key;
		this.output = output;

		this.client = new HttpClient(5000); // TODO configurable
	}

	public boolean execute(String user, String query) throws IOException {
		JsonObject request = new JsonObject()
				.add("key", key)
				.add("user", user)
				.add("query", query);

		JsonObject json = JsonObject.readFrom(client.post(url, request.toString()));

		String[] responseLines = new String[json.get("response").asArray().size()];
		for (int i = 0; i < json.get("response").asArray().size(); i++) {
			responseLines[i] = json.get("response").asArray().get(i).asString();
		}

		Response response = new Response(
				json.get("plugin").asString(),
				json.get("user").asString(),
				json.get("query").asString(),
				responseLines,
				json.get("image").asString()
		);

		output.output(response);

		return true;
	}

	public static final class Response {

		private final String plugin;
		private final String user;
		private final String query;
		private final String[] response;
		private final String image;

		public Response(String plugin, String user, String query, String[] response, String image) {
			this.plugin = plugin;
			this.user = user;
			this.query = query;
			this.response = response;
			this.image = image;
		}

		public String plugin() {
			return plugin;
		}

		public String user() {
			return user;
		}

		public String query() {
			return query;
		}

		public String[] response() {
			return response.clone();
		}

		public String image() {
			return image;
		}
	}

	public interface ResponseOutput {

		public void output(Response response);
	}
}
