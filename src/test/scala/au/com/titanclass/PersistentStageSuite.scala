/*
 * Copyright 2020 Titan Class P/L
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.com.titanclass

import akka.actor.ActorSystem
import akka.stream.scaladsl.{ Sink, Source }

import scala.concurrent.Future

class PersistentStageSuite extends munit.FunSuite {
  implicit val system: ActorSystem = ActorSystem("persistence-test")
  import system.dispatcher

  /**
    * Akka cluster sharding state - an async boundary which causes the
    * remainder to execute on another node potentially. Yields an entity
    * id along with the original event.
    */
  implicit class Sharded[Out, Mat](s: Source[Out, Mat]) {
    def sharded[T](f: Out => T): Source[(T, Out), Mat] = ???
  }

  /**
    * Maintains a map of entity ids to their current entity state.
    * Whenever an entity id is not found in its map then it will attempt
    * to recover its state by sourcing old events from persistence.
    * Its function is to return new state either by updating or removing it.
    * Removing state results in passivation whereby it can come back on new
    * events being received in the future.
    * Persisted scan functions can be expressed as finite state machines
    * given an event and an existing state possibly causing a transition
    * to a new state. Futures are used so that side-effecting operations
    * are able to occur, including a timeout for a given side effect.
    * Upon the new state being returned, its corresponding event is then
    * persisted. The new state is then emitted along with the entity id and
    * event.
    */
  implicit class PersistedScan[K, E, S, Mat](s: Source[(K, E), Mat]) {
    def persistedScan(zero: Map[K, S])(
        f: (S, E) => Future[Option[S]]
    ): Source[(K, E, S), Mat] = ???
  }

  /**
    * Given some state condition being met, snapshot it so that any
    * subsequent recovery of state by persistedScan is performant.
    */
  implicit class Snapshot[K, E, S, Mat](s: Source[(K, E, S), Mat]) {
    def snapshot(p: S => Boolean): Source[(K, E, S), Mat] = ???
  }

  test("it works") {
    case class FooEvent(domainId: String, domainValue: Int)
    case class FooState(domainId: String, domainSumValue: Int)

    val result = Source
      .single(FooEvent("hi", 1))
      .sharded(_.domainId)
      .persistedScan(Map.empty[String, FooState]) { (s, e) =>
        Future.successful(Some(s.copy(domainSumValue = s.domainSumValue + e.domainValue)))
      }
      .snapshot(_.domainSumValue > 0)
      .runWith(Sink.head)

    result.map { case (_, _, s) => assert(s.domainSumValue == 1) }
  }
}
