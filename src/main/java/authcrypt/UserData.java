package authcrypt;

//import com.dlsc.formsfx.model.iooily.search.Searchable;
import flashmonkey.FlashCardOps;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Comparator;

import static fileops.FileNaming.hashToHex;

/**
 * non encrypted User data. The authcrypt.user's data is encrypted until it is
 * used. It is used only when neccessary and it is decrypted at that time.
 * Then the clear-text-data is cleared from cache and the registers.
 */
public abstract class UserData {

      // Username is the unique name for this authcrypt.user.
      // Usually their email address that they
      // set at on the first use of this application.
      private static String userName = null;
      // The users common name, usually the first name.
      private static String name = "default_Name";
      private static Image userImage;

      public static String getUserMD5Hash() {
            if (userName.contains("nameNotSet")) {
                  //System.exit(1);
            }
            return hashToHex(userName);
      }

      /**
       * @return Returns the name the user sets
       * as their first name.
       */
      public static String getFirstName() {
            return name;
      }

      /**
       * Sets the users name that they are called.
       * Usually their first name.
       *
       * @param name name
       */
      public static void setFirstName(String name) {
            UserData.name = name;
      }

      /**
       * @return Returns the users unique name, Usually thier
       * email address.
       */
      public static @NotNull String getUserName() {
            return userName != "null" ? userName : null;
      }

      /**
       * Sets the users unique name. Usually their
       * email address. To set their common name, or
       * first name see setName.
       *
       * @param name name
       */
      public static void setUserName(String name) {
            userName = name;
      }


      public Image getUserImage() {
            setUserImage();
            return userImage;
      }

      /**
       * Saves the usersImage
       * to file.
       *
       * @param image Saves user image to file
       */
      public static void saveUserImage(Image image) {
            //FileNaming fileNaming = new FileNaming("notUsed", cID, qOrA, ".png");
            FlashCardOps fco = FlashCardOps.getInstance();
            // Save the user image to the userData directory
            fco.saveImage("userImg.png", image, ".png", 'z');
      }

      /**
       * if userImage is null,
       * sets userImage to the userImage file if it exists,
       * else it sets it to an emoji.
       */
      public void setUserImage() {
            if (userImage == null) {
                  if (!setUserImgFmFileHelper()) {
                        userImage = new Image("icon/myProfile.PNG");
                  }
            }
      }

      private boolean setUserImgFmFileHelper() {
            String imgFileName = "xxxxx";
            File imgFile = new File(imgFileName);
            if (imgFile.exists()) {
                  userImage = new Image(imgFileName);
                  return true;
            }
            return false;
      }

      /**
       * Sets the userImage to the param.
       *
       * @param userImage The users image
       */
      public void setUserImage(Image userImage) {
            UserData.userImage = userImage;
      }

      public static void clear() {
            userImage = new Image("image/flashFaces_sunglasses_60.png");
            userName = "nameNotSet@flashmonkey.xyz";
            //name = "default_Name";
      }

//      /**
//       * @return Returns the text
//       */
//      @Override
//      public String getText() {
//            return name;
//      }
//
//      /**
//       * Compares this object with the specified object for order.  Returns a
//       * negative integer, zero, or a positive integer as this object is less
//       * than, equal to, or greater than the specified object.
//       *
//       * <p>The implementor must ensure
//       * {@code sgn(x.compareTo(y)) == -sgn(y.compareTo(x))}
//       * for all {@code x} and {@code y}.  (This
//       * implies that {@code x.compareTo(y)} must throw an exception iff
//       * {@code y.compareTo(x)} throws an exception.)
//       *
//       * <p>The implementor must also ensure that the relation is transitive:
//       * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
//       * {@code x.compareTo(z) > 0}.
//       *
//       * <p>Finally, the implementor must ensure that {@code x.compareTo(y)==0}
//       * implies that {@code sgn(x.compareTo(z)) == sgn(y.compareTo(z))}, for
//       * all {@code z}.
//       *
//       * <p>It is strongly recommended, but <i>not</i> strictly required that
//       * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
//       * class that implements the {@code Comparable} interface and violates
//       * this condition should clearly indicate this fact.  The recommended
//       * language is "Note: this class has a natural ordering that is
//       * inconsistent with equals."
//       *
//       * <p>In the foregoing description, the notation
//       * {@code sgn(}<i>expression</i>{@code )} designates the mathematical
//       * <i>signum</i> function, which is defined to return one of {@code -1},
//       * {@code 0}, or {@code 1} according to whether the value of
//       * <i>expression</i> is negative, zero, or positive, respectively.
//       *
//       * @param o the object to be compared.
//       * @return a negative integer, zero, or a positive integer as this object
//       * is less than, equal to, or greater than the specified object.
//       * @throws NullPointerException if the specified object is null
//       * @throws ClassCastException   if the specified object's type prevents it
//       *                              from being compared to this object.
//       */
//      @Override
//      public int compareTo(@NotNull Searchable o) {
//            return 0;
//      }
//
//      /**
//       * <p>Compares its two arguments for order.  Returns a negative integer,
//       * zero, or a positive integer as the first argument is less than, equal
//       * to, or greater than the second.</p>
//       * <p>
//       * The implementor must ensure that {@code sgn(compare(x, y)) ==
//       * -sgn(compare(y, x))} for all {@code x} and {@code y}.  (This
//       * implies that {@code compare(x, y)} must throw an exception if and only
//       * if {@code compare(y, x)} throws an exception.)</p>
//       * <p>
//       * The implementor must also ensure that the relation is transitive:
//       * {@code ((compare(x, y)>0) && (compare(y, z)>0))} implies
//       * {@code compare(x, z)>0}.</p>
//       * <p>
//       * Finally, the implementor must ensure that {@code compare(x, y)==0}
//       * implies that {@code sgn(compare(x, z))==sgn(compare(y, z))} for all
//       * {@code z}.</p>
//       * <p>
//       * It is generally the case, but <i>not</i> strictly required that
//       * {@code (compare(x, y)==0) == (x.equals(y))}.  Generally speaking,
//       * any comparator that violates this condition should clearly indicate
//       * this fact.  The recommended language is "Note: this comparator
//       * imposes orderings that are inconsistent with equals."</p>
//       * <p>
//       * In the foregoing description, the notation
//       * {@code sgn(}<i>expression</i>{@code )} designates the mathematical
//       * <i>signum</i> function, which is defined to return one of {@code -1},
//       * {@code 0}, or {@code 1} according to whether the value of
//       * <i>expression</i> is negative, zero, or positive, respectively.</p>
//       *
//       * @param o1 the first object to be compared.
//       * @param o2 the second object to be compared.
//       * @return a negative integer, zero, or a positive integer as the
//       * first argument is less than, equal to, or greater than the
//       * second.
//       * @throws NullPointerException if an argument is null and this
//       *                              comparator does not permit null arguments
//       * @throws ClassCastException   if the arguments' types prevent them from
//       *                              being compared by this comparator.
//       */
//      @Override
//      public int compare(Searchable o1, Searchable o2) {
//            return 0;
//      }
}
