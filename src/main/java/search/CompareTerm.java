package search;

/*******************************************************************************
 * CLASS DESCRIPTION: This is the CompareTerm class ....
 ******************************************************************************/

/*** IMPORTS ***/

import search.Search;

import java.util.Comparator;

/**
 * Used to compare two terms for equality
 *
 * @author Lowell Stadelman
 */
public class CompareTerm implements Comparator<Search.Term> {
      @Override
      public int compare(Search.Term term1, Search.Term term2) {
            return term1.getWord().compareToIgnoreCase(term2.getWord());
      }
}
