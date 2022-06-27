package forms;

import com.dlsc.formsfx.model.structure.Form;

public interface FormModel {


      Form getFormInstance();

      void createForm();

      /**
       * Creates the form
       *
       * @param data
       */
      void formAction(FormData data);

      void formAction();

      /**
       * The button action once the form is
       * completed by the user.
       *
       * @param data
       * @return
       */
      boolean doAction(FormData data);

      void translate(String language);

      Descriptor getDescriptor();


}
