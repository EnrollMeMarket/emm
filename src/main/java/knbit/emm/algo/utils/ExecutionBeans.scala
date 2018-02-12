package knbit.emm.algo.utils


import java.util.concurrent.Executors

import org.springframework.stereotype.Component

import scala.concurrent.ExecutionContext

@Component
class ExecutionBeans {
  implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
}
