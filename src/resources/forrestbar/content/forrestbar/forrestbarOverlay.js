
function startforrestbar()
{
	//alert("function init");
	if(document.getElementById("forrestbar").getAttribute("hidden") == "false")
	{
		removeEventListener ("load",startforrestbar, true); // Keep the event from firing a hundred more times.
	}
}

addEventListener("load", startforrestbar, true); // Run the startup function when the window loads
	
function navigate(url) {
	window._content.document.location.href = url;
}


/**************************/

function search(searchID)
{
	var searchItem = document.getElementById(searchID);
	navigate('http://www.google.com/search?q=' + searchItem.value);
}

function navProject(searchID) {
	var searchItem = document.getElementById(searchID);
	navigate(searchItem.selectedItem.value);
}

function navNews() {
	navigate('http://www.apache.org/foundation/news.html');
}

function navDownload() {
	navigate('http://www.apache.org/dyn/closer.cgi');
}

function navGumpNightly() {
	navigate('http://cvs.apache.org/builds/gump/latest/');
}
	
function navCode() {
	navigate('http://cvs.apache.org/viewcvs/');
}

function navTools() {
	navigate('http://nagoya.apache.org/');
}

function navLocalForrest() {
	navigate('http://127.0.0.1:8888/');
}

function navAskSam() {
	navigate('mailto:forrest-user@xml.apache.org');
}

