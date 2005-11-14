/* slidy.js

   Copyright (c) 2005 W3C (MIT, ERCIM, Keio), All Rights Reserved.
   W3C liability, trademark, document use and software licensing
   rules apply, see:

   http://www.w3.org/Consortium/Legal/copyright-documents
   http://www.w3.org/Consortium/Legal/copyright-software
*/

var ns_pos = (typeof window.pageYOffset!='undefined');
var khtml = ((navigator.userAgent).indexOf("KHTML") >= 0 ? true : false);

window.onload = startup; // equivalent to onload on body element

var slidenum = 0;     // integer slide count: 0, 1, 2, ...
var slides;           // set to array of slide div's
var slideNumElement;  // element containing slide number
var notes;            // set to array of handout div's
var backgrounds;      // set to array of background div's
var toolbar;          // element containing toolbar
var title;            // document title
var lastShown = null; // last incrementally shown item
var eos = null;       // span element for end of slide indicator

var viewAll = 0;      // 1 to view all slides + handouts
var wantToolbar = 1;   // 0 if toolbar isn't wanted
var mouseClickEnabled = true;  // enables left click for next slide
var scrollhack = 0;   // IE work around for position: fixed

var helpPage = "http://www.w3.org/Talks/Tools/Slidy/help.html";
var helpText = "Navigate with click, space bar, Pg Up or Pg Dn." +
               " Use < and > (or - and + on number pad) to change font size.";

var sizeIndex = 0;
var sizes = new Array("10pt", "12pt", "14pt", "16pt", "18pt",
                      "20pt", "22pt", "24pt", "26pt");

// the corresponding expected toolbar height in pixels
// as measured with Internet Explorer and Galeon

var toolbarHeight = new Array(10,12,13,15,16,19,21,23,25);

var okayForIncremental = incrementalElementList();

// needed for efficient resizing
var lastWidth = 0;
var lastHeight = 0;

// Needed for cross browser support for relative width/height on
// object elements. The work around is to save width/height attributes
// and then to recompute absolute width/height dimensions on resizing
var objects;

/* general initialization */
function startup()
{
   title = document.title;
   toolbar = addToolbar();
   slides = collectSlides();
   notes = collectNotes();
   objects = document.body.getElementsByTagName("object");
   backgrounds = collectBackgrounds();
   patchAnchors();
   slidenum = findSlideNumber(location.href);

   if (slides.length > 0)
   {
      var slide = slides[slidenum];
      slide.style.position = "absolute";
   
      if (slidenum > 0)
      {
         setVisibilityAllIncremental("visible");
         lastShown = previousIncrementalItem(null);
         setEosStatus(true);
      }
      else
      {
         lastShown = null;
         setVisibilityAllIncremental("hidden");
         setEosStatus(!nextIncrementalItem(lastShown));
      }

      setLocation();
   }

   // bind even handlers
   document.onclick = mouseButtonClick;
   document.onkeydown = keyDown;
   window.onresize  = resized;
   window.onscroll = scrolled;
   singleSlideView();

   setLocation();
   resized();
   showToolbar();
   //fontAdjust();


   //if (!ns_pos)
   //   setTimeout(ieHack, 100);
}

// hack to persuade IE to compute correct document height
// as needed for simulating fixed positioning of toolbar
function ieHack()
{
   window.resizeBy(0,-1);
   window.resizeBy(0, 1);
}

// Safari and Konqueror don't yet support getComputedStyle()
// and they always reload page when location.href is updated
function isKHTML()
{
   var agent = navigator.userAgent;
   return (agent.indexOf("KHTML") >= 0 ? true : false);
}

// hack to work around variation in point size to pixels as
// Opera/Linux displays fonts 30% smaller than other browsers

function fontAdjust()
{
   var fontAdjustment = toolbarHeight[sizeIndex] / toolbar.offsetHeight;
   //alert("fontAdjustment is " + fontAdjustment);

   if (fontAdjustment > 1.1)
   {
      // calculated adjustment varies with Opera's window size
      // which causes problems, so let's fix it to 1.3
      fontAdjustment = 1.3;

      for (var i = 0; i < sizes.length; ++i)
      {
         size = parseInt(sizes[i]);
         sizes[i] = Math.round(fontAdjustment * size) + "pt";
      }

      document.body.style.fontSize = sizes[sizeIndex];

      // force correct positioning of toolbar
      setTimeout(showToolbar, 500);
      //alert("adjustment is " + fontAdjustment);
   }

   showToolbar();
}

