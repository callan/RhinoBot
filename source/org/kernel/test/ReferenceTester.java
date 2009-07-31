/**
 * ReferenceTester.java
 * Kernel
 * @package org.kernel.test
 */
package org.kernel.test;

import org.kernel.Kernel;
import org.kernel.Module;


/**
 * @author Chris
 *
 */
public class ReferenceTester implements Module
{
	/**
	 * Overidden
	 * @see org.kernel.Module#init()
	 */
	public void init ()
	{
		String[] str = new String[] { "lol", "ok" };
		String[] str2 = new String[] { "bar", "foo" };
		String[] str3 = new String[] { "LOL", "$$$$" };
		
		System.out.println(str[0] + " " + str[1]);
		referenceTester(str2, str);
		System.out.println(str[0] + " " + str[1]);
		referenceTester2(str3, str);
		System.out.println(str[0] + " " + str[1]);
	}

	public void referenceTester (String[] in, String[] out)
	{
		out[0] = in[0];
		out[1] = in[1];
	}
	
	public void referenceTester2 (String[] in, String[] out)
	{
		out = in;
	}
	
	/**
	 * Overidden
	 * @see org.kernel.Module#panic()
	 */
	public void panic ()
	{
	}

	/**
	 * Overidden
	 * @see org.kernel.Module#quit()
	 */
	public void quit ()
	{
	}

	/**
	 * Overidden
	 * @see org.kernel.Module#getModuleName()
	 */
	public String getModuleName () throws NullPointerException
	{
		return "ReferenceTester";
	}

	/**
	 * Overidden
	 * @see org.kernel.Module#getVersion()
	 */
	public String getVersion () throws NullPointerException
	{
		return "1.0.0";
	}

	/**
	 * Overidden
	 * @see org.kernel.Module#getDependencies()
	 */
	public String[] getDependencies ()
	{
		return null;
	}

	/**
	 * Overidden
	 * @see org.kernel.Module#dataTransferEvent(org.kernel.Kernel, java.lang.String, F)
	 */
	public <F> void dataTransferEvent (Kernel kernel, String from, F data)
	{
	}
}
