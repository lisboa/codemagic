package codemagic.util.client.common;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.query.client.Promise;
import com.google.gwt.query.client.plugins.deferred.PromiseFunction;

public abstract class PromiseUtil {
	
	/**
	 * <pre>
	 * Build a Rejected Promise that honor the function arguments returned by
	 * the GWT promises. That is, an array with 5 items:
	 *  [0] - null, 
	 *  [1] - error message, 
	 *  [2] - request, a dummy that has nothing, but is safe to use it.
	 *  [3] - null, 
	 *  [4] - RequestException
	 * </pre>
	 * @param errorMsg
	 * @return
	 */
	public static Promise buildRejectedPromise(final String errorMsg) {
		return new PromiseFunction() {
		    @Override
			public void f(final Deferred dfd) {
		      dfd.reject(null, errorMsg, new EmptyRequest(), null, new RequestException(errorMsg));
		    }
		  };
	}
	
	public static class EmptyRequest extends Request {

		@Override
		public void cancel() { /*Noop*/ }

		@Override
		public boolean isPending() { return false; }
		
	}
}
