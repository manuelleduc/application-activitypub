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

import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.internal.AbstractDocumentConfigurationSource;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.LocalDocumentReference;

import static java.util.Arrays.asList;

/**
 * {@link org.xwiki.configuration.ConfigurationSource} reading the values from the ActivityPub configuration page.
 *
 * @version $Id$
 * @since 1.1
 */
@Component
@Named("activitypub")
@Singleton
public class ActivityPubConfigurationSource extends AbstractDocumentConfigurationSource
{
    private static final List<String> SPACE_NAMES = asList("ActivityPub");

    private static final LocalDocumentReference CLASS_REFERENCE =
        new LocalDocumentReference(SPACE_NAMES, "ActivityPubConfigurationClass");

    private static final LocalDocumentReference DOC_REFERENCE =
        new LocalDocumentReference(SPACE_NAMES, "Configuration");

    @Override
    protected DocumentReference getDocumentReference()
    {
        return new DocumentReference(DOC_REFERENCE, this.getCurrentWikiReference());
    }

    @Override
    protected LocalDocumentReference getClassReference()
    {
        return CLASS_REFERENCE;
    }

    @Override
    protected String getCacheId()
    {
        return "configuration.document.activitypub";
    }
}
