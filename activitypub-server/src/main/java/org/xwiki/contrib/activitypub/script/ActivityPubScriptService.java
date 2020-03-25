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
package org.xwiki.contrib.activitypub.script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.activitypub.ActivityHandler;
import org.xwiki.contrib.activitypub.ActivityPubClient;
import org.xwiki.contrib.activitypub.ActivityPubException;
import org.xwiki.contrib.activitypub.ActivityPubObjectReferenceResolver;
import org.xwiki.contrib.activitypub.ActivityPubStorage;
import org.xwiki.contrib.activitypub.ActivityRequest;
import org.xwiki.contrib.activitypub.ActorHandler;
import org.xwiki.contrib.activitypub.entities.AbstractActor;
import org.xwiki.contrib.activitypub.entities.ActivityPubObject;
import org.xwiki.contrib.activitypub.entities.ActivityPubObjectReference;
import org.xwiki.contrib.activitypub.entities.Create;
import org.xwiki.contrib.activitypub.entities.Follow;
import org.xwiki.contrib.activitypub.entities.Note;
import org.xwiki.contrib.activitypub.entities.OrderedCollection;
import org.xwiki.contrib.activitypub.entities.ProxyActor;
import org.xwiki.script.service.ScriptService;
import org.xwiki.stability.Unstable;
import org.xwiki.text.StringUtils;
import org.xwiki.user.CurrentUserReference;
import org.xwiki.user.GuestUserReference;
import org.xwiki.user.UserReference;
import org.xwiki.user.UserReferenceResolver;

