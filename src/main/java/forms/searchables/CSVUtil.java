package forms.searchables;

import flashmonkey.FlashMonkeyMain;
import com.dlsc.formsfx.model.iooily.search.*;
//import org.jetbrains.annotations.NotNull;


import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Auxillery Class used to create Serilized Files. Serilized Files by design need to be used
 * by the same program that creates them. Thus this Auxillery Class exists within FlashMonkey.
 * <p>
 * Class is used to parse the CSVFiles from EDU.GOV list of Higher education entities and output a reduced
 * list converted to an ArrayList of SchoolObjs and set them in a file. Run On sentences are alright?
 *
 * The file that is downloaded from the Edu.gov website is a CSV file. We add institutions to the front of the list.
 * by editing them in a text editor such as IntelliJ. Do not use MS Excel. THe files will not parse.
 */
public class CSVUtil {


      private static final String SEPARATOR = "|";
      private static final char DEFAULT_QUOTE = '"';
      private static ArrayList<String> result = new ArrayList<>();
      // Descriminate if header file
      private static boolean notHeader = false;
	
//	public static void main(String[] args) {
//
//		String csvIN = "src/main/resources/InstitutionCampus.csv";
//		String csvOUT = "src/main/resources/processedCampus.dat";
//
//		try {
//			parseFile(csvIN, csvOUT);
//		} catch (FileNotFoundException e) {
//			e.getMessage();
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}


      /**
       * Parses the csv file based on a search for a CSV seperator and commas that are within a CSV cell.
       * Additionally, specifically for parsing out unneeded and confusing univeristy listings, such as ASU
       * where there are over 20 campuses. Thus according to the list provided by the department of education in 2020?
       * we remove the extra listings by checking that the third column is not empty.
       * @param csvIn
       * @param csvOut
       * @throws Exception
       */
//      public static void parseFile(String csvIn, String csvOut) throws Exception {
//            File file = new File(csvOut);
//            file.createNewFile();
//
//            ArrayList<SchoolObj> schoolList = new ArrayList<>();
//
//            try (BufferedReader br = new BufferedReader(new FileReader(csvIn)); ObjectOutputStream objOut = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(csvOut)))) {
//                  List<String> cellList;
//                  String line;
//                  int count = 0;
//                  // The send object
//                  //DataOutputStream dos = CsvOutput.dos;
//
//                  while ((line = br.readLine()) != null) {
//                        System.out.println("Count: " + count++);
//                        cellList = parseLine(line);
//                        if (notHeader) {
//                              // Check that the third colum is not empty.
//                              // Third column contains the Ipeds Unit ID
//                              // and is not a child location.
//                              if(cellList.get(2).length() > 3) {
//                                    String state = cellList.get(7);
//                                    int num = state.lastIndexOf(",");
//                                    state = state.substring(num + 2, num + 4);
//                                    schoolList.add(new SchoolObj(cellList.get(3), cellList.get(7), state));
//                              }
//                        }
//                  }
//                  //write the ArrayList to file
//                  objOut.writeObject(schoolList);
//            }
//      }


      /**
       * Parses the csv file finding the cell dividers
       * and does not have issues with comma's or extra
       * double parans in the middle of the cell.
       *
       * @param csvLine
       * @return
       */
//      public static List<String> parseLine(String csvLine) {
//
//            notHeader = false;
//
//            StringBuilder cell = new StringBuilder();
//            result = new ArrayList<>();
//
//
//            char one = ' ';
//            char two = ' ';
//            char three = ' ';
//
//            char[] chars = csvLine.toCharArray();
//            for (char ch : chars) {
//                  // write char to cell
//                  cell.append(ch);
//
//                  three = two;
//                  two = one;
//                  one = ch;
//
//                  // if matches a cell divider
//                  if (one == '\"' & two == ',' & three == '\"') {
//                        // indicate it's not a header
//                        notHeader = true;
//                        // Remove last three chars
//                        // and add sb to array.
//                        result.add(cell.substring(0, cell.length() - 3));
//                        // create a new buffer
//                        cell = new StringBuilder();
//                  }
//            }
//            return result;
//      }

      /**
       * School object contains the school name, address, and state.
       * The SchoolObj is Searchable
       */
      public static class SchoolObj extends Searchable implements Serializable, Externalizable {

            public static final long VERSION = FlashMonkeyMain.VERSION;

            public SchoolObj() {
                  /* no args constructor */
            }

            String name;
            String address;
            String state;

            public SchoolObj(String name, String addr, String state) {
                  this.name = name;
                  this.address = addr;
                  this.state = state;
            }

            public String getName() {
                  return name;
            }

            public void setName(String name) {
                  this.name = name;
            }

            public String getAddress() {
                  return address;
            }

            public void setAddress(String address) {
                  this.address = address;
            }

            public String getState() {
                  return state;
            }

            @Override
            public String getText() {
                  return name;
            }

            public void setState(String state) {
                  this.state = state;
            }

            /**
             * The object implements the writeExternal method to save its contents
             * by calling the methods of DataOutput for its primitive values or
             * calling the writeObject method of ObjectOutput for objects, strings,
             * and arrays.
             *
             * @param out the stream to write the object to
             * @throws IOException Includes any I/O exceptions that may occur
             * @serialData Overriding methods should use this tag to describe
             * the data layout of this Externalizable object.
             * List the sequence of element types and, if possible,
             * relate the element to a public/protected field and/or
             * method of this Externalizable class.
             */
            @Override
            public void writeExternal(ObjectOutput out) throws IOException {

                  out.writeUTF(name);
                  out.writeUTF(address);
                  out.writeUTF(state);
            }

            /**
             * The object implements the readExternal method to restore its
             * contents by calling the methods of DataInput for primitive
             * types and readObject for objects, strings and arrays.  The
             * readExternal method must read the values in the same sequence
             * and with the same types as were written by writeExternal.
             *
             * @param in the stream to read data from in order to restore the object
             * @throws IOException            if I/O errors occur
             * @throws ClassNotFoundException If the class for an object being
             *                                restored cannot be found.
             */
            @Override
            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
                  name = in.readUTF();
                  address = in.readUTF();
                  state = in.readUTF();
            }

            @Override
            public boolean equals(Object other) {
                  if (other == null) {
                        return false;
                  } else if (this.getClass() != other.getClass()) {
                        return false;
                  } else {
                        SchoolObj otherObj = (SchoolObj) other;

                        boolean bool1 = this.name.equalsIgnoreCase(otherObj.name);
                        boolean bool2 = this.address.equalsIgnoreCase(otherObj.address);
                        boolean bool3 = this.state.equalsIgnoreCase(otherObj.state);
                        return (bool1 & bool2 & bool3);
                  }
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
            public int compareTo(Searchable o) {
                  // getText returns the name.
                  return this.name.compareTo(o.getText());
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
                  //SchoolObj otherObj = (SchoolObj) o2;
                  return o1.getText().compareTo(o2.getText());
			/*
			if(this.name.hashCode() < otherObj.name.hashCode()) {
				return -1;
			} else if(this.name.hashCode() == otherObj.name.hashCode()) {
				return 0;
			} else {
				return 1;
			}
			
			 */
            }
      }
}
