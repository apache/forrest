/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
