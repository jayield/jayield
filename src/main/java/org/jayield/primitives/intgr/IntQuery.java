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

package org.jayield.primitives.intgr;

import org.jayield.Query;
import org.jayield.TraversableFinishError;
import org.jayield.Yield;
import org.jayield.boxes.BoolBox;
import org.jayield.boxes.IntBox;
import org.jayield.primitives.dbl.DoubleAdvancer;
import org.jayield.primitives.dbl.DoubleQuery;
import org.jayield.primitives.dbl.DoubleTraverser;
import org.jayield.primitives.intgr.ops.IntAdvancerArray;
import org.jayield.primitives.intgr.ops.IntAdvancerConcat;
import org.jayield.primitives.intgr.ops.IntAdvancerDistinct;
import org.jayield.primitives.intgr.ops.IntAdvancerDropWhile;
import org.jayield.primitives.intgr.ops.IntAdvancerFilter;
import org.jayield.primitives.intgr.ops.IntAdvancerFlatMap;
import org.jayield.primitives.intgr.ops.IntAdvancerGenerate;
import org.jayield.primitives.intgr.ops.IntAdvancerIterate;
import org.jayield.primitives.intgr.ops.IntAdvancerLimit;
import org.jayield.primitives.intgr.ops.IntAdvancerMap;
import org.jayield.primitives.intgr.ops.IntAdvancerMapToObj;
import org.jayield.primitives.intgr.ops.IntAdvancerPeek;
import org.jayield.primitives.intgr.ops.IntAdvancerSkip;
import org.jayield.primitives.intgr.ops.IntAdvancerStream;
import org.jayield.primitives.intgr.ops.IntAdvancerTakeWhile;
import org.jayield.primitives.intgr.ops.IntAdvancerZip;
import org.jayield.primitives.lng.LongAdvancer;
import org.jayield.primitives.lng.LongQuery;
import org.jayield.primitives.lng.LongTraverser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * A sequence of primitive int-valued elements supporting sequential
 * operations. This is the int primitive specialization of Query.
 */
public class IntQuery {

    private final IntAdvancer adv;
    private final IntTraverser trav;

    public IntQuery(IntAdvancer adv, IntTraverser trav) {
        this.adv = adv;
        this.trav = trav;
    }

    /**
     * Returns a sequential ordered {@code IntQuery} with elements
     * from the provided {@link IntStream} data.
     */
    public static IntQuery fromStream(IntStream src) {
        IntAdvancerStream strm = new IntAdvancerStream(src);
        return new IntQuery(strm, strm);
    }

    /**
     * Returns an infinite sequential ordered {@code IntQuery} produced by iterative
     * application of a function {@code f} to an initial element {@code seed},
     * producing a {@code IntQuery} consisting of {@code seed}, {@code f(seed)},
     * {@code f(f(seed))}, etc.
     */
    public static IntQuery iterate(int seed, IntUnaryOperator f) {
        IntAdvancerIterate iter = new IntAdvancerIterate(seed, f);
        return new IntQuery(iter, iter);
    }

    /**
     * Returns an infinite sequential unordered {@code IntQuery}
     * where each element is generated by the provided Supplier.
     */
    public static IntQuery generate(IntSupplier s) {
        IntAdvancerGenerate gen = new IntAdvancerGenerate(s);
        return new IntQuery(gen, gen);
    }

    /**
     * Yields elements sequentially in the current thread,
     * until all elements have been processed or an
     * exception is thrown.
     */
    public final void forEach(IntYield yield) {
        this.traverse(yield);
    }

    /**
     * Yields elements sequentially in the current thread,
     * until all elements have been processed or an
     * exception is thrown.
     */
    public final void traverse(IntYield yield) {
        this.trav.traverse(yield);
    }

    /**
     * If a remaining element exists, yields that element through
     * the given action.
     */
    public boolean tryAdvance(IntYield action) {
        return this.adv.tryAdvance(action);
    }

    /**
     * Returns a {@code IntQuery} consisting of the elements of this {@code IntQuery} that match
     * the given predicate.
     */
    public IntQuery filter(IntPredicate p) {
        IntAdvancerFilter filter = new IntAdvancerFilter(this, p);
        return new IntQuery(filter, filter);
    }

    /**
     * Returns a {@code IntQuery} consisting of the results of applying the given
     * IntUnaryOperator to the elements of this {@code IntQuery}.
     *
     * @param op
     *         IntUnaryOperator used to map the elements of this IntQuery
     */
    public IntQuery map(IntUnaryOperator op) {
        IntAdvancerMap map = new IntAdvancerMap(this, op);
        return new IntQuery(map, map);
    }

