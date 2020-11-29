package metadata;

import flashmonkey.FlashMonkeyMain;
import forms.FormData;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;

public class DeckSearchData implements Serializable, FormData {
	
	private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(DeckSearchData.class);
	/* VERSION */
	public static final long VERSION = FlashMonkeyMain.VERSION;
	
	@Override
	public void init() {
	
	}
	
	@Override
	public boolean set() {
		return false;
	}
	
	@Override
	public void set(HashMap<String, String> str) {
	
	}
	
	@Override
	public void close() {
	
	}
	
	@Override
	public void setDataMap(HashMap<String, String> dataAry) {
	
	}
	
	@Override
	public void updateDataMap() {
	
	}
	
	@Override
	public HashMap<String, String> getDataMap() {
		return new HashMap<>();
	}
	
	@Override
	public long getLastDate() {
		return 0;
	}
}
