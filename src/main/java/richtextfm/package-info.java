/**
 * Defines the view-related classes for rendering and editing an
 * {@link richtextfm.model.EditableStyledDocument EditableStyledDocument}.
 *
 * <p>
 *     The base area is {@link richtextfm.GenericStyledArea}. Those unfamiliar with this
 *     project should read through its javadoc. This class should be used for custom segments (e.g. text and images
 *     in the same area). {@link richtextfm.StyledTextArea} uses {@link java.lang.String}-only segments,
 *     and styling them are already supported in the two most common ways via
 *     {@link richtextfm.StyleClassedTextArea} and {@link richtextfm.InlineCssTextArea}.
 *     For those looking to use a base for a code editor, see {@link richtextfm.CodeArea}.
 * </p>
 * <p>
 *     For text fields there is {@link richtextfm.StyledTextField} using {@link java.lang.String}-only segments,
 *     and styling them are also already supported in the two most common ways via
 *     {@link richtextfm.StyleClassedTextField} and {@link richtextfm.InlineCssTextField}.
 * </p>
 *
 * @see richtextfm.model.EditableStyledDocument
 * @see richtextfm.model.TwoDimensional
 * @see richtextfm.model.TwoDimensional.Bias
 * @see richtextfm.GenericStyledArea
 * @see richtextfm.TextEditingArea
 * @see richtextfm.Caret
 * @see richtextfm.Selection
 */
package richtextfm;