
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
	navigate('http://xml.apache.org/news.html');
}

function navDownload() {
	navigate('http://xml.apache.org/dist/');
}

function navCode() {
	navigate('http://xml.apache.org/cvs.html');
}

function navTools() {
	navigate('http://nagoya.apache.org/');
}

function navAskSam() {
	navigate('mailto:forrest-user@xml.apache.org');
}


