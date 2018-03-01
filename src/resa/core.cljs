(ns resa.core
  (:require
   [sablono.core :as sab :include-macros true :refer [html]]
   [antizer.reagent :as ant]
   [reagent.core :as r]
   cljsjs.react
   cljsjs.react.dom)
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]]))

(enable-console-print!)

(defmulti ui-screen
  (fn [store] (:screen @store)))

;;; step1

(defn h-next
  [store]
  (swap! store assoc :screen :step2))

(defmethod ui-screen :step1
  [store]
  (let [{:keys [pax time name phone email]} (:data @store)]
    (html [:div
           [:br]
           [:input
            {:class "form-control"
             :type "number"
             :placeholder "Number of guests"
             :min 1
             :auto-focus true
             :value (or pax "")
             :on-change #(swap! store assoc-in [:data :pax]
                                (-> % .-target .-value))}]
           [:br]
           [:input
            {:class "form-control"
             :type "datetime-local"
             :value (or time "")
             :on-change #(swap! store assoc-in [:data :time]
                                (-> % .-target .-value))}]
           [:br]
           ; [:div {:class "container"}
           ;  [:div {:class "row"}
           ;   [:div {:class "col-md-4"}]
           ;   [:div {:class "col-md-4"}
           [:button
            {:class "btn btn-primary btn-block"
             :on-click #(h-next store)}
            "Next..."
           ; [:div {:class "col-md-4"}]
            [:br]]])))

;;; step2

(defn h-book
  [store]
  (swap! store assoc :screen :step3))

(defn h-back-to-step1
  [store]
  (swap! store assoc :screen :step1))

(defmethod ui-screen :step2
  [store]
  (let [{:keys [pax time name phone email]} (:data @store)]
    (html [:div
           [:br]
           [:input
            {:class "form-control"
             :placeholder "Your name"
             :type "text"
             ;:ref (fn [input] (when input (.focus input)))
             ;:ref #(and % (.focus %))
             :auto-focus true
             :value (or name "")
             :on-change #(swap! store assoc-in [:data :name]
                                (-> % .-target .-value))}]
           [:br]
           [:input
            {:class "form-control"
             :placeholder "Your phone number"
             :type "tel"
             :value (or phone "")
             :on-change #(swap! store assoc-in [:data :phone]
                                (-> % .-target .-value))}]
           [:br]
           [:input
            {:class "form-control"
             :placeholder "Your e-mail"
             :type "email"
             :value (or email "")
             :on-change #(swap! store assoc-in [:data :email]
                                (-> % .-target .-value))}]
           [:br]
           [:button
            {:class "btn btn-default btn-block"
             :on-click #(h-back-to-step1 store)}
            "< Back"]
           [:button
            {:class "btn btn-primary btn-block"
             :on-click #(h-book store)}
            "Book!"]
           [:br]])))

;;; step3

(defn h-start-again
  [store]
  (swap! store assoc :screen :step1))

(defn h-back-to-step2
  [store]
  (swap! store assoc :screen :step2))

(defn h-confirm
  [store]
  (swap! store assoc :screen :step4))

(defmethod ui-screen :step3
  [store]
  (let [data (:data @store)
        {:keys [pax time name phone email]} data]
    (html [:div
           [:br]
           [:ul
            [:li "Name: " (or name "")]
            [:li "Phone: " (or phone "")]
            [:li "E-mail: " (or email "")]
            [:li "Date/time: " (or time "")]
            [:li "N. of guests: " (or pax "")]]
           [:button
            {:class "btn btn-default btn-block"
             :on-click #(h-start-again store)}
            "<< Start again"]
           [:button
            {:class "btn btn-default btn-block"
             :on-click #(h-back-to-step2 store)}
            "< Back"]
           [:button
            {:class "btn btn-primary btn-block"
             :on-click #(h-confirm store)
             :auto-focus true}
            "Confirm booking!"]
           [:br]])))

;;; step4

(defmethod ui-screen :step4
  [store]
  (let [{:keys [pax time name phone email]} (:data @store)]
    (html [:div
           [:br]
           [:p "Dear MM. " (or name "")]
           [:p "Thank you for your reservation!"]
           [:p "A confirmation e-mail has been sent to: " (or email "")]
           [:button
            {:class "btn btn-default btn-block"
             :auto-focus true
             :on-click #(h-start-again store)}
            "<< Start again"]
           [:br]])))

;;; cards

(defcard step1
  (fn [store _]
    (ui-screen store))
  {:screen :step1}
  {:inspect-data true})

(defcard step2
  (fn [store _]
    (ui-screen store))
  {:screen :step2}
  {:inspect-data true})

(defcard step3
  (fn [store _]
    (ui-screen store))
  {:screen :step3}
  {:inspect-data true})

(defcard step4
  (fn [store _]
    (ui-screen store))
  {:screen :step4}
  {:inspect-data true})

;;; app

(defonce app-store (atom {:screen :step1}))

(defn ui-app
  [store]
  (html [:div
         (ui-screen store)]))

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (do
      (add-watch app-store :render
                 (fn [k a o n] (.render js/ReactDOM (ui-app a) node)))
      (.render js/ReactDOM (ui-app app-store) node))))

(main)

;; remember to run lein figwheel and then browse to
;; /cards.html