function resized()
{
   var width = 0;

   if ( typeof( window.innerWidth ) == 'number' )
      width = window.innerWidth;  // Non IE browser
   else if (document.documentElement && document.documentElement.clientWidth)
      width = document.documentElement.clientWidth;  // IE6
   else if (document.body && document.body.clientWidth)
      width = document.body.clientWidth; // IE4

   var height = 0;

   if ( typeof( window.innerHeight ) == 'number' )
      height = window.innerHeight;  // Non IE browser
   else if (document.documentElement && document.documentElement.clientHeight)
      height = document.documentElement.clientHeight;  // IE6
   else if (document.body && document.body.clientHeight)
      height = document.body.clientHeight; // IE4

   // IE fires onresize even when only font size is changed!
   // so we do a check to avoid blocking < and > actions
   if (width != lastWidth || height != lastHeight)
   {
      if (width > 1100)
         sizeIndex = 4;
      else if (width > 1000)
         sizeIndex = 3;
      else if (width > 800)
         sizeIndex = 2;
      else if (width > 600)
         sizeIndex = 1;
      else if (width)
         sizeIndex = 0;

      // enables cross browser use of relative width/height
      // on object elements for use with SVG and Flash media
      adjustObjectDimensions(width, height);

      document.body.style.fontSize = sizes[sizeIndex];

      lastWidth = width;
      lastHeight = height;

      // force reflow to work around Mozilla bug
      //if (ns_pos)
      {
         var slide = slides[slidenum];
         hideSlide(slide);
         showSlide(slide);
      }

      // force correct positioning of toolbar
      refreshToolbar(200);
   }
}

function scrolled()
{
   if (toolbar && !ns_pos)
   {
      // hide toolbar
      toolbar.style.display = "none";

      // make it reappear later
      if (scrollhack == 0 && !viewAll)
      {
         setTimeout(showToolbar, 1000);
         scrollhack = 1;
      }
   }
}

// used to ensure IE refreshes toolbar in correct position
function refreshToolbar(interval)
{
   hideToolbar();
   setTimeout(showToolbar, interval);
}

// restores toolbar after short delay
function showToolbar()
{
   if (wantToolbar)
   {
      if (!ns_pos)
      {
         // adjust position to allow for scrolling
         var xoffset = scrollXOffset();
         toolbar.style.left = xoffset;
         toolbar.style.right = xoffset;

         // determine vertical scroll offset
         //var yoffset = scrollYOffset();

         // bottom is doc height - window height - scroll offset
         //var bottom = documentHeight() - lastHeight - yoffset

         //if (yoffset > 0 || documentHeight() > lastHeight)
         //   bottom += 16;  // allow for height of scrollbar

         toolbar.style.bottom = 0; //bottom;
      }

      toolbar.style.display = "block";
      toolbar.style.visibility = "visible";
   }

   scrollhack = 0;
}

function test()
{
   var s = "docH: " + documentHeight() +
       " winH: " + lastHeight +
       " yoffset: " + scrollYOffset() +
       " toolbot: " + (documentHeight() - lastHeight - scrollYOffset());

   //alert(s);

   var slide = slides[slidenum];
   // IE getAttribute requires "class" to be "className"
   var name = ns_pos ? "class" : "className";
   var style = (slide.currentStyle ? slide.currentStyle["backgroundColor"] :
       document.defaultView.getComputedStyle(slide, '').getPropertyValue("background-color"));
   alert("class='" + slide.getAttribute(name) + "' backgroundColor: " + style);
}

function hideToolbar()
{
   toolbar.style.display = "none";
   toolbar.style.visibility = "hidden";
}

// invoked via F key
function toggleToolbar()
{
   if (!viewAll)
   {
      if (toolbar.style.display == "none")
      {
         toolbar.style.display = "block";
         toolbar.style.visibility = "visible";
         wantToolbar = 1;
      }
      else
      {
         toolbar.style.display = "none";
         toolbar.style.visibility = "hidden";
         wantToolbar = 0;
      }
   }
}

function scrollXOffset()
{
   if (window.pageXOffset)
      return self.pageXOffset;

   if (document.documentElement && 
             document.documentElement.scrollLeft)
      return document.documentElement.scrollLeft;

   if (document.body)
      return document.body.scrollLeft;

    return 0;
}


function scrollYOffset()
{
   if (window.pageYOffset)
      return self.pageYOffset;

   if (document.documentElement && 
             document.documentElement.scrollTop)
      return document.documentElement.scrollTop;

   if (document.body)
      return document.body.scrollTop;

    return 0;
}

function documentHeight()
{
   var sh, oh;

   sh = document.body.scrollHeight;
   oh = document.body.offsetHeight;

   if (sh && oh)
   {
      return (sh > oh ? sh : oh);
   }

   // no idea!
   return 0;
}

