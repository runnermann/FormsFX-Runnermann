package flashmonkey;

import fileops.FileNaming;
import fileops.FileOpsShapes;
import multimedia.AnswerMM;
import multimedia.QuestionMM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.cardtypes.CardLayout;
import type.celltypes.CellLayout;
import type.draw.shapes.GenericShape;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * <p>The FlashCardMM type is the top-level class for the FlashMonkey Test
 * type. Each Test Type contains an answer object and a question object.
 * Additional fields are provided to track each card and tell FlashMonkey
 * the TestTypes layout, and behavior. </p>
 * <p>Class provides the layout, its number for this iteration, if
 * it was answered correctly in the last session, the number of seconds it
 * took to answer it and the number of times it has been answered correctly
 * vs. the number of times it has been seen. Finally it is also provided
 * a date for the last time it was answered correctly. </p>
 * <p>These fields are to provide a means to score each card both with time,
 * that it takes to answer a question, and the number of times it has been answered.
 * Thus we can give a value to the remember field.</p>
 * <p>The remember field provides an ability to prioritize a card to shift left
 * in a session, shift right, or to be hidden. Hiding is provided by a threshold
 * field in ReadFlash?. </p>
 * <p>The testType const field assigns to ReadFlash and CreateFlash the TestType for
 * the card. TestTypes have a layout, behavior, a question, and an answer. Note that
 * Answer does not always have to be used, but is part of the object. The user may
 * select the TestType when the card is created or edited. ReadFlash uses TestType
 * for each card when it is displayed to the user. New TestTypes may be defined using
 * the SDK. </p>
 * <p>
 * copyright FlashMonkey Inc., all rights reserved, 2018/08/04
 * Creation date: 2018/08/04
 *
 * @author Lowell Stadelman
 */
public class FlashCardMM<T extends Comparable<T>> implements Serializable, Comparable<T>, Cloneable {
      private static final long serialVersionUID = FlashMonkeyMain.VERSION;

      private static final Logger LOGGER = LoggerFactory.getLogger(FlashCardMM.class);

      //*** CONSTANTS / DEFAULTS***
      public static final String C_ID = "0"; // Original card number. Never changes
      public static final int C_NUMBER = 777; // Card number this session
      public static final CardLayout CARD_LAYOUT = CardLayout.DOUBLE_VERTICAL; // FlashCard, Top and Bottom
      public static final int IS_RIGHT_COLOR = 0; // was it answered correctly this session, controls color in tree.
      public static final int REMEMBER = 0; // The prioirty of this card is set based on this number.
      public static final int SECONDS = 0; // seconds to answer this session.
      public static final int NUM_RIGHT = 0; // total number times right in it's existence
      public static final ZonedDateTime RT_DATE = ZonedDateTime.parse("2017-09-25T10:00:00-07:00[America/Los_Angeles]");
      public static final int SESSION_SEEN = 0; // total number of times it's been seen this session
      // Use a BitSet as a boolean array to indicate the tests
      // that are possible for this question.
      // Default is multiple choice, multiple answer
      // write it in, and true or false.
      public static final int DEF_TEST_TYPE = 0b0000000000011111;


      // *** CARD VARIABLES ***
      /**
       * A cards original card ID number, does not change during the life of a card. Used in associating
       * fileNames with a card. Formerly the qID or questionID.
       * !!! cID is set to the hash of the {@code userName_deckName_&_time }
       */
      private String cID;
      /* The cardNumber used as a priority as the flashcard is set in the tree. Changes each iteration. */
      private int cNumber;
      /* The card layout type **/
      private CardLayout cardLayout;
      /* the question */
      private QuestionMM questionMM;  // contains its section layout type
      /* the answer */
      private AnswerMM answerMM; // contains its section layout type
      /* did the EncryptedUser.EncryptedUser get the question correct on this iteration -1 if wrong, 0 if unanswered, 1 if correct*/
      private int isRightColor;
      /* Priority/remember, Used to set this card towards the front, back, or hide from the tree. */
      private int remember;
      /* tracks how long it takes to answer this question */
      private int seconds;
      /* The number of times this question has been answered correctly */
      private int numRight;
      /* Date last answered correctly */
      private ZonedDateTime rtDate;
      /* The total number of times a question has been seen. */
      private int sessionSeen;
      /* BitArray used for testTypes */
      private int testType;

