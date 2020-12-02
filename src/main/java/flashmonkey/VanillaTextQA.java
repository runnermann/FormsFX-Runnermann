package flashmonkey;

import fileops.CloudOps;
import fileops.FileNaming;
import type.testtypes.MultiChoice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This is an experimental class. It is not available direcly in FlashMonkey. It uses it's own main method.
 * The VanillaTextQA class parses a .txt file.
 * The questions are contained in {@code <Q> </Q>} and the answers are contained in {@code <A> </A>}
 *<pre>
 * Requirements
 *  - Get the name of the deck from the EncryptedUser.EncryptedUser and create the file
 *  - Get the name of the file that is being parsed
 *  - Ensure the file ends in .txt and does not contain any executable code
 *  - Parse the code
 *      - If there is a {@code <Q>}
 *          - If there is a {@code <I>}
 *              - add the file name for the image
 *              - When the {@code </I>} ,is found, add the following and add it to the String buffer until:
 *          - Add the following characters until a {@code </Q>} is found and save the String as
 *          the question.
 *
 *      - If there is an {@code <A>}
 *          - Add the following characters until a {@code </A>} is found and save the String as
 *          the answer.
 *          - If there is a {@code <I>}
 *  *              - add the file name for the image
 *  *              - When the {@code </I>} ,is found, add the following and add it to the String buffer until:
 *      - After the {@code </A>} is found, save the Question and Answer to a new card using the default
 *      data for the rest of the card.
 *</pre>
 * @author Lowell Stadelman
 */
// @todo add video and audio, add in additional info needed for saving the Multi-Media files
public class VanillaTextQA  {

    private ArrayList<FlashCardMM> creatorList;// = new ArrayList<>(10);
    private FlashCardOps ops;// = new FlashCardOps();
    protected FlashCardOps.FileOperations innerObj;// = ops.new FileOperations();

    StringBuilder stringBuilder;

    private String deckName;
    private String textFileName;
    private String mediaFile;

    private int counter;
    private String aText;
    private String qText;

    /**
     * no args constructor
     */
    public VanillaTextQA() {

        creatorList = new ArrayList<>(10);
        ops = FlashCardOps.getInstance();
        innerObj = ops.new FileOperations();

        stringBuilder = new StringBuilder();
        deckName = "";
        textFileName = "";
        counter = 0;
        aText = "";
        qText = "";
        mediaFile = "";
    }


    /**
     * Constructor: Reads in a file, Parses the file. If words are contained between {@code <Q>} and {@code </Q>}
     * the are inserted into the qText for the card. If words are contained between {@code <A>} and {@code </A>}
     * they are inserted into the aText for the card. Everything outside of the angle braces
     * is ignored.
     *
     * ! Note: This method is not thread safe.
     *
     * For now we are not parsing images or video files.
     * @param deckName
     * @param textFile
     */
    public VanillaTextQA( File deckName, File textFile) {

        this();
        // parse the file
        parseFile(textFile);
        // Save the creatorList to file, keep the last element
        innerObj.setListinFile(creatorList, '+');
        CloudOps co = new CloudOps();
        co.putDeck(ReadFlash.getInstance().getDeckName() + ".dat");
        //innerObj.
    }


    private void parseFile( File textFile) {

        System.out.println("Starting to parse the file");

        // Get the text from the stream.
        try (Scanner scan = new Scanner(new FileInputStream(textFile))) {

            // Read data from a file
            while (scan.hasNext()) {

                System.out.print(" . ");

                // get the next item and check for opening and closing angle braces
                String read = scan.next();
                // get question
                if (read.equals("<q>")) {

                    counter++;

                    while (scan.hasNext()) {
                        read = scan.nextLine();
                        if (read.equals("</q>")) {

                            // set the text in string builder to qText
                            qText = stringBuilder.toString();
                            // clear stringBuilder
                            stringBuilder.setLength(0);
                            // start next
                            break;
                        } else {
                            // add the text to the string builder
                            stringBuilder.append(read);

                        }
                    }
                } else if (read.equals("<a>")) {

                    while (scan.hasNext()) {
                        read = scan.nextLine();
                        if (read.equals("</a>")) {

                            aText = stringBuilder.toString();
                            if(! saveCard()) {
                                System.out.println("Card not saved");
                                System.exit(0);
                            };
                            stringBuilder.setLength(0);
                            break;

                        } else {
                            stringBuilder.append(read + "\n");

                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {

            System.err.println("ERROR retrieving file: Could not find the file: " + textFile);
        }

    }


    /**
     * Helper method to parseFile
     * Creates the card and saves it to the creatorList ArrayList
     */
    private boolean saveCard()
    {
        MultiChoice multiChoice = MultiChoice.getInstance();
        int cardNum = counter;
        FileNaming naming = new FileNaming();
            // Create the new card
            FlashCardMM newCard = new FlashCardMM(
                    naming.getCardHash(ReadFlash.getInstance().getDeckName(), "SHA"),                    // cardId
                    cardNum,                    // cardNumber
                    multiChoice.getTestType(),// BitSet testTypes
                    multiChoice.getCardLayout(),// cardLayout
                    qText,                      // questionText
                    't',                 // question Section Layout 'M'
                    getMediaFiles(),            // String[] of media files
                    aText,                      // answerText
                    cardNum,                    // answerNumber
                    't',                 // answer section layout
                    getMediaFiles(),            // String[] of media files
                    new ArrayList<Integer>()    //Arraylist of other correct answers
            );

        // add the new card to the creatorList
        return (creatorList.add(newCard));
    }

    private String[] getMediaFiles()
    {
        String[] s = {mediaFile, ""};
        return s;
    }

    protected void printCards(File deckName) {

        ArrayList<FlashCardMM> testList;
        testList = innerObj.getListFromFile();

        System.out.println("Printing cards if I was successful");

        for(FlashCardMM mm : testList) {
            System.out.println(mm.getQText());
            System.out.println(mm.getAText());
        }
    }

    /**
     * Tester
     */
/*
    public static void main(String[] args) {

        File deckName = new File("testDeckForAutoCreate");
        File textFileName = new File(FlashCardOps.FOLDER + "/Java_interview_questions.txt");

        System.out.println( "\t\t** ------------------ **" +
                            "\n\t\t*     Starting test    *" +
                            "\n\t\t** ------------------ **");

        VanillaTextQA createQAcards = new VanillaTextQA(deckName, textFileName);

        System.out.println("Attempting to create the cards from: " + textFileName.toString());
        createQAcards.printCards(deckName);

    }
*/



}
