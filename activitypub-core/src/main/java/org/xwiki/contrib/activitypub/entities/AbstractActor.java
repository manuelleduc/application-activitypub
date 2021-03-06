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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.xwiki.stability.Unstable;
import org.xwiki.text.XWikiToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract type to represent an ActivityStream Actor.
 * Note that we actually reused both definition from ActivityStream and ActivityPub to define this entity since here
 * it's aimed at being used for ActivityPub.
 *
 * @see <a href="https://www.w3.org/TR/activitystreams-core/#actors">ActivityStream Actor definition</a>
 * @see <a href="https://www.w3.org/TR/activitypub/#actor-objects">ActivityPub Actor definition</a>
 * @version $Id$
 * @since 1.0
 */
@Unstable
public abstract class AbstractActor extends ActivityPubObject
{
    // We might have some weird cases with XWiki special username (containing non UTF-8 characters for example)
    private String preferredUsername;
    private ActivityPubObjectReference<Inbox> inbox;
    private ActivityPubObjectReference<Outbox> outbox;
    private ActivityPubObjectReference<OrderedCollection<AbstractActor>> followers;
    private ActivityPubObjectReference<OrderedCollection<AbstractActor>> following;
    private PublicKey publicKey;

    /**
     * @return the username of the actor.
     */
    public String getPreferredUsername()
    {
        return preferredUsername;
    }

    /**
     * @param preferredUsername the username of the actor.
     * @param <T> the type of actor.
     * @return the current object for fluent API.
     */
    public <T extends AbstractActor> T setPreferredUsername(String preferredUsername)
    {
        this.preferredUsername = preferredUsername;
        return (T) this;
    }

    /**
     * @return a reference to the collection of actors following the current one.
     * @see <a href="https://www.w3.org/TR/activitypub/#followers">ActivityPub definition</a>
     */
    public ActivityPubObjectReference<OrderedCollection<AbstractActor>> getFollowers()
    {
        return followers;
    }

    /**
     * @param followers a reference to the collection of actors following the current one.
     * @param <T> the type of actor.
     * @return the current object for fluent API.
     * @see <a href="https://www.w3.org/TR/activitypub/#followers">ActivityPub definition</a>
     */
    public <T extends AbstractActor> T setFollowers(
        ActivityPubObjectReference<OrderedCollection<AbstractActor>> followers)
    {
        this.followers = followers;
        return (T) this;
    }

    /**
     * @return a reference to the collection of actors followed by the current one.
     * @see <a href="https://www.w3.org/TR/activitypub/#following">ActivityPub definition</a>
     */
    public ActivityPubObjectReference<OrderedCollection<AbstractActor>> getFollowing()
    {
        return following;
    }

    /**
     * @param following a reference to the collection of actors followed by the current one.
     * @param <T> the type of actor.
     * @return the current object for fluent API.
     * @see <a href="https://www.w3.org/TR/activitypub/#following">ActivityPub definition</a>
     */
    public <T extends AbstractActor> T setFollowing(
        ActivityPubObjectReference<OrderedCollection<AbstractActor>> following)
    {
        this.following = following;
        return (T) this;
    }

    /**
     * @return a reference to the {@link Inbox} of the actor.
     * @see <a href="https://www.w3.org/TR/activitypub/#inbox">ActivityPub definition</a>
     */
    public ActivityPubObjectReference<Inbox> getInbox()
    {
        return inbox;
    }

    /**
     * @param inbox a reference to the {@link Inbox} of the actor.
     * @param <T> the type of the actor.
     * @return the current object for fluent API.
     * @see <a href="https://www.w3.org/TR/activitypub/#inbox">ActivityPub definition</a>
     */
    public <T extends AbstractActor> T setInbox(ActivityPubObjectReference<Inbox> inbox)
    {
        this.inbox = inbox;
        return (T) this;
    }

    /**
     * @return a reference to the {@link Outbox} of the actor.
     * @see <a href="https://www.w3.org/TR/activitypub/#outbox">ActivityPub definition</a>
     */
    public ActivityPubObjectReference<Outbox> getOutbox()
    {
        return outbox;
    }

    /**
     * @param outbox a reference to the {@link Outbox} of the actor.
     * @param <T> the type of the actor.
     * @return the current object for fluent API.
     * @see <a href="https://www.w3.org/TR/activitypub/#outbox">ActivityPub definition</a>
     */
    public <T extends AbstractActor> T setOutbox(ActivityPubObjectReference<Outbox> outbox)
    {
        this.outbox = outbox;
        return (T) this;
    }

    /**
     * 
     * @return the actor's public key.
     */
    public PublicKey getPublicKey()
    {
        return this.publicKey;
    }

    /**
     * Set the public key of the actor.
     * @param publicKey A public key.
     * @return the current actor object.
     */
    public AbstractActor setPublicKey(PublicKey publicKey)
    {
        this.publicKey = publicKey;
        return this;
    }

    @Override
    public String toString()
    {
        return new XWikiToStringBuilder(this)
            .append("id", getId())
            .append("name", getName())
            .append("preferredUsername", getPreferredUsername())
            .append("publicKey", getPublicKey())
            .build();
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

        AbstractActor object = (AbstractActor) o;
        return new EqualsBuilder()
            .appendSuper(super.equals(o))
            .append(preferredUsername, object.preferredUsername)
            .append(inbox, object.inbox)
            .append(outbox, object.outbox)
            .append(followers, object.followers)
            .append(following, object.following)
            .append(publicKey, object.publicKey).build();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
            .appendSuper(super.hashCode())
            .append(preferredUsername)
            .append(inbox)
            .append(outbox)
            .append(followers)
            .append(following)
            .append(publicKey).build();
    }

    /**
     * @return the proxy actor for the current actor.
     * @since 1.1
     */
    @JsonIgnore
    public ProxyActor getProxyActor()
    {
        return new ProxyActor(this.getReference().getLink());
    }
}
