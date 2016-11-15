package codemagic.widget.client.common.util.validator;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import java.util.List;

import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.form.error.BasicEditorError;
import org.gwtbootstrap3.client.ui.form.validator.Validator;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;

import codemagic.util.client.common.StringUtilClient;

public class PasswordValidator implements IValidator{

	private final Input password;

	private final Input confirmPassword;
	
	private final int minLength;

	private Optional<Validator<String>> validator = Optional.absent();

	/**
	 * 
	 * @param password
	 *            The {@link Input} with the password to be checked
	 * @param confirmPassword
	 *            {@link Input} with the confirm password to be checked
	 * @param minLength
	 *            The minimum length of the password. A zero or negative value
	 *            disable this check.
	 */
	public PasswordValidator(
			final Input password, 
			final Input confirmPassword,
			final int minLength) {

		assert password != null : "Input password cannot be null";
		assert confirmPassword != null : "Input confirm password cannot be null";

		this.password = password;
		this.confirmPassword = confirmPassword;
		this.minLength = minLength;
	}

	@Override
	public void bind() {
		if (!validator.isPresent()) {
			validator = Optional.of(build(password, confirmPassword, minLength));
			password.addValidator(validator.get());
		}
	}

	@Override
	public void unBind() {
		if (validator.isPresent()) {
			password.removeValidator(validator.get());
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
	private static Validator<String> build(
			final Input password, 
			final Input confirmPassword,
			final int minLength) {

		return new Validator<String>() {

			@Override
			public int getPriority() {
				return 0;
			}

			@Override
			public List<EditorError> validate(final Editor<String> editor, final String value) {

				final List<EditorError> result = Lists.newArrayList();

				final String passwordText = sanitize(password.getText());

				// 1. Checks minimum password size
				if (minLength > 0 && passwordText.length() < minLength) {
					final String msg = StringUtilClient.format("password should have at least %s characters", minLength);
					result.add(new BasicEditorError(password, "", msg));
					return result;
				}

				// 2. Checks password and confirm password
				GWT.log(StringUtilClient.format("passwordText: '%s', confirmPassword: '%s'", passwordText, confirmPassword.getText()));
				final boolean check = passwordText.equals(confirmPassword.getText());
				if (!check) {
					final String msg = "password and confirm password does not match";
					result.add(new BasicEditorError(password, "", msg));
				}

				return result;
			}
		};
	}
}
