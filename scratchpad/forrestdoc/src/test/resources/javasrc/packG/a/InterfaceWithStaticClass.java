// an interface containing a static class

package test.packG.a;

public interface InterfaceWithStaticClass
{
	public void f();

	public static class StaticMemberClass
	{
		public void g()
		{
			System.out.println("hello from StaticMemberClass.g");
		}
	}
}

