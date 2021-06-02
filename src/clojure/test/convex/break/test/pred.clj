(ns convex.break.test.pred

  "Tests Convex core type predicate. 
  
   Specialized predicates such as `contains-key?` or `fn?` are located in relevant namespace."

  {:author "Adam Helinski"}

  (:require [clojure.test                  :as t]
            [clojure.test.check.generators :as TC.gen]
            [clojure.test.check.properties :as TC.prop]
            [convex.break.eval             :as $.break.eval]
            [convex.break.prop             :as $.break.prop]
            [convex.lisp                   :as $.lisp]
            [convex.lisp.gen               :as $.lisp.gen]))


;;;;;;;;;;


(defn- -prop

  ;; Used by [[pred-data-false]] and [[pred-data-true]].

  [form result? f gen]

  (TC.prop/for-all [x gen]
    (let [result ($.break.eval/result* (~form ~x))]

      ($.break.prop/mult*

        "Returns right boolean value"
        (result? result)
        
        "Consistent with Clojure"
        (if f
          (= result
             (f x))
          true)))))



(defn prop-false

  "Like [[pred-data-true]] but tests for negative results."


  ([form gen]

   (prop-false form
               nil
               gen))


  ([form f gen]

   (-prop form
          false?
          f
          gen)))



(defn prop-true

  "Tests if a value generated by `gen` passes a data predicate on the CVM.
  
   If `f-clojure` is given, also ensures that the very same value produces the exact same result
   in Clojure."


  ([form gen]

   (prop-true form
              nil
              gen))


  ([form f gen]

   (-prop form
          true?
          f
          gen)))


;;;;;;;;;;


($.break.prop/deftest account?--false

  (prop-false 'account?
              (TC.gen/such-that (comp not
                                      $.lisp/address?)
                                $.lisp.gen/any)))



($.break.prop/deftest address?--false

  (prop-false 'address?
              (TC.gen/such-that (comp not
                                      $.lisp/address?)
                                $.lisp.gen/any)))



($.break.prop/deftest address?--true

  (TC.prop/for-all* [$.lisp.gen/address]
                    #($.break.eval/result (list 'address?
                                                %))))



($.break.prop/deftest blob?--false

  (prop-false 'blob?
              (TC.gen/such-that (comp not
                                      $.lisp/blob?)
                                $.lisp.gen/any)))



($.break.prop/deftest blob?--true

  (prop-true 'blob?
             $.lisp.gen/blob))



($.break.prop/deftest boolean?--false

  (prop-false 'boolean?
              boolean?
              (TC.gen/such-that (comp not
                                      boolean?)
                                $.lisp.gen/any)))



(t/deftest boolean?--true

  (t/is (true? ($.break.eval/result true))
        "True")

  (t/is (false? ($.break.eval/result false))
        "False"))



#_($.break.prop/deftest coll?--false

  ;; TODO. Returns true on blob-like items.

  (prop-false 'coll?
              coll?
              $.lisp.gen/scalar))



($.break.prop/deftest coll?--true

  (prop-true 'coll?
             coll?
             $.lisp.gen/collection))



($.break.prop/deftest keyword?--false

  (prop-false 'keyword?
              keyword?
              (TC.gen/such-that (comp not
                                      keyword?)
                                $.lisp.gen/any)))



($.break.prop/deftest keyword?--true

  (prop-true 'keyword?
             keyword?
             $.lisp.gen/keyword))



($.break.prop/deftest list?--false

  (prop-false 'list?
              $.lisp/list?
              (TC.gen/such-that (comp not
                                      $.lisp/list?)
                                $.lisp.gen/any)))



($.break.prop/deftest list?--true

  (prop-true 'list?
             $.lisp/list?
             $.lisp.gen/list))



($.break.prop/deftest long?--false

  (prop-false 'long?
              int?
              (TC.gen/such-that (comp not
                                      int?)
                                $.lisp.gen/any)))



($.break.prop/deftest long?--true

  (prop-true 'long?
             int?
             $.lisp.gen/long))



($.break.prop/deftest map?--false

  (prop-false 'map?
              map?
              (TC.gen/such-that #(not (map? %))
                                $.lisp.gen/any)))



($.break.prop/deftest map?--true

  (prop-true 'map?
             map?
             $.lisp.gen/map))



($.break.prop/deftest nil?--false

  (prop-false 'nil?
              nil?
              (TC.gen/such-that some?
                                $.lisp.gen/any)))



(t/deftest nil?--true

  (t/is (true? (nil? ($.break.eval/result nil))))

  (t/is (true? (nil? ($.break.eval/result '(do nil))))))



($.break.prop/deftest number?--false

  (prop-false 'number?
              number?
              (TC.gen/such-that (comp not
                                      number?)
                                $.lisp.gen/any)))



($.break.prop/deftest number?--true

  (prop-true 'number?
             number?
             $.lisp.gen/number))



($.break.prop/deftest set?--false

  (prop-false 'set?
              set?
              (TC.gen/such-that (comp not
                                      set?)
                                $.lisp.gen/any)))



($.break.prop/deftest set?--true

  (prop-true 'set?
             set?
             $.lisp.gen/set))



($.break.prop/deftest str?--false

  (prop-false 'str?
              string?
              (TC.gen/such-that (comp not
                                      string?)
                                $.lisp.gen/any)))



($.break.prop/deftest str?--true

  (prop-true 'str?
             string?
             $.lisp.gen/string))



($.break.prop/deftest symbol?--false

  (prop-false 'symbol?
              (TC.gen/such-that (comp not
                                      $.lisp/quoted?)
                                $.lisp.gen/any)))



($.break.prop/deftest symbol?--true

  (prop-true 'symbol?
             $.lisp.gen/symbol-quoted))



($.break.prop/deftest vector?--false

  (prop-false 'vector?
              vector?
              (TC.gen/such-that (comp not
                                      vector?)
                                $.lisp.gen/any)))



($.break.prop/deftest vector?--true

  (prop-true 'vector?
             vector?
             $.lisp.gen/vector))