package fileops;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Utility {
	
	private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Utility.class);

	public static boolean isConnected() {
		try {
			String url = "https://www.flashmonkey.xyz";
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			
			if (responseCode == 0) {
				
				LOGGER.warn("internet is not connected: responseCode: {}", responseCode);
				
				return false;
			}
			LOGGER.info("we are connected to the internet");
			return true;
			
		} catch(ProtocolException e) {
			//LOGGER.warn(e.getMessage());
		} catch (IOException e) {
			//LOGGER.warn(e.getMessage());
		}
		return false;
	}

	/**
	 * Returns a Map<String, Object> from the JSON string in the argument
	 * @param json
	 * @return
	 * @throws JsonProcessingException
	 */
	public static Map<String, Object> jsonToMap(String json) throws JsonProcessingException {
		return new ObjectMapper().readValue(json, Map.class);
	}


}
