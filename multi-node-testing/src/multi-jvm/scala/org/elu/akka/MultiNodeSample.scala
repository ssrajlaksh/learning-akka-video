package org.elu.akka

import akka.actor.Props
import akka.remote.testkit.MultiNodeSpec
import akka.testkit.ImplicitSender
import MultiNodeSampleConfig._

/** Created by luhtonen on 18/04/16. */
class MultiNodeSample extends MultiNodeSpec(MultiNodeSampleConfig)
  with BasicMultiNodeSpec
  with ImplicitSender {

  // initial Participants
  def initialParticipants = roles.size

  "A MultiNodeSample" should "wait for all nodes to enter a barrier" in {
    enterBarrier("startup")
  }

  it should "send to and receive from a remote node" in {
    runOn(node2) {
      system.actorOf(Props[Worker], name = "worker")
      enterBarrier("deployed")
    }

    runOn(node1) {
      enterBarrier("deployed")
      val worker = system.actorSelection(node(node2) / "user" / "worker")

      worker ! Worker.Work

      expectMsg(Worker.Done)
    }

    enterBarrier("finished")
  }
}

class MultiNodeSampleMultiJvmNode1 extends MultiNodeSample
class MultiNodeSampleMultiJvmNode2 extends MultiNodeSample