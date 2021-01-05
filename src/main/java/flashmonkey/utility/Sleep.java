package flashmonkey.utility;


import fileops.MediaSync;
import flashmonkey.FlashCardOps;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.InputEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Sleep {

    private static final ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Sleep.class);
    // Delay in minutes
    private static final int delay = 10;

    /**
     * This method is not guaranteed, After the delay period has expired, this method will
     * Save the deck locally to file, and send to S3.
     * @param scene
     * @param stage
     */
    public static void storeOnDetected(Scene scene, Stage stage) {
        LOGGER.debug("OS Sleep detected");
        PublishSubject<InputEvent> sceneEventPublishable = PublishSubject.create();
        PublishSubject<WindowEvent> windowEventPublishable = PublishSubject.create();

        scene.addEventFilter(InputEvent.ANY, sceneEventPublishable::onNext);
        stage.addEventFilter(WindowEvent.ANY, windowEventPublishable::onNext);

        Observable.merge(sceneEventPublishable, windowEventPublishable)
                .switchMap(event -> Observable.just(event).delay(delay, TimeUnit.MINUTES, Schedulers.single()))
                .subscribe(e -> {
                    // save data to cloud if connected
                    FlashCardOps.getInstance().saveFlashList(); //saveFListToFile();
                    //new Thread(() -> {
                    //    LOGGER.info("Calling syncMedia from CreateFlash");
                    //    MediaSync.syncMedia();
                    //}).start();
                    // set window to login?
                    //Platform.exit();
                });
    }

    public static void requireLogin(Scene scene, Stage stage) {
        PublishSubject<InputEvent> sceneEventPublishable = PublishSubject.create();
        PublishSubject<WindowEvent> windowEventPublishable = PublishSubject.create();

        scene.addEventFilter(InputEvent.ANY, sceneEventPublishable::onNext);
        stage.addEventFilter(WindowEvent.ANY, windowEventPublishable::onNext);

        Observable.merge(sceneEventPublishable, windowEventPublishable)
                .switchMap(event -> Observable.just(event).delay(delay, TimeUnit.MINUTES, Schedulers.single()))
                .subscribe(e -> {
                    // save data to cloud if connected
                    // FlashCardOps.getInstance().saveFlashList(); //saveFListToFile();
                    //new Thread(() -> {
                    //    LOGGER.info("Calling syncMedia from CreateFlash");
                    //    MediaSync.syncMedia();
                    //}).start();
                    // set window to login?
                    Platform.exit();
                });
    }
}
