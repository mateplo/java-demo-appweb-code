package com.devops;

import io.prometheus.client.Counter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Métrique applicative basique : incrémente un compteur à chaque requête HTTP servie.
 * Exposé sous `app_http_requests_total` sur /metrics, à côté des métriques JVM.
 */
public class MetricsFilter implements Filter {

    private static final Counter REQUESTS = Counter.build()
            .name("app_http_requests_total")
            .help("Nombre total de requetes HTTP recues par la webapp.")
            .register();

    @Override
    public void init(FilterConfig filterConfig) {
        // Aucun paramètre à lire.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        REQUESTS.inc();
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Rien à libérer.
    }
}
