/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache Forrest" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.forrest.yer.use.cli;

import org.apache.forrest.yer.hierarchy.Entry;
import org.apache.forrest.yer.hierarchy.HierarchyReader;
import org.apache.forrest.yer.hierarchy.EntryFactory;
import org.apache.forrest.sax.SAXConvenience;

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

/** Class <code>org.apache.forrest.yer.use.cli.HierarchyDump</code> ...
 * 
 * @author $Author: jefft $
 * @version CVS $Id: HierarchyDump.java,v 1.3 2002/11/05 05:52:41 jefft Exp $
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
    String rootFactory = "org.apache.forrest.yer.impl.fs.FileEntryFactory";
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
