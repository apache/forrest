#!/bin/sh
echo "Fetching latest FOP and patched Cocoon jars..."
cd ../lib/core && cvs up -r with_pdf_images fop-20030320.jar cocoon-20030311.jar \
 cocoon-fop-block-20030311.jar
echo "... done"
echo "Now you must:"
echo " - download JIMI from http://java.sun.com/products/jimi/ and add jimi.jar to the lib/core directory."
echo " - delete lib/core/fop-0.20.4.jar"
