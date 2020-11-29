package s3;

import fileops.CloudOps;
import javafx.scene.image.Image;
import org.junit.Test;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@Tag("popup")
public class MediaTransferTester extends ApplicationTest {

    private final static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(MediaTransferTester.class);

    CloudOps clops = new CloudOps();
/*
    @Test
    @Order(2)
    public void checkImageDownloadtoS3_thumbs() {
        //clops.setBucketName("iooily.flashmonkey.deck-thumbs");

        ArrayList<String> names = new ArrayList<>(4);
        names.add("a823f34bac.png");
        names.add("img1.png");
        names.add("img2.png");
        names.add("img3.png");
        ArrayList<Image> imgs = clops.getMediaFmS3(names);

        assertTrue(imgs.get(0) != null);
        System.out.println("imgs size = " + imgs.size());
        assertTrue(imgs.size() == 4);
    }

    @Test
    @Order(1)
    public void checkImageUploadtos3_thumbs() {
        CloudOps clops = new CloudOps();
        //clops.setBucketName("iooily.flashmonkey.deck-thumbs");
        ArrayList<String> imgNames = new ArrayList<>(5);
        // imgNames.add("history2054.png");
        String name = "a823f34bac.png";

        clops.connectCloudOut('d',"", name);

        imgNames.add(name);
        ArrayList<Image> imgs = clops.getMediaFmS3(imgNames);
        assertTrue("if failed, comment out seperate thread in connectCloudOut()",(imgs.get(0)) != null);
    }

 */

}
