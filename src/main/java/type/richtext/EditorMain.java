package type.richtext;

import static richtextfm.model.TwoDimensional.Bias.*;
import static type.richtext.LinkProofer.LINK_PATTERN;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.regex.Matcher;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.PrefixSelectionComboBox;
import org.fxmisc.flowless.VirtualizedScrollPane;
//import org.kordamp.ikonli.entypo.Entypo;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid; // Don't like the fonts used
//import org.kordamp.ikonli.materialdesign2.*; // too complex too small
// import org.kordamp.ikonli.fluentui.*; // Lines are too thin
import org.reactfx.Subscription;
import org.reactfx.SuspendableNo;
import org.reactfx.util.Either;
import org.reactfx.util.Tuple2;

import richtextfm.GenericStyledArea;
import richtextfm.model.*;
import uicontrols.ButtoniKonClazz;
import uicontrols.SceneCntl;


/**
 * Information on RichTextEditor is at: https://github.com/FXMisc/RichTextFX. They provide a wiki
 * with in-depth information.
 *<p>css reference: https://github.com/FXMisc/RichTextFX/wiki/RichTextFX-CSS-Reference-Guide</p>
 * <p> Adjusting css for RichText Items may be done as such:</p>
 * <pre>
 *  //  .text-flow {
 *  //   //the alignment of the text, where <alignment> can be
 *  //       [ left | center | right | justify ] and defaults to "left"
 *  //            -fx-text-alignment:<alignment>;
 *  //
 *  //
 *  //     the amount of pixels to separate lines in a multi-line paragraph
 *  //      (e.g. when `area.isWrapText() == true`)
 *  //     -fx-line-spacing:<number>;
 *  //  }
 *
 *  When an area's text is styled by default using style classes, for example:
 *  // .styled-text-area .text {
 *  //   -fx-fill: white;
 *  // }
 *
 *  -- ... then any additional styling must include the same selector prefix. In other
 *  words, if one wanted to apply the style class "red" to the area, one would need
 *  to precede it with ".styled-text-area " in order for that style class to be
 *  applied properly. Thus, given the following code... --
 *  // StyleClassedTextArea area = // creation code;
 *  // from 0 to 19, default style is used
 *  // area.setStyle(20, 40, Collections.singleton("green");
 *  // area.setStyle(41, 60, Collections.singleton("red");
 *
 *
 *  //  -- THIS WILL NOT WORK AS IT DOES NOT INCLUDE DEFAULT's SELECTOR(S) PREFIX --
 *  // .green{
 *  //      -fx-fill:green;
 *  //  }
 *  // -- THIS WILL WORK --
 *  // .styled-text-area.red{
 *  //      -fx-fill:white;
 *  // }
 *
 *  -- The same applies if one sets the area's id, as shown by CodeArea, a subclass of
 *  StyleClassedTextArea: --
 *  //  CodeArea area = new CodeArea("int x = 3 + 5;");
 *  //  area.setId("codeArea");
 *  //  area.calcLinkLocations();
 *
 *  // #codeArea .text {
 *     -fx-fill: white;
 * }
 * -- Overriding styling for key words --
 * #codeArea.keyword{
 *      -fx-fill:blue;
 * }
 * -- Overriding styling for operators --
 * #codeArea.operator{
 *      -fx-fill:purple;
 * }
 * </pre>
 */
public class EditorMain implements Serializable {

    private final StyledArea textArea;// = new TextAreaStyled();
    private final SuspendableNo updatingToolbar;// = new SuspendableNo();
    private final VBox editorVBox;

    private final ButtoniKonClazz UNDO = new ButtoniKonClazz( "undo", FontAwesomeSolid.UNDO_ALT);
    private final ButtoniKonClazz REDO = new ButtoniKonClazz("redo", FontAwesomeSolid.REDO_ALT);
    private final ButtoniKonClazz BOLD = new ButtoniKonClazz("bold", FontAwesomeSolid.BOLD);
    private final ButtoniKonClazz ITALIC = new ButtoniKonClazz("italic", FontAwesomeSolid.ITALIC);
    private final ButtoniKonClazz UNDERLINE = new ButtoniKonClazz("underline", FontAwesomeSolid.UNDERLINE);
    private final ButtoniKonClazz LINE_THROUGH = new ButtoniKonClazz("line through", FontAwesomeSolid.STRIKETHROUGH);
    private final ButtoniKonClazz INCREASE_INDENT = new ButtoniKonClazz("increase indent", FontAwesomeSolid.INDENT);
    private final ButtoniKonClazz DECREASE_INDENT = new ButtoniKonClazz("decrease indent", FontAwesomeSolid.OUTDENT);