// these don't work well on Opera, for which
// we take advantage on @media projection
function smaller()
{
   if (sizeIndex > 0)
   {
      --sizeIndex;
   }

   toolbar.style.display = "none";
   document.body.style.fontSize = sizes[sizeIndex];
   var slide = slides[slidenum];
   hideSlide(slide);
   showSlide(slide);
   setTimeout(showToolbar, 300);
}

function bigger()
{
   if (sizeIndex < sizes.length - 1)
   {
      ++sizeIndex;
   }

   toolbar.style.display = "none";
   document.body.style.fontSize = sizes[sizeIndex];
   var slide = slides[slidenum];
   hideSlide(slide);
   showSlide(slide);
   setTimeout(showToolbar, 300);
}

// enables cross browser use of relative width/height
// on object elements for use with SVG and Flash media
// with thanks to Ivan Herman for the suggestion
function adjustObjectDimensions(width, height)
{
   for( var i = 0; i < objects.length; i++ )
   {
      var obj = objects[i];
      var mimeType = obj.getAttribute("type");

      if (mimeType == "image/svg+xml" || mimeType == "application/x-shockwave-flash")
      {
         if ( !obj.initialWidth ) 
            obj.initialWidth = obj.getAttribute("width");

         if ( !obj.initialHeight ) 
            obj.initialHeight = obj.getAttribute("height");

         if ( obj.initialWidth && obj.initialWidth.charAt(obj.initialWidth.length-1) == "%" )
         {
            var w = parseInt(obj.initialWidth.slice(0, obj.initialWidth.length-1));
            var newW = width * (w/100.0);
            obj.setAttribute("width",newW);
         }

         if ( obj.initialHeight && obj.initialHeight.charAt(obj.initialHeight.length-1) == "%" )
         {
            var h = parseInt(obj.initialHeight.slice(0, obj.initialHeight.length-1));
            var newH = height * (h/100.0);
            obj.setAttribute("height", newH);
         }
      }
   }
}

function cancel(event)
{
  event.cancel = true;
  event.returnValue = false;
  return false;
}

//  See e.g. http://www.quirksmode.org/js/events/keys.html for keycodes
function keyDown(event)
{
    var key;

    if (!event)
      var event = window.event;

    // kludge around NS/IE differences 
    if (window.event)
       key = window.event.keyCode;
    else if (event.which)
       key = event.which;
    else
       return true; // Yikes! unknown browser

    // ignore event if key value is zero
    // as for alt on Opera and Konqueror
    if (!key)
       return true;

    // check for concurrent control/command/alt key
    // but are these only present on mouse events?

    if (event.ctrlKey || event.altKey)
       return true;

    if (key == 34) // Page Down
    {
       nextSlide(false);
       return cancel(event);
    }
    else if (key == 33) // Page Up
    {
       previousSlide(false);
       return cancel(event);
    }
    else if (key == 32) // space bar
    {
       nextSlide(true);
       return cancel(event);
    }
    else if (key == 37) // Left arrow
    {
       previousSlide(true);
       return cancel(event);
    }
    else if (key == 36) // Home
    {
       firstSlide();
       return cancel(event);
    }
    else if (key == 35) // End
    {
       lastSlide();
       return cancel(event);
    }
    else if (key == 39) // Right arrow
    {
       nextSlide(true);
       return cancel(event);
    }
    else if (key == 188)  // < for smaller fonts
    {
       smaller();
       return cancel(event);
    }
    else if (key == 190)  // > for larger fonts
    {
       bigger();
       return cancel(event);
    }
    else if (key == 189 || key == 109)  // - for smaller fonts
    {
       smaller();
       return cancel(event);
    }
    else if (key == 187 || key == 191 || key == 107)  // = +  for larger fonts
    {
       bigger();
       return cancel(event);
    }
    else if (key == 86)  // V for smaller fonts
    {
       smaller();
       return cancel(event);
    }
    else if (key == 66)  // B for larger fonts
    {
       bigger();
       return cancel(event);
    }
    else if (key == 90)  // Z for last slide
    {
       lastSlide();
       return cancel(event);
    }
    else if (key == 70)  // F for toggle toolbar
    {
       toggleToolbar();
       return cancel(event);
    }
    else if (key == 65)  // A for toggle view single/all slides
    {
       toggleView();
       return cancel(event);
    }
    else if (key == 75)  // toggle action of left click for next page
    {
       mouseClickEnabled = !mouseClickEnabled;
       alert((mouseClickEnabled ? "enabled" : "disabled") +  " mouse click advance");
       return cancel(event);
    }
    else if (key == 84)  // T for test
    {
       test();
       return cancel(event);
    }
    //else alert("key code is "+ key);

    return true;
}

