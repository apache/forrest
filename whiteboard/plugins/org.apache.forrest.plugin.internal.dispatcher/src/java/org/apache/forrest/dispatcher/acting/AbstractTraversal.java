package org.apache.forrest.dispatcher.acting;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;

public abstract class AbstractTraversal extends ServiceableAction {

    
    SourceResolver resolver = null;
    String rest;
    String projectFallback;
    String projectDir;
    String projectExtension;
    String request;
    
    public AbstractTraversal() {
        super();
    }

    /**
     * @return Returns the projectDir.
     */
    public String getProjectDir() {
        return projectDir;
    }

    /**
     * @param projectDir
     *            The projectDir to set.
     */
    public void setProjectDir(String projectDir) {
        this.projectDir = projectDir;
    }

    /**
     * @return Returns the projectExtension.
     */
    public String getProjectExtension() {
        return projectExtension;
    }

    /**
     * @param projectExtension
     *            The projectExtension to set.
     */
    public void setProjectExtension(String projectExtension) {
        this.projectExtension = projectExtension;
    }

    /**
     * @return Returns the projectFallback.
     */
    public String getProjectFallback() {
        return projectFallback;
    }

    /**
     * @param projectFallback
     *            The projectFallback to set.
     */
    public void setProjectFallback(String projectFallback) {
        this.projectFallback = projectFallback;
    }

    /**
     * @return Returns the request.
     */
    public String getRequest() {
        return request;
    }

    /**
     * @param request
     *            The request to set.
     */
    public void setRequest(String request) {
        this.request = request;
    }

    /**
     * @return Returns the resolver.
     */
    public SourceResolver getResolver() {
        return resolver;
    }

    /**
     * @param resolver
     *            The resolver to set.
     */
    public void setResolver(SourceResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * @return Returns the rest.
     */
    public String getRest() {
        return rest;
    }

    /**
     * @param rest
     *            The rest to set.
     */
    public void setRest(String rest) {
        this.rest = rest;
    }

    protected String getFather() {
        return this.getRest().substring(0, this.getRest().lastIndexOf("/"));
    }

    /**
     * @see org.apache.excalibur.source.SourceFactory#release(org.apache.excalibur.source.Source)
     */
    public void release(Source source) {
        if (source != null) {
            resolver.release(source);
        }
    }

    protected void prepare(Parameters parameters) throws ParameterException {
        this.setRequest(parameters.getParameter("request"));
        this.setProjectFallback(parameters.getParameter("projectFallback"));
        this.setProjectExtension(parameters.getParameter("projectExtension"));
        this.setProjectDir(parameters.getParameter("projectDir"));
        this.setRest(this.getRequest());
    }

}