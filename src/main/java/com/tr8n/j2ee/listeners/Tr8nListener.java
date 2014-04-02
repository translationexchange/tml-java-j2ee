package com.tr8n.j2ee.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public abstract class Tr8nListener implements ServletContextListener {

	protected abstract void initTr8n();
	
	public void contextInitialized(ServletContextEvent sce) {
		initTr8n();
	}

	public void contextDestroyed(ServletContextEvent sce) {
	}
}
