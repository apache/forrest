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
 * 4. The names "Xerces" and "Apache Software Foundation" must
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
package org.apache.forrest.components.generator;

import org.apache.cocoon.generation.ComposerGenerator;
import org.apache.cocoon.caching.Cacheable;
import org.apache.cocoon.caching.CacheValidity;
import org.apache.cocoon.caching.TimeStampCacheValidity;
import org.apache.cocoon.environment.Source;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.ResourceNotFoundException;
//import org.apache.cocoon.components.resolver.Resolver;
import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.excalibur.xml.EntityResolver;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.util.EntityResolverWrapper;
import org.apache.xerces.parsers.AbstractSAXParser;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.util.Map;
import java.io.IOException;


/** Class <code>org.apache.forrest.components.generator.XNIConfigurableFileGenerator</code>
 *  This class implements a Cocoon Generator that is configurable through
 *  the XNI mechanism that is built into xerces 2.x
 *  The generator is used with:
 *
    <map:generators default="">
      <map:generator name="nekodtd" src="org.apache.forrest.components.generator.XNIConfigurableFileGenerator" label="content" />
    </map:generators>
 *
 * and:
 *
   <map:match pattern="foobar">
     <map:generate type="nekodtd" src="resources/schema/dtd/{1}.dtd">
       <map:parameter name="config-class" value="org.cyberneko.dtd.DTDConfiguration" />
     </map:generate>
     <map:serialize type="xml"/>
   </map:match>
 *
 * TODO: check how some XNIConfigurableXMLReader component (Excalibur style
 *   like the JaxpParser) can be built to do this.  Then the complete
 *   parser can be recycled.
 *
 * @author $Author: crossley $
 * @version CVS $Id: XNIConfigurableFileGenerator.java,v 1.3 2002/07/12 07:14:29 crossley Exp $
 */
public class XNIConfigurableFileGenerator
extends ComposerGenerator implements Cacheable, Recyclable
{

  /** Default constructor
   *
   */
  public XNIConfigurableFileGenerator()
  {
  }

  public static final String CONFIGCLASS_PARAMETER = "config-class";
  public static final String FULL_ENTITY_RESOLVER_PROPERTY_URI =
      org.apache.xerces.impl.Constants.XERCES_PROPERTY_PREFIX +
      org.apache.xerces.impl.Constants.ENTITY_RESOLVER_PROPERTY;
  /** The  source */
  private Source inputSource;

  /** The XNIConfiguredParser */
  XMLParserConfiguration parserConfig;

  /**
   * Recycle this component.
   * All instance variables are set to <code>null</code>.
   */
  public void recycle() {
    super.recycle();
    if (this.inputSource != null) {
      this.inputSource.recycle();
      this.inputSource = null;
    }
  }

  /**
   * Copy paste en serious cut from cocoon HTML Generator
   */
  public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
  throws ProcessingException, SAXException, IOException {
    super.setup(resolver, objectModel, src, par);
    this.inputSource = resolver.resolve(super.source);
    String parserName = null;

    try {
      parserName = par.getParameter(CONFIGCLASS_PARAMETER);
      parserConfig = (XMLParserConfiguration)Class.forName(parserName).newInstance();
    } catch(ParameterException e) {
      getLogger().error("Missing parameter " + CONFIGCLASS_PARAMETER, e);
      throw new ProcessingException("XNIConfigurable.setup()",e);
    } catch(InstantiationException e) {
      getLogger().error("Can not make instance of " + parserName, e);
      throw new ProcessingException("XNIConfigurable.setup()",e);
    } catch(IllegalAccessException e) {
      getLogger().error("Can not access constructor of " + parserName, e);
      throw new ProcessingException("XNIConfigurable.setup()",e);
    } catch(ClassNotFoundException e) {
      getLogger().error("Can not find " + parserName, e);
      throw new ProcessingException("XNIConfigurable.setup()",e);
    }
  }

  /**
   * Generate the unique key.
   * This key must be unique inside the space of this component.
   * This method must be invoked before the generateValidity() method.
   *
   * @return The generated key or <code>0</code> if the component
   *              is currently not cacheable.
   */
  public long generateKey() {
/* TODO: investigate how we can handle dependend files
   this work was originally for DTD stuff, and external entities to this
   will not be known to have changed, so currently no cache.

   if (this.inputSource.getLastModified() != 0) {
      return HashUtil.hash(this.inputSource.getSystemId());
    }
 */
    return 0;
  }

  /**
   * Generate the validity object.
   * Before this method can be invoked the generateKey() method
   * must be invoked.
   *
   * @return The generated validity object or <code>null</code> if the
   *         component is currently not cacheable.
   */
  public CacheValidity generateValidity() {
    if (this.inputSource.getLastModified() != 0) {
      return new TimeStampCacheValidity(this.inputSource.getLastModified());
    }
    return null;
  }

  /**
   * Generate XML data.
   */
  public void generate()
  throws IOException, SAXException, ProcessingException {
    EntityResolver catalogResolver = null;
    final String[] extendRecognizedProperties = {FULL_ENTITY_RESOLVER_PROPERTY_URI};
    try {
      getLogger().debug("XNIConfigurable generator start generate()");

      //TODO?: Make XNIConfigurableParser an avalon component in it's own right
      //      Let the resolver and namespace stuff be configured and composed on that level.
      //  some ideas on this: (any others?)
      // - build a XNIConfigurableParser Component Interface with its ROLE
      // - add a XNIConfigurableParserSelector that can select() based on full qualified class name
      // - and release() after usage
      // the select method could do 6 next lines:
      catalogResolver = (EntityResolver)this.manager.lookup(EntityResolver.ROLE);
      parserConfig.addRecognizedProperties(extendRecognizedProperties);
      parserConfig.setProperty(FULL_ENTITY_RESOLVER_PROPERTY_URI, new EntityResolverWrapper(catalogResolver));
      final XMLReader parser = new AbstractSAXParser(parserConfig){};
      parser.setFeature("http://xml.org/sax/features/namespaces", true);
      parser.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
      parser.setContentHandler(this.contentHandler);
      parser.parse(this.inputSource.getInputSource());

    } catch (IOException e){
      getLogger().warn("XNIConfigurable.generate()", e);
      throw new ResourceNotFoundException("Could not get resource to process:\n["
              + "src = " + this.inputSource.getSystemId() + "]\n", e);
    } catch (SAXException e){
      getLogger().error("XNIConfigurable.generate()", e);
      throw e;
    } catch (ProcessingException e){
      throw e;
    } catch (XMLConfigurationException e) {
      getLogger().error( "Misconfig " + e.getType(), e);
      throw new ProcessingException("XNIConfigurable.generate()",e);
    } catch (Exception e){
      getLogger().error("Some strange thing just happened!!", e);
      throw new ProcessingException("XNIConfigurable.generate()",e);
    } finally {
      this.manager.release(catalogResolver);
    }
  }
}
