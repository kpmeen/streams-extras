package net.scalytica.akka

import akka.NotUsed
import akka.stream.scaladsl.Flow

object Flows {

  def sortedMergeSame[A, Prop](
      groupByProp: A => Prop
  )(
      merge: (A, A) => A
  ): Flow[A, A, NotUsed] =
    Flow[A].via(new GroupSortedBySameProp(groupByProp)).map { elems =>
      elems.reduceLeft[A] { (acc, curr) =>
        merge(acc, curr)
      }
    }

}
