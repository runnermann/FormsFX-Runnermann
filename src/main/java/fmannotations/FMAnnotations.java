package fmannotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class FMAnnotations {


    /**
     * Notifies that this method should
     * be removed before the code is deployed. It is for
     * testing only.
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface DoNotDeployMethod {
        /* empty */
    }


    /**
     * Notifies that this field should
     * be removed before the code is deployed. It is for
     * testing only.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface DoNotDeployField {
        /* empty */
    }


    /**
     * Notifies that this type should
     * be removed before the code is deployed. It is for
     * testing only.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface DoNotDeployType {
        /* empty */
    }


}
