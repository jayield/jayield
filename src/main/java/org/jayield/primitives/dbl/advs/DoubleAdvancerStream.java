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

package org.jayield.primitives.dbl.advs;

import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.dbl.DoubleTraverser;
import org.jayield.primitives.dbl.DoubleYield;

import java.util.Spliterator;
import java.util.function.DoubleConsumer;
import java.util.stream.DoubleStream;

public class DoubleAdvancerStream implements DoubleAdvancer, DoubleTraverser {
    private final Spliterator.OfDouble upstream;
    private boolean operated = false;

    public DoubleAdvancerStream(DoubleStream data) {
        this.upstream = data.spliterator();
    }

    @Override
    public void traverse(DoubleYield yield) {
        DoubleConsumer cons = yield::ret;
        upstream.forEachRemaining(cons);
    }

    @Override
    public boolean tryAdvance(DoubleYield yield) {
        DoubleConsumer cons = yield::ret;
        return upstream.tryAdvance(cons);
    }
}
