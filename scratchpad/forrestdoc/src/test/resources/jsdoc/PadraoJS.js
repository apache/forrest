/**
* @summary Funções para validação de campos de formulário.afasdafasdfas
* @summary Funções para formatação de CPF, CNPJ, e CC. 
* 
*/

Padrão de codificação JavaScript:

Variable declaration:

The name of the variables has to start with a lower-case letter, 
and the words composing the name must not be separated by a space,
but should start with a higher-case letter. 

For every variable declaration, there should be included a 
comment on its functionality, as in the following example: 

var customerName; // the name of the client
var customerAddress; // the office address of the client


Declaração de funções:

O nome da função deverá ser iniciado por letra minúscula,
e suas palavras não serão separadas por espaço, e sim pela
inicial da palavra maiúscula.
Para cada declaração de função, deverá ser incluído um 
comentário com a sua funcionalidade, como no exemplo:

/**
* fecha uma modal que está ativada	
* @author Mailton Almeida											
* @param x: um parâmetro qualquer												
* @return um retorno qualquer 
*/

function fechaModal(){
	...
}

/**
* função de teste 					
* @author Mailton Almeida											
* @param nome: o nome do cliente
* @param agencia: a agencia onde o cliente possui conta
* @return a conta do cliente 
*/

function funcaoTeste(nome, agencia)
{
	...
}


Declaração de blocos condicionais deverão conter um
comentário ao final de cada bloco descrevendo a
condição de entrada para cada caso.
Trechos de código que pertencerem a um bloco condicional
deverão ser identados com um "tab".

Ex:

if(navigator.appName == "Netscape"){	//se o browser for o netscape	
	self.window.close();
}else{ 									//se o browser for o IE
	window.close();
}

