/* File Creation Info [User: mpo | Date: 30-apr-02 | Time: 0:53:07 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.use.cli;

import org.outerj.yer.hierarchy.Entry;
import org.outerj.yer.hierarchy.HierarchyReader;
import org.outerj.yer.hierarchy.EntryFactory;
import org.outerj.sax.SAXConvenience;

import org.xml.sax.ContentHandler;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FilterOutputStream;

/** Class <code>org.outerj.yer.use.cli.HierarchyDump</code> ...
 * 
 * @author $Author: stevenn $
 * @version CVS $Id: HierarchyDump.java,v 1.1 2002/06/11 13:19:21 stevenn Exp $
 */
public class HierarchyDump
{

  /** Default constructor
   * 
   */
  public HierarchyDump()
  {
  }

  /** Main method for launching the logic conceived in this class.
   *  Usage for this class:
   *
   * @param args array of command line arguments.
   */
  public static void main(String[] args)
  {
    //FIXME: get these form CL or sysprops.
    String rootFactory = "org.outerj.yer.impl.fs.FileEntryFactory";
    String rootLocation = "./src/documentation";
    OutputStream sink = System.out;
    if (args.length > 0) {
      String outLocation = args[0];
      File outFile = new File(outLocation);
      File parent = outFile.getParentFile();
      if (parent != null && !parent.exists()) parent.mkdirs();
      try {
        sink = new MyOutputStream(new FileOutputStream(outFile));
      } catch(IOException e) {
        e.printStackTrace();
      }
    }

    //get an implementation of a rootEntry
    EntryFactory ef = EntryFactory.newInstance(rootFactory);
    Entry rootEntry = ef.getRootEntry(rootLocation);
    //get a reader
    HierarchyReader hr = new HierarchyReader();
    // get a listener
    ContentHandler serializer = SAXConvenience.getSAXSink(
        new StreamResult(sink));

    // get going
    hr.setContentHandler(serializer);
    hr.startReading(rootEntry);
  }

  static class MyOutputStream extends FilterOutputStream{
    MyOutputStream (OutputStream os){
      super(os);
    }

    public void write(int i)  throws IOException {
      super.write(i);
      super.flush();
      System.out.write(i);
    }
  }
}
