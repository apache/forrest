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
package org.apache.forrest.yer.libre;

import org.apache.forrest.yer.hierarchy.SimpleEntryList;
import org.apache.forrest.yer.hierarchy.Entry;
import org.apache.forrest.yer.hierarchy.EntryVisitor;
import org.apache.forrest.yer.hierarchy.Collection;
import org.apache.forrest.yer.hierarchy.HierarchyConfig;
import org.apache.forrest.yer.hierarchy.AttributeReader;
import org.apache.forrest.yer.hierarchy.EntryList;
import org.apache.forrest.yer.hierarchy.SortingEntryList;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Comparator;


/** Class <code>org.apache.forrest.yer.libre.LibreConfig</code> holds the configuration
 *  information that the libre system needs.
 *
 * @author $Author: mpo $
 * @version CVS $Id: LibreConfig.java,v 1.4 2003/01/09 00:06:45 mpo Exp $
 */
public class LibreConfig
{
  public LibreConfig() {}
  public LibreConfig(ChildDefinition parentDef){
    ChildDefinition newDef = this.new AutoChildDefinition();
    newDef.inheritDefinition(parentDef);
    addChildDefinition(newDef);
  }

  /** Member variable that essentially holds the sequence of ChildDefinitions
   *  that make up the complete LibreConfig
   */
  private List sequenceOfChildDefinitions = new ArrayList();
  public void addChildDefinition(ChildDefinition newChild) {
    this.sequenceOfChildDefinitions.add(newChild);
  }

  public EntryList makeChildList(final Collection parentEntry){
    final EntryList theChildList = new SimpleEntryList();
    final Set unicityExcluder = new HashSet();
    final Iterator sequence = sequenceOfChildDefinitions.iterator();
    while (sequence.hasNext()){
      final ChildDefinition cd = (ChildDefinition) sequence.next();
      final EntryList theSorter;
      if (cd.hasSpecificSorting()) {
        theSorter = new SortingEntryList(cd);
      } else {
        theSorter = new SimpleEntryList();
      }
      cd.visitDefinedChildEntries(parentEntry, new EntryVisitor() {
        public void visit(Entry visitedEntry)
        {
          Object uniqueKey = visitedEntry.getLocalUniqueKey();
          if (!unicityExcluder.contains(uniqueKey)){
            if (cd.filterAccepts(visitedEntry)){
              //theChildList.addEntry(visitedEntry);
              cd.applyAttributeReaders(visitedEntry);
              theSorter.addEntry(visitedEntry);
              unicityExcluder.add(uniqueKey);
            }
          }
        }
      });
      theChildList.copyEntries(theSorter);
    }
    return theChildList;
  }

  abstract class ChildDefinition implements HierarchyConfig, Comparator {
    public abstract void visitDefinedChildEntries(Collection parentEntry, EntryVisitor theVisitor);
    /*
    public String getSortKey(Entry theEntry) {
      //FIXME: prefix with the current index to hexvalue
      // then append the result from this childs proper sortKey AttributeReader on the passed Entry
      return null;
    }
    */

    private AttributeReader filterTest;
    public void setFilterTest(AttributeReader reader)
    {
      this.filterTest = reader;
    }
    public boolean filterAccepts(Entry entryToAccept)
    {
      if (this.filterTest != null){
        return this.filterTest.isAttributeValue(entryToAccept);
      } else {
        return true;
      }
    }

    private LibreAttributeReader sortKeyGen;
    public void setSortKeyGen(LibreAttributeReader reader)
    {
      this.sortKeyGen = reader;
    }
    public boolean hasSpecificSorting(){
      return (this.sortKeyGen!=null);
    }

    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.<p>
     *
     * The implementor must ensure that <tt>sgn(compare(x, y)) ==
     * -sgn(compare(y, x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>compare(x, y)</tt> must throw an exception if and only
     * if <tt>compare(y, x)</tt> throws an exception.)<p>
     *
     * The implementor must also ensure that the relation is transitive:
     * <tt>((compare(x, y)&gt;0) &amp;&amp; (compare(y, z)&gt;0))</tt> implies
     * <tt>compare(x, z)&gt;0</tt>.<p>
     *
     * Finally, the implementer must ensure that <tt>compare(x, y)==0</tt>
     * implies that <tt>sgn(compare(x, z))==sgn(compare(y, z))</tt> for all
     * <tt>z</tt>.<p>
     *
     * It is generally the case, but <i>not</i> strictly required that
     * <tt>(compare(x, y)==0) == (x.equals(y))</tt>.  Generally speaking,
     * any comparator that violates this condition should clearly indicate
     * this fact.  The recommended language is "Note: this comparator
     * imposes orderings that are inconsistent with equals."
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * 	       first argument is less than, equal to, or greater than the
     *	       second.
     * @throws ClassCastException if the arguments' types prevent them from
     * 	       being compared by this Comparator.
     */
    public int compare(Object o1, Object o2)
    {
      return compareEntries( (Entry)o1, (Entry)o2);
    }
    private int compareEntries(Entry ent1, Entry ent2){
      String key1 = this.sortKeyGen.getAttributeValue(ent1);
      String key2 = this.sortKeyGen.getAttributeValue(ent2);
      String keyA = key1, keyB = key2;
      if (this.sortKeyGen.isOrderDescending()){
        keyA = key2; keyB = key1;
      }
      if (keyA==null) {
        return -1;
      } else if (keyB==null) {
        return 1;
      } else {
        return keyA.compareTo(keyB);
      }
    }

    private HashMap attributeReaders = new HashMap();
    public void setAttributeReader(String attName, AttributeReader reader) {
      this.attributeReaders.put(attName, reader);
    }
    public void applyAttributeReaders(final Entry target){
      //Question: should we change this to only apply AttributReaders on available
      // attrNames as declared by the embedded (in the Entry) Property object???
      // and then handle the issue of not having a reader for it.
      Iterator attNames = this.attributeReaders.keySet().iterator();
      while (attNames.hasNext()) {
        String attName = (String)attNames.next();
        AttributeReader attReader = (AttributeReader)this.attributeReaders.get(attName);
        String attValue = attReader.getAttributeValue(target);
        if (attValue != null) {
          target.setAttribute(attName, attValue );
        }
      }
    }
    public void inheritDefinition(HierarchyConfig parentCfg){
      if (parentCfg != null){
        try {
          ChildDefinition parentDef = (ChildDefinition)parentCfg;
          this.filterTest = parentDef.filterTest;
          this.sortKeyGen = parentDef.sortKeyGen;
          // we need to clone the MAP, else the kid can add/overwrite stuff
          // in the parent map.
          this.attributeReaders = (HashMap)parentDef.attributeReaders.clone();
        } catch(ClassCastException e) {
          ; //currently don't know how to inherit from other then self
        }
      }
    }
  }

  class AutoChildDefinition extends ChildDefinition {
    public void visitDefinedChildEntries(final Collection parentEntry, final EntryVisitor theVisitor){
      EntryList newList = parentEntry.createChildList(this);
      newList.visitEntries(theVisitor);
    }
  }
  class EntryChildDefinition extends ChildDefinition {
    private final String location;
    public EntryChildDefinition(String location) {
      this.location = location;
    }
    public void visitDefinedChildEntries(final Collection parentEntry, final EntryVisitor theVisitor){
      Entry newEntry = parentEntry.createChildEntry(this.location, this);
      if (newEntry != null) {
        theVisitor.visit(newEntry);
      }
    }
  }
}
