package ecosystem;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import fileops.DirectoryMgr;
import fileops.FileNaming;
import flashmonkey.FlashCardOps;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

import static fileops.FileNaming.hashToHex;

public class QrCode {

    public QrCode() throws WriterException, IOException {
        /* no args constructor */
    }


    public static Path buildDeckQrCode(long deck_id, String folder, String deckFileName, String userName) throws WriterException, IOException {
        // Where we will store the QR image.
        String pathName = folder + "/" + deckFileName.substring(0, deckFileName.length() - 4) + ".png";

        // The distributor's version of the deck
        String fmVertx = "https://www.flashmonkey.xyz/Q52/FFG415/:" ;
        String vertxGet = fmVertx + createDeckFetchString(deck_id, FlashCardOps.getInstance().getDeckLabelName(), userName);

        // generate QR code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(vertxGet, BarcodeFormat.QR_CODE, 200, 200);

        // write to file
        Path path = Paths.get(pathName);
        File f = new File(pathName);
        if(!f.exists()) {
            f.mkdirs();
        }
        MatrixToImageWriter.writeToPath(bitMatrix, "png", path);
        return path;
    }

    /**
     * Currently we are using the deck_id to retrieve this deck
     * when a QR code is used. The deck_id and userName unencrypted are
     * hashed and may be used to verify data later. Thus preventing
     * the payee from being manipulated.
     * @param deck_id
     * @param deckName
     * @param thisUserName
     * @return Returns the following string deck_id-md5Hash-predicessorHash
     */
    private static String createDeckFetchString(Long deck_id, String deckName, String thisUserName) {
        String md5HashString = hashToHex(thisUserName + deck_id);
        return deck_id + "";// + "-" + md5HashString;
    }

    public static Path buildPredicessorQRCode(int id, String userEmail, String folder) throws WriterException, IOException {
        String hex = FileNaming.hashToHex(userEmail);
        // Where we will store the QR image.
        String pathName = folder + "/" + id + "-" + hex + ".png";

        // The distributor's version of the deck
        String fmVertx = "https://www.flashmonkey.xyz/Q52/A017BA/:" + id +"-" + hex;

        // generate QR code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(fmVertx, BarcodeFormat.QR_CODE, 300, 300);

        // write to file
        Path path = Paths.get(pathName);
        File f = new File(pathName);
        if(!f.exists()) {
            f.mkdirs();
        }

        MatrixToImageWriter.writeToPath(bitMatrix, "png", path);
        return path;
    }

    /**
     * Saves an image of a QR code at the path provided in the return.
     * @param cKeyID The id of the Campaign
     * @param userEmail
     * @param folder
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static Path buildPredicessorWithImage(int cKeyID, String userEmail, String folder) throws WriterException, IOException {

        String logoPath = "image/logo/blue_flash_bkgd_128.png";

        String hex = FileNaming.hashToHex(userEmail);
        // Where we will store the QR image.
        // String pathName = folder + "/" + id + "-" + hex + ".png";
        String pathName = folder + "/" + "facebook" + ".png";

        // The distributor's version of the deck
        String index = "https://www.flashmonkey.xyz";
        String fmVertx = index + "/Q52/A017BA/:" + cKeyID +"-" + hex;
        String instagram = "https://www.instagram.com/flashmonkey.get_smart/";
        String facebook = "https://www.facebook.com/FlashMonkey-399314090825681/";

        // generate QR code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        //BitMatrix bitMatrix = qrCodeWriter.encode(fmVertx, BarcodeFormat.QR_CODE, 400, 400);
        BitMatrix bitMatrix = qrCodeWriter.encode(facebook, BarcodeFormat.QR_CODE, 400, 400);

        MatrixToImageConfig conf = new MatrixToImageConfig(0x22276FFF,-15883559); // orange = -761561
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, conf);
        //InputStream is = QrCode.class.getClassLoader().getResourceAsStream(logoPath);


        Image overlay = new Image(QrCode.class.getClassLoader().getResourceAsStream(logoPath), 64, 64, true, true);
        BufferedImage overlayImg = SwingFXUtils.fromFXImage(overlay, null);

        int size = qrImage.getWidth();
        int xy = (size - overlayImg.getHeight()) / 2;

        BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) combined.getGraphics();

        g.drawImage(qrImage, 10, 0, null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        //g.drawImage(overlayImg, xy + 14, xy + 6, null);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        Path path = Paths.get(pathName);
        ImageIO.write(combined, "png", outStream);
        Files.copy(new ByteArrayInputStream(outStream.toByteArray()), path, StandardCopyOption.REPLACE_EXISTING);

        return path;
    }


    public static void main(String[] args) throws WriterException, IOException {
        String dir = DirectoryMgr.getMediaPath('q');
        //Path path = buildPredicessorWithImage(1, "flash@flashmonkey.xyz", dir);
        Path path = buildPredicessorQRCode(15, "rugbyuf@gmail.com", dir);

        System.out.println(path.toString());
    }
}


