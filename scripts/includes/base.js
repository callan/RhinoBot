
function assert (condition, bool)
{
	if (condition instanceof String)
		eval('condition = (' + condition + ');');

	if ( ((condition != true) && (condition != false)) ||
	     ((bool != true) && (bool != false)) )
		return;

	if (condition != bool)
		Rhino.assertionException();
}