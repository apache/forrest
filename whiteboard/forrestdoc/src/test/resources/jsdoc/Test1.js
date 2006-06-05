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
/**
* @summary Functions to validate the values
* @summary Functions for formatting CPF, CNPJ, and CC. 
* 
*/
/*
Variable declaration:

The name of the variables has to start with a lower-case letter, 
and the words composing the name must not be separated by a space,
but should start with a higher-case letter. 

For every variable declaration, there should be included a 
comment on its functionality, as in the following example: 
*/

var customerName; // the name of the client
var customerAddress; // the office address of the client

/*
Function declaration

The name of the funcitions has to start with a lower-case letter, 
and the words composing the name must not be separated by a space,
but should start with a higher-case letter. 

For every function declaration, there should be included a 
comment on its functionality, as in the following example: 
*/
/**
* get the mode that has been activated
* @param x: a parameter											
* @return a return value
*/

function getMode(){
	...
}

/**
* test function			
* @param name: client name
* @param agency: an agency where the client can go
* @return the number of the client id
*/

function testFunction(name, agency)
{
	...
}

/*
The declaration of conditional blocks should have a 
comment on the entry condition for every case.
*/


if(navigator.appName == "Netscape"){	//if the browser is netscape
	self.window.close();
}else{ 					//if the browser is IE
	window.close();
}