// right mouse button click is reserved for context menus
// it is more reliable to detect rightclick than leftclick
function mouseButtonClick(e)
{
   var rightclick = false;
   var target;

   if (!e)
      var e = window.event;

   if (e.target)
      target = e.target;
   else if (e.srcElement)
      target = e.srcElement;

   // work around Safari bug
   if (target.nodeType == 3)
      target = target.parentNode;

   if (e.which)
      rightclick = (e.which == 3);
   else if (e.button)
      rightclick = (e.button == 2);

   // check if target is something that probably want's clicks
   // e.g. embed, object, input, textarea, select, option

   if (mouseClickEnabled && !rightclick &&
        target.nodeName != "EMBED" &&
        target.nodeName != "OBJECT" &&
        target.nodeName != "INPUT" &&
        target.nodeName != "TEXTAREA" &&
        target.nodeName != "SELECT" &&
        target.nodeName != "OPTION")
   {
      nextSlide(true);
      stopPropagation(e);
      e.cancel = true;
      e.returnValue = false;
   }
}

function previousSlide(incremental)
{
   if (!viewAll)
   {
      var slide;

      if ((incremental || slidenum == 0) && lastShown != null)
      {
         lastShown = hidePreviousItem(lastShown);
         setEosStatus(false);
      }
      else if (slidenum > 0)
      {
         slide = slides[slidenum];
         hideSlide(slide);

         slidenum = slidenum - 1;
         slide = slides[slidenum];
         setVisibilityAllIncremental("visible");
         lastShown = previousIncrementalItem(null);
         setEosStatus(true);
         showSlide(slide);
      }

      setLocation();

      if (!ns_pos)
         refreshToolbar(200);
   }
}

function nextSlide(incremental)
{
   if (!viewAll)
   {
      var slide;

      if (incremental || slidenum == slides.length - 1)
         lastShown = revealNextItem(lastShown);

      if ((!incremental || lastShown == null) && slidenum < slides.length - 1)
      {
         slide = slides[slidenum];
         hideSlide(slide);

         slidenum = slidenum + 1;
         slide = slides[slidenum];
         lastShown = null;
         setVisibilityAllIncremental("hidden");
         showSlide(slide);
      }

      setLocation();

      setEosStatus(!nextIncrementalItem(lastShown));

      if (!ns_pos)
         refreshToolbar(200);
   }
}

// to first slide with nothing revealed
// i.e. state at start of presentation
function firstSlide()
{
   if (!viewAll)
   {
      var slide;

      if (slidenum != 0)
      {
         slide = slides[slidenum];
         hideSlide(slide);

         slidenum = 0;
         slide = slides[slidenum];
         lastShown = null;
         setVisibilityAllIncremental("hidden");
         showSlide(slide);
      }

      setEosStatus(!nextIncrementalItem(lastShown));
      setLocation();
   }
}


// to last slide with everything revealed
// i.e. state at end of presentation
function lastSlide()
{
   if (!viewAll)
   {
      var slide;

      lastShown = null; //revealNextItem(lastShown);

      if (lastShown == null && slidenum < slides.length - 1)
      {
         slide = slides[slidenum];
         hideSlide(slide);
         slidenum = slides.length - 1;
         slide = slides[slidenum];
         setVisibilityAllIncremental("visible");
         lastShown = previousIncrementalItem(null);

         showSlide(slide);
      }
      else
      {
         setVisibilityAllIncremental("visible");
         lastShown = previousIncrementalItem(null);
      }

      setEosStatus(true);
      setLocation();
   }
}

function setEosStatus(state)
{
   if (eos)
      eos.style.color = (state ? "rgb(240,240,240)" : "red");
}

function showSlide(slide)
{
   syncBackground(slide);
   slide.style.visibility = "visible";
   slide.style.display = "block";
}

function hideSlide(slide)
{
   slide.style.visibility = "hidden";
   slide.style.display = "none";
}

function toggleView()
{
   if (viewAll)
   {
      singleSlideView();
      showToolbar();
      viewAll = 0;
   }
   else
   {
      showAllSlides();
      hideToolbar();
      viewAll = 1;
   }
}

