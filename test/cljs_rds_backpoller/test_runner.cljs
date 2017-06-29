(ns cljs-rds-backpoller.test-runner
 (:require [doo.runner :refer-macros [doo-tests]]
           [cljs-rds-backpoller.core-test]
           [cljs.nodejs :as nodejs]))

(try
  (.install (nodejs/require "source-map-support"))
  (catch :default _))

(doo-tests
 'cljs-rds-backpoller.core-test)
