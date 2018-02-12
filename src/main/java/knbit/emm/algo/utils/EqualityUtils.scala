package knbit.emm.algo.utils

import knbit.emm.model.Swap

object EqualityUtils {

  def jSwapsEqual(a: Swap, b: Swap): Boolean = a === b

  implicit class SwapEqualUtils(private val lhs: Swap) extends AnyVal {

    def ===(rhs: Swap): Boolean = {
      lhs.getGive == rhs.getGive && lhs.getStudent == rhs.getStudent && lhs.getSwapId == rhs.getSwapId && lhs.getTake == rhs.getTake
    }

  }

}