    /**
     * Returns a {@code Query} consisting of the results of applying the given
     * function to the elements of this {@code IntQuery}.
     *
     * @param function
     *         IntFunction used to map the elements of this IntQuery
     */
    public <U> Query<U> mapToObj(IntFunction<? extends U> function) {
        IntAdvancerMapToObj<U> map = new IntAdvancerMapToObj<>(this, function);
        return new Query<>(map, map);
    }

    /**
     * Returns a {@code IntQuery} consisting of the results of replacing each element of
     * this {@code IntQuery} with the contents of a mapped {@code IntQuery} produced by applying
     * the provided mapping function to each element.
     */
    public IntQuery flatMap(IntFunction<? extends IntQuery> function) {
        IntAdvancerFlatMap map = new IntAdvancerFlatMap(this, function);
        return new IntQuery(map, map);
    }

    /**
     * Returns a query consisting of the distinct elements (according to
     * {@link Object#equals(Object)}) of this query.
     */
    public IntQuery distinct() {
        IntAdvancerDistinct dis = new IntAdvancerDistinct(this);
        return new IntQuery(dis, dis);
    }

    /**
     * Returns a {@code IntQuery} consisting of the elements of this {@code IntQuery},
     * sorted according to the same logic as {@code Arrays.sort(int[] a)}.
     * <p>
     * This is a stateful intermediate operation.
     */
    public IntQuery sorted() {
        int[] state = this.toArray();
        Arrays.sort(state);
        IntAdvancerArray arr = new IntAdvancerArray(state);
        return new IntQuery(arr, arr);
    }

    /**
     * Returns an array containing the elements of this {@code IntQuery}.
     */
    public int[] toArray() {
        List<Integer> list = toList();
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    /**
     * Returns a List containing the elements of this {@code IntQuery}.
     */
    public List<Integer> toList() {
        ArrayList<Integer> result = new ArrayList<>();
        this.traverse(result::add);
        return result;
    }

    /**
     * Returns a {@code IntQuery} consisting of the elements of this {@code IntQuery}, additionally
     * performing the provided action on each element as elements are consumed
     * from the resulting {@code IntQuery}.
     */
    public IntQuery peek(IntConsumer action) {
        IntAdvancerPeek peek = new IntAdvancerPeek(this, action);
        return new IntQuery(peek, peek);
    }

    /**
     * Returns a {@code IntQuery} consisting of the elements of this query, truncated
     * to be no longer than {@code n} in length.
     *
     * @param n
     *         maximum amount of elements to retrieve from this {@code IntQuery}
     */
    public IntQuery limit(int n) {
        IntAdvancerLimit lim = new IntAdvancerLimit(this, n);
        return new IntQuery(lim, lim);
    }

    /**
     * Returns a {@code IntQuery} consisting of the remaining elements of this {@code IntQuery}
     * after discarding the first {@code n} elements of the {@code IntQuery}.
     *
     * @param n
     *         number of elements to discard
     */
    public IntQuery skip(int n) {
        IntAdvancerSkip skip = new IntAdvancerSkip(this, n);
        return new IntQuery(skip, skip);
    }

    /**
     * Returns an {@link OptionalInt} with the resulting reduction of the elements of this {@code IntQuery},
     * if a reduction can be made, using the provided accumulator.
     */
    public OptionalInt reduce(IntBinaryOperator accumulator) {
        IntBox box = new IntBox();
        if(this.tryAdvance(box::setValue)) {
            return OptionalInt.of(this.reduce(box.getValue(), accumulator));
        } else {
            return OptionalInt.empty();
        }
    }

    /**
     * Returns the result of the reduction of the elements of this {@code IntQuery},
     * using the provided identity value and accumulator.
     */
    public int reduce(int identity, IntBinaryOperator accumulator) {
        class BoxAccumulator extends IntBox implements IntYield {
            public BoxAccumulator(int identity) {
                super(identity);
            }
            @Override
            public void ret(int item) {
                this.value = accumulator.applyAsInt(value, item);
            }
        }
        BoxAccumulator box = new BoxAccumulator(identity);
        this.traverse(box);
        return box.getValue();
    }

    /**
     * Returns the lowest int of this {@code IntQuery}
     */
    public OptionalInt min() {
        IntBox b = new IntBox();
        this.traverse(e -> {
            if (!b.isPresent()) {
                b.turnPresent(e);
            } else if (e < b.getValue()) {
                b.setValue(e);
            }
        });
        return b.isPresent() ? OptionalInt.of(b.getValue()) : OptionalInt.empty();
    }

    /**
     * Returns the highest int of this {@code IntQuery}
     */
    public OptionalInt max() {
        IntBox b = new IntBox();
        this.traverse(e -> {
            if (!b.isPresent()) {
                b.turnPresent(e);
            } else if (e > b.getValue()) {
                b.setValue(e);
            }
        });
        return b.isPresent() ? OptionalInt.of(b.getValue()) : OptionalInt.empty();
    }

    /**
     * Returns the count of elements in this {@code IntQuery}.
     */
    public final long count() {
        class Counter implements IntYield {
            long n = 0;

            @Override
            public void ret(int item) {
                ++n;
            }
        }
        Counter c = new Counter();
        this.traverse(c);
        return c.n;
    }

    /**
     * Returns an OptionalDouble describing the arithmetic mean of elements of this {@code IntQuery},
     * or an empty optional if this {@code IntQuery} is empty. This is a special case of a reduction.
     * <p>
     * This is a terminal operation.
     */
    public OptionalDouble average() {
        int[] data = this.toArray();
        double count = data.length;
        if (count == 0) {
            return OptionalDouble.empty();
        }
        double sum = IntQuery.of(data).sum();
        return OptionalDouble.of(sum / count);
    }

    /**
     * Returns the sum of elements in this {@code IntQuery} .
     * <p>
     * This is a special case of a reduction.
     */
    public int sum() {
        return this.reduce(0, Integer::sum);
    }

    /**
     * Returns a sequential ordered {@code IntQuery} whose elements
     * are the specified values in data parameter.
     */
    public static IntQuery of(int... data) {
        IntAdvancerArray arr = new IntAdvancerArray(data);
        return new IntQuery(arr, arr);
    }

    /**
     * Returns an IntSummaryStatistics describing various summary data about
     * the elements of this {@code IntQuery}. This is a special case of a reduction.
     * <p>
     * This is a terminal operation.
     */
    public IntSummaryStatistics summaryStatistics() {
        return this.collect(IntSummaryStatistics::new, IntSummaryStatistics::accept);
    }

    /**
     * Performs a mutable reduction operation on the elements of this {@code IntQuery}.
     * A mutable reduction is one in which the reduced value is a mutable result container, such as an ArrayList,
     * and elements are incorporated by updating the state of the result rather than by replacing the result.
     */
    public <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator) {
        R result = supplier.get();
        this.traverse(elem -> accumulator.accept(result, elem));
        return result;
    }

