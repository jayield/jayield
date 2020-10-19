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

package org.jayield.primitives.lng.advs;

import org.jayield.primitives.lng.LongAdvancer;
import org.jayield.primitives.lng.LongTraverser;
import org.jayield.primitives.lng.LongYield;

import java.util.Spliterator;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;

public class LongAdvancerStream implements LongAdvancer, LongTraverser {
    private final Spliterator.OfLong upstream;

    public LongAdvancerStream(LongStream data) {
        this.upstream = data.spliterator();
    }

    @Override
    public void traverse(LongYield yield) {
        LongConsumer cons = yield::ret;
        upstream.forEachRemaining(cons);
    }

    @Override
    public boolean tryAdvance(LongYield yield) {
        LongConsumer cons = yield::ret;
        return upstream.tryAdvance(cons);
    }
}
