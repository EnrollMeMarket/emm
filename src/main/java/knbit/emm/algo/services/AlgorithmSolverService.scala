package knbit.emm.algo.services

import java.util.{List => JList}

import knbit.emm.algo.ScalaSolver
import knbit.emm.algo.data.MarketData
import knbit.emm.algo.utils.ExecutionBeans
import knbit.emm.model.{Market, Swap}
import knbit.emm.repository._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps


/**
  * For now it is ugly as night because has to block for the future computation
  *
  * @param marketRepository
  * @param courseRepository
  * @param uClassRepository
  * @param studentRepository
  * @param swapRepository
  * @param executionBeans
  */
@Service
class AlgorithmSolverService @Autowired()(marketRepository: MarketRepository,
                                          courseRepository: CourseRepository,
                                          uClassRepository: UClassRepository,
                                          studentRepository: StudentRepository,
                                          swapRepository: SwapRepository,
                                          executionBeans: ExecutionBeans) {

  import executionBeans._

  def run(market: Market): JList[Swap] = {
    val marketData = getMarketData(market)
    val solver = new ScalaSolver(market, marketData)
    val actionResult = for {
      swaps <- solver.solve().map(_.asScala)
    } yield {
      for (swap <- swaps) {
        swap.setDone(true)
        val lucker = swap.getStudent
        val timetable = lucker.getTimetable
        timetable.remove(swap.getGive)
        timetable.add(swap.getTake)
        market.setWasProcessedByAlgorithm(true)
        marketRepository.save(market)
      }
    }

    val finalVersion = actionResult.map { _ =>
      marketRepository.findOne(market.getName)
    }.map { upMarket =>
      market.getCourses
        .iterator()
        .asScala
        .flatMap(_.getClasses.asScala)
        .flatMap(_.getSwapsGive.asScala)
        .filter(_.isDone)
        .toList.asJava
    }

    Await.result(finalVersion, 20 seconds)
  }

  @Transactional
  private def getMarketData(market: Market): MarketData = {
    val courses = market.getCourses
    val uClasses = courses.iterator().asScala.flatMap(_.getClasses.asScala).toList.asJava
    val students = market.getStudents
    val swaps = swapRepository.findByDone(false)
    new MarketData(courses, uClasses, students, swaps)
  }

}