// prepare for printing
function showAllSlides()
{
   var slide;

   for (var i = 0; i < slides.length; ++i)
   {
      slide = slides[i];

      slide.style.position = "relative";
      slide.style.borderTopStyle = "solid";
      slide.style.borderTopWidth = "thin";
      slide.style.borderTopColor = "black";

      setVisibilityAllIncremental("visible");
      showSlide(slide);
   }

   var note;

   for (var i = 0; i < notes.length; ++i)
   {
      showSlide(notes[i]);
   }
}

// restore after printing
function singleSlideView()
{
   var slide;

   for (var i = 0; i < slides.length; ++i)
   {
      slide = slides[i];

      slide.style.position = "absolute";

      if (i == slidenum)
      {
         slide.style.borderStyle = "none";
         showSlide(slide);
      }
      else
      {
         slide.style.borderStyle = "none";
         hideSlide(slide);
      }
   }

   setVisibilityAllIncremental("visible");
   lastShown = previousIncrementalItem(null);

   var note;

   for (var i = 0; i < notes.length; ++i)
   {
      hideSlide(notes[i]);
   }
}

// the string str is a whitespace separated list of tokens
// test if str contains a particular token, e.g. "slide"
function hasToken(str, token)
{
   if (str)
   {
      // define pattern as regular expression
      var pattern = /\w+/g;

      // check for matches
      // place result in array
      var result = str.match(pattern);

      // now check if desired token is present
      for (var i = 0; i < result.length; i++)
      {
         if (result[i] == token)
            return true;
      }
   }

   return false;
}

// return new array of all slides
function collectSlides()
{
   var slides = new Array();
   var divs = document.body.getElementsByTagName("div");

   // IE getAttribute requires "class" to be "className"
   var name = ns_pos ? "class" : "className";

   for (var i = 0; i < divs.length; ++i)
   {
      div = divs.item(i);

      if (hasToken(div.getAttribute(name), "slide"))
      {
         // add slide to collection
         slides[slides.length] = div;

         // hide each slide as it is found
         div.style.display = "none";
         div.style.visibility = "hidden";
         // add dummy <br/> at end for scrolling hack
         var node = document.createElement("br");
         div.appendChild(node);
      }
   }

   return slides;
}

// return new array of all <div class="handout">
function collectNotes()
{
   var notes = new Array();
   var divs = document.body.getElementsByTagName("div");

   // IE getAttribute requires "class" to be "className"
   var name = ns_pos ? "class" : "className";

   for (var i = 0; i < divs.length; ++i)
   {
      div = divs.item(i);

      if (hasToken(div.getAttribute(name), "handout"))
      {
         // add slide to collection
         notes[notes.length] = div;

         // hide handout notes as they are found
         div.style.display = "none";
         div.style.visibility = "hidden";
      }
   }

   return notes;
}

// return new array of all <div class="background">
// including named backgrounds e.g. class="background titlepage"
function collectBackgrounds()
{
   var backgrounds = new Array();
   var divs = document.body.getElementsByTagName("div");

   // IE getAttribute requires "class" to be "className"
   var name = ns_pos ? "class" : "className";
   var background;

   for (var i = 0; i < divs.length; ++i)
   {
      div = divs.item(i);

      background = div.getAttribute(name);

      if (hasToken(background, "background"))
      {
         // add slide to collection
         backgrounds[backgrounds.length] = div;

         // hide named backgrounds as they are found
         // e.g. class="background epilog"
         if (background != "background")
         {
            div.style.display = "none";
            div.style.visibility = "hidden";
         }
      }
   }

   return backgrounds;
}

// show just the backgrounds pertinent to this slide
function syncBackground(slide)
{
   var background;
   var bgColor;

   if (slide.currentStyle)
      bgColor = slide.currentStyle["backgroundColor"];
   else if (document.defaultView)
   {
      var styles = document.defaultView.getComputedStyle(slide,null);

      if (styles)
          bgColor = styles.getPropertyValue("background-color");
      else // broken implementation probably due Safari or Konqueror
      {
          //alert("defective implementation of getComputedStyle()");
          bgColor = "transparent";
      }
   }
   else
      bgColor == "transparent";

   if (bgColor == "transparent")
   {
      // IE getAttribute requires "class" to be "className"
      var name = ns_pos ? "class" : "className";
      var slideClass = slide.getAttribute(name);
      var bgClass;

      for (var i = 0; i < backgrounds.length; i++)
      {
         background = backgrounds[i];

         bgClass = background.getAttribute(name);

         if (matchingBackground(slideClass, bgClass))
         {
            background.style.display = "block";
            background.style.visibility = "visible";
         }
         else
         {
            background.style.display = "none";
            background.style.visibility = "hidden";
         }
      }
   }
   else // forcibly hide all backgrounds
   {
      for (var i = 0; i < backgrounds.length; i++)
      {
         background = backgrounds[i];
         background.style.display = "none";
         background.style.visibility = "hidden";
      }
   }
}