      // *** CONSTRUCTORS *** /

      /**
       * Default constructor
       * Uses the default cHash, which may or may not be a new cID
       */
      public FlashCardMM() {
            this.answerMM = new AnswerMM();
            this.testType = DEF_TEST_TYPE;  // Default Bit Array
            this.questionMM = new QuestionMM();
            this.cardLayout = CARD_LAYOUT;
            // Card ID. Never changes after it's been created
            this.cID = createCardHash(FlashCardOps.getInstance().getDeckFileName() + authcrypt.UserData.getUserName()) + "_" + System.currentTimeMillis();
            this.cNumber = C_NUMBER;     // Card number former qNumber, it's priority in the tree
            this.remember = REMEMBER;    // priority
            this.isRightColor = IS_RIGHT_COLOR;     // Was it answered correctly this session?
            this.numRight = NUM_RIGHT;   // number of times answered correctly
            this.seconds = SECONDS;      // time it took to answer this question
            this.rtDate = RT_DATE;       // last time answered correctly
            this.sessionSeen = SESSION_SEEN;     // number of times seen

            LOGGER.info("\n\n*** Noargs constructor, cID: {} ***\n\n", cID);
      }

      /**
       * Create card constructor, creates a new FlashCardMM. Called only once in the life of a card.
       * for a new card, only need to provide the qNum, aNum, questionTxt, questionType, mmQfileName,
       * answerTxt, answerType, answerMMFileName, Array of like answers, and card cardLayout. All
       * other variables are the defaults.
       *
       * @param deckName   Used to create a new cID/cHash. The current deck's name.
       * @param cNum       A changing number used to compare the card, sets its order in the tree
       * @param testType   An int used as a boolean array indicating the tests for this session. Default is
       *                   multiple choice, multiple answer, write it in, and true or false.
       * @param aNum       The answer number, used to retrieve an answer from flashList. This is the index of the flashList
       *                   that is set during FlashCardOps.buildTree().
       * @param qTxt       The question text
       * @param qType      The type section the question will use
       * @param qFileNames The files associated with the question. IE for an image, sound or video, and shapes
       * @param aTxt       The answer text
       * @param aType      The type section the answer will use
       * @param aFileNames The files associated with the answer. IE for an image, sound, or video, and shapes
       * @param ansSt      The set of answers that would also be correct for this question
       * @param cardLayout     The cardLayout for this card. IE full or half sections
       */
      public FlashCardMM(final String deckName, int cNum, int testType, CardLayout cardLayout, String qTxt, CellLayout qType, String[] qFileNames, String aTxt, int aNum,
                         CellLayout aType, String[] aFileNames, ArrayList<Integer> ansSt) {
            this.answerMM = new AnswerMM(aTxt, aNum, aType, ansSt, aFileNames); // AnswerMM
            this.questionMM = new QuestionMM(qTxt, qType, qFileNames);
            this.testType = testType;
            this.cardLayout = cardLayout;
            this.cID = createCardHash(deckName); // may be created previously in the default constructor
            this.cNumber = cNum;
            this.remember = REMEMBER;
            this.isRightColor = IS_RIGHT_COLOR;
            this.numRight = NUM_RIGHT;
            this.seconds = SECONDS;
            this.rtDate = RT_DATE;
            this.sessionSeen = SESSION_SEEN;

            LOGGER.info("\n\n ~~~ FlashCard full constructor ~~~\n \tcID: {}", cID);
      }


