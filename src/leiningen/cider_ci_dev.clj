(ns leiningen.cider-ci_dev
  (:require
    [clj-yaml.core :as yaml]
    [clojure.tools.cli :refer [parse-opts]]

    [logbug.catcher :refer [snatch]]
    ))

(def usage-commands
  (->> ["Cider-CI Leiningen Development Plugin"
        ""
        "Usage: lein cider-ci_dev COMMAND"
        ""
        "Where command is one of: "
        "  name"
        "  group"
        "  project"
        "  version"
        ""]
       flatten (clojure.string/join \newline)))

(defn print-version [project]
  (println (:version project)))

(defn print-name [project]
  (println (:name project)))

(defn print-group [project]
  (println (:group project)))

(defn print-edition [project]
  (println (:edition project)))

(defn print-project [project]
  (clojure.pprint/pprint project))


(defn default []
  (println usage-commands)
  (System/exit -1))

(defn cider-ci_dev
  "Utilities to develop Cider-CI itself."
  [project cmd & args]
  (snatch {:return-fn (fn [_] (println usage-commands) nil)}
          (case cmd
            "version" (print-version project)
            "edition" (print-edition project)
            "name" (print-name project)
            "group" (print-group project)
            "project" (print-project project)
            (default)
            )))
