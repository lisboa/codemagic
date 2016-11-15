package codemagic.util.client.common;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.query.client.Properties;

public abstract class ResponseUtil {

	/**
	 * @param argument
	 *            A {@link JavaScriptObject} success server response. Within a
	 *            setSuccess(..), this is getArgument(0). Within a promise.done(..), this
	 *            is getArgument(0,0)
	 * @return
	 */
	public static String successAsJsonStr(final Object argument) {
		final JavaScriptObject jso = (JavaScriptObject) argument;
		final Properties props = jso.<Properties>cast();
		final String responseJson = props.toJson();
		return responseJson;
	}
	
	/**
	 * Fix a bug with {@link RequestException} implementation: Prepend the String
	 * "HTTP ERROR: 500 Internal Server Error" to server error.
	 * 
	 * See issue https://github.com/ArcBees/gwtquery/issues/359
	 * @param argument
	 *            A {@link JavaScriptObject} error server response. Within a
	 *            setFail(..), this is getArgument(4). Within a
	 *            promise.fail(..), this is getArgument(0,4)
	 * @return
	 */
	public static String extracJsonFromFailure(final RequestException exception) {
		final String message = sanitize(exception.getMessage()).trim();
		final int left = message.indexOf("{");
		if (left > -1) {
			final int right = message.lastIndexOf("}");
			if (right > -1) {
				return message.substring(left, right + 1); // include the right bracket
			}
		}
		return "";
	}
}
