package fileops.utility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum FileExtension {

    IS_AUDIO () {
        public boolean check(String mime) {
            return AUDIO_HASHSET.contains(mime);
        }
    },
    IS_VIDEO () {
        public boolean check(String mime) {
            return VIDEO_HASHSET.contains(mime);
        }
    },
    IS_FX_IMAGE () {
        public boolean check(String mime) {
            return FX_IMAGE_EXTENSIONS.contains(mime);
        }
    },
    IS_FX_AV () {
        public boolean check(String mime) {
            return FX_AV_EXTENSIONS.contains(mime);
        }
    },
    IS_JAVE_VIDEO() {
        public boolean check(String mime) {
            return VIDEO_EXTENSIONS_LIST.contains(mime);
        }
    };

    // --------------------------------- --------------------------------- //
    //                             FIELDS
    // --------------------------------- --------------------------------- //

    private final static Set<String> FX_AV_EXTENSIONS = new HashSet<>(Arrays.asList("flv", "mp3", "mp4", "m4a", "m4v", "wav"));
    private final static Set<String> FX_IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList("gif", "jpg", "JPG", "PNG","png"));
    private final static List<String> AUDIO_EXTENSIONS_LIST = Arrays.asList("mp3", "wav", "ogg", "opus", "aac",
            "flac", "aiff", "au", "speex", "webm", "wma", "amr", "ape", "awb", "dct", "dss", "dvf", "aa", "aax", "act",
            "m4a", "m4b", "m4p", "mpc", "msv", "oga", "mogg", "raw", "tta", "aifc", "ac3", "spx");
    private final static List<String> VIDEO_EXTENSIONS_LIST = Arrays.asList("mp4", "flv", "avi", "wmv", "mov", "MOV",
            "3gp", "webm", "mkv", "vob", "yuv", "m4v", "svi", "3g2", "f4v", "f4p", "f4a", "f4b", "swf");
    private static final Set<String> AUDIO_HASHSET = new HashSet<>(AUDIO_EXTENSIONS_LIST);
    private static final Set<String> VIDEO_HASHSET = new HashSet<>(VIDEO_EXTENSIONS_LIST);
    private static final Set<String> IMAGE_EXTENSIONS = new HashSet<>(
            Arrays.asList("png", "jpg", "jpeg", "gif", "bmp", "exif", "tiff", "webp", "heif", "bat", "bpg", "svg", "JPEG"));
    static final Set<String> POPULAR_ZIP_EXTENSIONS = new HashSet<>(
            Arrays.asList("zip", "7z", "rar", "zipx", "bz2", "gz"));


    // --------------------------------- --------------------------------- //
    //                             METHODS
    // --------------------------------- --------------------------------- //
        FileExtension() { /* empty constructor */ }

        public abstract boolean check(String mime);

}
