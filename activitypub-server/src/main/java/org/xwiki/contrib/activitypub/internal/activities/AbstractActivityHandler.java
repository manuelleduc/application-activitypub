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
package org.xwiki.contrib.activitypub.internal.activities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.xwiki.contrib.activitypub.ActivityPubJsonParser;
import org.xwiki.contrib.activitypub.ActivityPubNotifier;
import org.xwiki.contrib.activitypub.ActivityPubObjectReferenceResolver;
import org.xwiki.contrib.activitypub.ActivityPubStore;
import org.xwiki.contrib.activitypub.entities.Activity;
import org.xwiki.contrib.activitypub.ActivityPubJsonSerializer;
import org.xwiki.contrib.activitypub.internal.ActorHandler;

public abstract class AbstractActivityHandler
{
    @Inject
    protected ActivityPubJsonSerializer activityPubJsonSerializer;

    @Inject
    protected ActivityPubJsonParser activityPubJsonParser;

    @Inject
    protected ActivityPubStore activityPubStorage;

    @Inject
    protected ActivityPubNotifier notifier;

    @Inject
    protected ActivityPubObjectReferenceResolver activityPubObjectReferenceResolver;

    @Inject
    protected ActorHandler actorHandler;

    protected HttpClient httpClient;

    public AbstractActivityHandler()
    {
        this.httpClient = new HttpClient();
    }

    protected void answer(HttpServletResponse response, int statusCode, Activity activity) throws IOException
    {
        if (response != null) {
            response.setStatus(statusCode);
            response.setContentType("application/activity+json");
            this.activityPubJsonSerializer.serialize(response.getOutputStream(), activity);
        }
    }

    protected void answerError(HttpServletResponse response, int statusCode, String error) throws IOException
    {
        if (response != null) {
            response.setStatus(statusCode);
            response.setContentType("text/plain");
            response.getOutputStream().write(error.getBytes(StandardCharsets.UTF_8));
        }
    }
}
