package test.packG.b;

import test.packG.a.InterfaceWithStaticClass;

public class Test implements InterfaceWithStaticClass
{
	public void f() 
	{
		InterfaceWithStaticClass.StaticMemberClass fismc = new InterfaceWithStaticClass.StaticMemberClass();
		fismc.g();
		System.out.println("hello from Test.f");
	}
}