    private final ButtoniKonClazz ALIGN_LEFT = new ButtoniKonClazz("align left", FontAwesomeSolid.ALIGN_LEFT);
    private final ButtoniKonClazz ALIGN_RIGHT = new ButtoniKonClazz("align right", FontAwesomeSolid.ALIGN_RIGHT);
    private final ButtoniKonClazz ALIGN_CENTER = new ButtoniKonClazz("align center", FontAwesomeSolid.ALIGN_CENTER);
    private final ButtoniKonClazz ALIGN_JUSTIFY = new ButtoniKonClazz("align justify", FontAwesomeSolid.ALIGN_JUSTIFY);

    private Button undoBtn, redoBtn, boldBtn, italicBtn, underlineBtn, strikeBtn, increaseIndentBtn, decreaseIndentBtn;
    private ToggleButton alignLeftBtn, alignCenterBtn, alignRightBtn, alignJustifyBtn;
    private PrefixSelectionComboBox<Integer> sizeCombo;
    private PrefixSelectionComboBox<String> fontCombo;
    private ColorPicker textColorPicker;
    private ColorPicker backgroundColorPicker;
    private ColorPicker paragraphBackgroundPicker;
    private ToggleGroup alignmentGrp;

    private ToolBar toolBar1;
    private ToolBar toolBar2;

    /**
     * Constructor. The SectionEditor provides the TextAreaStyled
     * that is edited. There is no access to the TextAreaStyled
     * from this class.
     */
    public EditorMain(StyledArea styledArea) {
        this.textArea = styledArea;
        textArea.setWrapText(true);
        this.updatingToolbar = new SuspendableNo();
        this.editorVBox = buildRichTextEditor();
    }

    private ExecutorService executor;

    // @TODO call stop when window is closed
    public void stop() {
        executor.shutdown();
    }

    public VBox getEditorVBox() {
        return this.editorVBox;
    }


