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

package org.jayield.primitives.intgr.ops;

import org.jayield.boxes.BoolBox;
import org.jayield.primitives.intgr.IntAdvancer;
import org.jayield.primitives.intgr.IntQuery;
import org.jayield.primitives.intgr.IntTraverser;
import org.jayield.primitives.intgr.IntYield;

import java.util.function.IntPredicate;

public class IntAdvancerFilter implements IntAdvancer, IntTraverser {
    private final IntQuery upstream;
    private final IntPredicate p;

    public IntAdvancerFilter(IntQuery adv, IntPredicate p) {
        this.upstream = adv;
        this.p = p;
    }

    @Override
    public void traverse(IntYield yield) {
        upstream.traverse(e -> {
            if (p.test(e)) {
                yield.ret(e);
            }
        });
    }

    @Override
    public boolean tryAdvance(IntYield yield) {
        BoolBox found = new BoolBox();
        while(found.isFalse()) {
            boolean hasNext = upstream.tryAdvance(item -> {
                if(p.test(item)) {
                    yield.ret(item);
                    found.set();
                }
            });
            if(!hasNext) break;
        }
        return found.isTrue();
    }
}
