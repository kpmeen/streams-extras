package net.scalytica.akka

import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}

/**
 * A GraphStage that groups elements by the property returned by the given
 * function. The graph buffers {{{A}}}'s until the {{{B}}} properties no longer
 * match. It will then emit a {{{Seq[A]}}} containing all the buffered {{{A}}}'s
 * collected up to that point. The buffer is then reset and starts keeping track
 * of the next set of {{{A}}}'s.
 *
 * An example where this implementation can be useful is; when you have
 * a streaming data source (from e.g. Slick 3), where the query is a
 * {{{LEFT OUTER JOIN}}}, and you want to group all the rows with the
 * same ID.
 *
 * @note This implementation assumes that the incoming stream is pre-sorted.
 *
 * @param extractProp Function to extract the property used for comparing A's
 * @tparam A The type of elements passing through the stream
 * @tparam B The type of the property used to compare A's
 *
 * @see [[http://doc.akka.io/api/akka/2.5/akka/stream/stage/GraphStage.html]]
 */
final class GroupSortedBySameProp[A, B](
    extractProp: A => B
) extends GraphStage[FlowShape[A, Seq[A]]] {

  val in  = Inlet[A]("AggregateSame.in")
  val out = Outlet[Seq[A]]("AggregateSame.out")

  override val shape = FlowShape(in, out)

  override def createLogic(attributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      // format: off
    private var currentState: Option[A] = None
    private val buffer = Vector.newBuilder[A]
    // format: on

      setHandlers(
        in = in,
        out = out,
        handler = new InHandler with OutHandler {

          override def onPush(): Unit = {
            val nextElement = grab(in)
            val nextProp    = extractProp(nextElement)
            val isSameProp  = currentState.map(extractProp).contains(nextProp)

            if (currentState.isEmpty || isSameProp) {
              buffer += nextElement
              pull(in)
            } else {
              val result = buffer.result()
              buffer.clear()
              buffer += nextElement
              push(out, result)
            }
            currentState = Some(nextElement)
          }

          override def onPull(): Unit = pull(in)

          override def onUpstreamFinish(): Unit = {
            val result = buffer.result()
            if (result.nonEmpty) emit(out, result)
            completeStage()
          }
        }
      )

      override def postStop(): Unit = buffer.clear()
    }
}
