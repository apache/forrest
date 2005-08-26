This doesn't *do* much at the moment.  It is supposed to be a start on a sitemap
analysis tool.  

Here's a wishlist of functionality for it:

o) Show all components and where they are defined so that duplicate definitions might
  be detected.  I think it's agreed that if components are used in more than one place
  it makes sense for them to be defined at the root sitemap.  By printing them out, we
  can see when components are defined in multiple places so that it'll be easier to 
  see if that's desireable. (partly working)

o) Show where components are actually used.
  -> Supports the identification of unused sitemap components.
  -> Supports identification of sitemap components that are only used in one place
      (important to see if the definition should be moved to a lower sitemap)
      
o) ...



Following command line assuming xalan is installed and it is run from $FORREST_HOME\etc\sitemap-analysis

>java org.apache.xalan.xslt.Process -IN ..\..\main\webapp\sitemap.xmap -XSL sitemap-evaluator.xsl -OUT output.html