# clj-stream

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
          "StreamEx streams:"
          (-> (range 1000000)
              (s/filter even?)
              (s/map #(Math/sqrt %))
              (s/reduce +))))))
```

Running `lein test` shows:
```
Clojure seqs:
"Elapsed time: 5257.114293 msecs"
StreamEx streams:
"Elapsed time: 4272.70208 msecs"
```

## License

Copyright © Divyansh Prakash, 2018

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
