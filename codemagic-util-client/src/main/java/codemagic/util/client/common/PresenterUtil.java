package codemagic.util.client.common;

import com.google.gwt.query.client.Function;
import com.google.gwt.query.client.Promise;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public abstract class PresenterUtil {

	/**
	 * This method is useful to implements the
	 * <a href="http://dev.arcbees.com/gwtp/core/navigation/manual-reveal.html">
	 * GWTP manual reveal pattern</a>.
	 * 
	 * <p>
	 * Note: Remember to implements the method useManualReveal, as showed
	 * bellow.
	 * 
	 * <pre>
	 * {@code
	 *    &#64;Override
	 *    public boolean useManualReveal() {
	 *	   return true;
	 *    }
	 * }
	 * </pre>
	 * 
	 * @param promise
	 *            The {@link Promise} used to known when the request is
	 *            finished.
	 * @param presenter
	 *            The presenter that should be revealed.
	 */
	public static void manualReveal(final Promise promise, final Presenter<?, ?> presenter) {

		assert promise != null : "promise cannot be null";
		
		assert presenter != null : "presenter cannot be null";
		
		promise.done(new Function() {
			@Override
			public void f() {
				((ProxyPlace<?>) presenter.getProxy()).manualReveal(presenter);
			}
		}).fail(new Function() {
			@Override
			public void f() {
				((ProxyPlace<?>) presenter.getProxy()).manualRevealFailed();
			}
		});
	}
	
	public static void manualReveal(final Presenter<?, ?> presenter, final boolean success) {
		if (success) {
			((ProxyPlace<?>) presenter.getProxy()).manualReveal(presenter);
		} else {
			((ProxyPlace<?>) presenter.getProxy()).manualRevealFailed();
		}
	}
}
