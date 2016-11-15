package codemagic.widget.client.common.util.validator;

public interface IValidator {

	 /** Should be called in onBind event */
	 void bind();
	 
	 /** Should be called in onUnbind event */
	 void unBind();
}
