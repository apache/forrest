function appendText2() {
  var extraTextNode = document.createTextNode("; from second JS resources");
  var theElement = document.getElementById("demo-js-css-1");
  theElement.appendChild(extraTextNode);
}
