(ns poke.util
  (:require
   [clojure.edn :as edn]
   [clojure.data.csv :as csv]
   [clojure.java.io :as io]))

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map keyword) ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(defn read-csv-data* [filepath]
  (with-open [reader (io/reader filepath)]
    (vec (csv-data->maps (csv/read-csv reader)))))

(def read-csv-data (memoize read-csv-data*))


(defn vals->floats
  "Convert a map/dictionary to have _all_ float values. All values must be numerical."
  [m]
  (->> m
       (map (fn [[k v]] [k (edn/read-string v)]))
       (into {})))
