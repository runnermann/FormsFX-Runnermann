package security.io;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URL;
import java.rmi.activation.ActivationInstantiator;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


/**
 * An <code>ObjectInputStream</code> that's restricted to deserialize
 * a limited set of classes.
 *
 * <p>
 * Various accept/reject methods allow for specifying which classes
 * can be deserialized.
 * </p>
 *
 * <p>
 * Design inspired by <a
 * href="http://www.ibm.com/developerworks/library/se-lookahead/">IBM
 * DeveloperWorks Article</a>.
 * </p>
 */
public class ValidatingObjectInputStream extends ObjectInputStream {

    // An attempted whitelist to be searched at compiletime
    //static final String[] packages = {"type", "multimedia", "draw.shapes", "video", "flashmonkey"};

    // An attempt to create a white list at compile time. Very hard to achieve and
    // did not have all of the needed classes. But it would create the list at compile time
    // making the list hard to change.
    //@Target(ElementType.METHOD)
    //@Retention(RetentionPolicy.CLASS)
    //@interface AcceptedClasses { // runs at compile time only.
    //    ArrayList<Class> ACCEPTED_CLASSES_BUILDER = getClasses(packages);
    //}

    public static final String[] blackListPkgs = {"javax.management.JMX", "java.rmi" };


    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    @interface RejectedClasses { // runs at compile time only.
        ArrayList<Class> REJECT_CLASSES_BUILDER = getClasses(blackListPkgs);
    }

    public static ArrayList<Class> BLACKLIST_CLASSES = RejectedClasses.REJECT_CLASSES_BUILDER;

    //public static ArrayList<Class> ACCEPTED_CLASSES = AcceptedClasses.ACCEPTED_CLASSES_BUILDER;


        private final List<ClassNameMatcher> acceptMatchers = new ArrayList<>();
        private final List<ClassNameMatcher> rejectMatchers = new ArrayList<>();


        /**
         * Constructs an object to deserialize the specified input stream.
         * At least one accept method needs to be called to specify which
         * classes can be deserialized, as by default no classes are
         * accepted.
         *
         * @param input an input stream
         * @throws IOException if an I/O error occurs while reading stream header
         */
        public ValidatingObjectInputStream(final InputStream input) throws IOException {
            super(input);
            System.out.println("ValidatingObjectInputStream COnstructor called");
        }

        /** Check that the classname conforms to requirements.
         * @param name The class name
         * @throws InvalidClassException when a non-accepted class is encountered
         */
        private void validateClassName(final String name) throws InvalidClassException {

            // Reject has precedence over accept
            for (final ClassNameMatcher m : rejectMatchers) {
                if (m.matches(name)) {
                    invalidClassNameFound(name);
                }
            }

            boolean ok = false;
            for (final ClassNameMatcher m : acceptMatchers) {
                if (m.matches(name)) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                invalidClassNameFound(name);
            }
        }

        /**
         * Called to throw <code>InvalidClassException</code> if an invalid
         * class name is found during deserialization. Can be overridden, for example
         * to log those class names.
         *
         * @param className name of the invalid class
         * @throws InvalidClassException if the specified class is not allowed
         */
        protected void invalidClassNameFound(final String className) throws InvalidClassException {
            throw new InvalidClassException("Class name not accepted: " + className);
        }

        @Override
        protected Class<?> resolveClass(final ObjectStreamClass osc) throws IOException, ClassNotFoundException {
            validateClassName(osc.getName());
            return super.resolveClass(osc);
        }

        /**
         * Accept the specified classes for deserialization, unless they
         * are otherwise rejected.
         *
         * @param classes Classes to accept
         * @return this object
         */
        public ValidatingObjectInputStream accept(final ArrayList<Class> classes) {
            for (final Class<?> c : classes) {
                acceptMatchers.add(new FullClassNameMatcher(c.getName()));
            }
            return this;
        }

        /**
         * Reject the specified classes for deserialization, even if they
         * are otherwise accepted.
         *
         * @param classes Classes to reject
         * @return this object
         */
        public ValidatingObjectInputStream reject(final ArrayList<Class>  classes) {
        for (final Class<?> c : classes) {
            rejectMatchers.add(new FullClassNameMatcher(c.getName()));
        }
        return this;
    }

        /**
         * Accept class names where the supplied ClassNameMatcher matches for
         * deserialization, unless they are otherwise rejected.
         *
         * @param m the matcher to use
         * @return this object
         */
        public ValidatingObjectInputStream accept(final ClassNameMatcher m) {
            acceptMatchers.add(m);
            return this;
        }

        /**
         * Reject class names where the supplied ClassNameMatcher matches for
         * deserialization, even if they are otherwise accepted.
         *
         * @param m the matcher to use
         * @return this object
         */
        public ValidatingObjectInputStream reject(final ClassNameMatcher m) {
            rejectMatchers.add(m);
            return this;
        }

    private static ArrayList<Class> getClasses(String ... packageNames) {

        System.out.println("Called get classes in validating object stream");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ArrayList<Class> classes = new ArrayList<>();
        for (String packageName : packageNames) {
            try {
                String path = packageName.replace('.', '/');
                Enumeration<URL> resources = classLoader.getResources(path);
                List<File> dirs = new ArrayList<File>();
                while (resources.hasMoreElements()) {
                    URL resource = resources.nextElement();
                    dirs.add(new File(resource.getFile()));
                }

                for (File directory : dirs) {
                    classes.addAll(findClasses(directory, packageName));
                }
                //.toArray(new Class[classes.size()]);
            } catch(IOException e){
                e.printStackTrace();
            } catch(ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        //classes.add(flashmonkey.FlashCardMM.class);
        //classes.add(flashmonkey.Answer.class);
        //classes.add(flashmonkey.Question.class);
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
   // }
}
