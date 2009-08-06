/**
 * RhinoBot
 * Modular IRC Bot
 * By Christopher Allan <chris@geartech.org>
 *
 * If you like this software, please e-mail me with suggestions/comments/etc..
 *
 * Copyright (c) 2009, Christopher Allan
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the product nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY Christopher Allan ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Christopher Allan BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.geartech.rhinobot.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.*;

/**
 * Config class. Serves as a central tool for all config things considered.
 * Works with JSON.
 */
public class Config
{
	/**#@+
	 * Static Methods
	 */
	
	/**
	 * Where the config instances will be stored. Default config instance is called _default
	 */
	protected static HashMap<String, Config> _configs = new HashMap<String, Config>(2);
	
	/**
	 * Loads the default config, named "_default"
	 * 
	 * @param fileName
	 * @return Config
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JSONException 
	 */
	public static Config loadConfig (String fileName) throws FileNotFoundException, IOException, JSONException
	{
		return Config.loadConfig(fileName, "__default");
	}
	
	/**
	 * Loads in a specific config based on configName
	 * 
	 * @param fileName
	 * @param configName
	 * @return Config
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JSONException 
	 */
	public static Config loadConfig (String fileName, String configName) throws FileNotFoundException, IOException, JSONException
	{
		Config config = new Config(fileName);
		
		_configs.put(configName, config);
		return config;
	}

	/**
	 * Returns the default config, if it has been instantiated.
	 * 
	 * @return Config
	 */
	public static Config getConfig ()
	{
		return _configs.get("__default");
	}
	
	/**
	 * Returns a config under the name configName
	 * 
	 * @param configName
	 * @return Config
	 */
	public static Config getConfig (String configName)
	{
		return _configs.get(configName);
	}
	
	protected HashMap<String, String> _storage = new HashMap<String, String>(30);
	protected JSONObject              _json;
	
	/**#@+
	 * Non Static Methods
	 */
	
	/**
	 * Constructor. Loads fileName (from JSON format) and converts it to java data structures. 
	 * 
	 * @param fileName
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JSONException 
	 */
	public Config (String fileName) throws FileNotFoundException, IOException, JSONException
	{
		File file = new File(fileName);
		
		if (!file.exists() || !file.canRead())
			throw new FileNotFoundException();
		
		BufferedReader reader = new BufferedReader( new FileReader(file) );
		StringBuilder builder = new StringBuilder((int) file.length());
		
		while (reader.ready())
		{
			builder.append(reader.readLine());
		}
		
		_json = new JSONObject(builder.toString());

		System.out.println(_json.toString());
		
		reader.close();
		builder.setLength(0);
	}

	/**
	 * 
	 */
	public JSONObject getNetworks ()
	{
		try
		{
			return _json.getJSONObject("networks");
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
