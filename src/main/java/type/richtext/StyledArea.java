package type.richtext;

import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import richtextfm.GenericStyledArea;
import richtextfm.TextExt;
import richtextfm.model.*;
import org.reactfx.util.Either;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;


                                //GenericStyledArea<Void, Either<String, Hyperlink>, TextStyle>
public class StyledArea extends GenericStyledArea<ParStyle, Either<String, Hyperlink>, TextStyle> {

    // INNER CLASS FOR StyledTextArea

    private static final TextOps<String, TextStyle> STYLED_TEXT_OPS = SegmentOps.styledTextOps();
    private static final HyperlinkOps<TextStyle> HYPERLINK_OPS = new HyperlinkOps<>();
    private static final TextOps<Either<String, Hyperlink>, TextStyle> EITHER_OPS = STYLED_TEXT_OPS._or(HYPERLINK_OPS, (s1, s2) -> Optional.empty());


    public StyledArea() {
        super(
                        ParStyle.EMPTY,
                        (paragraph, style) -> paragraph.setStyle(style.toCss()),
                        TextStyle.EMPTY.updateFontSize(10).updateFontFamily("Arial").updateTextColor(Color.BLACK),
                        EITHER_OPS,
                        e -> e.getSegment().unify(
                                text ->
                                        createStyledTextNode(t -> {
                                            t.setText(text);
                                            t.setStyle(e.getStyle().toCss());
                                        }),
                                hyperlink ->
                                        createStyledTextNode(t -> {
                                            if (hyperlink.isReal()) {
                                                t.setText(hyperlink.getDisplayedText());
                                                t.getStyleClass().add("hyperlink");
                                                t.setOnMouseClicked(ae -> {
                                                    try
                                                    {
                                                        Desktop.getDesktop().browse(new URI(hyperlink.getLink()));
                                                    }
                                                    catch (IOException | URISyntaxException ex)
                                                    {
                                                        throw new RuntimeException(ex);
                                                    }
                                                });
                                            }
                                        })
                        )
                );    // Node creator and segment style setter



        // call when no longer need it: `cleanupWhenFinished.unsubscribe();`

                getStyleClass().add("text-hyperlink-area");
                //getStylesheets().add(TextHyperlinkArea.class.getResource("text-hyperlink-area.css").toExternalForm());
                getStylesheets().add("css/text-hyperlink-area.css");
    }


//            private void linkTask() {
//                String text = this.getText();
//                Task task = new Task() {
//                    @Override
//                    protected void call() throws Exception {
//                         linkProofer(text);
//                    }
//                };
//
//            }

    private void callWeb(String link) {
        try
        {
            Desktop.getDesktop().browse(new URI(link));
        }
        catch (IOException | URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Do not use with Async
     */
    public void linkProofer() {
        Matcher matcher = LinkProofer.LINK_PATTERN.matcher(this.getText());

        while(matcher.find()) {
            String s = matcher.group("HYPERLINK");
            link(matcher.group(), matcher.start(), matcher.end());
        }
    }



    public void setLinkSpans(Collection<LinkSpan> spans) {
        spans.stream()
                .forEach(e -> {
                    link(e.getLink().getLink(), e.getStart(), e.getEnd());
                });
    }

    private void link(String displayedText, int start, int end) {
        replaceWithLink(start, end, displayedText, displayedText);
    }


    private void replaceWithLink(int start, int end, String displayedText, String link) {
        replace(start, end, ReadOnlyStyledDocument.fromSegment(
                Either.right(new Hyperlink(displayedText, displayedText, link)),
                ParStyle.EMPTY,
                TextStyle.EMPTY,
                EITHER_OPS
        ));
    }

    void applyHyperlinks(StyleSpans<TextExt> styleSpans) {
        //this.setStyleSpans(0, styleSpans);
        styleSpans.stream()
                .forEach(s -> {
                    Object o = s.getStyle();
                    String link = ((Hyperlink) o).getLink();
                });
    }

    @Override
    public void setStyleSpans(int from, StyleSpans<? extends TextStyle> styleSpans) {
        super.setStyleSpans(from, styleSpans);
    }

    public static TextExt createStyledTextNode(Consumer<TextExt> applySegment) {
        TextExt t = new TextExt();
        t.setTextOrigin(VPos.TOP);
        applySegment.accept(t);
        return t;
    }

    public void foldParagraphs( int startPar, int endPar ) {
        foldParagraphs( startPar, endPar, getAddFoldStyle() );
    }

    public void foldSelectedParagraphs() {
        foldSelectedParagraphs( getAddFoldStyle() );
    }

    public void foldText( int start, int end ) {
        fold( start, end, getAddFoldStyle() );
    }

    public void unfoldParagraphs( int startingFromPar ) {
        unfoldParagraphs( startingFromPar, getFoldStyleCheck(), getRemoveFoldStyle() );
    }

    public void unfoldText( int startingFromPos ) {
        startingFromPos = offsetToPosition( startingFromPos, Bias.Backward ).getMajor();
        unfoldParagraphs( startingFromPos, getFoldStyleCheck(), getRemoveFoldStyle() );
    }

    protected UnaryOperator<ParStyle> getAddFoldStyle() {
        return pstyle -> pstyle.updateFold( true );
    }

    protected UnaryOperator<ParStyle> getRemoveFoldStyle() {
        return pstyle -> pstyle.updateFold( false );
    }

    protected Predicate<ParStyle> getFoldStyleCheck() {
            return pstyle -> pstyle.isFolded();
        }
}


