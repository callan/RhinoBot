/**
 * NewConfigReaderTest.java
 * Kernel
 * @package org.kernel.test
 */
package org.kernel.test;

import org.kernel.ConfigReader;

/**
 * @author Chris
 *
 */
public class NewConfigReaderTest
{

	/**
	 * @param args
	 */
	public static void main (String[] args)
	{
		try
		{
			new ConfigReader("kernel.conf");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
