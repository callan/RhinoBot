
var REQUEST_UNINITIALIZED = 0;
var REQUEST_LOADING       = 1;
var REQUEST_LOADED        = 2;
var REQUEST_INTERACTIVE   = 3;
var REQUEST_COMPLETE      = 4;

function onError (instance)
{
	java.lang.System.out.println(instance);
	java.lang.System.out.println(instance.hashCode());
}

/**
 * Wrapper for XMLHttpRequest object in browser that aren't IE-based
 *
 * @param String url
 * @return void
 */
function initXmlHttpRequest (url)
{
	try
	{
		requestObject = new XMLHttpRequest();
	}
	catch (Exception)
	{
		requestObject = null;
		return Exception;
	}
	
	requestObject.open('GET', url, true);
	requestObject.onreadystatechange = handleStateChange;
	requestObject.onerror = onError;
	requestObject.send('');
}

/**
 * Handles the state changes from XMLHttpRequest
 * @return void
 */
function handleStateChange ()
{
	if (requestObject.readyState == REQUEST_COMPLETE)
	{
		var db = new Database();
		db.delLongVar('XMLHttpRequest', 'message');
		db.setLongVar('XMLHttpRequest', 'message', new String(requestObject.responseText));
		
		try
		{
			var xml = requestObject.responseXML;
			
			if (requestObject.mimeType == 'text/xml')
			{
				globalBot.privmsg(globalChannel, "Second..");
				globalBot.privmsg(globalChannel, "Response Method: " + xml.method);
				globalBot.privmsg(globalChannel, "Response Result: " + xml.result);
			}
		}
		catch (e)
		{
			java.lang.System.err.println("Error: " +e);
		}
	}
	
	/*
    if ( (requestObject.readyState & REQUEST_COMPLETE) == REQUEST_COMPLETE )
    {
		// 200 = Okay
		// 404 = Not found
		// etc ..
		if ((requestObject.status == 200) || (requestObject.status == 200.0))
		{
			
        }
        else
        {
        }
    }
    */
}