    /**
     * Returns whether all elements of this {@code IntQuery} match the provided
     * {@link IntPredicate}.  May not evaluate the predicate on all elements if not
     * necessary for determining the result.  If the query is empty then
     * {@code true} is returned and the predicate is not evaluated.
     *
     * @param p
     *         IntPredicate used to test elements of this {@code IntQuery}
     */
    public boolean allMatch(IntPredicate p) {
        BoolBox succeed = new BoolBox(true);
        shortCircuit(item -> {
            if (!p.test(item)) {
                succeed.set(false);
                Yield.bye();
            }
        });
        return succeed.isTrue();
    }

    /**
     * Yields elements sequentially in the current thread,
     * until all elements have been processed or the traversal
     * exited normally through the invocation of yield.bye().
     */
    public final void shortCircuit(IntYield yield) {
        try {
            this.trav.traverse(yield);
        } catch (TraversableFinishError e) {
            /* Proceed */
        }
    }

    /**
     * Returns whether no elements of this {@code IntQuery} match the provided
     * {@link IntPredicate}.  May not evaluate the predicate on all elements if not
     * necessary for determining the result.  If the query is empty then
     * {@code true} is returned and the predicate is not evaluated.
     *
     * @param p
     *         IntPredicate used to test elements of this {@code IntQuery}
     */
    public boolean noneMatch(IntPredicate p) {
        return !this.anyMatch(p);
    }

    /**
     * Returns whether any elements of this {@code IntQuery} match the provided
     * {@link IntPredicate}.  May not evaluate the predicate on all elements if not
     * necessary for determining the result.  If the query is empty then
     * {@code false} is returned and the predicate is not evaluated.
     *
     * @param p
     *         IntPredicate used to test elements of this {@code IntQuery}
     */
    public boolean anyMatch(IntPredicate p) {
        BoolBox found = new BoolBox();
        shortCircuit(item -> {
            if (p.test(item)) {
                found.set();
                Yield.bye();
            }
        });
        return found.isTrue();
    }

    /**
     * Returns an {@link OptionalInt} describing any element of this {@code IntQuery},
     * or an empty {@code OptionalInt} if this {@code IntQuery} is empty.
     */
    public OptionalInt findAny() {
        return this.findFirst();
    }

    /**
     * Returns an {@link OptionalInt} describing the first element of this {@code IntQuery},
     * or an empty {@code OptionalInt} if this {@code IntQuery} is empty.
     */
    public OptionalInt findFirst() {
        IntBox box = new IntBox();
        this.tryAdvance(box::turnPresent);
        return box.isPresent()
                ? OptionalInt.of(box.getValue())
                : OptionalInt.empty();
    }

