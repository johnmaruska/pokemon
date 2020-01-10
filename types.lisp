;;; Arch Lisp group, Jan 9, 2020 hack thing. Reimplemented part of Clojure type utility
;;; in Common Lisp with Travis Sunderland and Chris Gore. Not intended for use.

(defparameter *type-effectiveness* (make-hash-table :test 'equal))

(defun process-row (line)
  (let* ((data (str:split "," line))
         (type (first data))
         (modifiers (rest data)))
    (cons type (mapcar (compose #'rational #'read-from-string)
                       modifiers))))

(defun parse-file (file)
  (let* ((all-lines (str:lines (read-file-into-string file)))
         (headers (str:split "," (first all-lines)))
         (rows (mapcar 'process-row (rest all-lines))))
    (->> (mapcar (lambda (row) (mapcar #'cons headers row)) rows)
         (mapcar (lambda (row) (cons (cdar row) (rest row)))))))

(defvar *effectiveness*
  (parse-file "resources/type-effectiveness.csv"))

(defun get-best-types (comparator matchups &keys init-val)
  (let* ((best-val (reduce (lambda (acc m)
                             (if (funcall comparator (cdr m) acc) (cdr m) acc))
                           matchups
                           :initial-value init-val)))
    (loop for (def-type . modifier)
          in matchups
          when (= modifier best-val)
          collect def-type)))

(defun best-defender (atk-type)
  (let ((matchups (assoc-value *effectiveness* atk-type :test 'equal)))
    (get-best-types < matchups :init-val 4)))

(defun best-attacker (def-type)
  (let ((matchups (mapcar (lambda (row)
                            (cons (car row) (assoc-value (cdr row) def-type :test 'equal)))
                          *effectiveness*)))
    (get-best-types > matchups :init-value 0)))
