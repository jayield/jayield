/*
 * Copyright (c) 2020, Fernando Miguel Carvalho, mcarvalho@cc.isel.ipl.pt
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

package org.jayield.primitives.intgr;

import static org.jayield.primitives.intgr.IntQuery.fromStream;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.stream.IntStream;

import org.testng.annotations.Test;

/**
 * @author Miguel Gamboa
 */
public class IntQueryToIntStreamTest {

    @Test
    public void testFromStream() {
        int[] src = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        PrimitiveIterator.OfInt expected = IntStream.of(src).iterator();
        IntQuery nrs = fromStream(IntStream.of(src));
        while (nrs.tryAdvance(item -> assertEquals(item, expected.nextInt()))) {
        }
        assertFalse(expected.hasNext());
    }

    @Test
    public void testFromAndToStream() {
        int[] src = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        PrimitiveIterator.OfInt expected = IntStream.of(src).iterator();
        IntQuery nrs = fromStream(IntStream.of(src));
        Spliterator<Integer> actual = nrs.toStream().spliterator();
        while (actual.tryAdvance(curr -> assertEquals(curr, expected.next()))) {
        }
        assertFalse(expected.hasNext());
    }
}
