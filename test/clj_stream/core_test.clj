(ns clj-stream.core-test
  (:require [clojure.test :refer :all]
            [clj-stream.core :as s]))

(defmacro bench [text expr]
  `(do
     (println ~text)
     (time
      (doall
       (for [_# (range 100)]
         ~expr)))))

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
