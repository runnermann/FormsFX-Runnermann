package media.sound;

import javafx.scene.media.AudioClip;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public enum SoundEffects {
    APP_START{
        public void play() {
            sound = "sound/techno_glitch_logo.wav";
            go(.3);
        }
    },
    APP_END{
        public void play() {
            //sound = "sound/intro_one.wav";
           // go(.2);
        }
    },
    ACCESS_GRANTED {
        public void play() {
            sound = "sound/robot_access_granted.wav";
            go(.2);
        }
    },
    ATTENTION {
        public void play() {
            //@TODO setup sound
            sound = "sound/robot_warning.wav";
            go(.3);
        }
    },
    BAD_ERROR {
        public void play() {
            //@TODO setup sound
            sound = "sound/robot_warning.wav";
            go(.3);
        }
    },
    ERROR{
        public void play() {
            //sound = "sound/digital_phonering.wav";
            sound = "sound/notification.wav";
            go(.3);
        }
    },
    FART {
        public void play() {
            //@TODO setup sound
            sound = "sound/Fart_BW.61633.wav";
            go(.3);
        }
    },
    WRONG_ANSWER_1{
        public void play() {
            sound = "sound/wrong_answer.wav";
            go(.4);
        }
    },
    WRONG_ANSWER_2{
        public void play() {
            // I would like to put something more playful in here and realistic like a man gruning or a scream.
            // But given what Russia is Doing to innocent civilians in Ukraine we cannot due to the terror
            // the civilian populace, the children, the women, the crippled and elderly, the mothers giving birth
            // and the good men are suffering. So we shall rather say F^@K YOU RUSSIA.
            sound = "sound/awe_sympathy_crowd.wav";
            go(.4);
        }
    },
    CORRECT_ANSWER{
        public void play() {
            sound = "sound/soft_magic_glow.wav";
            go(.4);
        }
    },
    GOTO_FILE_SELECT{
        public void play() {
            //@TODO setup sound
            sound = "sound/transition_pop.wav";
            go(.5);
        }
    },
    GOTO_MENU{
        public void play() {
            //@TODO setup sound
            sound = "sound/transition_pop.wav";
            go(.5);
        }
    },
    PRESS_BUTTON_COMMON{
        public void play() {
            sound = "sound/transition_pop.wav";
            go(.5);
        }
    },
    PRESS_STUDY{
        public void play() {
            //@TODO setup sound
            sound = "sound/transition_pop.wav";
            go(.5);
        }
    },
    PRESS_CREATE{
        public void play() {
            sound = "sound/transition_pop.wav";
            go(.5);
        }
    },
    PRESS_FLASHCARD{
        public void play() {
            sound = "sound/transition_pop.wav";
            go(.5);
        }
    },
    PRESS_TEST{
        public void play() {
            //sound = "sound/static_cool_buzz_intro.wav";
            //go();
        }
    },
    TREE_PRESS_NODE{
        public void play() {
            sound = "sound/transition_pop.wav";
            go(.5);
        }
    },
    DECK_END_HIGHSCORE{
        public void play() {
            sound = "sound/ApplsCheeringWhiste.wav";
            go(.5);
        }
    },
    GAME_OVER {
        public void play() {
            sound = "sound/game_over.wav";
            go(.3);
        }
    },
    CAMERA {
        public void play() {
            sound = "sound/camera-1.wav";
            go(.6);
        }
    },
    NOTIFICATION_NORM{
        public void play() {
            //@TODO setup sound
            sound = "sound/notification.wav";
//            sound = "sound/transition_pop.wav";
            go(.4);
        }
    },
    SLIDE_LEFT {
        public void play() {
            sound = "sound/transition_pop.wav";
            go(.6);
        }
    },
    SLIDE_RIGHT {
        public void play() {
            sound = "sound/transition_pop.wav";
            go(.5);
        }
    },
    GOTO_START {
        public void play() {
            sound = "sound/transition_pop.wav";
            go(.5);
        }
    },
    GOTO_END {
        public void play() {
            sound = "sound/transition_pop.wav";
            go(.5);
        }
    },
    ROBOT_SERVO {
        public void play() {
            sound = "sound/robot-servo4.wav";
            go(.5);
        }
    },
    ROBOT_SERVO_2 {
        public void play() {
            sound = "sound/robot_servo3.wav";
            go(.4);
        }
    },
    ROBOT_SERVO_3 {
        public void play() {
            sound = "sound/ServoMotor_2c8ln_09.wav";
            go(.2);
        }
    },
    ROBOT_SERVO_START {
        public void play() {
            sound = "sound/RobotServo_znM6w_18.wav";
            go(.2);
        }
    },
    Yeah_ITS_COOL {
        public void play() {
            sound = "sound/Yeah_Its_Cool_vocal_Short.wav";
            go(.2);
        }
    };

    // ^^^^^^^^^^^^^^^ COMMON ^^^^^^^^^^^^^^^^^^ //

    String sound = "sound/transition_pop.wav";

    public abstract void play();

    /**
     * @param v sets the volume for this sound.
     */
    void go(double v) {
        Executor exec = Executors.newSingleThreadExecutor();
        exec.execute(() -> {
            //s.set(new AudioClip(this.getClass().getClassLoader().getResource(this.sound).toExternalForm()));
            AudioClip sound = new AudioClip(this.getClass().getClassLoader().getResource(this.sound).toExternalForm());
            sound.setVolume(v);
            sound.setCycleCount(1);
            sound.play();
        });
    };
}
