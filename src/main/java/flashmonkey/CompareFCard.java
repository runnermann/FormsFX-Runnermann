/*
 * AUTHOR: Lowell Stadelman
 */

package flashmonkey;

/*** IMPORTS ***/
//import avltree.*;
import fmtree.FMTree.Node;
import java.util.Comparator;

/**
 * Used to compare two FlashCards for equality. The FCardCompare uses a 
 * flashCards QNumber to set it's value. The QNumber is used to set the overall
 * priority of a card and when it will be shown in the flash deck. 
 * @author Lowell Stadelman
 */
public class CompareFCard<E> implements Comparator<FlashCardMM>
{
    @Override
    public int compare(FlashCardMM card1, FlashCardMM card2)
    {
        return card1.getCNumber() - card2.getCNumber();
    }
    
    /**
     * Compares node1 cNumber with node2 cNumber.
     * If card 1 QNumber minus card 2 QNumber is less t
     * @param node1 of type {@link FlashCardMM}
     * @param node2 of type {@link FlashCardMM}
     * @return returns Qnumber of card 1 minus QNumber of card 2. 
     * ie this card is QNumber value 80 and its parent is 60. It returns
     * a negitive value otherwise it is 0(should not happen)
     * or it is positive and above 0. 
     */
    public int compare(Node<FlashCardMM> node1, Node<FlashCardMM> node2)
    {

        int num = 0;
        try
        {
            num = node1.getData().getCNumber() - node2.getData().getCNumber();
        } catch (NullPointerException e) {
           //System.out.println("null pointer exception in compareFCard class");
        }
        return num;
    }
}
