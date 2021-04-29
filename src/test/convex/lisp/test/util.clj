(ns convex.lisp.test.util

  ""
  {:author "Adam Helinski"}

  (:require [clojure.core]
            [clojure.test.check.results      :as tc.result]
            [convex.lisp                     :as $]
            [convex.lisp.schema              :as $.schema]
            [malli.core                      :as malli]
            [malli.generator                 :as malli.gen])
  (:refer-clojure :exclude [eval]))


(declare eval-source
         registry)


;;;;;;;;;; Registry and fetching generators


(defn generator

  [k]

  (malli.gen/generator k
                       {:registry registry}))



(def registry
     (-> (malli/default-schemas)
         $.schema/registry))


;;;;;;;;;; Helpers


(defn eq

  "Substitute for `=` so that NaN equals NaN."

  [& arg+]

  (apply =
         (clojure.core/map hash
              			   arg+)))



(defn eval

  "Evals the given Clojure `form` representing Convex Lisp code and returns the result
   as Clojure data."

  [form]

  (-> form
      $/clojure->source
      eval-source))



(defn eval-source

  "Reads Convex Lisp source, evals it and converts the result to a Clojure value."

  [source]

  (-> source
      $/read
      $/eval
      $/result
      $/to-clojure))



(defn fail

  "Returns a `test.check` error with an error message."

  [string-error]

  (reify tc.result/Result

    (pass? [_]
      false)

    (result-data [_]
      {:convex.lisp/error string-error})))



(defmacro prop+

  "Multiplexes a `test.check` properties.
  
   Tests each pair of error message and test proving that error. Fails with [[faill]]
   when needed.

   ```clojure
   (prop+

     \"3 must be greater than 4\"
     (not (< 3 4))

     \"Result must be double\"
     (not (double? ...)))
   ```"

  [& prop-pair+]

  (assert (even? (count prop-pair+)))
  `(if-some [string-error# (cond
                            ~@(reduce (fn [acc [string-error form-test]]
                                        (conj acc
                                              form-test
                                              string-error))
                                      []
                                      (partition 2
                                                 prop-pair+)))]
     (fail string-error#)
     true))