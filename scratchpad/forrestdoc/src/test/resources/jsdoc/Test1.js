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
* @author Mailton Almeida											
* @param x: a parameter											
* @return a return value
*/

function getMode(){
	...
}

/**
* test function			
* @author Mailton Almeida											
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