// compare classes for slide and background
function matchingBackground(slideClass, bgClass)
{
   if (bgClass == "background")
      return true;

   // define pattern as regular expression
   var pattern = /\w+/g;

   // check for matches and place result in array
   var result = slideClass.match(pattern);

   // now check if desired name is present for background
   for (var i = 0; i < result.length; i++)
   {
      if (hasToken(bgClass, result[i]))
         return true;
   }

   return false;
}

// left to right traversal of root's content
function nextNode(root, node)
{
   if (node == null)
      return root.firstChild;

   if (node.firstChild)
      return node.firstChild;

   if (node.nextSibling)
      return node.nextSibling;

   for (;;)
   {
      node = node.parentNode;

      if (node == root)
         return null;

      if (node.nextSibling)
         return node.nextSibling;
   }

   return null;
}

// right to left traversal of root's content
function previousNode(root, node)
{
   if (node == null)
   {
      node = root.lastChild;

      if (node)
      {
         while (node.lastChild)
            node = node.lastChild;
      }

      return node;
   }

   if (node.previousSibling)
   {
      node = node.previousSibling;

      while (node.lastChild)
         node = node.lastChild;

      return node;
   }

   if (node.parentNode != root)
      return node.parentNode;

   return null;
}

// HTML elements that can be used with class="incremental"
// note that you can also put the class on containers like
// up, ol, dl, and div to make their contents appear
// incrementally. Upper case is used since this is what
// browsers report for HTML node names (text/html).
function incrementalElementList()
{
   var inclist = new Array();
   inclist["P"] = true;
   inclist["PRE"] = true;
   inclist["LI"] = true;
   inclist["BLOCKQUOTE"] = true;
   inclist["DT"] = true;
   inclist["DD"] = true;
   inclist["H2"] = true;
   inclist["H3"] = true;
   inclist["H4"] = true;
   inclist["H5"] = true;
   inclist["H6"] = true;
   inclist["SPAN"] = true;
   inclist["ADDRESS"] = true;
   inclist["TABLE"] = true;
   inclist["TR"] = true;
   inclist["TH"] = true;
   inclist["TD"] = true;
   inclist["IMG"] = true;
   inclist["OBJECT"] = true;
   return inclist;
}

function nextIncrementalItem(node)
{
   var slide = slides[slidenum];

   // IE getAttribute requires "class" to be "className"
   var classattr = ns_pos ? "class" : "className";


   for (;;)
   {
      node = nextNode(slide, node);

      if (node == null || node.parentNode == null)
         break;

      if (node.nodeType == 1)  // ELEMENT
      {
         if (hasToken(node.parentNode.getAttribute(classattr), "incremental")
             && node.nodeName != "BR")
            return node;

         if (hasToken(node.getAttribute(classattr), "incremental")
             && okayForIncremental[node.nodeName])
         {
            return node;
         }
      }
   }

   return node;
}

function previousIncrementalItem(node)
{
   var slide = slides[slidenum];

   // IE getAttribute requires "class" to be "className"
   var classattr = ns_pos ? "class" : "className";


   for (;;)
   {
      node = previousNode(slide, node);

      if (node == null || node.parentNode == null)
         break;

      if (node.nodeType == 1)
      {
         if (hasToken(node.parentNode.getAttribute(classattr), "incremental")
             && node.nodeName != "BR")
            return node;

         if (hasToken(node.getAttribute(classattr), "incremental")
             && okayForIncremental[node.nodeName])
         {
            return node;
         }
      }
   }

   return node;
}

// set visibility for all elements on current slide with
// a parent element with attribute class="incremental"
function setVisibilityAllIncremental(value)
{
   var node = nextIncrementalItem(null);

   while (node)
   {
      node.style.visibility = value;
      node = nextIncrementalItem(node);
   }
}

// reveal the next hidden item on the slide
// node is null or the node that was last revealed
function revealNextItem(node)
{
   node = nextIncrementalItem(node);

   if (node && node.nodeType == 1)  // an element
      node.style.visibility = "visible";

   return node;
}


// exact inverse of revealNextItem(node)
function hidePreviousItem(node)
{
   if (node && node.nodeType == 1)  // an element
      node.style.visibility = "hidden";

   return previousIncrementalItem(node);
}


/* set click handlers on all anchors */
function patchAnchors()
{
   var anchors = document.body.getElementsByTagName("a");

   for (var i = 0; i < anchors.length; ++i)
   {
      anchors[i].onclick = clickedAnchor;
   }
}

