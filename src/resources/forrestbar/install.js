var err = initInstall("ForrestBar", "forrestbar", ""); 
//alert("initInstall");
addFile("forrestbar", "forrestbar.jar", getFolder("Chrome"),"");
//alert("addFile");	
registerChrome(CONTENT | DELAYED_CHROME, getFolder("Chrome","forrestbar.jar"), "content/forrestbar/");
//alert("registerChrome");
if (err==SUCCESS){  
  //alert("SUCCESS");
  performInstall();
} else {
  cancelInstall(err);
}
