package test.packB;

import test.packA.*;

public class Bone extends Aone
{  
  private Aone _a;

  public Bone()
  {
    _a = foo();
  }

  public Aone foo()
  {
    return (new Aone());
  }

  public static void main(String args[])
  {
    Bone b = new Bone();
    b.init();
  }
}
