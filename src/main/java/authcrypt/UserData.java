package authcrypt;

//import com.dlsc.formsfx.model.iooily.search.Searchable;
import flashmonkey.FlashCardOps;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.io.File;

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
      private static String name = "default_app_Name";
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
            userName = "nameNotSet@flashmonkey.co";
            //name = "default_Name";
      }
}
