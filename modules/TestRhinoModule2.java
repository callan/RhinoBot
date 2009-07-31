import org.mozilla.javascript.ScriptableObject;

public class TestRhinoModule2 extends ScriptableObject
{
	public TestRhinoModule2 ()
	{
	
	}
	
	public String getClassName ()
	{
		return "TestRhinoModule2";
	}
	
	public void jsConstructor ()
	{
		System.out.println("TestRhinoModule2 called!");
	}
}