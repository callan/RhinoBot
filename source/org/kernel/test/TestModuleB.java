/**
 * TestModuleB.java
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
public class TestModuleB implements Module
{

	/**
	 * @see org.kernel.Module#init()
	 */
	public void init ()
	{
		System.out.println("TestModuleB: init() called!");
	}

	/**
	 * @see org.kernel.Module#panic()
	 */
	public void panic ()
	{
	}

	/**
	 * @see org.kernel.Module#quit()
	 */
	public void quit ()
	{
	}

	/**
	 * @see org.kernel.Module#getModuleName()
	 */
	public String getModuleName () throws NullPointerException
	{
		return "TestModuleB";
	}

	/**
	 * @see org.kernel.Module#getVersion()
	 */
	public String getVersion () throws NullPointerException
	{
		return "Version 1.0.0";
	}

	/**
	 * @see org.kernel.Module#getDependencies()
	 */
	public String[] getDependencies ()
	{
		return new String[] { "org.kernel.test.TestModuleA" };
	}

	/**
	 * @see org.kernel.Module#dataTransferEvent(org.kernel.Kernel, java.lang.String, F)
	 */
	public <F> void dataTransferEvent (Kernel kernel, String from, F data)
	{
		System.out.print(
				"Module: " + getModuleName() + "\r\n" +
				"From: " + from + "\r\n" +
				"Hmm: " + from.equals("TestModuleA") + "\r\n" +
				"Data: " + (data instanceof String) + "\r\n" +
				"\t" + data + "\r\n"
		);
		
		if (from.equals("TestModuleA"))
		{
			if (data instanceof String)
			{
				String sData = (String) data;
				
				if (sData.equals("INIT CALLED"))
				{
					System.out.println("TestModuleA's INIT has been called");
				}
				else if (sData.equals("TEST"))
				{
					kernel.<String>sendData(this, "TestModuleA", "INIT");
				}
			}
		}
	}
}
