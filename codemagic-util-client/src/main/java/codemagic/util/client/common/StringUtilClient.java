package codemagic.util.client.common;

import com.google.common.base.Preconditions;

public abstract class StringUtilClient {

	/**
	 * <a href="http://stackoverflow.com/a/24826635">What is the alternative for String.format() in GWT?</a>
	 * 
	 * @param format
	 * @param args
	 * @return
	 */
	public static String format(final String format, final Object... args)
	{
		Preconditions.checkArgument( format != null, "'format' could not be null");
		Preconditions.checkArgument( args != null, "'args' could not be null");
		
	    final String pattern = "%s";

	    int start, last = 0, argsIndex = 0;
	    final StringBuilder result = new StringBuilder();
	    while ((start = format.indexOf(pattern, last)) != -1)
	    {
	        if (args.length <= argsIndex)
	        {
	            throw new IllegalArgumentException("There is more replace patterns than arguments!");
	        }
	        result.append(format.substring(last, start));
	        result.append(args[argsIndex++]);

	        last = start + pattern.length();
	    }

	    if (args.length > argsIndex)
	    {
	        throw new IllegalArgumentException("There is more arguments than replace patterns!");
	    }

	    result.append(format.substring(last));
	    return result.toString();
	}
	
	
	public static String format2(final String format, final Object... args) {
	   return "[" + EventCounter.incrementAndGet() + "] " + format(format, args);	
	}
	
	/**
	 * Add the {@link EventCounter} as prefix.
	 * @param s
	 * @return
	 */
	public static String wrap(final String s) {
		   return "[" + EventCounter.incrementAndGet() + "] " + s;	
		}
}
