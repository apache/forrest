package test.packC;

import test.packB.*;
import test.packA.*;

public class Cone extends Bone
{
  private Bone b; 
  
  public Cone()
  {
  }

  public void bar()
  {
    b = new Bone();
    b.foo().init();

    String s = new String("Hello");
    System.out.println(s.length());

    Bone b2 = new Bone();
    b2.foo();
  }

  public static void main(String args[])
  {
    Cone c = new Cone();
    c.init();

    Aone.getInstance().init();
  }

  class ConeInner
  {
     private int mInnerInt;

     public void innerOne()
     {
        bar();
     }
  }
}