      /**
       * Constructor, Used when editing a flashCard.
       *
       * @param cID        should not change from previous card. Used to associate files with this card.
       * @param cNum       A changing number used to compare the question, sets its order in the tree
       * @param testType   An int used as a boolean array indicating the tests for this session. Default is
       *                   multiple choice, multiple answer, write it in, and true or false.
       * @param CardLayout     The card CardLayout (Should not change, copied from previous card.)
       * @param qTxt       The question text
       * @param qType      The type section the question will use
       * @param qFileNames The files associated with the question. IE for an image, sound or video
       * @param aTxt       The answer text
       * @param aNum       The answer number, used to retrieve an answer from flashList
       * @param aType      The type section the answer will use
       * @param aFileNames The files associated with the answer. IE for an image, sound, or video
       * @param ansSt      The set of answers that would also be correct for this question
       * @param remember   Used when a question is answered, determins a cards priority (Should not change)
       * @param isRightColor    If this card was answered correctly
       * @param numRight   The number of times this card has been answerd correctly
       * @param sec        The amount of time it takes to answer the question
       * @param date       The date this question was last answered correctly
       * @param sessionSeen    The number of times this card has been seen.
       */
      public FlashCardMM(final String cID, int cNum, int testType, CardLayout CardLayout, String qTxt, CellLayout qType, String[] qFileNames, String aTxt, int aNum,
                         CellLayout aType, String[] aFileNames, ArrayList<Integer> ansSt, int remember, int isRightColor, int numRight,
                         int sec, ZonedDateTime date, int sessionSeen) {
            this.answerMM = new AnswerMM(aTxt, aNum, aType, ansSt, aFileNames); // AnswerMM
            this.questionMM = new QuestionMM(qTxt, qType, qFileNames);
            this.testType = testType;
            this.cardLayout = CardLayout;
            this.cID = cID;
            this.cNumber = cNum;
            this.remember = remember;
            this.isRightColor = isRightColor;
            this.numRight = numRight;
            this.seconds = sec;
            this.rtDate = date;
            this.sessionSeen = sessionSeen;

            LOGGER.info("\n\n ~~~ FlashCard full constructor for editing card~~~\n \tcID: {}", cID);
      }


      /**
       * FlashCard shallow copy constructor
       */
      public FlashCardMM(FlashCardMM original) {
            if (original == null) //can't copy "nothing".
            {
                  LOGGER.warn("ERROR: FlashCard Class Copy Constructor given NULL. "
                      + "Exiting.");
                  System.exit(0);
            } else {
                  this.cID = original.cID;
                  this.cardLayout = original.cardLayout;
                  this.answerMM = original.answerMM; // may cause an error
                  this.testType = original.testType;
                  this.questionMM = original.questionMM;
                  this.cNumber = original.cNumber;
                  this.remember = original.remember;
                  this.numRight = original.numRight;
                  this.sessionSeen = original.sessionSeen;
                  this.rtDate = original.rtDate;
                  this.isRightColor = original.isRightColor;
                  this.seconds = original.seconds;
            }
      }

      /**
       * Clone constructor .. Creates a new FlashCardMM
       * from the original.
       * Creates a deep copy of the FlashCardMM object.
       */
      @Override
      public FlashCardMM clone() {
            LOGGER.debug("called clone()");

            if (this.answerMM == null && this.questionMM == null) //can't copy "nothing"! avoids crashes
            {
                  LOGGER.error("ERROR: FlashCard Class Copy Constructor given NULL. "
                      + "Exiting.");
                  //System.exit(0);
                  return new FlashCardMM(); // keep the compiler happy :)
            } else {
                  return new FlashCardMM(
                      this.cID,
                      this.cNumber,
                      this.testType,
                      this.cardLayout,
                      this.questionMM.getQText(),
                      this.questionMM.getQType(),
                      this.getQFiles(),
                      this.answerMM.getAText(),
                      this.answerMM.getANumber(),
                      this.answerMM.getAType(),
                      this.getAFiles(),
                      this.answerMM.getAnswerSet(),
                      this.remember,
                      this.isRightColor,
                      this.numRight,
                      this.seconds,
                      this.rtDate,
                      this.sessionSeen);
            }
      }


