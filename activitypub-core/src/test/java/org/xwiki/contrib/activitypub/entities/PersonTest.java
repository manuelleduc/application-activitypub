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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.xwiki.contrib.activitypub.ActivityPubException;
import org.xwiki.test.LogLevel;
import org.xwiki.test.junit5.LogCaptureExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of {@link Person}.
 *
 * @since 1.0
 * @version $Id$
 */
public class PersonTest extends AbstractEntityTest
{
    @RegisterExtension
    LogCaptureExtension logCapture = new LogCaptureExtension(LogLevel.INFO);

    @Test
    void serializePerson1() throws URISyntaxException, IOException, ActivityPubException
    {
        Person person = new Person()
                            .setPublicKey(new PublicKey().setId("pkid").setOwner("pkowner").setPublicKeyPem("pkpem"))
                            .setPreferredUsername("Foo bar")
                            .setId(new URI("http://www.xwiki.org/wiki/activitypub/Foo"))
                            .setName("XWiki.Foo");
        String expectedPerson = this.readResource("person/person1.json");
        String serializedPerson = this.serializer.serialize(person);
        assertEquals(expectedPerson, serializedPerson);
    }

    @Test
    void parsePerson1() throws FileNotFoundException, URISyntaxException, ActivityPubException
    {
        Person person = new Person()
                            .setPublicKey(new PublicKey().setId("pkid").setOwner("pkowner").setPublicKeyPem("pkpem"))
                            .setPreferredUsername("Foo bar")
                            .setId(new URI("http://www.xwiki.org/wiki/activitypub/Foo"))
                            .setName("XWiki.Foo");

        String personJson = this.readResource("person/person1.json");
        Person obtainedPerson = this.parser.parse(personJson, Person.class);
        assertEquals(person, obtainedPerson);

        AbstractActor obtainedActor = this.parser.parse(personJson, AbstractActor.class);
        assertEquals(person, obtainedActor);

        obtainedPerson = this.parser.parse(personJson);
        assertEquals(person, obtainedPerson);
    }

    @Test
    void parsePerson2() throws URISyntaxException, IOException, ActivityPubException
    {
        Person person = new Person()
                            .setPreferredUsername("alyssa")
                            .setInbox(new ActivityPubObjectReference<Inbox>()
                                          .setLink(new URI("https://social.example/alyssa/inbox/")))
                            .setOutbox(new ActivityPubObjectReference<Outbox>()
                                           .setLink(new URI("https://social.example/alyssa/outbox/")))
                            .setFollowers(new ActivityPubObjectReference<OrderedCollection<AbstractActor>>()
                                              .setLink(new URI("https://social.example/alyssa/followers/")))
                            .setFollowing(new ActivityPubObjectReference<OrderedCollection<AbstractActor>>()
                                              .setLink(new URI("https://social.example/alyssa/following/")))
                            .setId(new URI("https://social.example/alyssa/"))
                            .setName("Alyssa P. Hacker")
                            .setSummary("Lisp enthusiast hailing from MIT");

        String personJson = this.readResource("person/person2.json");
        Person obtainedPerson = this.parser.parse(personJson, Person.class);
        assertEquals(person, obtainedPerson);

        AbstractActor obtainedActor = this.parser.parse(personJson, AbstractActor.class);
        assertEquals(person, obtainedActor);

        obtainedPerson = this.parser.parse(personJson);
        assertEquals(person, obtainedPerson);
    }

    @Test
    void parsePersonMastodon() throws Exception
    {
        String personJson = this.readResource("person/mastodon.json");
        Person obtainedPerson = this.parser.parse(personJson, Person.class);
        assertEquals(2, obtainedPerson.getContext().size());
        assertEquals(URI.create("https://www.w3.org/ns/activitystreams"), obtainedPerson.getContext().get(0));
        assertEquals(URI.create("https://w3id.org/security/v1"), obtainedPerson.getContext().get(1));
        assertEquals(1, this.logCapture.size());
        assertEquals("The JsonNode [{\"manuallyApprovesFollowers\":\"as:manuallyApprovesFollowers\"," 
                         + "\"toot\":\"http://joinmastodon.org/ns#\"," 
                         + "\"featured\":{\"@id\":\"toot:featured\",\"@type\":\"@id\"}," 
                         + "\"alsoKnownAs\":{\"@id\":\"as:alsoKnownAs\",\"@type\":\"@id\"}," 
                         + "\"movedTo\":{\"@id\":\"as:movedTo\",\"@type\":\"@id\"}," 
                         + "\"schema\":\"http://schema.org#\",\"PropertyValue\":\"schema:PropertyValue\"," 
                         + "\"value\":\"schema:value\",\"IdentityProof\":\"toot:IdentityProof\"," 
                         + "\"discoverable\":\"toot:discoverable\"}] has been ignored.", this.logCapture.getMessage(0));
    }
}