function clickedAnchor(e)
{
   if (!e)
      var e = window.event;

   // compare this.href with location.href
   // for link to another slide in this doc

   if (pageAddress(this.href) == pageAddress(location.href))
   {
      // yes, so find new slide number
      var newslidenum = findSlideNumber(this.href);

      if (newslidenum != slidenum)
      {
         slide = slides[slidenum];
         hideSlide(slide);
         slidenum = newslidenum;
         slide = slides[slidenum];
         showSlide(slide);
         setLocation();
      }
   }
   else if (this.target == null)
      location.href = this.href;

   this.blur();
   stopPropagation(e);
}

function pageAddress(uri)
{
   var i = uri.indexOf("#");

   // check if anchor is entire page

   if (i < 0)
      return uri;  // yes

   return uri.substr(0, i);
}

function showSlideNumber()
{
   slideNumElement.innerHTML = "slide " +
           (slidenum + 1) + "/" + slides.length;
}

function setLocation()
{
   var uri = pageAddress(location.href);

   if (slidenum > 0)
      uri = uri + "#[" + (slidenum+1) + "]";

   if (uri != location.href && !khtml)
      location.href = uri;

   document.title = title + " [" + (slidenum+1) + "]";
   //document.title = (slidenum+1) + ") " + slideName();

   showSlideNumber();
}

// find current slide based upon location
// first find target anchor and then look
// for associated div element enclosing it
// finally map that to slide number
function findSlideNumber(uri)
{
   // first get anchor from page location

   var i = uri.indexOf("#");

   // check if anchor is entire page

   if (i < 0)
      return 0;  // yes

   var anchor = uri.substr(i+1);

   // now use anchor as XML ID to find target
   var target = document.getElementById(anchor);

   if (!target)
   {
      // does anchor look like "[2]" for slide 2 ??
      // where first slide is [1]
      var re = /\[(\d)+\]/;

      if (anchor.match(re))
      {
         var num = parseInt(anchor.substring(1, anchor.length-1));

         if (num > slides.length)
            num = 1;

         if (--num < 0)
            num = 0;

         return num;
      }

      // oh dear unknown anchor
      return 0;
   }

   // search for enclosing slide

   // IE getAttribute requires "class" to be "className"
   var name = ns_pos ? "class" : "className";

   while (true)
   {
      // browser coerces html elements to uppercase!
      if (target.nodeName.toLowerCase() == "div" &&
          target.getAttribute(name) == "slide")
      {
         // found the slide element
         break;
      }

      // otherwise try parent element if any

      target = target.parentNode;

      if (!target)
      {
         return 0;   // no luck!
      }
   };

   for (i = 0; i < slides.length; ++i)
   {
      if (slides[i] == target)
         return i;  // success
   }

   // oh dear still no luck
   return 0;
}

// find slide name from h1 element
// should allow for h1 within a div (FIX ME)
// default to document title
function slideName()
{
   var nodes = slides[slidenum].childNodes

   for (var i = 0; i < nodes.length; ++i)
   {
      var node = nodes[i];

      if (node.nodeType == 1 &&
           (node.nodeName == "H1" || node.nodeName == "h1"))
         return node.innerHTML;
   }

   return title;
}

// find copyright text from meta element
function findCopyright()
{
   var name, content;
   var meta = document.getElementsByTagName("meta");

   for (var i = 0; i < meta.length; ++i)
   {
      name = meta[i].getAttribute("name");
      content = meta[i].getAttribute("content");

      if (name == "copyright")
         return content;
   }

   return null;
}