      // ************************************** ******* **************************************
      // ************************************** SETTERS **************************************
      // ************************************** ******* **************************************

      /**
       * Sets the following values, the remainder retain the values of this card.
       *
       * @param cNum
       * @param testType
       * @param layout
       * @param qTxt
       * @param qType
       * @param qFileNames
       * @param aTxt
       * @param aNum
       * @param aType
       * @param aFileNames
       * @param ansSt
       */
      public void setAll(int cNum, int testType, CardLayout layout, String qTxt,
                         CellLayout qType, String[] qFileNames, String aTxt, int aNum,
                         CellLayout aType, String[] aFileNames, ArrayList<Integer> ansSt) {
            this.answerMM = new AnswerMM(aTxt, aNum, aType, ansSt, aFileNames); // AnswerMM
            this.questionMM = new QuestionMM(qTxt, qType, qFileNames);
            this.testType = testType;
            this.cardLayout = layout;
            this.cNumber = cNum;
      }


      protected void set_Q_Text(String q) {
            this.questionMM.setQText(q);
      }

      protected void set_A_Text(String ans) {
            this.answerMM.setAText(ans);
      }

      protected void set_Q_Type(CellLayout qType) {
            this.questionMM.setQType(qType);
      }

      protected void set_A_Type(CellLayout aType) {
            this.answerMM.setAType(aType);
      }

      /**
       * The media files
       * @param files
       */
      protected void setQFiles(String[] files) {
            this.questionMM.setQFiles(files);
      }

      protected void setAFiles(String[] files) {
            this.answerMM.setAFiles(files);
      }

      protected void setCardLayout(CardLayout layout) {
            this.cardLayout = layout;
      }

      /**
       * Sets the answer object to the answer in the parameter
       *
       * @param ansO
       */
      protected void setAnswerMM(AnswerMM ansO) {
            this.answerMM = ansO;
      }

      /**
       * Sets the BitSet or array of bits to the BitSet in the parameter
       * The TestBSet sets the possible tests that the EncryptedUser.EncryptedUser may see. If left
       * to default, AI(future) will select the tests set to true based on
       * the history-matrix. Currently only tests 1 - 4 may be used with AI.
       * In order starting from least significant bit as 0
       * 0 = reserved for later but used by ai (may not be neccessary)
       * 1 = Multiple choice
       * 2 = Multiple answer
       * 3 = q {@code &} a
       * 4 = True or false
       * 5 = FITB fill in the blank
       * 6 = Turn in video
       * 7 = Turn in audio
       * 8 = write it in
       * 9 = Draw
       * 10 = Draw over image
       * 11 = Math card
       * 12 = Graph card
       * 13 = Note Taker
       * ai = bits little endian 0,1,2,3,4
       */
      protected void setTestType(int testType) {
            this.testType = testType;
      }

      /**
       * Sets the number of times this flashCard has been seen.
       *
       * @param num
       */
      public void setSessionSeen(int num) {
            this.sessionSeen = num;
      }


      /**
       * setIsRight sets the int value for the users
       * response to this session. Upstream method
       * / EncryptedUser.EncryptedUser method should be set to 0 unanswered, -1 wrong, 1
       * correct.
       *
       * @param correct
       */
      public void setIsRightColor(int correct) {
            this.isRightColor = correct;
      }

      /**
       * Sets the number of times this question has been answered correctly
       *
       * @param num
       */
      public void setNumRight(int num) {
            this.numRight = num;
      }

      /**
       * Sets the question object to the question in the parameter
       *
       * @param qO
       */
      protected void setQuestion(QuestionMM qO) {
            this.questionMM = qO;
      }

      /**
       * Sets the qNumber to the parameter
       *
       * @param qNum
       */
      protected void setCNumber(int qNum) {
            this.cNumber = qNum;
      }

      public void setANumber(int aNum) {
            if (this.answerMM == null) {
                  System.exit(1);
            }
            this.answerMM.setANumber(aNum);
      }

