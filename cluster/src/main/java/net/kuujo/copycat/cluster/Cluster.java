/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kuujo.copycat.cluster;

import net.kuujo.copycat.EventListener;
import net.kuujo.copycat.io.serializer.Serializer;

import java.util.Arrays;
import java.util.Collection;

/**
 * Copycat cluster.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public interface Cluster {

  /**
   * Returns the local cluster member.<p>
   *
   * When messages are sent to the local member, Copycat will send the messages asynchronously on an event loop.
   *
   * @return The local cluster member.
   */
  LocalMember member();

  /**
   * Returns a member by member identifier.
   *
   * @param id The unique member identifier.
   * @return The member or {@code null} if the member does not exist.
   */
  Member member(int id);

  /**
   * Returns an immutable set of all cluster members.
   *
   * @return An immutable set of all members in the cluster.
   */
  Collection<Member> members();

  /**
   * Returns the cluster serializer.
   *
   * @return The cluster serializer.
   */
  Serializer serializer();

  /**
   * Broadcasts a message to the cluster.<p>
   *
   * Message broadcasting to the Copycat cluster should not be considered reliable. Copycat sends broadcast messages to
   * all currently known members of the cluster in a fire-and-forget manner. Broadcast messages do not support replies.
   * To send messages with replies to all members of the cluster, iterate over the collection of
   * {@link net.kuujo.copycat.cluster.Cluster#members()} in the cluster.
   *
   * @param topic The topic to which to broadcast the message. Members with broadcast listeners registered for this
   *              topic will receive the broadcast message.
   * @param message The message to broadcast. This will be serialized using the serializer configured in the resource
   *                configuration.
   * @param <T> The message type.
   * @return The cluster.
   */
  <T> Cluster broadcast(String topic, T message);

  /**
   * Adds a membership listener to the cluster.<p>
   *
   * Membership listeners are triggered when {@link Member.Type#PASSIVE} members join or leave the cluster. Copycat uses
   * a gossip based failure detection algorithm to detect failures, using vector clocks to version cluster
   * configurations. In order to prevent false positives due to network partitions, Copycat's failure detection
   * algorithm will attempt to contact a member from up to three different nodes before considering that node failed.
   * If the membership listener is called with a {@link net.kuujo.copycat.cluster.MembershipChangeEvent.Type#LEAVE} event, that indicates that Copycat
   * has attempted to contact the missing member multiple times.<p>
   *
   * {@link Member.Type#ACTIVE} members never join or leave the cluster since they are explicitly configured, active,
   * voting members of the cluster. However, this may change at some point in the future to allow failure detection for
   * active members as well.
   *
   * @param listener The membership event listener to add.
   * @return The cluster.
   */
  Cluster addMembershipListener(EventListener<MembershipChangeEvent> listener);

  /**
   * Removes a membership listener from the cluster.
   *
   * @param listener The membership event listener to remove.
   * @return The cluster.
   */
  Cluster removeMembershipListener(EventListener<MembershipChangeEvent> listener);

  /**
   * Cluster builder.
   */
  public static interface Builder<BUILDER extends Builder<BUILDER, MEMBER>, MEMBER extends ManagedMember> extends net.kuujo.copycat.Builder<ManagedCluster> {

    /**
     * Sets the local member ID.
     *
     * @param id The local member ID.
     * @return The cluster builder.
     */
    BUILDER withMemberId(int id);

    /**
     * Sets the local member type.
     *
     * @param type The local member address.
     * @return The cluster builder.
     */
    BUILDER withMemberType(Member.Type type);

    /**
     * Sets the cluster serializer.
     *
     * @param serializer The cluster serializer.
     * @return The cluster builder.
     */
    BUILDER withSerializer(Serializer serializer);

    /**
     * Sets the cluster seed members.
     *
     * @param seeds The set of cluster seed members.
     * @return The cluster builder.
     */
    @SuppressWarnings("unchecked")
    default BUILDER withSeeds(MEMBER... seeds) {
      if (seeds != null) {
        return withSeeds(Arrays.asList(seeds));
      }
      return (BUILDER) this;
    }

    /**
     * Sets the cluster seed members.
     *
     * @param seeds The set of cluster seed members.
     * @return The cluster builder.
     */
    BUILDER withSeeds(Collection<MEMBER> seeds);

    /**
     * Adds a cluster seed member.
     *
     * @param member The cluster seed member to add.
     * @return The cluster builder.
     */
    BUILDER addSeed(MEMBER member);
  }

}