    private VBox buildRichTextEditor() {
        this.executor = Executors.newSingleThreadExecutor();
        setButtons();
        setComboBoxs();
        setBindings();
        setToolBars();

        // call when no longer need it: `cleanupWhenFinished.unsubscribe();`
        /*Subscription cleanupWhenDone = textArea.multiRichChanges()
                .successionEnds(Duration.ofMillis(3000))
                .retainLatestUntilLater(executor)
                .supplyTask(this::calcHyperlinksAsync)
                .awaitLatest(textArea.multiRichChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(textArea::applyHyperlinks);*/

        textArea.setWrapText(true);
        textArea.setParagraphGraphicFactory(new BulletFactory(textArea));  // and folded paragraph indicator
        textArea.setContextMenu(new DefaultContextMenu());
        // Request the largest size
        textArea.setPrefHeight(SceneCntl.getScreenHt());
        //textArea.setMaxHeight(Double.MAX_VALUE);

        textArea.beingUpdatedProperty().addListener((o, old, beingUpdated) -> {
            if (!beingUpdated) {
                boolean bold, italic, underline, strike;
                Integer fontSize;
                String fontFamily;
                Color textColor;
                Color backgroundColor;

                IndexRange selection = textArea.getSelection();
                if (selection.getLength() != 0) {
                    StyleSpans<TextStyle> styles = textArea.getStyleSpans(selection);
                    bold = styles.styleStream().anyMatch(s -> s.bold.orElse(false));
                    italic = styles.styleStream().anyMatch(s -> s.italic.orElse(false));
                    underline = styles.styleStream().anyMatch(s -> s.underline.orElse(false));
                    strike = styles.styleStream().anyMatch(s -> s.strikethrough.orElse(false));
                    int[] sizes = styles.styleStream().mapToInt(s -> s.fontSize.orElse(-1)).distinct().toArray();
                    fontSize = sizes.length == 1 ? sizes[0] : -1;
                    String[] families = styles.styleStream().map(s -> s.fontFamily.orElse(null)).distinct().toArray(String[]::new);
                    fontFamily = families.length == 1 ? families[0] : null;
                    Color[] colors = styles.styleStream().map(s -> s.textColor.orElse(null)).distinct().toArray(Color[]::new);
                    textColor = colors.length == 1 ? colors[0] : null;
                    Color[] backgrounds = styles.styleStream().map(s -> s.backgroundColor.orElse(null)).distinct().toArray(i -> new Color[i]);
                    backgroundColor = backgrounds.length == 1 ? backgrounds[0] : null;
                } else {
                    int p = textArea.getCurrentParagraph();
                    int col = textArea.getCaretColumn();
                    TextStyle style = textArea.getStyleAtPosition(p, col);
                    bold = style.bold.orElse(false);
                    italic = style.italic.orElse(false);
                    underline = style.underline.orElse(false);
                    strike = style.strikethrough.orElse(false);
                    fontSize = style.fontSize.orElse(-1);
                    fontFamily = style.fontFamily.orElse(null);
                    textColor = style.textColor.orElse(null);
                    backgroundColor = style.backgroundColor.orElse(null);
                }

                int startPar = textArea.offsetToPosition(selection.getStart(), Forward).getMajor();
                int endPar = textArea.offsetToPosition(selection.getEnd(), Backward).getMajor();
                List<Paragraph<ParStyle, Either<String, Hyperlink>, TextStyle>> pars = textArea.getParagraphs().subList(startPar, endPar + 1);

                @SuppressWarnings("unchecked")
                Optional<TextAlignment>[] alignments = pars.stream().map(p -> p.getParagraphStyle().alignment).distinct().toArray(Optional[]::new);
                Optional<TextAlignment> alignment = alignments.length == 1 ? alignments[0] : Optional.empty();

                @SuppressWarnings("unchecked")
                Optional<Color>[] paragraphBackgrounds = pars.stream().map(p -> p.getParagraphStyle().backgroundColor).distinct().toArray(Optional[]::new);
                Optional<Color> paragraphBackground = paragraphBackgrounds.length == 1 ? paragraphBackgrounds[0] : Optional.empty();

                updatingToolbar.suspendWhile(() -> {
                    if (bold) {
                        if (!boldBtn.getStyleClass().contains("pressed")) {
                            boldBtn.getStyleClass().add("pressed");
                        }
                    } else {
                        boldBtn.getStyleClass().remove("pressed");
                    }

                    if (italic) {
                        if (!italicBtn.getStyleClass().contains("pressed")) {
                            italicBtn.getStyleClass().add("pressed");
                        }
                    } else {
                        italicBtn.getStyleClass().remove("pressed");
                    }

                    if (underline) {
                        if (!underlineBtn.getStyleClass().contains("pressed")) {
                            underlineBtn.getStyleClass().add("pressed");
                        }
                    } else {
                        underlineBtn.getStyleClass().remove("pressed");
                    }

                    if (strike) {
                        if (!strikeBtn.getStyleClass().contains("pressed")) {
                            strikeBtn.getStyleClass().add("pressed");
                        }
                    } else {
                        strikeBtn.getStyleClass().remove("pressed");
                    }

                    if (alignment.isPresent()) {
                        TextAlignment al = alignment.get();
                        switch (al) {
                            case LEFT:
                                alignmentGrp.selectToggle(alignLeftBtn);
                                break;
                            case CENTER:
                                alignmentGrp.selectToggle(alignCenterBtn);
                                break;
                            case RIGHT:
                                alignmentGrp.selectToggle(alignRightBtn);
                                break;
                            case JUSTIFY:
                                alignmentGrp.selectToggle(alignJustifyBtn);
                                break;
                        }
                    } else {
                        alignmentGrp.selectToggle(null);
                    }

                    paragraphBackgroundPicker.setValue(paragraphBackground.orElse(null));

                    if (fontSize != -1) {
                        sizeCombo.getSelectionModel().select(fontSize);
                    } else {
                        sizeCombo.getSelectionModel().clearSelection();
                    }

                    if (fontFamily != null) {
                        fontCombo.getSelectionModel().select(fontFamily);
                    } else {
                        fontCombo.getSelectionModel().clearSelection();
                    }

                    if (textColor != null) {
                        textColorPicker.setValue(textColor);
                    }

                    backgroundColorPicker.setValue(backgroundColor);
                });
            }
        });

        textArea.appendText("Some text in the area\n" +
                "www.google.com");
        //textArea.linkProofer();


        VirtualizedScrollPane<GenericStyledArea<ParStyle, Either<String, Hyperlink>, TextStyle>> vsPane = new VirtualizedScrollPane<>(textArea);
        VBox vbox = new VBox();
        VBox.setVgrow(vsPane, Priority.ALWAYS);
        vbox.getChildren().addAll(toolBar1, toolBar2, vsPane);
        return vbox;
    }




