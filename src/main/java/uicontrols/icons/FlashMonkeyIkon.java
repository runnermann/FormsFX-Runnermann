package uicontrols.icons;

import org.kordamp.ikonli.Ikon;

public enum FlashMonkeyIkon implements Ikon {

	CARD_ADD("icon-card_add", '\ue800'),
	CARD_INSERT("icon-card_insert", '\ue801'),
	CARD_DELETE("icon-card_delete", '\ue802'),
	CARD_UNDO("icon-undo", '\ue803');
	
	private String description;
	private char icon;
	
	FlashMonkeyIkon(String description, char icon) {
		this.description = description;
		this.icon = icon;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public char getCode() {
		return icon;
	}
	
	public static FlashMonkeyIkon findByDescription(String description) {
		for (FlashMonkeyIkon icon : values()) {
			if (icon.description.equals(description)) {
				return icon;
			}
		}
		throw new IllegalArgumentException("Icon not supported: " + description);
	}
}

