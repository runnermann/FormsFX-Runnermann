package type.richtext;

import fileops.utility.Utility;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import richtextfm.model.Codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Hyperlink extends TextStyle {

    private final String originalDisplayedText;
    private final String displayedText;
    private final String link;
    private static final String SEP = "'>%<'";

    Hyperlink(String originalDisplayedText, String displayedText, String link) {
        this.originalDisplayedText = originalDisplayedText;
        this.displayedText = displayedText;
        this.link = link;
    }

    public static <S> Codec<Hyperlink> codec() {
        return new Codec<Hyperlink>() {
            @Override
            public String getName() {
                return "Hyperlink";
            }


            @Override
            public void encode(DataOutputStream os, Hyperlink Hyperlink) throws IOException {
                if(!Hyperlink.isEmpty()) {
                    os.writeBoolean(true);
                    String txt = Hyperlink.getOriginalDisplayedText() + SEP + Hyperlink.getDisplayedText() + SEP + Hyperlink.getLink();
                    Codec.STRING_CODEC.encode(os, txt);
                }
            }

            @Override
            public Hyperlink decode(DataInputStream is) throws IOException {
                if(is.readBoolean()) {
                    String ret = Codec.STRING_CODEC.decode(is);
                    String[] strAry = ret.split(SEP);
                    return new Hyperlink(strAry[0], strAry[1], strAry[3]);
                } else {
                    return new Hyperlink("","","");
                }
            }
        };
    }

//    public Node createNode() {
//        Image image = new Image("file:" + "\"C:\\Users\\Me\\Pictures\\eve_AI_idkSize2.jpg\""); // XXX: No need to create new Image objects each time -
//        // could be cached in the model layer
//        ImageView result = new ImageView(image);
//        return result;
//    }

    public boolean isEmpty() {
        return length() == 0;
    }

    public boolean isReal() {
        return length() > 0 && Utility.isConnected(link);
    }

    public boolean shareSameAncestor(Hyperlink other) {
        return link.equals(other.link);
    }

    public int length() {
        return displayedText.length();
    }

    public char charAt(int index) {
        return isEmpty() ? '\0' : displayedText.charAt(index);
    }

    public String getOriginalDisplayedText() { return originalDisplayedText; }

    public String getDisplayedText() {
        return displayedText;
    }

    public String getLink() {
        return link;
    }

    public Hyperlink subSequence(int start, int end) {
        return new Hyperlink(originalDisplayedText, displayedText.substring(start, end), link);
    }

    public Hyperlink subSequence(int start) {
        return new Hyperlink(originalDisplayedText, displayedText.substring(start), link);
    }

    public Hyperlink mapDisplayedText(String text) {
        return new Hyperlink(originalDisplayedText, text, link);
    }

    @Override
    public String toString() {
        return isEmpty()
                ? String.format("EmptyHyperlink[original=%s link=%s]", originalDisplayedText, link)
                : String.format("RealHyperlink[original=%s displayedText=%s, link=%s]",
                                    originalDisplayedText, displayedText, link);
    }

}
