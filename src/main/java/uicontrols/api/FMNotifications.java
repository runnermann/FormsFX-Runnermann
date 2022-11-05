/**
 * This Class is modified by Lowell Stadelman on 2020-06-05.
 * - The following modifications are made.
 * - Allow flexability for styles and fonts.
 * No claims of warranty from derived software are made.
 * <p>
 * Copyright (c) 2014, 2019, ControlsFX
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package uicontrols.api;

//import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
//import javafx.animation.KeyFrame;
//import javafx.animation.KeyValue;
//import javafx.animation.ParallelTransition;
//import javafx.animation.Timeline;
//import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
//import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.*;
import javafx.util.Duration;

import org.controlsfx.control.action.Action;
import org.controlsfx.tools.Utils;
//import impl.org.controlsfx.skin.NotificationBar;

/**
 * NOTE: Call FXNotify, Don't use this class directly!
 *
 *
 * An API to show popup notification messages to the user in the corner of their
 * screen, unlike the ControlsFX NotificationPane which shows notification messages
 * within your application itself.
 *
 * Screenshot
 * <p>
 * The following screenshot shows a sample notification rising from the
 * bottom-right corner of my screen:
 *</p>
 * <br>
 * <br>
 * <img src="notifications.png" alt="Screenshot of Notifications">
 *
 * Code Example:
 * <p>
 * To create the notification shown in the screenshot, simply do the following:
 *</p>
 * <pre>
 * {@code
 * Notifications.create()
 *              .title("Title Text")
 *              .text("Hello World 0!")
 *              .showWarning();
 * }
 * </pre>
 *
 * <p>When there are too many notifications on the screen, one can opt to collapse
 * the notifications into a single notification using {@code Notifications#threshold(int, Notifications)}.</p>
 * <pre>
 * Notifications.create()
 *              .title("Title Text")
 *              .text("Hello World 0!")
 *              .threshold(3, Notifications.create().title("Collapsed Notification"))
 *              .showWarning();
 * </pre>
 *
 */
public class FMNotifications {

      /***************************************************************************
       * * Static fields * *
       **************************************************************************/

      private static final String STYLE_CLASS_DARK = "dark"; //$NON-NLS-1$
      private static final String STYLE_CLASS_PURPLE = "purple";
      private static final String STYLE_CLASS_BLUE = "blue";
      private static final String STYLE_CLASS_WARNING = "warning";

      /***************************************************************************
       * * Private fields * *
       **************************************************************************/

      private String title;
      private String text;
      private Node graphic;
      private ObservableList<Action> actions = FXCollections.observableArrayList();
      private Pos position = Pos.BOTTOM_RIGHT;
      private Duration hideAfterDuration = Duration.seconds(5);
      private boolean hideCloseButton;
      private EventHandler<ActionEvent> onAction;
      private Window owner;
      private Screen screen = null;

      private final List<String> styleClass = new ArrayList<>();
      private int threshold;
      private FMNotifications thresholdNotification;

      /***************************************************************************
       * * Constructors * *
       **************************************************************************/

      // we do not allow instantiation of the FMNotifcations class directly - users
      // must go via the builder API (that is, calling create())
      private FMNotifications() {
            // no-op
      }

      /***************************************************************************
       * * Public API * *
       **************************************************************************/

      /**
       * Call this to begin the process of building a notification to show.
       * @return returns an FMNotifications
       */
      public static FMNotifications create() {
            return new FMNotifications();
      }

      /**
       * Specify the text to show in the notification.
       * @param text ..
       * @return returns an FMNotifications
       */
      public FMNotifications text(String text) {
            this.text = text;
            return this;
      }

      /**
       * Specify the title to show in the notification.
       * @param title ..
       * @return this FMNotification
       */
      public FMNotifications title(String title) {
            this.title = title;
            return this;
      }

      /**
       * Specify the graphic to show in the notification.
       * @param graphic The graphic for this notification
       * @return This
       */
      public FMNotifications graphic(Node graphic) {
            this.graphic = graphic;
            return this;
      }

      /**
       * Specify the position of the notification on screen, by default it is
       * {@link Pos#BOTTOM_RIGHT bottom-right}.
       * @param position ..
       * @return this
       */
      public FMNotifications position(Pos position) {
            this.position = position;
            return this;
      }

      public FMNotifications padding(int num) {
            NotificationPopupHandler.padding = num;
            return this;
      }

      /**
       * The dialog window owner - which can be {@link Screen}, {@link Window}
       * or {@link Node}. If specified, the FMNotifcations will be inside
       * the owner, otherwise the FMNotifcations will be shown within the whole
       * primary (default) screen.
       * @param owner The owner object
       * @return this
       */
      public FMNotifications owner(Object owner) {
            if (owner instanceof Screen) {
                  this.screen = (Screen) owner;
            } else {
                  this.owner = Utils.getWindow(owner);
            }
            return this;
      }

      /**
       * Specify the duration that the notification should show, after which it
       * will be hidden.
       * @param duration ..
       * @return this
       */
      public FMNotifications hideAfter(Duration duration) {
            this.hideAfterDuration = duration;
            return this;
      }

      /**
       * Specify what to do when the user clicks on the notification (in addition
       * to the notification hiding, which happens whenever the notification is
       * clicked on).
       * @param onAction EventHandler
       * @return this
       */
      public FMNotifications onAction(EventHandler<ActionEvent> onAction) {
            this.onAction = onAction;
            return this;
      }

      /**
       * Specify that the notification should use the built-in dark styling,
       * rather than the default 'modena' notification style (which is a
       * light-gray).
       * @return this
       */
      public FMNotifications darkStyle() {
            styleClass.add(STYLE_CLASS_DARK);
            return this;
      }

