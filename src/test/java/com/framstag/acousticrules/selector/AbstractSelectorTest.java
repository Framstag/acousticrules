/*
 * AcousticRuler
 * Copyright 2023 Tim Teulings
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

package com.framstag.acousticrules.selector;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class AbstractSelectorTest {

  @Test
  void testNullReasonIsDefault() {
    var selector = new SelectWithKey(Collections.emptySet());
    var returnedReason = selector.getReason();

    Assertions.assertNull(returnedReason,"'null' reason is default");
  }

  @Test
  void testHasReasonReturnsFalseIfNoReasonSet() {
    var selector = new SelectWithKey(Collections.emptySet());

    var hasReason = selector.hasReason();

    Assertions.assertFalse(hasReason,"If no reason is set, hasReason() returns false");
  }

  @Test
  void testHasReasonReturnsFalseIfReasonContainsOnlySpaces() {
    var selector = new SelectWithKey(Collections.emptySet());
    var reason = "  ";

    selector.setReason(reason);
    var hasReason = selector.hasReason();

    Assertions.assertFalse(hasReason,"If reason contains only blanks, hasReason() returns false");
  }

  @Test
  void testReasonGetReturnsValueOfSetCall() {
    var selector = new SelectWithKey(Collections.emptySet());
    var reason = "BECAUSE";

    selector.setReason(reason);
    var returnedReason = selector.getReason();

    Assertions.assertEquals(reason,returnedReason,"Getter returns value of setter");
  }

  @Test
  void testReasonStringIsSameAsReasonIfNoPrefixIsGiven() {
    var selector = new SelectWithKey(Collections.emptySet());
    var reason = "BECAUSE";

    selector.setReason(reason);
    var returnedReasonString = selector.getReasonString("");

    Assertions.assertEquals(reason,returnedReasonString,"Getter returns value of setter");
  }

  @Test
  void testReasonStringIsEmptyIfNoReasonIsGiven() {
    var selector = new SelectWithKey(Collections.emptySet());

    var returnedReasonString = selector.getReasonString(null);

    Assertions.assertEquals("",returnedReasonString,"Expected empty String if no reason is given");
  }

  @Test
  void testReasonStringIsPrefixedIfPrefixIsGiven() {
    var selector = new SelectWithKey(Collections.emptySet());
    var prefix = "PREFIX";
    var reason = "BECAUSE";

    selector.setReason(reason);
    var returnedReasonString = selector.getReasonString(prefix);

    Assertions.assertEquals(prefix+reason,returnedReasonString,"Expected reasonString = prefix+reason");
  }
}
