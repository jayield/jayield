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

package org.jayield.primitives.intgr.advs;

import java.util.stream.IntStream;

import org.jayield.primitives.intgr.IntAdvancer;
import org.jayield.primitives.intgr.IntIterator;
import org.jayield.primitives.intgr.IntYield;

public class IntAdvancerStream implements IntAdvancer {
    private final IntStream upstream;
    private IntIterator current;
    private boolean operated = false;

    public IntAdvancerStream(IntStream data) {
        this.upstream = data;
    }

    @Override
    public int nextInt() {
        return current().nextInt();
    }

    public IntIterator current() {
        if (operated) {
            return current;
        }
        operated = true;
        current = IntIterator.from(upstream.iterator());
        return current;
    }

    @Override
    public boolean hasNext() {
        return current().hasNext();
    }

    @Override
    public void traverse(IntYield yield) {
        upstream.forEach(yield::ret);
    }
}
