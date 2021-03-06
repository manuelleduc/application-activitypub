/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.activitypub.internal;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.xpn.xwiki.XWikiContext;

/**
 * Default component in charge of handling Servlet URLs.
 *
 * @version $Id$
 * @since 1.1
 */
@Component(roles = DefaultURLHandler.class)
@Singleton
public class DefaultURLHandler
{
    @Inject
    private Provider<XWikiContext> contextProvider;

    @Inject
    private Logger logger;

    /**
     * @return the URL of the current instance based on the current context.
     * @throws MalformedURLException in case of problem when retrieving the URL.
     */
    public URL getServerUrl() throws MalformedURLException
    {
        XWikiContext context = this.contextProvider.get();
        return context.getURLFactory().getServerURL(context);
    }

    /**
     * Resolve an URI against the current {@link #getServerUrl()}.
     * @param instanceResource the URI to resolve.
     * @return an URI resolved against the server URL which should be absolute.
     * @throws MalformedURLException in case of problem to get the server URL.
     * @throws URISyntaxException in case of problem to retrieve an URI.
     */
    public URI getAbsoluteURI(URI instanceResource) throws MalformedURLException, URISyntaxException
    {
        return getServerUrl().toURI().resolve(instanceResource);
    }

    /**
     * Check that the given URI is part of the current instance.
     * @param id the URI to check.
     * @return {@code true} if it belongs to the current instance.
     */
    public boolean belongsToCurrentInstance(URI id)
    {
        if (id.isAbsolute()) {
            try {
                URL idUrl = id.toURL();
                return Objects.equals(getServerUrl().getHost(), idUrl.getHost())
                    && Objects.equals(getServerUrl().getProtocol(), idUrl.getProtocol())
                    && this.normalizePort(getServerUrl().getPort()) == this.normalizePort(idUrl.getPort());
            } catch (MalformedURLException e) {
                this.logger.error("Error while comparing server URL and actor ID", e);
            }
        }
        return false;
    }

    private int normalizePort(int sup)
    {
        int serverUrlPort;
        if (sup == -1) {
            serverUrlPort = 80;
        } else {
            serverUrlPort = sup;
        }
        return serverUrlPort;
    }
}
