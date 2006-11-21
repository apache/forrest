package org.apache.forrest.examples.wicket;

import org.apache.forrest.examples.wicket.WelcomePage;

import wicket.Component;
import wicket.MarkupContainer;
import wicketbench.junit.WicketBenchTestCase;
import wicketbench.web.IComponentFactory;

public class ApplicationTest extends WicketBenchTestCase {

	@Override
	public IComponentFactory<?> createFactory() {
		return new IComponentFactory() {
			public Component createComponent(MarkupContainer container, String id) {
            	WelcomePage page = new WelcomePage();
                return page;
			}
        };
	}

}
