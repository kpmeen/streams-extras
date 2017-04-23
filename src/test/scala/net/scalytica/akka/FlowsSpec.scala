package net.scalytica.akka

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.testkit.TestKit
import org.scalatest.Inspectors.forAll
import org.scalatest.{
  BeforeAndAfter,
  BeforeAndAfterAll,
  MustMatchers,
  WordSpecLike
}
import org.scalatest.concurrent.ScalaFutures

class FlowsSpec
    extends TestKit(ActorSystem())
    with WordSpecLike
    with MustMatchers
    with ScalaFutures
    with BeforeAndAfterAll {

  implicit val mat = ActorMaterializer()

  override def afterAll(): Unit = system.terminate()

  case class Foo(id: Int, values: Seq[String])

  val str1 = "first element"
  val str2 = "second element"
  val str3 = "third element"

  val elems1 = (1 to 1000).map(i => Foo(i, Seq(str1)))
  val elems2 = (1 to 1000).filter(i => i % 3 == 0).map(i => Foo(i, Seq(str2)))
  val elems3 = (1 to 1000).filter(i => i % 5 == 0).map(i => Foo(i, Seq(str3)))

  val unsortedSource       = elems1 ++ elems2 ++ elems3
  val sortedSourceIterator = unsortedSource.sortBy(_.id).toIterator

  val mergeFlow = Flows.sortedMergeSame[Foo, Int](f => f.id) {
    case (f1, f2) =>
      f1.copy(values = f1.values ++ f2.values)
  }

  "A stream with of elements sorted by a given non-distinct property" should {
    "be merged to a stream with 1 element per value of the property" in {
      val res = Source
        .fromIterator(() => sortedSourceIterator)
        .via(mergeFlow)
        .runWith(Sink.seq)
        .futureValue

      res.size mustBe elems1.size

      forAll(res) { r =>
        (r.id % 3, r.id % 5) match {
          case (0, 0) => r.values must contain allOf (str1, str2, str3)
          case (0, _) => r.values must contain allOf (str1, str2)
          case (_, 0) => r.values must contain allOf (str1, str3)
          case (_, _) => r.values mustBe Seq(str1)
        }
      }
    }
  }

  "A stream of unsorted elements" should {
    "not be merged to a stream with 1 element per value of the property" in {
      val res = Source
        .fromIterator(() => unsortedSource.toIterator)
        .via(mergeFlow)
        .runWith(Sink.seq)
        .futureValue

      res.size mustBe (elems1.size + elems2.size + elems3.size)
    }
  }

}
