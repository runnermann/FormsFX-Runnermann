package uicontrols.icons;

import org.kordamp.ikonli.AbstractIkonHandler;
import org.kordamp.ikonli.Ikon;

public class IkonHandler extends AbstractIkonHandler {
	
	public boolean supports(String description) {
		return description != null && description.startsWith("icon-");
	}
	
	public Ikon resolve(String description) {
		return FlashMonkeyIkon.findByDescription(description);
	}
	
	public String getFontResourcePath() {
		return "/resources/font/flashmonkey-icons.ttf"; //"/resources/flashmonkey-icons.ttf";
	}
	
	public String getFontFamily() {
		return "flashmonkey-icons";
	}
}
