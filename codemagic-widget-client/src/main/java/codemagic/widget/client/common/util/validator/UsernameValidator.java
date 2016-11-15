package codemagic.widget.client.common.util.validator;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;
import static codemagic.util.shared.common.StringUtil.normalizeMore;

import java.util.List;

import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.LongBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.base.ValueBoxBase;
import org.gwtbootstrap3.client.ui.form.error.BasicEditorError;
import org.gwtbootstrap3.client.ui.form.validator.Validator;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

public class UsernameValidator implements IValidator{

	private final ValueBoxBase<String> boxUsername;

	private Optional<Validator<String>> validator = Optional.absent();

	/**
	 * 
	 * @param boxUsername
	 *            The {@link ValueBoxBase} used to store the username. A
	 *            {@link ValueBoxBase} can references an {@link Input},
	 *            {@link TextBox}, {@link LongBox}, etc.
	 */
	public UsernameValidator(final ValueBoxBase<String> boxUsername) {

		assert boxUsername != null : "Input password cannot be null";

		this.boxUsername = boxUsername;
	}

	@Override
	public void bind() {
		if (!validator.isPresent()) {
			validator = Optional.of(build(boxUsername));
			boxUsername.addValidator(validator.get());
		}
	}

	@Override
	public void unBind() {
		if (validator.isPresent()) {
			boxUsername.removeValidator(validator.get());
			validator = Optional.absent();
		}
	}

	// ~~~~~~~~~~~~//
	// Utilities //
	// ~~~~~~~~~~~~//

	/**
	 * Ensures that the text of password and confirm password are equals
	 * 
	 * @return
	 */
	private static Validator<String> build(final ValueBoxBase<String> boxUsername) {

		return new Validator<String>() {

			@Override
			public int getPriority() {
				return 0;
			}

			@Override
			public List<EditorError> validate(final Editor<String> editor, final String value) {

				final List<EditorError> result = Lists.newArrayList();

				final String username = sanitize(boxUsername.getText()).toLowerCase();

				// Remove accents, spaces and any chars different from letters
				// (a-z), digits (0-9), dot and underline. 
				final String modifiedUserName = normalizeMore(username);
				
				if ( ! username.equals(modifiedUserName)) {
					final String msg = "Only letters (a-z) without accents, digits (0-9), dot and underline are allowed";
					result.add(new BasicEditorError(boxUsername, "", msg));
				}
						
				return result;
			}
		};
	}
}
