/**
 * TestModuleA.java
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
public class TestModuleA implements Module
{

	public void init ()
	{
		System.out.println("TestModuleA: init() called!");
		Kernel kernel = Kernel.getInstance();
		kernel.<String>sendData(this, "TestModuleB", "TEST");
	}

	public void panic ()
	{
	}

	public void quit ()
	{
	}

	public String getModuleName () throws NullPointerException
	{
		return "TestModuleA";
	}

	public String getVersion () throws NullPointerException
	{
		return "1.0.0";
	}

	public String[] getDependencies ()
	{
		return new String[] { "org.kernel.test.TestModuleB" };
	}

	public <F> void dataTransferEvent (Kernel kernel, String from, F data)
	{
		System.out.print(
				"Module: " + getModuleName() + "\r\n" +
				"From: " + from + "\r\n" +
				"Hmm: " + from.equals("TestModuleB") + "\r\n" +
				"Data: " + (data instanceof String) + "\r\n" +
				"\t" + data + "\r\n"
		);
		
		if (from.indexOf("TestModuleB") != -1)
		{
			if (data instanceof String)
			{
				String sData = (String) data;
				
				if (sData.equals("INIT"))
				{
					System.out.println("Test Module A initializing");
					kernel.<String>sendData(this, from, "INIT CALLED");
				}
			}
		}
	}
}
