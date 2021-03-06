/*
 Copyright (C) 2013 Choucri FAHED

 This source code is release under the BSD License.

 This file is part of QuantScale, a free-software/open-source library
 for financial quantitative analysts and developers - 
 http://github.com/choucrifahed/quantscale

 QuantScale is free software: you can redistribute it and/or modify it
 under the terms of the QuantScale license.  You should have received a
 copy of the license along with this program; if not, please email
 <choucri.fahed@mines-nancy.org>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.

 QuantScale is based on QuantLib. http://quantlib.org/
 When applicable, the original copyright notice follows this notice.
 */
/*
 Copyright (C) 2007 Ferdinando Ametrano
 Copyright (C) 2000, 2001, 2002, 2003 RiskMap srl

 This file is part of QuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://quantlib.org/

 QuantLib is free software: you can redistribute it and/or modify it
 under the terms of the QuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <quantlib-dev@lists.sf.net>. The license is also available online at
 <http://quantlib.org/license.shtml>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.
*/

package org.qslib.quantscale

import org.qslib.quantscale.pattern._

/**
 * Base trait for market observables.
 *
 * @param T either Money or Real
 */
trait Quote[T] extends Observable {

  /** @return the current value */
  def apply(): Option[T]

  def map[U](f: T => U): Quote[U] = new FunctionQuote(this, f)

  override def toString() = s"Quote(${apply()})"
}

/** Market element returning a stored value. */
// FIXME toString() needs to tested
final class SimpleQuote(override val initialValue: Option[Real] = None) extends Quote[Real]
  with ObservableValue[Option[Real]] with ObservableDefImpl

object SimpleQuote {
  def apply(initialValue: Real) = new SimpleQuote(Some(initialValue))
}

final class FunctionQuote[T, U](originalQuote: Quote[T], f: T => U)
  extends Quote[U] with ObservableDefImpl with Updatable {

  originalQuote.registerObserver(this)

  override final def update() = notifyObservers()

  override final def apply() = originalQuote() map f

  override final def map[V](g: U => V) = new FunctionQuote[T, V](originalQuote, g compose f)
}
