package no.priv.bang.ukelonn.impl;

import javax.inject.Inject;
import javax.inject.Provider;

import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

/**
 * Create an {@link HttpContext} and expose it as an OSGi service
 *
 * @author Steinar Bang
 *
 */
public class HttpContextProvider implements Provider<HttpContext> {
    private HttpService httpService;

    @Inject
    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    @Override
    public HttpContext get() {
        if (httpService != null) {
            return httpService.createDefaultHttpContext();
        }

        return null;
    }

}