/**
 * Script service for ActivityPub.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Singleton
@Named("activitypub")
@Unstable
public class ActivityPubScriptService implements ScriptService
{
    private static final String GET_CURRENT_ACTOR_ERR_MSG = "Failed to retrieve the current actor. Cause [{}].";

    private static final String GET_CURRENT_ACTOR_UNEXPECTED_ERR_MSG =
        "Failed to retrieve the current actor. Unexpected Cause [{}].";

    @Inject
    private ActivityPubClient activityPubClient;

    @Inject
    private ActorHandler actorHandler;

    @Inject
    private ActivityPubStorage activityPubStorage;

    @Inject
    private ActivityPubObjectReferenceResolver activityPubObjectReferenceResolver;

    @Inject
    private ActivityHandler<Create> createActivityHandler;

    @Inject
    private UserReferenceResolver<CurrentUserReference> userReferenceResolver;

    @Inject
    private Logger logger;

    private void checkAuthentication() throws ActivityPubException
    {
        UserReference userReference = this.userReferenceResolver.resolve(null);
        if (userReference == GuestUserReference.INSTANCE) {
            throw new ActivityPubException("You need to be authenticated to use this method.");
        }
    }

    /**
     * Retrieve an ActivityPub actor to be used in the methods of the script service.
     * It follows this strategy for resolving the given string:
     *   - if the string is null or blank, it resolves it by getting the current logged-in user actor. It will throw an
     *   {@link ActivityPubException} if no one is logged-in.
     *   - if the string starts with {@code http} or {@code @}, the actor will be resolved as a remote ActivityPub actor
     *   using {@link ActorHandler#getRemoteActor(String)}.
     *   - in all other cases, the actor will be resolved as a local actor using
     *   {@link ActorHandler#getLocalActor(String)}.
     * The method returns null if no actor is found.
     * @param actor the string to resolved following the strategy described above.
     * @return an ActivityPub actor or null.
     * @since 1.1
     */
    @Unstable
    public AbstractActor getActor(String actor)
    {
        AbstractActor result = null;
        try {
            if (StringUtils.isBlank(actor)) {
                this.checkAuthentication();
                result = this.actorHandler.getCurrentActor();
            } else {
                String trimmedActor = actor.trim();
                if (trimmedActor.startsWith("http://") || trimmedActor.contains("@")) {
                    result = this.actorHandler.getRemoteActor(trimmedActor);
                } else {
                    result = this.actorHandler.getLocalActor(trimmedActor);
                }
            }
        } catch (ActivityPubException e) {
            this.logger.error("Error while trying to get the actor [{}].", actor, e);
        }
        return result;
    }

    /**
     * Verify if the given actor is the current user.
     * @param actor the retrieved actor to check.
     * @return {@code true} if the given actor is the current logged user.
     * @since 1.1
     */
    @Unstable
    public boolean isCurrentUser(AbstractActor actor)
    {
        UserReference userReference = this.userReferenceResolver.resolve(null);
        try {
            return userReference.equals(this.actorHandler.getXWikiUserReference(actor));
        } catch (ActivityPubException e) {
            this.logger.error("Error while getting user reference for actor [{}]", actor, e);
            return false;
        }
    }

    /**
     * Send a Follow request to the given actor.
     * @param remoteActor the actor to follow.
     * @return {@code true} iff the request has been sent properly.
     */
    public boolean follow(AbstractActor remoteActor)
    {
        boolean result = false;

        try {
            this.checkAuthentication();
            AbstractActor currentActor = this.actorHandler.getCurrentActor();
            Follow follow = new Follow().setActor(currentActor).setObject(remoteActor);
            this.activityPubStorage.storeEntity(follow);
            HttpMethod httpMethod = this.activityPubClient.postInbox(remoteActor, follow);
            try {
                this.activityPubClient.checkAnswer(httpMethod);
            } finally {
                httpMethod.releaseConnection();
            }
            result = true;
        } catch (ActivityPubException | IOException e) {
            this.logger.error("Error while trying to send a follow request to [{}].", remoteActor, e);
        }

        return result;
    }

    /**
     * Resolve and returns the given {@link ActivityPubObjectReference}.
     * @param reference the reference to resolve.
     * @param <T> the type of the reference.
     * @return the resulted object.
     * @throws ActivityPubException in case of error during the resolving.
     */
    public <T extends ActivityPubObject> T resolve(ActivityPubObjectReference<T> reference) throws ActivityPubException
    {
        try {
            T ret = this.activityPubObjectReferenceResolver.resolveReference(reference);
            return ret;
        } catch (ActivityPubException e) {
            this.logger.error("Error while trying to resolve a reference [{}]", reference, e);
            return null;
        }
    }

    /**
     * Publish the given content as a note to be send to the adressed target.
     * The given targets can take different values:
     *   - followers: means that the note will be sent to the followers
     *   - an URI qualifying an actor: means that the note will be sent to that actor
     * If the list of targets is empty or null, it means the note will be private only.
     * @param targets the list of targets for the note (see below for information about accepted values)
     * @param content the actual concent of the note
     */
    public void publishNote(List<String> targets, String content)
    {
        try {
            checkAuthentication();
            AbstractActor currentActor = this.actorHandler.getCurrentActor();
            Note note = new Note()
                            .setAttributedTo(Collections.singletonList(currentActor.getReference()))
                            .setContent(content);
            if (targets != null && !targets.isEmpty()) {
                List<ProxyActor> to = new ArrayList<>();
                for (String target : targets) {
                    if ("followers".equals(target)) {
                        to.add(new ProxyActor(currentActor.getFollowers().getLink()));
                    } else if ("public".equals(target)) {
                        to.add(ProxyActor.getPublicActor());
                    } else {
                        AbstractActor remoteActor = this.getActor(target);
                        if (remoteActor != null) {
                            to.add(remoteActor.getProxyActor());
                        }
                    }
                }
                note.setTo(to);
            }
            this.activityPubStorage.storeEntity(note);

            Create create = new Create()
                                .setActor(currentActor)
                                .setObject(note)
                                .setAttributedTo(note.getAttributedTo())
                                .setTo(note.getTo())
                                .setPublished(new Date());
            this.activityPubStorage.storeEntity(create);

            this.createActivityHandler.handleOutboxRequest(new ActivityRequest<>(currentActor, create));
        } catch (IOException | ActivityPubException e) {
            this.logger.error("Error while posting a note.");
        }
    }

    /**
     *
     * @return the list of actor followed by the current user.
     */
    public List<AbstractActor> following()
    {
        try {
            AbstractActor currentActor = this.actorHandler.getCurrentActor();
            ActivityPubObjectReference<OrderedCollection<AbstractActor>> following = currentActor.getFollowing();
            if (following != null) {
                OrderedCollection<AbstractActor> activityPubObjectReferences =
                    this.activityPubObjectReferenceResolver.resolveReference(following);
                return activityPubObjectReferences.getOrderedItems().stream().map(this::resolveActor)
                           .filter(Objects::nonNull).collect(Collectors.toList());
            }
        } catch (ActivityPubException e) {
            this.logger.warn(GET_CURRENT_ACTOR_ERR_MSG, ExceptionUtils.getRootCauseMessage(e));
        } catch (Exception e) {
            this.logger.warn(GET_CURRENT_ACTOR_UNEXPECTED_ERR_MSG, ExceptionUtils.getRootCauseMessage(e));
        }
        return Collections.emptyList();
    }

    private AbstractActor resolveActor(ActivityPubObjectReference<AbstractActor> it)
    {
        try {
            return this.activityPubObjectReferenceResolver.resolveReference(it);
        } catch (ActivityPubException e) {
            return null;
        }
    }

    /**
     *
     * @return the list of actors following the current user.
     */
    public List<AbstractActor> followers()
    {
        try {
            AbstractActor currentActor = this.actorHandler.getCurrentActor();
            ActivityPubObjectReference<OrderedCollection<AbstractActor>> followers = currentActor.getFollowers();
            if (followers != null) {
                OrderedCollection<AbstractActor> activityPubObjectReferences =
                    this.activityPubObjectReferenceResolver.resolveReference(followers);
                return activityPubObjectReferences.getOrderedItems().stream().map(this::resolveActor)
                           .filter(Objects::nonNull).collect(Collectors.toList());
            }
        } catch (ActivityPubException e) {
            this.logger.warn(GET_CURRENT_ACTOR_ERR_MSG, ExceptionUtils.getRootCauseMessage(e));
        } catch (Exception e) {
            this.logger.warn(GET_CURRENT_ACTOR_UNEXPECTED_ERR_MSG, ExceptionUtils.getRootCauseMessage(e));
        }
        return Collections.emptyList();
    }
}
