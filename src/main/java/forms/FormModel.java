package forms;

import com.dlsc.formsfx.model.structure.Form;

public interface FormModel {


	public Form getFormInstance();
	
	public void createForm();
	
	/**
	 * Creates the form
	 * @param data
	 */
	public void formAction(FormData data);
	
	/**
	 * The button action once the form is
	 * completed by the user.
	 * @param data
	 * @return
	 */
	public boolean doAction(FormData data);
	
	public void translate(String language);
	
	public Descriptor getDescriptor();
	
	
}
