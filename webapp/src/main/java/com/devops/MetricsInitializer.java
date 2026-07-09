package com.devops;

import io.prometheus.client.hotspot.DefaultExports;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Enregistre les métriques JVM standard au démarrage de la webapp : mémoire heap,
 * garbage collector, threads, classes chargées, CPU process... Elles deviennent alors
 * exposées sur /metrics (cf. MetricsServlet dans web.xml) et scrapées par Prometheus.
 */
public class MetricsInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DefaultExports.initialize();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Rien à libérer : le registre Prometheus est statique et suit le cycle de la JVM.
    }
}