    /**
     * Returns a {@code LongQuery} consisting of the elements of this {@code IntQuery},
     * converted to long.
     * <p>
     * This is an intermediate operation.
     */
    public LongQuery asLongQuery() {
        return this.mapToLong(i -> i);
    }

    /**
     * Returns a {@code LongQuery} consisting of the results of applying the given
     * function to the elements of this {@code IntQuery}.
     *
     * @param function
     *         IntToLongFunction used to map the elements of this IntQuery
     */
    public LongQuery mapToLong(IntToLongFunction function) {
        return new LongQuery(LongAdvancer.from(adv, function), LongTraverser.from(trav, function));
    }

    /**
     * Returns a {@code DoubleQuery} consisting of the elements of this {@code IntQuery},
     * converted to double.
     * <p>
     * This is an intermediate operation.
     */
    public DoubleQuery asDoubleQuery() {
        return this.mapToDouble(i -> i);
    }

    /**
     * Returns a {@code DoubleQuery} consisting of the results of applying the given
     * function to the elements of this {@code IntQuery}.
     *
     * @param function
     *         IntToDoubleFunction used to map the elements of this IntQuery
     */
    public DoubleQuery mapToDouble(IntToDoubleFunction function) {
        return new DoubleQuery(DoubleAdvancer.from(adv, function), DoubleTraverser.from(trav, function));
    }

    /**
     * Returns a Stream consisting of the elements of this {@code IntQuery},
     * each boxed to an Integer.
     */
    public Query<Integer> boxed() {
        return new Query<>(adv, trav);
    }

    public IntStream toStream() {
        Spliterator.OfInt iter = new Spliterators.AbstractIntSpliterator(Long.MAX_VALUE, Spliterator.ORDERED) {
            @Override
            public boolean tryAdvance(IntConsumer action) {
                return adv.tryAdvance(action::accept);
            }

            @Override
            public void forEachRemaining(IntConsumer action) {
                trav.traverse(action::accept);
            }
        };
        return StreamSupport.intStream(iter, false);
    }


    /**
     * The {@code then} operator lets you encapsulate a piece of an operator
     * chain into a function.
     * That function {@code next} is applied to this {@code DoubleQuery} to produce a new
     * {@code IntTraverser} object that is encapsulated in the resulting {@code DoubleQuery}.
     * On the other hand, the {@code nextAdv} is applied to this query to produce a new
     * {@code IntAdvancer} object that is encapsulated in the resulting query.
     */
    public final IntQuery then(
        Function<IntQuery, IntAdvancer> nextAdv,
        Function<IntQuery, IntTraverser> next)
    {
        return new IntQuery(nextAdv.apply(this), next.apply(this));
    }

    /**
     * The {@code then} operator lets you encapsulate a piece of an operator
     * chain into a function.
     * That function {@code next} is applied to this {@code IntQuery} to produce a new
     * {@code IntTraverser} object that is encapsulated in the resulting {@code IntQuery}.
     */
    public final IntQuery then(Function<IntQuery, IntTraverser> next) {
        IntAdvancer nextAdv = item -> { throw new UnsupportedOperationException(
            "Missing tryAdvance() implementation! Use the overloaded then() providing both Advancer and Traverser!");
        };
        return new IntQuery(nextAdv, next.apply(this));
    }

    /**
     * Returns a {@code IntQuery} consisting of the longest prefix of elements taken from
     * this {@code IntQuery} that match the given predicate.
     */
    public final IntQuery takeWhile(IntPredicate predicate) {
        IntAdvancerTakeWhile take = new IntAdvancerTakeWhile(this, predicate);
        return new IntQuery(take, take);
    }

    /**
     * Creates a concatenated {@code Query} in which the elements are
     * all the elements of this {@code Query} followed by all the
     * elements of the other {@code Query}.
     */
    public final IntQuery concat(IntQuery other) {
        IntAdvancerConcat cat = new IntAdvancerConcat(this, other);
        return new IntQuery(cat, cat);
    }

    /**
     * Returns a {@code IntQuery} consisting of the remaining elements of this query
     * after discarding the first sequence of elements that match the given Predicate.
     */
    public final IntQuery dropWhile(IntPredicate predicate) {
        IntAdvancerDropWhile drop = new IntAdvancerDropWhile(this, predicate);
        return new IntQuery(drop, drop);
    }

    /**
     * Applies a specified function to the corresponding elements of two
     * sequences, producing a sequence of the results.
     */
    public final IntQuery zip(IntQuery other, IntBinaryOperator zipper) {
        IntAdvancerZip zip = new IntAdvancerZip(this, other, zipper);
        return new IntQuery(zip, zip);
    }
}
