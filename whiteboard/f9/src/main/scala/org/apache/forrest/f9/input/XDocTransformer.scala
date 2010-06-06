/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.forrest.f9

import scala.xml.transform._
import scala.xml._

/**
* Naive at this point just so I can begin to see some content.
**/
object XDocTransformer extends RuleTransformer(
		documentRule, headerRule, sectionRule) 

object documentRule extends RewriteRule {
  override def transform(n: Node): Seq[Node] = n match {
    case Elem(prefix, "document", attribs, scope, child@_*)  =>
      new Elem(prefix, "html", attribs, scope, child:_*)
    case other => other
  }
}

object headerRule extends RewriteRule {
  override def transform(n: Node): Seq[Node] = n match {
    case Elem(prefix, "header", attribs, scope, child@_*)  =>
      new Elem(prefix, "head", attribs, scope, child:_*)
    case other => other
  }
}
object sectionRule extends RewriteRule {
  override def transform(n: Node): Seq[Node] = n match {
    case Elem(prefix, "section", attribs, scope, child@_*)  =>
      new Elem(prefix, "div", attribs, scope, child:_*)
    case other => other
  }
}