function addToolbar()
{
   var slideCounter, page;

   var toolbar = createElement("div");
   toolbar.setAttribute("class", "toolbar");

   if (ns_pos) // a reasonably behaved browser
   {
      var right = document.createElement("div");
      right.setAttribute("style", "float: right; text-align: right");

      slideCounter = document.createElement("div")
      slideCounter.innerHTML = "slide n/m";
      right.appendChild(slideCounter);
      toolbar.appendChild(right);

      var left = document.createElement("div");
      left.setAttribute("style", "text-align: left");

      // global end of slide indicator
      eos = document.createElement("span");
      eos.innerHTML = "* ";
      left.appendChild(eos);

      var help = document.createElement("a");
      help.setAttribute("href", helpPage);
      help.setAttribute("title", helpText);
      help.innerHTML = "help?";
      left.appendChild(help);

      var gap = document.createTextNode(" ");
      left.appendChild(gap);

      var i = location.href.indexOf("#");

      // check if anchor is entire page

      if (i > 0)
         page = location.href.substr(0, i);
      else
         page = location.href;

      var start = document.createElement("a");
      start.setAttribute("href", page);
      start.setAttribute("title", "restart presentation");
      start.innerHTML = "restart?";
      left.appendChild(start);

      var copyright = findCopyright();

      if (copyright)
      {
         var span = document.createElement("span");
         span.innerHTML = copyright;
         span.style.color = "black";
         span.style.marginLeft = "4em";
         left.appendChild(span);
      }

      toolbar.appendChild(left);
   }
   else // IE so need to work around its poor CSS support
   {
      toolbar.style.position = "absolute";
      toolbar.style.zIndex = "200";
      toolbar.style.width = "100%";
      toolbar.style.height = "1.2em";
      toolbar.style.top = "auto";
      toolbar.style.bottom = "0";
      toolbar.style.left = "0";
      toolbar.style.right = "0";
      toolbar.style.textAlign = "left";
      toolbar.style.fontSize = "60%";
      toolbar.style.color = "red";
      toolbar.borderWidth = 0;
      toolbar.style.background = "rgb(240,240,240)";

      // would like to have help text left aligned
      // and page counter right aligned, floating
      // div's don't work, so instead use nested
      // absolutely positioned div's.

      var sp = document.createElement("span");
      sp.innerHTML = "&nbsp;&nbsp;*&nbsp;";
      toolbar.appendChild(sp);
      eos = sp;  // end of slide indicator

      var help = document.createElement("a");
      help.setAttribute("href", helpPage);
      help.setAttribute("title", helpText);
      help.innerHTML = "help?";
      toolbar.appendChild(help);

      var gap = document.createTextNode(" ");
      toolbar.appendChild(gap);

      var i = location.href.indexOf("#");

      // check if anchor is entire page

      if (i > 0)
         page = location.href.substr(0, i);
      else
         page = location.href;

      var start = document.createElement("a");
      start.setAttribute("href", page);
      start.setAttribute("title", "restart presentation");
      start.innerHTML = "restart?";
      toolbar.appendChild(start);

      var copyright = findCopyright();

      if (copyright)
      {
         var span = document.createElement("span");
         span.innerHTML = copyright;
         span.style.color = "black";
         span.style.marginLeft = "2em";
         toolbar.appendChild(span);
      }

      slideCounter = document.createElement("div")
      slideCounter.style.position = "absolute";
      slideCounter.style.width = "auto"; //"20%";
      slideCounter.style.height = "1.2em";
      slideCounter.style.top = "auto";
      slideCounter.style.bottom = 0;
      slideCounter.style.right = "0";
      slideCounter.style.textAlign = "right";
      slideCounter.style.color = "red";
      slideCounter.style.background = "rgb(240,240,240)";

      slideCounter.innerHTML = "slide n/m";
      toolbar.appendChild(slideCounter);
   }

   // ensure that click isn't passed through to the page
   toolbar.onclick = stopPropagation;
   document.body.appendChild(toolbar);
   slideNumElement = slideCounter;
   setEosStatus(false);

   return toolbar;
}

function replaceByNonBreakingSpace(str)
{
   for (var i = 0; i < str.length; ++i)
      str[i] = 160;
}

function stopPropagation(e)
{
   if (window.event)
   {
      window.event.cancelBubble = true;
      //window.event.returnValue = false;
   }
   else
   {
      e.cancelBubble = true;
      e.stopPropagation();
      //e.preventDefault();
   }
}

// works with text/html and text/xhtml+xml with thanks to Simon Willison
function createElement(element)
{
   if (typeof document.createElementNS != 'undefined')
   {
      return document.createElementNS('http://www.w3.org/1999/xhtml', element);
   }

   if (typeof document.createElement != 'undefined')
   {
      return document.createElement(element);
   }

   return false;
}

// designed to work with both text/html and text/xhtml+xml
function getElementsByTagName(name)
{
   if (typeof document.getElementsByTagNameNS != 'undefined')
   {
      return document.getElementsByTagNameNS('http://www.w3.org/1999/xhtml', name);
   }

   if (typeof document.getElementsByTagName != 'undefined')
   {
      return document.getElementsByTagName(name);
   }

   return null;
}

// clean alternative to innerHTML method, but on IE6
// it doesn't work with named entities like &nbsp;
// which need to be replaced by numeric entities
function insertText(element, text)
{
   if (element.textContent)  // DOM3 only
      element.textContent = text;
   else
   {
      if (element.firstChild)
      {
         // remove current children
         while (element.firstChild)
            element.removeChild(element.firstChild);
      }

      element.appendChild(document.createTextNode(text));
   }
}
