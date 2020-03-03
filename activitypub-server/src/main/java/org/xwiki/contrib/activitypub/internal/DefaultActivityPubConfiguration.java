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

import javax.inject.Inject;
import javax.inject.Named;

import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.contrib.activitypub.ActivityPubConfiguration;

import static org.xwiki.contrib.activitypub.ActivityPubConfiguration.FollowPolicy.REJECT;
import static org.xwiki.contrib.activitypub.ActivityPubConfiguration.FollowPolicy.valueOf;

/**
 * Default configuration: it always accept Follow request for now.
 *
 * @version $Id$
 */
public class DefaultActivityPubConfiguration implements ActivityPubConfiguration
{
    @Inject
    @Named("activitypub")
    private ConfigurationSource configuration;

    @Override
    public FollowPolicy getFollowPolicy()
    {
        String followPolicy = this.configuration.getProperty("followPolicy", "reject");
        try {
            return valueOf(followPolicy.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return REJECT;
        }
    }
}
