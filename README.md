# streamex-benchmark

Clojure wrapper over Java streams using StreamEx (https://github.com/amaembo/streamex)

## Usage

From `clj-stream.core-test`:
```clojure
(deftest tests
  (is (= (bench
          "Clojure seqs:"
          (->> (range 1000000)
               (filter even?)
               (map #(Math/sqrt %))
               (reduce +)))

         (bench
          "Clojure transducers:"
          (transduce (comp (filter even?)
                           (map #(Math/sqrt %)))
                     +
                     (range 1000000)))

         (bench
          "StreamEx streams:"
          (-> (range 1000000)
              (s/filter even?)
              (s/map #(Math/sqrt %))
              (s/reduce +))))))
```

Running `lein test` shows:
```
Clojure seqs:
"Elapsed time: 6151.199012 msecs"
Clojure transducers:
"Elapsed time: 3928.352095 msecs"
StreamEx streams:
"Elapsed time: 5092.265878 msecs"
```

So... faster than seqs, but slower than transducers. Hmm.

## License

Copyright © Divyansh Prakash, 2018

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
