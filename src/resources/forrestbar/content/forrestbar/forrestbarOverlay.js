
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

