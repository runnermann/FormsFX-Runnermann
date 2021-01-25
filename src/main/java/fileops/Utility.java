package fileops;

import com.amazonaws.AmazonClientException;
import com.amazonaws.util.Base64;
import com.amazonaws.util.BinaryUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static com.amazonaws.util.StringUtils.UTF8;

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

	public static String getMd5hex(File file) throws IOException {
		return hex(computeMD5Hash(file));
	}


	/**
	 * Converts a byte[] to a hex string
	 * @param bytes
	 * @return returns a hex string
	 */
	private static String hex(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte aByte : bytes) {
			int decimal = (int) aByte & 0xff;               // bytes widen to int, need mask, prevent sign extension
			// get last 8 bits
			String hex = Integer.toHexString(decimal);
			if (hex.length() % 2 == 1) {                    // if half hex, pad with zero, e.g \t
				hex = "0" + hex;
			}
			result.append(hex);
		}
		return result.toString();
	}

	/**
	 * Computes the MD5 of the given file.
	 */
	private static byte[] computeMD5Hash(File file) throws FileNotFoundException, IOException {
		return computeMD5Hash(new FileInputStream(file));
	}

	private static final int SIXTEEN_K = 1 << 14;
	private static byte[] computeMD5Hash(InputStream is) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[SIXTEEN_K];
			int bytesRead;
			while ( (bytesRead = bis.read(buffer, 0, buffer.length)) != -1 ) {
				messageDigest.update(buffer, 0, bytesRead);
			}
			return messageDigest.digest();
		} catch (NoSuchAlgorithmException e) {
			// should never get here
			throw new IllegalStateException(e);
		} finally {
			bis.close();
		}
	}

	/**
	 * Returns the hex-encoded MD5 hash String of the given message body.
	 */
	private static String calculateMessageBodyMd5(String messageBody) {

		byte[] expectedMd5;
		try {
			expectedMd5 = computeMD5Hash(messageBody.getBytes(UTF8));
		} catch (Exception e) {
			throw new AmazonClientException("Unable to calculate the MD5 hash of the message body. " + e.getMessage(),
					e);
		}
		String expectedMd5Hex = BinaryUtils.toHex(expectedMd5);

		return expectedMd5Hex;
	}



	/**
	 * Returns the MD5 in base64 for the data from the given input stream.
	 * Note this method closes the given input stream upon completion.
	 */
	public static String md5AsBase64(InputStream is) throws IOException {
		return Base64.encodeAsString(computeMD5Hash(is));
	}

	/**
	 * Computes the MD5 hash of the given data and returns it as an array of
	 * bytes.
	 */
	public static byte[] computeMD5Hash(byte[] input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(input);
		} catch (NoSuchAlgorithmException e) {
			// should never get here
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Returns the MD5 in base64 for the given byte array.
	 */
	public static String md5AsBase64(byte[] input) {
		return Base64.encodeAsString(computeMD5Hash(input));
	}
}
