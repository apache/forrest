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
//
// Comment
//
package test.packA;

import java.awt.event.*;

/**
 * One
 * Two
 * Three
 * Four
 */
public class Aone
{
  public static Aone getInstance() { return _instance; }

  public Aone()
  {
    System.out.println("Aone constructor");
    init();
    init("Hello");
  }

/*
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
  public void init()
  {
    System.out.println("A init");
  }
 */
  public void init()
  {
    System.out.println("A init");
    
    new WindowAdapter() {
            public void windowClosing(WindowEvent evt) { System.exit(0); }
        };
  }

  public void init(String s)
  {
    System.out.println(s);
  }

  public void init(String s1,String s2)
  {
    System.out.println(s1+s2);
  }

  public static void initStatics() 
  {
    _instance = new Aone();
  }

  private static Aone _instance;
}

/*
 * This is a comment.
 */
class Atwo
{

  public Atwo()
  {
  }

}
