package flashmonkey;

import fmtree.FMTWalker;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import search.Search;
import type.cardtypes.GenericCard;
import type.testtypes.QandA;
//import type.testtypes.QandAMain;

import java.util.*;

/**
 * Provides the UI for card search. Not deck search.
 *
 * @author lowell Stadelman
 */
public final class SearchPane {

      // singleton
      private static SearchPane CLASS_INSTANCE;

      // *** Panes ***
    /** Provides a scrollable pane to display the results of a search */
      private static ScrollPane scrollPane;
    /** Displays the results of the search inside of the scrollPane*/
      private static VBox resultPane;
    /** Contains the scrollPane and sent to the calling method **/
      private static VBox finalPane;

    /** The resulting search */
      //   private static Set<Hyperlink> newHypLinkSet;
      private final Set<Hyperlink> unionHypLinkSet;
      private SortedSet<SearchHyperLink> newSortedSet;
      private SortedSet<SearchHyperLink> unionSortedSet;
      private static TextField searchField;
      private Button clearButton;

      /**
       * Constructor for singleton class
       *
       * @param height Provide the height of the pane containing
       *               the search results or the selected card. IE
       *               the height of the centerPane.
       */
      private SearchPane(double height, double width) {
            unionHypLinkSet = new HashSet<>(30);
            unionSortedSet = new TreeSet();
            scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            resultPane = new VBox();
            resultPane.setMinWidth(width);
            clearButton = new Button("X");
            searchField = new TextField();
            searchField.setPrefColumnCount(120);
            finalPane = new VBox();
            finalPane.setPadding(new Insets(2, 2, 2, 2));
            finalPane.setStyle("-fx-background-color: TRANSPARENT");
      }

      /**
       * Provides only one instance of this class.
       *
       * @param height Provide the height of the pane containing
       *               *               the search results or the selected card. IE
       *               *               the height of the centerPane.
       * @return Returns an instance of the class. Ensures that only
       * one instance exists. This method is Synchronized.
       */
      public synchronized static SearchPane getInstance(double height, double width) {
            if (CLASS_INSTANCE == null) {
                  CLASS_INSTANCE = new SearchPane(height, width);
            }
            return CLASS_INSTANCE;
      }


      /** ------ ------ ------ GETTERS ----- ------ ------ **/


        // ---*---*---*---*--- * ---*---*---*---*--- //

