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

package org.jayield.async;

import org.jayield.AsyncQuery;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class AsyncQueryFlatMapConcat<T, R> extends AsyncQuery<R> {
    private final AsyncQuery<T> upstream;
    private final Function<? super T, ? extends AsyncQuery<? extends R>> mapper;


    public AsyncQueryFlatMapConcat(AsyncQuery<T> upstream, Function<? super T, ? extends AsyncQuery<? extends R>> mapper) {
        this.upstream = upstream;
        this.mapper = mapper;
    }

    @Override
    public CompletableFuture<Void> subscribe(BiConsumer<? super R, ? super Throwable> cons) {
        return upstream.subscribe((item, err) -> {
            if(err != null) {
                cons.accept(null, err);
                return;
            }
            mapper
                .apply(item)
                .subscribe(cons::accept)
                .join(); // !!!! Replace this by Continuation !!!
        });
    }
}