      /**
       * setRemember()  sets the priority that a flashcard is seen.
       * Setting is done either by the rdo button in editFlashPane
       * or when a EncryptedUser.EncryptedUser misses a question in the test.
       * Convention/Rule:
       * forget (hides this question by setting the priority to a value
       * higher than a threshold. Normal will not change the listing of the
       * flashCard from the original index/qNumber, and shortList will set
       * the flashCard to a negative number if done by the EncryptedUser.EncryptedUser. If done
       * by FlashMonkey ReadFlash it will bump the priority down by a percentage
       * until it is negative.
       * Less than threshold for forget, 0 for normal, greater than 0 for shortList seen first
       */
      public void setRemember(int rem) {
            this.remember = rem;
      }

      /**
       * Sets the date to the date the question was answered correctly. Or when
       * <p><i>Note</i> This should be set after the EncryptedUser.EncryptedUser is done with the question.
       * The Threshold() relies on this method/value to set a FlashCards metrics.
       * uses </p>
       *
       * @param date
       */
      public void setRtDate(ZonedDateTime date) {
            this.rtDate = date;
      }

      /**
       * overide setSeconds with parameter sets seconds to the argument.
       *
       * @param seconds
       */
      public void setSeconds(int seconds) {
            this.seconds = seconds;
      }

      // ************************************** ******* **************************************
      //                                        GETTERS
      // ************************************** ******* **************************************

      /**
       * For convenience, AnswerMM is inherited, QuestionMM is not,
       * thus, getAText() is available. this method is simply to reduce
       * the "confucion" of implementation for the authors.
       *
       * @return
       */
      public String getQText() {
            return this.questionMM.getQText();
      }

      public String getAText() {
            return this.answerMM.getAText();
      }

      public String[] getQFiles() {
            return this.questionMM.getQFiles();
      }

      public String[] getAFiles() {
            return this.answerMM.getAFiles();
      }

      public CellLayout getQType() {
            return this.questionMM.getQType();
      }

      public CellLayout getAType() {
            return this.answerMM.getAType();
      }

      /**
       * Returns the int containing the test types in a boolean array/ int.
       * See setter for information on what test is set by what bit.
       *
       * @return
       */
      public int getTestType() {
            return this.testType;
      }


      /**
       * getQuestion()
       *
       * @return returns this Question object
       */
      public Question getQuestionMM() {
            return this.questionMM;
      }

      public AnswerMM getAnswerMM() {
            return this.answerMM;
      }

      /**
       * getIsRight() returns the value in current card
       * isRight variable.
       *
       * @return
       */
      public int getIsRightColor() {
            return this.isRightColor;
      }

      /**
       * Returns the number of times this question has been answered correctly.
       *
       * @return The number of times this question has been answered correctly.
       */
      public int getNumRight() {
            return this.numRight;
      }

      /**
       * returns the number of times this flashCard has been seen.
       *
       * @return Returns the number of times this flashCard has been seen.
       */
      public int getSessionSeen() {
            return this.sessionSeen;
      }

      /**
       * Returns the date that this question was last answered correctly.
       *
       * @return the date that this question was last answered.
       */
      public ZonedDateTime getRtDate() {
            if (this.rtDate != null) {
                  return this.rtDate;
            } else {
                  // Set the rtDate to default
                  this.setRtDate(RT_DATE);
                  return this.rtDate;
            }
      }

      /**
       * getRemember() remembers which list the question is on. I.e.
       * forget (forgets this question, normal (keep showing this
       * question, EncryptedUser added repeat this question, and FlashMonkey
       * added repeat this question(when the EncryptedUser.EncryptedUser gets it wrong).
       *
       * @return Convention/Rule = returns -1 for forget,
       * 0 for normal, 1 for self added longList/repeat, 2 for FM added
       * longList/repeat.
       * Note that this depends on what is set in the setRemember
       * provided by the upstream/EncryptedUser.EncryptedUser method.
       */
      public int getRemember() {
            return this.remember;
      }

