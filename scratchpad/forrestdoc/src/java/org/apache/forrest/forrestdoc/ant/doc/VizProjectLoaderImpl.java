/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/ant/doc/VizProjectLoaderImpl.java,v 1.1 2004/02/09 11:09:21 nicolaken Exp $
 * $Revision: 1.1 $
 * $Date: 2004/02/09 11:09:21 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Alexandria", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.forrest.forrestdoc.ant.doc;

import org.apache.tools.ant.BuildException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * An implementation of VizProjectLoader.
 * 
 * @author <a href="mailto:kengo@tt.rim.or.jp">KOSEKI Kengo</a>
 */

public class VizProjectLoaderImpl implements VizProjectLoader {

    private InputStream stream;

    private boolean uniqueref;

    private boolean ignoreAnt;

    private boolean ignoreAntcall;

    private boolean ignoreDepends;

    public VizProjectLoaderImpl() {

        uniqueref = false;

        ignoreAnt = false;

        ignoreAntcall = false;

        ignoreDepends = false;

    }

    public void uniqueRef(boolean uniqueref) {

        this.uniqueref = uniqueref;

    }

    public void ignoreAnt(boolean ignoreAnt) {

        this.ignoreAnt = ignoreAnt;

    }

    public void ignoreAntcall(boolean ignoreAntcall) {

        this.ignoreAntcall = ignoreAntcall;

    }

    public void ignoreDepends(boolean ignoreDepends) {

        this.ignoreDepends = ignoreDepends;

    }

    public void setInputStream(InputStream stream) {

        this.stream = stream;

    }

    public Vector getProjects() throws BuildException {

        try {

            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

            SAXHandler handler = new SAXHandler();

            parser.parse(new InputSource(stream), handler);

            return handler.getProjects();

        } catch (SAXException e) {

            throw new BuildException(e);

        } catch (ParserConfigurationException e) {

            throw new BuildException(e);

        } catch (IOException e) {

            throw new BuildException(e);

        }

    }

    private class SAXHandler extends DefaultHandler {

        private String defaultName;

        private Vector projects;

        private VizTarget target;

        private VizProject baseProject;

        public SAXHandler() {

            defaultName = "main";

            baseProject = new VizProject();

        }

        public Vector getProjects() {

            return projects;

        }

        public void startDocument() {

            projects = new Vector();

            projects.addElement(baseProject);

        }

        public void startElement(String uri,

                                 String name,

                                 String qName,

                                 Attributes atts) {

            if ("project".equals(qName)) {

                String def = atts.getValue("default");

                String dir = atts.getValue("dir");

                if (def != null)

                    defaultName = def;

                if (dir != null)

                    baseProject.setDir(dir);

            } else if ("target".equals(qName)) {

                String targetName = atts.getValue("name");

                String depends = atts.getValue("depends");

                target = baseProject.getTarget(targetName);

                target.setDefault(targetName.equals(defaultName));

                baseProject.appendTarget(target);

                if (null != depends && !ignoreDepends)

                    addDepends(target, depends);

            } else if ("antcall".equals(qName)) {

                if (!ignoreAntcall)

                    addAntCall(target, atts.getValue("target"));

            } else if ("ant".equals(qName)) {

                if (!ignoreAnt)

                    addAnt(target,

                            atts.getValue("dir"),

                            atts.getValue("antfile"),

                            atts.getValue("target"));

            }

        }

        private void addReference(VizTarget from, VizTarget to, int type) {

            VizReference reference = new VizReference();

            reference.setFrom(from);

            reference.setTo(to);

            reference.setType(type);

            from.addReferenceOut(reference, uniqueref);

            to.addReferenceIn(reference, uniqueref);

        }

        private void addDepends(VizTarget from, String toNames) {

            StringTokenizer st = new StringTokenizer(toNames, ",");

            while (st.hasMoreTokens()) {

                VizTarget to = baseProject.getTarget(st.nextToken().trim());

                addReference(from, to, VizReference.DEPENDS);

            }

        }

        private void addAntCall(VizTarget from, String toName) {

            VizTarget to = baseProject.getTarget(toName);

            addReference(from, to, VizReference.ANTCALL);

        }

        private void addAnt(VizTarget from,

                            String toDir, String toFile, String toName) {

            toDir = (toDir != null) ? toDir : "";

            toFile = (toFile != null) ? toFile : "";

            toName = (toName != null) ? toName : "";

            VizProject toProject = getProject(toDir, toFile);

            VizTarget to = toProject.getTarget(toName);

            if ("".equals(toName))

                to.setDefault(true);

            toProject.appendTarget(to);

            addReference(from, to, VizReference.ANT);

        }

        private VizProject getProject(String dir, String file) {

            Enumeration enum = projects.elements();

            while (enum.hasMoreElements()) {

                VizProject project = (VizProject) enum.nextElement();

                if (dir.equals(project.getDir()) && file.equals(project.getFile()))

                    return project;

            }

            VizProject project = new VizProject();

            project.setDir(dir);

            project.setFile(file);

            projects.addElement(project);

            return project;

        }

    }

}











