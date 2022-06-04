package flashmonkey;

import type.draw.shapes.GenericShape;
import org.controlsfx.control.PrefixSelectionComboBox;
import org.junit.jupiter.api.*;
import type.celleditors.SectionEditor;

import java.util.ArrayList;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("CreateFlash")
public class CreateFlashTester {
	
	SectionEditor editorU;
	SectionEditor editorL;
	String bla;
	String aShapeName;
	PrefixSelectionComboBox<CreateFlash.TestMapper> entryComboBox;// = new PrefixSelectionComboBox<>();
	
	ArrayList<GenericShape> shapesFileNames;
	
	//@BeforeAll
	public void setUp() {
		editorU = new SectionEditor();
		editorL = new SectionEditor();
		bla = "bla bla bla";
		
		//tri = new FMTriangle(10, 10, 25, 25, 2, UIColors.FM_GREY, UIColors.TRANSPARENT, 1);
		aShapeName = "aShapeName";
		shapesFileNames = new ArrayList<>(1);
		
		entryComboBox = new PrefixSelectionComboBox<>();
		
	}
	
	
/*
	@Test
	@Order(1)
	public void checkTextInEditorUisTrue() {
		setUp();
		// check if card shows has data in upper editor. should return 1
		editorU.tCell.getTextArea().setText(bla);
		int result = CreateFlash.getInstance().checkContent(editorU, editorL);
		
		assertEquals("Incorrect response.", true,  result == 2);
	}
	
	@Test
	@Order(2)
	public void checkMediaInEditorUisTrue() {
		setUp();
		// check if card shows has data in upper editor. should return 1
		editorU.setShapeFile(aShapeName);
		CreateFlash cf = CreateFlash.getInstance();
		cf.CardSaver cs =
		int result = .checkContent(editorU, editorL);
		
		assertEquals("Incorrect response.", true,  result == 4);
	}
	
	@Test
	@Order(3)
	public void checkTextInEditorLisTrue() {
		setUp();
		// check if card shows has data in upper editor. should return 1
		editorL.tCell.getTextArea().setText(bla);
		int result = CreateFlash.getInstance().checkContent(editorU, editorL);
		
		assertEquals("Incorrect response.", true,  result == 8);
	}
	
	@Test
	@Order(4)
	public void checkMediaInEditorLisTrue() {
		setUp();
		// check if card shows has data in upper editor. should return 1
		editorL.setShapeFile(aShapeName);
		int result = CreateFlash.getInstance().checkContent(editorU, editorL);
		
		assertEquals("Incorrect response.", true,  result == 16);
	}
	
	//@Test
	//@Order(5)
	public void checkTestTypeisSet() {
		setUp();
		// check if card shows has data in upper editor. should return 1
		editorL.setShapeFile(aShapeName);
		boolean bool = CreateFlash.getInstance().getTestType() == null;
		
		assertEquals("Incorrect testType response.", true,  bool == true);
	}
*/
	
	
}