      /**
       * Returns the searchBox containing the search field and the clear button. Also
       * includes the keyActions for the searchfield.
       *
       * @param treeWalker
       * @param flashList
       * @return HBox searchBox
       */
      public HBox getSearchBox(FMTWalker treeWalker, ArrayList<FlashCardMM> flashList) {
            HBox searchBox = new HBox();
            //searchBox.setAlignment(Pos.CENTER);
            //searchField = new TextField();
            clearButton.setMaxSize(20, Double.MAX_VALUE);
            clearButton.setId("clearButton");
            searchField.setId("searchText");
            searchBox.setPadding(new Insets(0, 0, 5, 0));
            searchBox.getChildren().addAll(searchField, clearButton);
            searchField.setMaxSize(215, Double.MAX_VALUE);
            searchField.setPromptText("Search");

            // KEY ACTIONS
            /** convienience when the EncryptedUser.EncryptedUser presses enter, or space key, the
             searchAction() is executed
             */
            searchField.setOnKeyPressed((KeyEvent e) ->
            {
                  if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) {
                        searchAction(searchField.getText(), flashList);
                  }
            });
            /** clears the searchField and textArea */
            clearButton.setOnAction((ActionEvent e) ->
            {
                  GenericCard gc = new GenericCard();
                  searchField.setPromptText("search");

                  if (!searchField.getText().isEmpty()) {
                        resultPane.getChildren().clear();
                        finalPane.getChildren().clear();
                        searchField.clear();
                        FlashMonkeyMain.AVLT_PANE.clearHighlighted();
                        FlashMonkeyMain.AVLT_PANE.displayTree();

                        FlashCardMM fc = (FlashCardMM) FMTWalker.getInstance().getCurrentNode().getData();

                        ReadFlash.rpCenter.getChildren().clear();
                        ReadFlash.rpCenter.getChildren().add(QandA.QandASession.getInstance().getTReadPane(fc, gc, ReadFlash.getInstance().getMasterBPane()));
                  }
            });
            return searchBox;
      }

      // ---*---*---*---*--- * ---*---*---*---*--- //


      public TextField getSearchField() {
            return searchField;
      }

      // ---*---*---*---*--- * ---*---*---*---*--- //

      /**
       * Shows the results of the search, or the selected card after
       * the EncryptedUser.EncryptedUser clicks on the hyperlink
       *
       * @return Returns a Pane containing either the scrollPane with search results,
       * or the selected card
       */
      public Pane getResultPane() {
            return finalPane;
      }

      // ---*---*---*---*--- * ---*---*---*---*--- //

      /**
       * Displays selected result from search result. Use
       *
       * @param findThis The string combination of terms for search
       */
      private void searchAction(String findThis, ArrayList<FlashCardMM> flashList) {
            Search search;

            if (findThis.length() > 2) {
                  // Store the original contents of the parentPane to be used
                  // when we are done with search.
                  resultPane.getChildren().clear();
                  search = new Search();
                  search.buildMap(flashList);
                  /** The search action & result **/

                  Set returnCardSet = search.find(findThis, flashList);
                  newSortedSet = new TreeSet();

                  // convert returned flashcards to a hyperlink and store in newHypLinkSet
                  if (search.getBool() == true) {
                        Iterator<FlashCardMM> setIterator = returnCardSet.iterator();

                        // Loop through results and display
                        while (setIterator.hasNext()) {
                              FlashCardMM flashCardMM = setIterator.next();
                              // show results in lower text pane and create hyperlink.
                              SearchHyperLink searchHLink = new SearchHyperLink(flashCardMM);

                              newSortedSet.add(searchHLink);
                        }

                        // retain previous links that are in the new set to
                        // preserve links already visited
                        if (unionSortedSet.isEmpty()) {
                              unionSortedSet = newSortedSet; //.addAll(newSortedSet);
                        } else {
                              unionSortedSet.retainAll(newSortedSet);
                              unionSortedSet.addAll(newSortedSet);
                        }

                        // Display the union of old and new links in the scrollpane
                        Iterator<SearchHyperLink> unionSetIterator = unionSortedSet.iterator();

                        while (unionSetIterator.hasNext()) {
                              resultPane.getChildren().add(unionSetIterator.next());
                        }
                        scrollPane.setContent(resultPane);
                  } else {
                        TextArea nothing = new TextArea("Oops! \n\n \"" + findThis + "\" wasn't here. Check the terms " +
                            "spelling or try different search terms.");
                        nothing.setWrapText(true);
                        scrollPane.setContent(nothing);
                  }

                  searchField.setPromptText("search");
                  finalPane.getChildren().clear();
                  finalPane.getChildren().add(scrollPane);
            } else {
                  // Display message in search field for 2 seconds.
                  Task<Void> sleeper = new Task<Void>() {
                        @Override
                        protected Void call() {
                              try {
                                    searchField.setStyle("-fx-text-fill: #039ED3");
                                    searchField.setText("not enough letters");
                                    // Delay to temporarily display the message
                                    Thread.sleep(2000);
                                    searchField.clear();
                                    searchField.setStyle("");
                              } catch (InterruptedException e) {
                                    // left blank
                              }
                              return null;
                        }
                  };
                  new Thread(sleeper).start();
            }
      }

      public void onClose() {
            CLASS_INSTANCE = null;
            scrollPane = null;
            resultPane = null;
            finalPane = null;
            unionHypLinkSet.clear();
            //newHypLinkSet.clear();
            searchField = null;
            clearButton = null;
      }

      private class SearchHyperLink extends Hyperlink implements Comparable {
            protected SearchHyperLink(FlashCardMM flashCardMM) {
                  String str = flashCardMM.getQText();
                  if (str.length() > 61) {
                        str = str.substring(0, 60);
                  }

                  super.setText(str);
                  super.setMaxWidth(/*scrollPane.getWidth()*/300 - 20);
                  super.textAlignmentProperty().isEqualTo(resultPane);
                  super.setWrapText(true);
                  super.setId("hl");
                  super.setOnAction((ActionEvent h) -> {
                        GenericCard gc = new GenericCard();
                        finalPane.getChildren().clear();
                        finalPane.getChildren().addAll(gc.cardFactory(
                            flashCardMM.getQText(),
                            flashCardMM.getQType(),
                            flashCardMM.getAText(),
                            flashCardMM.getAType(),
                            'D',
                            flashCardMM.getQFiles(),
                            flashCardMM.getAFiles()));

                        // Indicate the current node in the tree
                        FlashMonkeyMain.AVLT_PANE.setHighlighted(flashCardMM);
                        FlashMonkeyMain.AVLT_PANE.displayTree();
                  });
            }

            @Override
            public int compareTo(Object other) {
                  if (other == null) {
                        throw new NullPointerException("Object is null when comparing HyperLinks in SearchPane.InnerClass ");
                  }
                  if (other.getClass().equals(this)) {
                        throw new ClassCastException("Object is not a Hyperlink class");
                  } else {
                        SearchHyperLink otherLink = (SearchHyperLink) other;
                        return this.getText().compareTo(otherLink.getText());
                  }
            }
      }

}
