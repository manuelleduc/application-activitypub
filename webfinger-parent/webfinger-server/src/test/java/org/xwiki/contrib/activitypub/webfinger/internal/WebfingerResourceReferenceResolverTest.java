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
package org.xwiki.contrib.activitypub.webfinger.internal;

import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.xwiki.contrib.activitypub.webfinger.WebfingerResourceReference;
import org.xwiki.resource.ResourceType;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.url.ExtendedURL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 *
 * Test of {@link WebfingerResourceReferenceResolver}.
 *
 * @since 1.1
 * @version $Id$
 */
@ComponentTest
class WebfingerResourceReferenceResolverTest
{
    @InjectMockComponents
    private WebfingerResourceReferenceResolver resolver;

    @Test
    void resolve() throws Exception
    {
        ExtendedURL representation = mock(ExtendedURL.class);
        ResourceType resourceType = mock(ResourceType.class);
        HashMap parameters = mock(HashMap.class);
        WebfingerResourceReference actual = this.resolver.resolve(representation, resourceType, parameters);
        assertEquals(new HashMap<String, List<String>>(), actual.getParameters());
        verifyNoInteractions(parameters);
        verifyNoInteractions(resourceType);
        when(representation.getParameters()).thenReturn(new HashMap<>());
    }
}