/*
 * Copyright (c) 2018, Fernando Miguel Carvalho, mcarvalho@cc.isel.ipl.pt
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

package org.jayield.operations;

import org.jayield.AdvancerExtensions;
import org.jayield.IntAdvancer;
import org.jayield.IntTraversable;
import org.jayield.IntYield;
import org.jayield.Traversable;
import org.jayield.Yield;

import java.util.function.ToIntFunction;

/**
 * @author Miguel Gamboa
 *         created on 06-02-2018
 */
public class TraversableMapToInt<T> implements IntTraversable {
    private final Traversable<T> src;
    private final ToIntFunction<T> mapper;

    public TraversableMapToInt(Traversable<T> src, ToIntFunction<T> mapper) {
        this.src = src;
        this.mapper = mapper;
    }

    @Override
    public void traverse(IntYield yield) {
        src.traverse(e -> yield.ret(mapper.applyAsInt(e)));
    }

    @Override
    public IntAdvancer intAdvancer() {
        return AdvancerExtensions.mapToInt(src.advancer(), mapper);
    }


}
