(ns test.clj-deps.deps-test
  (:use (clojure test))
  (:require
     [clj-deps.deps :as dep]))

(defmacro test-deps [libs, ns-form]
  (let [form `(quote ~ns-form)
        name (second (second form))
        result `(quote ~(cons name libs))]
    `(is (= ~result (dep/process-ns ~form)))))

(deftest no-deps
  (test-deps () (ns my-ns))
  (test-deps () (ns #^{:author "SBG"} my-ns))
  (test-deps () (ns #^{:author "SBG"} my-ns "docstring")))

(deftest ignore-forms
  (test-deps () (ns my-ns (:refer-clojure :exclude [printf])))
  (test-deps () (ns my-ns (:import java.util.Date (java.sql Connection))))
  (test-deps () (ns my-ns (:load "path")))
  (test-deps () (ns my-ns (:get-class))))

(deftest simple-libspec
  (test-deps (my-lib) (ns my-ns (:use my-lib)))
  (test-deps (my-lib other-lib) (ns my-ns (:use my-lib other-lib)))
  (test-deps (my-lib other and-other) (ns my-ns (:use my-lib) (:require other and-other))))

(deftest vector-libspec
  (test-deps (my-lib) (ns my-ns (:use [my-lib])))
  (test-deps (my-lib) (ns my-ns (:use [my-lib :only mm])))
  (test-deps (my-lib) (ns my-ns (:use [my-lib :exclude mm :only pp])))
  (test-deps (my-lib other-lib) (ns my-ns (:use [my-lib :only mm] [other-lib])))
  (test-deps (your-lib my-lib other-lib) (ns my-ns (:use your-lib [my-lib :only mm] [other-lib :rename {a b}])))
  (test-deps (my-lib your-lib our-lib) (ns my-ns (:use [my-lib]) (:require your-lib) (:use [our-lib :only a]))))

(deftest prefix-list
  (test-deps (lib.a) (ns my-ns (:require (lib a))))
  (test-deps (lib.a lib.b) (ns my-ns (:require (lib a b))))
  (test-deps (lib1.a lib1.b lib2.c) (ns my-ns (:require (lib1 a b) (lib2 c))))
  (test-deps (lib1.a lib1.b lib2.c lib3.d lib3.e) (ns my-ns (:require (lib1 a b)) (:use (lib2 c) (lib3 d e))))
  (test-deps (lib.a lib.b) (ns my-ns (:require (lib a [b :only pp])))))

(deftest mixed
  (test-deps (l1 l2 l3.a l3.b l4 l5) 
             (ns #^{:author "SBG"} my-ns "docstring"
               (:use l1 [l2 :as p])
               (:require (l3 a [b :only p :as g]) l4 [l5])
               (:import java.util.Date (java.sql Connection))
               (:get-class))))

