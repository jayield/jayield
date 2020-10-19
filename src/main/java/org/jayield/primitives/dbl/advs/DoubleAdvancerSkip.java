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
import org.jayield.primitives.dbl.DoubleQuery;
import org.jayield.primitives.dbl.DoubleTraverser;
import org.jayield.primitives.dbl.DoubleYield;

public class DoubleAdvancerSkip implements DoubleAdvancer, DoubleTraverser {
    private final DoubleQuery upstream;
    private final int n;
    int index;

    public DoubleAdvancerSkip(DoubleQuery adv, int n) {
        this.upstream = adv;
        this.n = n;
        index = 0;
    }

    /**
     * Continues from the point where tryAdvance or next left the
     * internal iteration.
     *
     * @param yield
     */
    @Override
    public void traverse(DoubleYield yield) {
        upstream.traverse(item -> {
            if (index++ >= n) {
                yield.ret(item);
            }
        });
    }

    @Override
    public boolean tryAdvance(DoubleYield yield) {
        for (; index < n; index++)
            upstream.tryAdvance(item -> {});
        return upstream.tryAdvance(yield);
    }
}
