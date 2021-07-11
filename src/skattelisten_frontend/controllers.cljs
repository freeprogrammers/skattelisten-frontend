(ns skattelisten-frontend.controllers
  (:require
   [ajax.core :refer [GET]]
   [re-frame.core :as rf]))

(defonce typesense-uri "")
(defonce typesense-key "")
(defonce timeouts (atom {}))

(rf/reg-event-db
 :initialize-db
 (fn [_ _]
   {:companies []
    :search-text ""}))

(rf/reg-fx
 :dispatch-debounce
 (fn [[id event-vec n]]
   (js/clearTimeout (@timeouts id))
   (swap! timeouts assoc id
          (js/setTimeout (fn []
                           (rf/dispatch event-vec)
                           (swap! timeouts dissoc id))
                         n))))

(rf/reg-fx
 :stop-debounce
 (fn [id]
   (js/clearTimeout (@timeouts id))
   (swap! timeouts dissoc id)))

(rf/reg-event-fx
 :search-text-updated
 (fn [_ [_ current-search-string]]
   {:dispatch-debounce [::search [:load-companies current-search-string] 350]}))

(rf/reg-event-fx
 :set-search-text
 (fn [{:keys [db]} [_ search-text]]
   {:db (assoc db :search-text search-text)
    :dispatch [:search-text-updated search-text]}))

(rf/reg-event-db
 :set-companies
 (fn [db [_ companies]]
   (assoc db :companies
          (->> (companies :hits)
               (map #(:document %))))))

(rf/reg-fx
 :get-companies
 (fn [[url api-key query handler]]
   (GET (str url "/collections/records/documents/search")
     {:handler handler
      :error-handler (fn [{:keys [status status-text]}]
                       (js/console.log status status-text))
      :params {:q query
               :query_by "company_name"
               :per_page 25}
      :headers {:X-TYPESENSE-API-KEY api-key}
      :response-format :json
      :keywords? true})))

(rf/reg-event-fx
 :load-companies
 (fn [_ [_ query]]
   {:get-companies [typesense-uri typesense-key query #(rf/dispatch [:set-companies %])]}))

(rf/reg-sub
 :search-text
 (fn [db _]
   (:search-text db)))

(rf/reg-sub
 :companies
 (fn [db _]
   (:companies db)))
