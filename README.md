# JAYield

[![Build Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.jayield%3Ajayield&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.jayield%3Ajayield)
[![Coverage Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.jayield%3Ajayield&metric=coverage)](https://sonarcloud.io/component_measures?id=com.github.jayield%3Ajayield&metric=Coverage)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.jayield/jayield/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.jayield/jayield)

_Minimalistic_, _extensible_, _non-parallel_ and _lazy_ sequence implementation interoperable with Java
`Stream` (`toStream` and `fromStream`), which provides an idiomatic `yield` like _generator_.

JAYield `Query` provides similar operations to Java `Stream`, or
[jOOλ][18] `Seq`, or [StreamEx][16], or [Vavr][19] Stream. 
Yet, `Query` is **extensible** and its methods can be [chained
fluently](#extensibility-and-chaining) with new operations in a pipeline.
Furthermore, `Query` has lower per-element access cost and offers an optimized
fast-path traversal, which presents better sequential processing performance in
some benchmarks, such as [sequences-benchmarks][20] and [jayield-jmh][21].

The core API of `Query` provides well-known query methods that can be 
composed fluently (_pipeline_), e.g.:

```java
// pipeline: iterate-filter-map-limit-forEach
//
Query.iterate('a', prev -> (char) ++prev).filter(n -> n%2 != 0).map(Object::toString).limit(10).forEach(out::println);
```


## Extensibility and chaining

Notice how it looks a JAYield custom `collapse()` method that merges series of adjacent elements.
It has a similar shape to that one written in any language providing the `yield` operator
such as C\#.

<table class="table">
    <tr class="row">
        <td>

```java
class Queries {
  private U prev = null;
  <U> Traverser<U>  collapse(Query<U> src) {
    return yield -> {
      src.traverse(item -> {
        if (prev == null || !prev.equals(item))
        yield.ret(prev = item);
      });
    };
  }
}
```

</td>
<td>

```csharp
static class Extensions {
  static IEnumerable <T> Collapse <T>(this IEnumerable <T> src) {
    IEnumerator <T> iter = src.GetEnumerator();
    T prev = null;
    while(iter.MoveNext ()) {
      if(prev == null || !prev.Equals(iter.Current))
      yield return prev = iter.Current;
    }
  }
}
```

</td>
</tr>
</table>

These methods can be chained in queries, such as:

<table class="table">
    <tr class="row">
        <td>

```java
Query
    .of(7, 7, 8, 9, 9, 8, 11, 11, 9, 7)
    .then(new Queries()::collapse)
    .filter(n -> n%2 != 0)
    .map(Object::toString)
    .traverse(out::println);

```

</td>
<td>

```csharp
new int[]{7, 7, 8, 9, 9, 8, 11, 11, 9, 7}
    .Collapse()
    .Where(n => n%2 != 0)
    .Select(n => n.ToString())
    .ToList()
    .ForEach(Console.WriteLine);
```

</td>
</tr>
</table>

## Internals Overview

`Traverser` is the primary choice for traversing the `Query` elements in bulk and 
supports all its methods including _terminal_, _intermediate_ and _short-circuting_
operations.
To that end, the traversal's consumer - `Yield` - provides one method to return
an element (`ret`) and other to finish the iteration (`bye`).
`Advancer` is the alternative iterator of `Query` that provides individually traversal.

<img src="assets/jayield-yuml.svg" width="600px">

## Installation

In order to include it to your Maven project, simply add this dependency:

```xml
<dependency>
    <groupId>com.github.jayield</groupId>
    <artifactId>jayield</artifactId>
    <version>1.4.0</version>
</dependency>
```

You can also download the artifact directly from [Maven
Central Repository](http://repo1.maven.org/maven2/com/github/jayield/jayield/)


## License

This project is licensed under [Apache License,
version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

[16]: https://github.com/amaembo/streamex
[18]: https://github.com/jOOQ/jOOL
[19]: https://github.com/vavr-io/vavr
[20]: https://github.com/tinyield/sequences-benchmarks
[21]: https://github.com/jayield/jayield-jmh