      /**
       * Specify that the notification should use the built-in purple styling,
       * rather than the default 'modena' notification style (which is a
       * light-gray).
       * @return this
       */
      public FMNotifications purpleStyle() {
            styleClass.add(STYLE_CLASS_PURPLE);
            return this;
      }

      /**
       * Specify that the notification should use the built-in blue styling,
       * rather than the default 'modena' notification style (which is a
       * light-gray).
       * @return this
       */
      public FMNotifications blueStyle() {
            styleClass.add(STYLE_CLASS_BLUE);
            return this;
      }

      /**
       * Specify that the notification should use the built-in warning styling,
       * rather than the default 'modena' notification style (which is a
       * light-gray).
       * @return this
       */
      public FMNotifications warningStyle() {
            styleClass.add(STYLE_CLASS_WARNING);
            return this;
      }

      /**
       * Specify that the close button in the top-right corner of the notification
       * should not be shown.
       * @return this
       */
      public FMNotifications hideCloseButton() {
            this.hideCloseButton = true;
            return this;
      }

      /**
       * Specify the actions that should be shown in the notification as buttons.
       * @param actions an array or single action
       * @return this
       */
      public FMNotifications action(Action... actions) {
            this.actions = actions == null ? FXCollections.observableArrayList() : FXCollections
                .observableArrayList(actions);
            return this;
      }

      /**
       * Collapses all the current FMNotifcations into a single notification when the
       * number of FMNotifcations exceed the threshold limit. A value of zero will disable
       * the threshold behavior.
       *
       * @param threshold The number of FMNotifcations to show before they can be collapsed
       *                 into a single notification.
       * @param thresholdNotification The {@link FMNotifications notification} to show when
       *                              threshold is reached.
       * @return this
       */
      public FMNotifications threshold(int threshold, FMNotifications thresholdNotification) {
            this.threshold = threshold;
            this.thresholdNotification = thresholdNotification;
            return this;
      }

      /**
       * Instructs the notification to be shown, and that it should use the
       * built-in 'warning' graphic.
       */
      public void showWarning() {
            graphic(new ImageView(FMNotifications.class.getResource("/org/controlsfx/dialog/dialog-warning.png").toExternalForm())); //$NON-NLS-1$
            show();
      }

      /**
       * Instructs the notification to be shown, and that it should use the
       * built-in 'information' graphic.
       */
      public void showInformation() {
            graphic(new ImageView(FMNotifications.class.getResource("/org/controlsfx/dialog/dialog-information.png").toExternalForm())); //$NON-NLS-1$
            show();
      }

      /**
       * Instructs the notification to be shown, and that it should use the
       * built-in 'error' graphic.
       */
      public void showError() {
            graphic(new ImageView(FMNotifications.class.getResource("/org/controlsfx/dialog/dialog-error.png").toExternalForm())); //$NON-NLS-1$
            show();
      }

      /**
       * Instructs the notification to be shown, and that it should use the
       * built-in 'confirm' graphic.
       */
      public void showConfirm() {
            graphic(new ImageView(FMNotifications.class.getResource("/org/controlsfx/dialog/dialog-confirm.png").toExternalForm())); //$NON-NLS-1$
            show();
      }

      /**
       * Instructs the notification to be shown.
       */
      public void show() {
            NotificationPopupHandler.getInstance().show(this);
      }

      public boolean isShowing() {
            return NotificationPopupHandler.getInstance().isShowing;
      }

      /***************************************************************************
       * * Private support classes * *
       **************************************************************************/

      // not public so no need for JavaDoc

      private static final class NotificationPopupHandler {

            private static final NotificationPopupHandler INSTANCE = new NotificationPopupHandler();
            private static final String FINAL_ANCHOR_Y = "finalAnchorY";

            private double startX;
            private double startY;
            private double screenWidth;
            private double screenHeight;

            static final NotificationPopupHandler getInstance() {
                  return INSTANCE;
            }

            private final Map<Pos, List<Popup>> popupsMap = new HashMap<>();
            private static double padding = 15;
            private static final double SPACING = 15;

            // for animating in the FMNotifcations
//            private final ParallelTransition parallelTransition = new ParallelTransition();

            private boolean isShowing = false;

            public void show(FMNotifications notification) {
                  Window window;
                  if (notification.owner == null) {
                        /*
                         * If the owner is not set, we work with the whole screen.
                         */
                        window = Utils.getWindow(null);
                        Screen screen = notification.screen != null
                            ? notification.screen
                            : getScreenBounds(window).orElse(Screen.getPrimary());
                        Rectangle2D screenBounds = screen.getBounds();
                        startX = screenBounds.getMinX();
                        startY = screenBounds.getMinY();
                        screenWidth = screenBounds.getWidth();
                        screenHeight = screenBounds.getHeight();
                  } else {
                        /*
                         * If the owner is set, we will make the FMNotifcations popup
                         * inside its window.
                         */
                        startX = notification.owner.getX();
                        startY = notification.owner.getY();
                        screenWidth = notification.owner.getWidth();
                        screenHeight = notification.owner.getHeight();
                        window = notification.owner;
                  }
//                  show(window, notification);
            }

            private Optional<Screen> getScreenBounds(Window window) {
                  if (window == null) {
                        return Optional.empty();
                  }
                  final ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(window.getX(),
                      window.getY(),
                      400,
                      window.getHeight());
                  return screensForRectangle.stream()
                      .filter(Objects::nonNull)
                      .findFirst();
            }
      }
}