      /**
       * getSeconds
       *
       * @return int seconds that it took for the
       * EncryptedUser.EncryptedUser to answer a question.
       */
      public int getSeconds() {
            return this.seconds;
      }

      /**
       * Returns the cNumber (Card number) of this object
       *
       * @return the cNumber (Card number) for this object.
       */
      public int getCNumber() {
            return this.cNumber;
      }

      public int getANumber() {
            return this.answerMM.getANumber();
      }

      /**
       * Returns this cards cID number which never changes.
       *
       * @return Returns this cards cID number. Do not change
       */
      public final String getCID() {
            return this.cID;
      }

      /**
       * Helper method to saveNewCardToCreator. Returns a new cHash, internal method only
       *
       * @param deckName
       * @return
       */
      private static String createCardHash(String deckName) {
            FileNaming fName = new FileNaming();
            String cHash = fName.getCardHash(deckName, "SHA");
            return cHash;
      }


      // ************************************** ******* **************************************
      // **************************************  OTHER  **************************************
      // ************************************** ******* **************************************

      /**
       * Compares the questionMM and answerMM for equality with the other card.
       * This method is the top level method for testing equality between this cards
       * content, and another cards content. Does not compare non-content fields such
       * as CardNumber, time, etc. As more capabilities are added, those classes should
       * provide an eauals statement to compare its content.
       *
       * @param other
       * @return true if the content of two cards are identical.
       */
      @Override
      public boolean equals(Object other) {
            if (other == null) {
                  return false;
            } else if (this.getClass() != other.getClass()) {
                  return false;
            } else {
                  FlashCardMM otherFC;
                  otherFC = (FlashCardMM) other;

                  String qImgFileName = "";
                  String aImgFileName = "";

                  // GenericShapes
                  String gsQ = "";
                  String gsA = "";

                  if (this.questionMM.getQFiles() != null && this.questionMM.getQFiles().length > 0) {
                        qImgFileName = this.questionMM.getQFiles()[0] == null ? "" : this.questionMM.getQFiles()[0];
                        if (this.questionMM.getQFiles().length == 2) {
                              gsQ = this.questionMM.getQFiles()[1];
                        }
                  }
                  if (this.answerMM.getAFiles() != null && this.answerMM.getAFiles().length > 0) {
                        aImgFileName = this.answerMM.getAFiles()[0] == null ? "" : this.answerMM.getAFiles()[0];
                        if (this.answerMM.getAFiles().length == 2) {
                              gsA = this.answerMM.getAFiles()[1];
                        }
                  }

                  String otherQImgFileName = "";
                  String otherAImgFileName = "";
                  String gsQother = "";
                  String gsAother = "";
                  if (otherFC.questionMM.getQFiles() != null) {
                        otherQImgFileName = otherFC.questionMM.getQFiles()[0] == null ? "" : otherFC.questionMM.getQFiles()[0];
                        if (otherFC.questionMM.getQFiles().length == 2) {
                              gsQother = otherFC.questionMM.getQFiles()[1];
                        }
                  }
                  if (otherFC.answerMM.getAFiles() != null) {
                        otherAImgFileName = otherFC.answerMM.getAFiles()[0] == null ? "" : otherFC.answerMM.getAFiles()[0];
                        if (otherFC.answerMM.getAFiles().length == 2) {
                              gsAother = otherFC.answerMM.getAFiles()[1];
                        }
                  }

                  // Text area
                  boolean bool1 = this.questionMM.getQText().equals(otherFC.questionMM.getQText());
                  if (!bool1) {
                        return false;
                  }
                  boolean bool2 = this.answerMM.getAText().equals(otherFC.answerMM.getAText());
                  if (!bool2) {
                        return false;
                  }
                  // Shape files
                  boolean bool3 = compareShapeFiles(gsQ, gsQother);
                  if (!bool3) {
                        return false;
                  }
                  boolean bool4 = compareShapeFiles(gsA, gsAother);
                  if (!bool4) {
                        return false;
                  }
                  // image file names. They are named by their images
                  boolean bool5 = qImgFileName.equals(otherQImgFileName);
                  if (!bool5) {
                        return false;
                  }
                  boolean bool6 = aImgFileName.equals(otherAImgFileName);
                  if (!bool6) {
                        return false;
                  }
                  return true;
            }
      }

