(ns convex.lib.trust

  "Dev environment for the Trust library."

  {:author           "Adam Helinski"
   :clj-kondo/config '{:linters {:unused-namespace {:level :off}}}}

  (:require [convex.code     :as $.code]
            [convex.cvm      :as $.cvm]
            [convex.clj.eval :as $.clj.eval]
            [convex.disk     :as $.disk]
            [convex.clj      :as $.clj]))


;;;;;;;;;;


(comment


  (def w*ctx
       ($.disk/watch {'trust "src/convex/lib/trust.cvx"}
                     (fn [env]
                       (update env
                               :ctx
                               $.clj.eval/ctx
                               '(def trust
                                     (deploy trust))))))

  ($.cvm/exception @w*ctx)

  (.close w*ctx)



  ($.clj.eval/result @w*ctx
                     '(do
                        (let [addr (deploy (trust/build-whitelist {:whitelist [42]}))]
                          [(trust/trusted? addr
                                           42)
                           (trust/trusted? addr
                                           100)])))


  ($.clj.eval/result @w*ctx
                     '(do
                        (let [addr (deploy (trust/add-trusted-upgrade nil))]
                          (call addr
                                (upgrade '(def foo 42)))
                          (lookup addr
                                  foo))))

  )
