/**
* @summary Funções de auxílio à cálculos monetários.
*/

Padrão de codificação JavaScript:

Declaração de variáveis:

O nome da variável deverá ser iniciado por letra minúscula,
e suas palavras não serão separadas por espaço, e sim pela
inicial da palavra maiúscula.
Para cada declaração de variável, deverá ser incluído um 
comentário com a sua funcionalidade, como no exemplo:

var nomeCliente; // armazena o nome do cliente
var endResidCliente; // armazena o endereço residencial do cliente;


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

