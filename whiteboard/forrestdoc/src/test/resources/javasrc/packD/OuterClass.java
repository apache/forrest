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
