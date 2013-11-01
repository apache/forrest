window.onload = function() {
  appendText1();
  appendText2();
  appendText3();
}
function appendText1() {
  var extraTextNode = document.createTextNode("from first JS resources");
  var theElement = document.getElementById("demo-js-css-1");
  theElement.appendChild(extraTextNode);
}
