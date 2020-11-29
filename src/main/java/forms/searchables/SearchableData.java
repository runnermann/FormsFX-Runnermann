package forms.searchables;

import com.dlsc.formsfx.model.iooily.search.Searchable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Use this class for the majority of searches that do not require
 * special objects such as SchoolObj. IE when the implementation is for
 * Strings and outputs Strings.
 */
public class SearchableData extends Searchable {
	
	ArrayList<String> stringData;
	
	public SearchableData(ArrayList<String> dataAry) {
		stringData = new ArrayList<>(dataAry.size());
		dataAry.stream()
				.forEach(stringData::add);
	}
	
	public ArrayList<String> getData() {
		System.out.println("Calling SearchableData.getData()");
		return stringData;
	}
	
	@Override
	public String getText() {
		System.out.println("Calling SearchableData.getText()");
		
		StringBuilder sb = new StringBuilder();
		if(stringData.isEmpty()) {
			return null;
		} else {
			for(String s : stringData) {
				sb.append(s + " ");
			}
		}
		System.out.println("returns: " + sb.toString());
		return sb.toString().trim();
	}
	
	/**
	 * Compares this object with the specified object for order.  Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 *
	 * <p>The implementor must ensure
	 * {@code sgn(x.compareTo(y)) == -sgn(y.compareTo(x))}
	 * for all {@code x} and {@code y}.  (This
	 * implies that {@code x.compareTo(y)} must throw an exception iff
	 * {@code y.compareTo(x)} throws an exception.)
	 *
	 * <p>The implementor must also ensure that the relation is transitive:
	 * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
	 * {@code x.compareTo(z) > 0}.
	 *
	 * <p>Finally, the implementor must ensure that {@code x.compareTo(y)==0}
	 * implies that {@code sgn(x.compareTo(z)) == sgn(y.compareTo(z))}, for
	 * all {@code z}.
	 *
	 * <p>It is strongly recommended, but <i>not</i> strictly required that
	 * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
	 * class that implements the {@code Comparable} interface and violates
	 * this condition should clearly indicate this fact.  The recommended
	 * language is "Note: this class has a natural ordering that is
	 * inconsistent with equals."
	 *
	 * <p>In the foregoing description, the notation
	 * {@code sgn(}<i>expression</i>{@code )} designates the mathematical
	 * <i>signum</i> function, which is defined to return one of {@code -1},
	 * {@code 0}, or {@code 1} according to whether the value of
	 * <i>expression</i> is negative, zero, or positive, respectively.
	 *
	 * @param o the object to be compared.
	 * @return a negative integer, zero, or a positive integer as this object
	 * is less than, equal to, or greater than the specified object.
	 * @throws NullPointerException if the specified object is null
	 * @throws ClassCastException   if the specified object's type prevents it
	 *                              from being compared to this object.
	 */
	@Override
	public int compareTo(@NotNull Searchable o) {
		return this.getText().compareTo(o.getText());
	}
	
	/**
	 * Compares its two arguments for order.  Returns a negative integer,
	 * zero, or a positive integer as the first argument is less than, equal
	 * to, or greater than the second.<p>
	 * <p>
	 * The implementor must ensure that {@code sgn(compare(x, y)) ==
	 * -sgn(compare(y, x))} for all {@code x} and {@code y}.  (This
	 * implies that {@code compare(x, y)} must throw an exception if and only
	 * if {@code compare(y, x)} throws an exception.)<p>
	 * <p>
	 * The implementor must also ensure that the relation is transitive:
	 * {@code ((compare(x, y)>0) && (compare(y, z)>0))} implies
	 * {@code compare(x, z)>0}.<p>
	 * <p>
	 * Finally, the implementor must ensure that {@code compare(x, y)==0}
	 * implies that {@code sgn(compare(x, z))==sgn(compare(y, z))} for all
	 * {@code z}.<p>
	 * <p>
	 * It is generally the case, but <i>not</i> strictly required that
	 * {@code (compare(x, y)==0) == (x.equals(y))}.  Generally speaking,
	 * any comparator that violates this condition should clearly indicate
	 * this fact.  The recommended language is "Note: this comparator
	 * imposes orderings that are inconsistent with equals."<p>
	 * <p>
	 * In the foregoing description, the notation
	 * {@code sgn(}<i>expression</i>{@code )} designates the mathematical
	 * <i>signum</i> function, which is defined to return one of {@code -1},
	 * {@code 0}, or {@code 1} according to whether the value of
	 * <i>expression</i> is negative, zero, or positive, respectively.
	 *
	 * @param o1 the first object to be compared.
	 * @param o2 the second object to be compared.
	 * @return a negative integer, zero, or a positive integer as the
	 * first argument is less than, equal to, or greater than the
	 * second.
	 * @throws NullPointerException if an argument is null and this
	 *                              comparator does not permit null arguments
	 * @throws ClassCastException   if the arguments' types prevent them from
	 *                              being compared by this comparator.
	 */
	@Override
	public int compare(Searchable o1, Searchable o2) {
		return o1.getText().compareTo(o2.getText());
	}
}