    /**
     * <b>Async related</b>
     * <p>Note: This is used by the Subscription in buildRichTextEditor, and is
     *      expected to be initiated when the keys have not been pressed
     *      for more than???? milliseconds.</p>
     * @return Returns a Task that evaluates the entire Text Area for Hyperlinks,
     *      If there is a match for a hyperlink, the text is converted to
     *      a Hyperlink.
     */
    private Task<StyleSpans<TextStyle>> calcHyperlinksAsync() {
        String text = textArea.getText();
        Task<StyleSpans<TextStyle>> task = new Task<>() {
            @Override
            protected StyleSpans<TextStyle> call() throws Exception {
                return calcLinkLocations(text);
            }
        };
        executor.execute(task);
        return task;
    }



    private static StyleSpans<TextStyle> calcLinkLocations(String text) {
        //ArrayList<LinkSpan> spans = new ArrayList<>();
        Matcher matcher = LINK_PATTERN.matcher(text);
        int lastMatchEnd = 0;
        Hyperlink h = null;
        StyleSpansBuilder<TextStyle> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String s = matcher.group("HYPERLINK");
            //String s = matcher.group();
            h = new Hyperlink(s, s, s);

            spansBuilder.add(h, matcher.start() - lastMatchEnd);
            spansBuilder.add(h, matcher.end() - matcher.start());
            lastMatchEnd = matcher.end();
        }
        spansBuilder.add(h, text.length() - lastMatchEnd);
        return spansBuilder.create();
    }





    // **** BUTTONS ****

    private void setButtons() {
        undoBtn = createButton(UNDO, textArea::undo);
        undoBtn.setId("editorBtn");
        redoBtn = createButton(REDO, textArea::redo);
        redoBtn.setId("editorBtn");
        boldBtn = createButton(BOLD, this::toggleBold);
        boldBtn.setId("editorBtn");
        italicBtn = createButton(ITALIC, this::toggleItalic);
        italicBtn.setId("editorBtn");
        underlineBtn = createButton(UNDERLINE, this::toggleUnderline);
        underlineBtn.setId("editorBtn");
        strikeBtn = createButton(LINE_THROUGH, this::toggleStrikethrough);
        strikeBtn.setId("editorBtn");
        increaseIndentBtn = createButton(INCREASE_INDENT, this::increaseIndent);
        increaseIndentBtn.setId("editorBtn");
        decreaseIndentBtn = createButton(DECREASE_INDENT, this::decreaseIndent);
        decreaseIndentBtn.setId("editorBtn");

        alignmentGrp = new ToggleGroup();
        alignLeftBtn = createToggleButton(alignmentGrp, ALIGN_LEFT, this::alignLeft);
        alignLeftBtn.setId("editorBtn");
        alignCenterBtn = createToggleButton(alignmentGrp, ALIGN_CENTER, this::alignCenter);
        alignCenterBtn.setId("editorBtn");
        alignRightBtn = createToggleButton(alignmentGrp, ALIGN_RIGHT, this::alignRight);
        alignRightBtn.setId("editorBtn");
        alignJustifyBtn = createToggleButton(alignmentGrp, ALIGN_JUSTIFY, this::alignJustify);
        alignJustifyBtn.setId("editorBtn");
    }

    private void setComboBoxs() {
        sizeCombo = new PrefixSelectionComboBox<>();
        sizeCombo.getItems().addAll(FXCollections.observableArrayList(5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 16, 18, 20, 22, 24, 28, 32, 36, 40, 48, 56, 64, 72));
        sizeCombo.getSelectionModel().select(Integer.valueOf(12));
        sizeCombo.setTooltip(new Tooltip("Font size"));
        sizeCombo.setId("editorBtn");
        sizeCombo.setOnAction(evt -> updateFontSize(sizeCombo.getValue()));

        fontCombo = new PrefixSelectionComboBox<>();
        fontCombo.getItems().addAll(FXCollections.observableList(Font.getFamilies()));
        fontCombo.getSelectionModel().select("Serif");
        fontCombo.setTooltip(new Tooltip("Font family"));
        fontCombo.setId("editorBtn");
        fontCombo.setOnAction(evt -> updateFontFamily(fontCombo.getValue()));

        textColorPicker = new ColorPicker(Color.BLACK);
        textColorPicker.setTooltip(new Tooltip("Text color"));
        textColorPicker.setId("editorBtn");
        textColorPicker.valueProperty().addListener((o, old, color) -> updateTextColor(color));

        backgroundColorPicker = new ColorPicker();
        backgroundColorPicker.setTooltip(new Tooltip("Text background"));
        backgroundColorPicker.setId("editorBtn");
        backgroundColorPicker.valueProperty().addListener((o, old, color) -> updateBackgroundColor(color));

        paragraphBackgroundPicker = new ColorPicker();
        paragraphBackgroundPicker.valueProperty().addListener((o, old, color) -> updateParagraphBackground(color));
        paragraphBackgroundPicker.setId("editorBtn");
        paragraphBackgroundPicker.setTooltip(new Tooltip("Paragraph background"));
    }

    private void setToolBars() {
        toolBar1 = new ToolBar(
                undoBtn, redoBtn, new Separator(Orientation.VERTICAL),
                boldBtn, italicBtn, underlineBtn, strikeBtn, new Separator(Orientation.VERTICAL),
                alignLeftBtn, alignCenterBtn, alignRightBtn, alignJustifyBtn, new Separator(Orientation.VERTICAL),
                increaseIndentBtn, decreaseIndentBtn);

        //toolBar1.getStyleClass().addAll("segmented-button-bar");

        toolBar2 = new ToolBar(sizeCombo, fontCombo, textColorPicker, backgroundColorPicker);
        //toolBar2.getStyleClass().addAll("segmented-button-bar");

        toolBar1.setStyle("-fx-background-color: TRANSPARENT; -fx-border-width: 0 0 1 0; -fx-border-color: #393E4620;");
        toolBar2.setStyle("-fx-background-color: TRANSPARENT; -fx-border-width: 0 0 1 0; -fx-border-color: #393E4620;");
    }

    private void setBindings() {
        undoBtn.disableProperty().bind(textArea.undoAvailableProperty().map(x -> !x));
        redoBtn.disableProperty().bind(textArea.redoAvailableProperty().map(x -> !x));

        BooleanBinding selectionEmpty = new BooleanBinding() {
            {
                bind(textArea.selectionProperty());
            }

            @Override
            protected boolean computeValue() {
                return textArea.getSelection().getLength() == 0;
            }
        };

    }

    private Button createButton(ButtoniKonClazz ikonClazz, Runnable action) {
        Button button = ikonClazz.get();
        button.setOnAction(evt -> {
            action.run();
            textArea.requestFocus();
        });

        return button;
    }

    private ToggleButton createToggleButton(ToggleGroup grp, ButtoniKonClazz ikonClazz, Runnable action) {

        ToggleButton button = new ToggleButton("", ikonClazz.get().getGraphic());
        button.setToggleGroup(grp);
        button.setOnAction(evt -> {
            action.run();
            textArea.requestFocus();
        });

        return button;
    }

    private void toggleBold() {
        updateStyleInSelection(spans -> TextStyle.bold(!spans.styleStream().allMatch(style -> style.bold.orElse(false))));
    }

    private void toggleItalic() {
        updateStyleInSelection(spans -> TextStyle.italic(!spans.styleStream().allMatch(style -> style.italic.orElse(false))));
    }

    private void toggleUnderline() {
        updateStyleInSelection(spans -> TextStyle.underline(!spans.styleStream().allMatch(style -> style.underline.orElse(false))));
    }

    private void toggleStrikethrough() {
        updateStyleInSelection(spans -> TextStyle.strikethrough(!spans.styleStream().allMatch(style -> style.strikethrough.orElse(false))));
    }

    private void alignLeft() {
        updateParagraphStyleInSelection(ParStyle.alignLeft());
    }

    private void alignCenter() {
        updateParagraphStyleInSelection(ParStyle.alignCenter());
    }

    private void alignRight() {
        updateParagraphStyleInSelection(ParStyle.alignRight());
    }

    private void alignJustify() {
        updateParagraphStyleInSelection(ParStyle.alignJustify());
    }

    // **** END BUTTONS ****


    private void load(File file) {
        if (textArea.getStyleCodecs().isPresent()) {
            Tuple2<Codec<ParStyle>, Codec<StyledSegment<Either<String, Hyperlink>, TextStyle>>> codecs = textArea.getStyleCodecs().get();
            Codec<StyledDocument<ParStyle, Either<String, Hyperlink>, TextStyle>>
                    codec = ReadOnlyStyledDocument.codec(codecs._1, codecs._2, textArea.getSegOps());

            try {
                FileInputStream fis = new FileInputStream(file);
                DataInputStream dis = new DataInputStream(fis);
                StyledDocument<ParStyle, Either<String, Hyperlink>, TextStyle> doc = codec.decode(dis);
                fis.close();

                if (doc != null) {
                    textArea.replaceSelection(doc);
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Intended for use in the Save Deck.
     * See StyledDocument in Docs
     * @return Returns the StyledDocument as a StyledDocument<ParStyle, Either<String, HyperLink>, TextStyle>
     */
    public final StyledDocument<ParStyle, Either<String, Hyperlink>, TextStyle> getDoc() {
        return textArea.getDocument();
    }

    private void increaseIndent() {
        updateParagraphStyleInSelection(ps -> ps.increaseIndent());
    }

    private void decreaseIndent() {
        updateParagraphStyleInSelection(ps -> ps.decreaseIndent());
    }

    private void updateStyleInSelection(Function<StyleSpans<TextStyle>, TextStyle> mixinGetter) {
        IndexRange selection = textArea.getSelection();
        if (selection.getLength() != 0) {
            StyleSpans<TextStyle> styles = textArea.getStyleSpans(selection);
            TextStyle mixin = mixinGetter.apply(styles);
            StyleSpans<TextStyle> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
            textArea.setStyleSpans(selection.getStart(), newStyles);
        }
    }

    private void updateStyleInSelection(TextStyle mixin) {
        IndexRange selection = textArea.getSelection();
        if (selection.getLength() != 0) {
            StyleSpans<TextStyle> styles = textArea.getStyleSpans(selection);
            StyleSpans<TextStyle> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
            textArea.setStyleSpans(selection.getStart(), newStyles);
        }
    }

    private void updateParagraphStyleInSelection(Function<ParStyle, ParStyle> updater) {
        IndexRange selection = textArea.getSelection();
        int startPar = textArea.offsetToPosition(selection.getStart(), Forward).getMajor();
        int endPar = textArea.offsetToPosition(selection.getEnd(), Backward).getMajor();
        for (int i = startPar; i <= endPar; ++i) {
            Paragraph<ParStyle, Either<String, Hyperlink>, TextStyle> paragraph = textArea.getParagraph(i);
            textArea.setParagraphStyle(i, updater.apply(paragraph.getParagraphStyle()));
        }
    }

    private void updateParagraphStyleInSelection(ParStyle mixin) {
        updateParagraphStyleInSelection(style -> style.updateWith(mixin));
    }

    private void updateFontSize(Integer size) {
        if (!updatingToolbar.get()) {
            updateStyleInSelection(TextStyle.fontSize(size));
        }
    }

    private void updateFontFamily(String family) {
        if (!updatingToolbar.get()) {
            updateStyleInSelection(TextStyle.fontFamily(family));
        }
    }

    private void updateTextColor(Color color) {
        if (!updatingToolbar.get()) {
            updateStyleInSelection(TextStyle.textColor(color));
        }
    }

    private void updateBackgroundColor(Color color) {
        if (!updatingToolbar.get()) {
            updateStyleInSelection(TextStyle.backgroundColor(color));
        }
    }

    private void updateParagraphBackground(Color color) {
        if (!updatingToolbar.get()) {
            updateParagraphStyleInSelection(ParStyle.backgroundColor(color));
        }
    }

    // INNER CLASS FOR
    private class DefaultContextMenu extends ContextMenu {
        private MenuItem fold, unfold;

        public DefaultContextMenu() {
            fold = new MenuItem( "Fold selected text" );
            fold.setOnAction( AE -> { hide(); fold(); } );

            unfold = new MenuItem( "Unfold from cursor" );
            unfold.setOnAction( AE -> { hide(); unfold(); } );

            getItems().addAll( fold, unfold );
        }

        /**
         * Folds multiple lines of selected text, only showing the first line and hiding the rest.
         */
        private void fold()
        {
            ((StyledArea) getOwnerNode()).foldSelectedParagraphs();
        }

        /**
         * Unfold the CURRENT line/paragraph if it has a fold.
         */
        private void unfold() {
            StyledArea area = (StyledArea) getOwnerNode();
            area.unfoldParagraphs( area.getCurrentParagraph() );
        }
    }
}
