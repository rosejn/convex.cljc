(ns convex.lisp.test.core.account

  "Testing account utilities."

  {:author "Adam Helinski"}

  (:require [clojure.test.check.clojure-test :as tc.ct]
            [convex.lisp.form                :as $.form]
            [convex.lisp.test.eval           :as $.test.eval]
            [convex.lisp.test.mult           :as $.test.mult]
            [convex.lisp.test.prop           :as $.test.prop]))


;;;;;;;;;; Default values


(def max-size-coll

  ""

  5)


;;;;;;;;;;


(tc.ct/defspec account-inexistant

  ($.test.prop/check [:and
                      :int
                      [:>= 50]]
                     (fn [x]
                       ($.test.prop/mult*

                         "Account does not exist"
                         (false? ($.test.eval/form (list 'account?
                                                         x)))

                         "Actor does not exist"
                         (false? ($.test.eval/form (list 'actor?
                                                         x)))))))



(tc.ct/defspec create-account--

  ($.test.prop/check :convex/hexstring-32
                     (fn [x]
                       ($.test.prop/mult
                         ($.test.mult/new-account []
                                                  ($.test.eval/form->context ($.form/templ {'?hexstring x}
                                                                                           '(def addr
                                                                                                 (create-account ?hexstring))))
                                                  false?)))))