(ns poke.jobs)

(defn has-type? [type pokemon]
  (or (= type (pokemon :primary-type))
      (= type (pokemon :secondary-type))))

(defn dual-type? [pokemon]
  (boolean (:secondary-type pokemon)))

(defn get-types
  "From a sequence of jobs, get the set of all required types."
  [jobs]
  (into #{} (map :type jobs)))

(defn all-match? [jobs-types pokemon]
  (every? #(contains? types) [primary secondary]))

(defn any-match? [jobs-types pokemon]
  (any? #(contains? types) [(:primary-type pokemon)
                            (:secondary-type pokemon)]))

(defn category
  "Get the job category for a single pokemon based on what jobs are available."
  [pokemon jobs]
  (let [single? (nil? (:secondary-type pokemon))
        dual? (not single?)]
    (cond
      (and single? (any-match? types pokemon))
      :single-with-job

      (and dual? (all-match? types pokemon))
      :dual-with-both-jobs

      (and dual? (any-match? types pokemon))
      :dual-with-single-job

      :else :no-jobs)))

(defn categorize-all
  "Bucket all pokemon based on their category."
  [pokemon jobs]
  (let [types (get-types jobs)]
    (reduce (fn [acc p] (update acc (category p) conj p))
            {:single-with-job      []
             :dual-with-single-job []
             :dual-with-both-jobs  []
             :no-jobs              []}
            pokemon)))

(defn assign-workers
  "Get available pokemon to work a job. Return pokemon selected and remaining
  categorized pokemon."
  [job pokemon]
  (let [single-with-job      (filter (partial has-type? (:type job))
                                     (:single-with-job pokemon))
        dual-with-single-job (filter (partial has-type? (:type job))
                                     (:dual-with-single-job pokemon))
        dual-with-both-jobs  (filter (partial has-type? (:type job))
                                     (:dual-with-both-jobs pokemon))
        no-jobs              (:no-jobs pokemon)  ; never match type
        ]
    ;; grab # pokemon from priority order
    ))

(defn main- [& args]
  ;; get jobs in [Name, Type, #, Stars] format

  ;; read in inventory of pokemon

  ;; get types against pokedex

  ;; dump pokemon into one of three lists
  ;; 1. Single-type with a job
  ;; 2. Dual-type with a job
  ;; 3. Dual-type with jobs for both type
  ;; 4. Other
  ;; Exclude level 100s as an option

  ;; Fill jobs with priority order above, in order of # slots
  )
