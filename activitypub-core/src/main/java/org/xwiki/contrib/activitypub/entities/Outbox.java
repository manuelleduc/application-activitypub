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
package org.xwiki.contrib.activitypub.entities;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.xwiki.stability.Unstable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Represents an Outbox as defined by ActivityPub.
 *
 * @see <a href="https://www.w3.org/TR/activitypub/#outbox">ActivityPub Outbox definition</a>
 * @version $Id$
 * @since 1.0
 */
@Unstable
@JsonDeserialize(as = Outbox.class)
public class Outbox extends OrderedCollection<AbstractActivity>
{
    private Map<String, AbstractActivity> items;

    /**
     * Default constructor.
     */
    public Outbox()
    {
        this.items = new HashMap<>();
    }

    /**
     * Store activities.
     * @param activity the activity to store.
     */
    public void addActivity(AbstractActivity activity)
    {
        if (activity.getId() == null) {
            throw new IllegalArgumentException("The activity ID must not be null.");
        }
        this.items.put(activity.getId().toASCIIString(), activity);
    }

    /**
     * @return all the activities of the outbox.
     */
    @JsonIgnore
    public java.util.Collection<AbstractActivity> getAllActivities()
    {
        return this.items.values();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Outbox object = (Outbox) o;
        return new EqualsBuilder()
            .appendSuper(super.equals(o))
            .append(items, object.items).build();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
            .appendSuper(super.hashCode())
            .append(items).build();
    }
}
