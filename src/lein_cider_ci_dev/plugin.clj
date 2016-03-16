(ns lein_cider-ci_dev.plugin
  (:require
    [clj-yaml.core :as yaml]
    [clojure.tools.cli :refer [parse-opts]]

    [logbug.catcher :refer [snatch]]
    )
  (:import
    [java.io File]
    ))

(defn version []
  (let [releases (yaml/parse-string (slurp "../config/releases.yml"))
        current-release-info (-> releases :releases first)
        version (str (:version_major current-release-info)
                     "."
                     (:version_minor current-release-info)
                     "."
                     (:version_patch current-release-info)
                     (when-let [pre (:version_pre current-release-info)]
                       (str "-" pre))
                     (when-let [build (:version_build current-release-info)]
                       (str "+" build)))]
        version))


(defn edition []
  (let [releases (yaml/parse-string (slurp "../config/releases.yml"))]
    (-> releases :releases first :edition)))

(defn normalize [path-part]
  (->  path-part
      .toLowerCase
      (.replace  \- \_)))

(defn write-clj-self-file [project]
  (let [path (clojure.string/join File/separator
                                  [(first (:source-paths project))
                                   (normalize (:group project))
                                   "self.clj"])
        code (->> [(str "(ns "(:group project)".self)")
                   (str "(def GROUP \"" (:group project) "\")")
                   (str "(def NAME \"" (:name project) "\")")
                   (str "(def EDITION \"" (edition) "\")")
                   (str "(def VERSION \"" (version) "\")")
                   (str "")]
                  flatten (clojure.string/join \newline))]
    (spit path code)))

(defn middleware [project]
  (let [version (version)
        edition (edition)
        project (assoc project
                       :version version
                       :edition edition
                       :uberjar-name (str (:name project) ".jar")
                       )]
    (write-clj-self-file project)
    project))
