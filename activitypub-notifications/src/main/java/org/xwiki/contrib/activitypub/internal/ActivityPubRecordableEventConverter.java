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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.activitypub.ActivityPubEvent;
import org.xwiki.contrib.activitypub.ActivityPubJsonSerializer;
import org.xwiki.eventstream.Event;
import org.xwiki.eventstream.RecordableEvent;
import org.xwiki.eventstream.RecordableEventConverter;

@Component
@Singleton
@Named("activitypub")
public class ActivityPubRecordableEventConverter implements RecordableEventConverter
{
    @Inject
    private RecordableEventConverter defaultConverter;

    @Inject
    private ActivityPubJsonSerializer activityPubJsonSerializer;

    @Override
    public Event convert(RecordableEvent recordableEvent, String source, Object data) throws Exception
    {
        Event convertedEvent = this.defaultConverter.convert(recordableEvent, source, data);

        ActivityPubEvent activityPubEvent = (ActivityPubEvent) recordableEvent;
        Map<String, String> parameters = new HashMap<>(convertedEvent.getParameters());
        parameters.put("activity", this.activityPubJsonSerializer.serialize(activityPubEvent.getActivity()));
        convertedEvent.setParameters(parameters);
        convertedEvent.setType("activitypub");
        return convertedEvent;
    }

    @Override
    public List<RecordableEvent> getSupportedEvents()
    {
        return Arrays.asList(new ActivityPubEvent<>(null, null));
    }
}