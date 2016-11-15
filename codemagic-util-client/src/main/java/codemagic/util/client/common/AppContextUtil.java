package codemagic.util.client.common;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;


/**
 * Utilities for applications. 
 *
 */
public abstract class AppContextUtil {

	/**
	 * Best-effort method for determining whether or not we're running dev mode.
	 * GWT.isProd and isScript cannot be used to determine this in SDM.
	 * References <a href=
	 * "https://code.google.com/p/google-web-toolkit/issues/detail?id=7634#c13"
	 * ></a>
	 *
	 * Note: To run production mode in the localhost, you should add another
	 * domain to /etc/hosts pointing to localhost and use it.
	 * 
	 * Example
	 * <pre>
	 *    1. Add this entry do /etc/hosts - 127.0.0.1 localdemo
	 *    2. call the app in the browser: http://localdemo/... 
	 * </pre>
	 * 
	 * 
	 * @return true for dev mode, false if not
	 */
	public static boolean inDevMode() {
		return Location.getHost().contains("localhost") || Location.getHost().contains("127.0.0.1") ;
	}
	
	
	public static String getBaseUrlForRestServices() {
		return buildLocationOrigin();	
	}

	/**
	 * 
	 * @return The timeout used to connect/read from server. Now, this timeout
	 *         is hard coded in 30 seconds (30000 millisec).
	 */
	public static int getTimeoutInMilliseconds() {
		return 30000;
	}

	/**
	 * <pre>
	 * @return for the browser url http://[host]:[port]/[xxxx], returns http://[host]:[port]
	 * </pre>
	 */
	public static String buildLocationOrigin() {
		return Window.Location.getProtocol() + "//" + Window.Location.getHost();
	}
}
