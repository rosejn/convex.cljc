(ns convex.break

  "Miscellaneous utilities related to the Break project."

  {:author "Adam Helinski"}

  (:require [convex.cvm      :as $.cvm]
            [convex.cvm.file :as $.cvm.file]
            [convex.cvm.raw  :as $.cvm.raw]))


;;;;;;;;;;


(defn ctx

  "Prepares a default context for dev and testing."

  []

  ($.cvm.file/load [["src/convex/break/util.cvx"
                     {:wrap (partial $.cvm.raw/deploy
                                     '$)}]]
                   {:after-run $.cvm/juice-refill}))
