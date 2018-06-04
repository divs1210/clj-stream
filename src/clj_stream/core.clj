(ns clj-stream.core
  "Clojure wrapper over Java streams using StreamEx (https://github.com/amaembo/streamex)"
  (:refer-clojure :exclude [count filter first map reduce seq])
  (:require [clojure.core :as clj])
  (:import [clojure.lang Seqable]
           [java.util Optional]
           [java.util.concurrent ForkJoinPool ForkJoinTask]
           [java.util.function Consumer Predicate UnaryOperator BinaryOperator]
           [java.util.stream Stream]
           [one.util.streamex StreamEx]))

;; Java Functional API
;; ===================
(defn consumer [f]
  (reify Consumer
    (accept [_ v]
      (f v))))

(defn predicate [f]
  (reify Predicate
    (test [_ v]
      (f v))))

(defn unary-op [f]
  (reify UnaryOperator
    (apply [_ v]
      (f v))))

(defn binary-op [f]
  (reify BinaryOperator
    (apply [_ x y]
      (f x y))))

(defn <-optional
  "Returns the value of this `Optional` if it has one,
  otherwise returns the supplied default, else nil."
  ([^Optional o]
   (<-optional o nil))
  ([^Optional o default]
   (if (.isPresent o)
     (.get o)
     default)))


;; seq <=> stream Conversion
;; =========================
(declare first)

(defn stream?
  "Returns true if coll is a stream."
  [coll]
  (instance? Stream coll))

(defn ^Stream stream
  "Returns coll if it is already a stream,
  otherwise a new stream from it."
  [coll]
  (if (stream? coll)
    coll
    (-> (clj/seq coll)
        .stream
        StreamEx/of
        first)))

(defn seq
  "Returns a collection from the given stream."
  [coll]
  (-> (stream coll)
      .iterator
      iterator-seq))


;; Stream Operations
;; =================
(defn first
  "Returns the first item in the stream, or nil."
  [coll]
  (-> (stream coll)
      .findFirst
      <-optional))

(defn count
  "Like `clj/count`, but for streams."
  [coll]
  (.count (stream coll)))

(defn map
  "Like `clj/map`, but for streams."
  [coll f]
  (.map (stream coll)
        (unary-op f)))

(defn ^Stream filter
  "Like `clj/filter`, but for streams."
  [coll f]
  (.filter (stream coll)
           (predicate f)))

(defn reduce
  "Like `clj/reduce`, but for streams."
  ([coll f]
   (-> (stream coll)
       (.reduce (binary-op f))
       <-optional))
  ([coll f x]
   (.reduce (stream coll)
            x
            (binary-op f))))

(defn foreach
  "Like `clj/map`, but for side-effects on streams.
  Returns nil."
  ([coll f]
   (foreach coll f false))
  ([coll f ordered?]
   (let [s (stream coll)
         c (consumer f)]
     (if ordered?
       (.forEachOrdered s c)
       (.forEach s c)))))
