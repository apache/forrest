package org.apache.forrest.examples.wicket;

import wicket.Component;
import wicket.MarkupContainer;
import wicketbench.junit.WicketBenchTestCase;
import wicketbench.web.IComponentFactory;

public class LogInTest extends WicketBenchTestCase {

	@Override
	public IComponentFactory<?> createFactory() {
		return new IComponentFactory() {
			public Component createComponent(MarkupContainer container, String id) {
            	LogInPage page = new LogInPage();
                return page;
			}
        };
	}

}
