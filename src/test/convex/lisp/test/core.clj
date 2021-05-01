(ns convex.lisp.test.core

  "Testing Convex Core by generating Convex Lisp forms as Clojure data, converting them to source,
   and evaling."

  {:author "Adam Helinski"}

  (:require [clojure.test                    :as t]
            [clojure.test.check.properties   :as tc.prop]
            [clojure.test.check.clojure-test :as tc.ct]
            [convex.lisp                     :as $]
            [convex.lisp.test.util           :as $.test.util]))


;;;;;;;;;;


(defn prop-clojure

  ""

  [core-symbol schema f-related]

  (tc.prop/for-all* [($.test.util/generator schema)]
                    (fn [x]
                      ($.test.util/eq (apply f-related
                                             x)
                                      ($.test.util/eval (list* core-symbol
                                                               x))))))



(defn prop-compare

  ""

  [core-symbol f-related]

  (prop-clojure core-symbol
                [:vector
                 {:min 1}
                 :convex/number]
                f-related))



(defn prop-double

  ""

  [core-symbol]

  (tc.prop/for-all* [($.test.util/generator [:vector
                                             {:min 1}
                                             :convex/number])]
                    (fn [x]
                      (double? ($.test.util/eval (list* core-symbol
                                                        x))))))



(defn prop-numeric

  ""

  [core-symbol]

  (tc.prop/for-all* [($.test.util/generator [:vector
                                             {:min 1}
                                             :convex/long])]
                    (fn [x]
                      ($.test.util/prop+

                        "Numerical computation of longs must result in a long"
                        (int? ($.test.util/eval (list* core-symbol
                                                       x)))

                        "Numerical computation with at least one double must result in a double"
                        (double? ($.test.util/eval (list* core-symbol
                                                          (update x
                                                                  (rand-int (dec (count x)))
                                                                  double))))))))


;;;;;;;;;;


(tc.ct/defspec |*

  (prop-numeric '*))



(tc.ct/defspec |+

  (prop-numeric '+))



(tc.ct/defspec |-

  (prop-numeric '-))



(tc.ct/defspec |div

  (prop-double '/))



(tc.ct/defspec |<

  (prop-compare '<
                <))



(tc.ct/defspec |<=

  (prop-compare '<=
                <=))



(tc.ct/defspec |=

  (prop-compare '=
                =))



(tc.ct/defspec |>=

  (prop-compare '>=
                >=))



(tc.ct/defspec |>

  (prop-compare '>
                >))



(tc.ct/defspec abs

  (tc.prop/for-all* [($.test.util/generator :convex/number)]
                    (fn [x]
                      (let [x-2 ($.test.util/eval (list 'abs
                                                        x))]
                        ($.test.util/prop+

                          "Must be positive"
                          (>= x-2
                              0)

                          "Type is preserved"
                          (= (type x-2)
                             (type x)))))))


(tc.ct/defspec |byte

  (tc.prop/for-all* [($.test.util/generator :convex/number)]
                    (fn [x]
                      (<= Byte/MIN_VALUE
                          ($.test.util/eval (list 'byte
                                                  x))
                          Byte/MAX_VALUE))))



(tc.ct/defspec ceil

  (prop-clojure 'ceil
                [:tuple :convex/number]
                #(StrictMath/ceil %)))



(tc.ct/defspec dec--double

  ;; Unintuitive behavior. When sufficiently small double, is cast to 0.
  ;; Not small enough, get cast to `Long/MIN_VALUE` and underflows.

  (tc.prop/for-all* [($.test.util/generator [:double
                                             {:min (double Long/MIN_VALUE)}])]
                    (fn [x]
                      (let [x-2 ($.test.util/eval (list 'dec
                                                        x))]
                        ($.test.util/prop+

                          "Result is always a long"
                          (int? x-2)

                          "Decrement higher than maximum long"
                          (if (>= x
                                  Long/MAX_VALUE)
                            (= x-2
                               (dec Long/MAX_VALUE))
                            true)

                          "Decrement in long range"

                          (if (< Long/MIN_VALUE
                                 x
                                 Long/MAX_VALUE)
                            (= x-2
                               (dec (long x)))
                            true))))))




(t/deftest dec--double-underflow

  (t/is (= Long/MAX_VALUE
           ($.test.util/eval (list 'dec
                                   (double Long/MIN_VALUE))))))



(tc.ct/defspec dec--long

  (tc.prop/for-all* [($.test.util/generator :convex/long)]
                    (fn [x]
                      (let [x-2 ($.test.util/eval (list 'dec
                                                        x))]
                        ($.test.util/prop+

                          "Result is always a long"
                          (int? x-2)

                          "Decrement or underflow"
                          (= x-2
                             (if (= x
                                    Long/MIN_VALUE)
                               Long/MAX_VALUE
                               (dec x))))))))



(tc.ct/defspec exp

  (prop-clojure 'exp
                [:tuple :convex/number]
                #(StrictMath/exp %)))



(tc.ct/defspec floor

  (prop-clojure 'floor
                [:tuple :convex/number]
                #(StrictMath/floor %)))



(tc.ct/defspec inc--double

  ;; See [[dec-double]].

  (tc.prop/for-all* [($.test.util/generator [:double
                                             {:min (double Long/MIN_VALUE)}])]
                    (fn [x]
                      (let [x-2 ($.test.util/eval (list 'inc
                                                        x))]
                        ($.test.util/prop+

                          "Result is always a long"
                          (int? x-2)

                          "Overflow"
                          (if (>= x
                                  Long/MAX_VALUE)
                            (= x-2
                               Long/MIN_VALUE)
                            true)

                          "Increment in long range"
                          (if (< Long/MIN_VALUE
                                 x
                                 Long/MAX_VALUE)
                            (= x-2
                               (inc (long x)))
                            true))))))



(tc.ct/defspec inc--long

  (tc.prop/for-all* [($.test.util/generator :convex/long)]
                    (fn [x]
                      (let [x-2 ($.test.util/eval (list 'inc
                                                        x))]
                        ($.test.util/prop+

                          "Result is always a long"
                          (int? x-2)

                          "Increment or overflow"
                          (= x-2
                             (if (= x
                                    Long/MAX_VALUE)
                               Long/MIN_VALUE
                               (inc x))))))))



;; TODO. `log`, weird, no docstring and behaves like `vector`



(tc.ct/defspec |max

  (prop-compare 'max
                max))



(tc.ct/defspec |min

  (prop-compare 'min
                min))



(tc.ct/defspec pow

  (tc.prop/for-all* [($.test.util/generator [:tuple
                                             :convex/number
                                             :convex/number])]
                    (fn [[x y]]
                      ($.test.util/eq (StrictMath/pow x
                                                      y)
                                      ($.test.util/eval (list 'pow
                                                              x
                                                              y))))))



(tc.ct/defspec signum

  (tc.prop/for-all* [($.test.util/generator :convex/number)]
                    (fn [x]
                      (let [x-2 ($.test.util/eval (list 'signum
                                                        x))]
                        ($.test.util/prop+

                          "Negative"
                          (if (neg? x)
                            (= -1
                               x-2)
                            true)

                          "Positive"
                          (if (pos? x)
                            (= 1
                               x-2)
                            true)

                          "Zero"
                          (if (zero? x)
                            (zero? x-2)
                            true))))))




(tc.ct/defspec sqrt

  (prop-clojure 'sqrt
                [:tuple :convex/number]
                #(StrictMath/sqrt %)))