      /**
       * Compares the ArrayList of shapes contained within the
       * provided shape files.
       *
       * @param thisShapeFileName
       * @param otherShapeFileName
       * @return true if the two are equal by X,Y, Wd, and Ht. or other as defined by
       * that shape class.
       */
      private boolean compareShapeFiles(String thisShapeFileName, String otherShapeFileName) {
            FileOpsShapes fs = new FileOpsShapes();
            boolean thisIsNull = thisShapeFileName == null;
            boolean otherIsNull = otherShapeFileName == null;

            if ( otherIsNull
                    ||  ! thisShapeFileName.equals(otherShapeFileName)) {
                  return false;
            } else if (thisShapeFileName.isEmpty()
                    || thisIsNull && otherIsNull) {
                  return true;
            } else {
                  ArrayList<GenericShape> shapes1 = fs.getListFromFile(thisShapeFileName);
                  ArrayList<GenericShape> shapes2 = fs.getListFromFile(otherShapeFileName);
                  // if sizes do not match, return false.
                  if (shapes1.size() != shapes2.size()) {
                        return false;
                  }
                  // compare the shapes within the files.
                  for (int i = 0; i < shapes1.size(); i++) {
                        // if they are not the same shape class, return false
                        // Should already be done at the shape class equals.
                        boolean bool = shapes1.get(i).getShape().equals(shapes2.get(i).getShape());
                        if (!bool) {
                              return false;
                        }
                  }
                  return true;
            }
      }

      /**
       * Default compareTo compares only the value of cNumber
       * cNumber is the flashList index number that is set when the card is
       * read into the tree from the flashList.
       * As cards are answered , remember is changed
       * and changes the cNumber (Done elsewhere). This compare method is also used in navigation.
       * Thus, if the value changes, it could cause errors in navigation.
       * All other comparisons should be done with a comparator.
       * // @param otherAnswer, the objected being compared to.
       *
       * @return -1 if the other flashCard priority is larger.
       * 0 if the other flashCard priority is equal.
       * 1 if the other flashCard priority is smaller.
       */
      //@SuppressWarnings("unchecked")
      @Override
      public int compareTo(T other) {
            Integer thisValue = 0;
            Integer otherValue = 0;

            // special case, when node is root,
            // parent is null.
            if (other == null) {
                  LOGGER.warn("CompareTo is attempting to compare: this.getCNumber: ({}) with other.cNumber({}) and is a null item", this.getCNumber(), ((FlashCardMM) other).getCNumber());
                  //return -1;
            }
            try {
                  thisValue = this.cNumber;
                  otherValue = ((FlashCardMM) other).cNumber;

            } catch (NullPointerException e) {
                  // do nothing
            }
            return thisValue.compareTo(otherValue);
      }

      public int compare(Object other) {
            FlashCardMM fc = (FlashCardMM) other;
            int num = fc.cNumber;
            if (num < this.cNumber) {
                  return -1;
            } else if (num > this.cNumber) {
                  return 1;
            } else {
                  return 0;
            }
      }

      /**
       * toString()  printed in the treePane
       */
      @Override
      public String toString() {
            return "Q: " + this.cNumber
                + "\n\t CID = " + this.cID
                + "\n\t layout = " + this.cardLayout
                + "\n\t QuestionMM = \n" + this.questionMM
                + "\n\t AnswerMM  = \n" + this.answerMM
                + "\n\t isRight  = " + this.isRightColor
                + "\n\t remember  = " + this.remember
                + "\n\t seconds  = " + this.seconds
                + "\n\t numRight  = " + this.numRight
                + "\n\t rtDate  = " + this.rtDate
                + "\n\t numSeen  = " + this.sessionSeen
                + "\n\t testType  = " + this.testType;
      }
}
