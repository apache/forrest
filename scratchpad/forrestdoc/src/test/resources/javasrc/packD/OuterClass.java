package test.packD;

public class OuterClass 
{
	public void f() 
	{
		InnerClass ic = new InnerClass();
		ic.g();
		pi = 2;
	}
	
	class InnerClass
	{
// 		InnerClass() 
// 		{
// 		}
		
		void g() 
		{
			System.out.println("you called InnerClass.g");
			pi = 3;
		}
		
	}

	public static void main(String args[])
	{
		//		new OuterClass().f();
		OuterClass oc = new OuterClass();
		oc.f();

		FileClass fc = new FileClass();
		fc.h();
	}

	public static int pi;
}

class FileClass
{
	public void h()
	{
		InnerClass ic = new InnerClass();
		ic.w();
		OuterClass.pi = 4;
	}
	
	class InnerClass
	{
		void w()
		{
			System.out.println("you called InnerClass.w");
			OuterClass.pi = 5;
		}
	}
}
