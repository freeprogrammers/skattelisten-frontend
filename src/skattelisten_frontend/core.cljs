(ns skattelisten-frontend.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [ajax.core :refer [GET]]))

(def typesense-api-key "")
(def typesense-url "")
(def companies (r/atom []))

(defn search-company! [query]
  (GET (str typesense-url "/collections/records/documents/search")
    {:handler #(->> %
                    :hits
                    (map (fn [hit] (:document hit)))
                    (reset! companies))
     :error-handler (fn [{:keys [status status-text]}]
                      (js/console.log status status-text))
     :params {:q query
              :query_by "company_name"
              :per_page 25}
     :headers {:X-TYPESENSE-API-KEY typesense-api-key}
     :response-format :json
     :keywords? true}))

(defn search-bar [value]
  [:div.search-bar
   [:input.search-bar__input
    {:type "text"
     :value @value
     :placeholder "Search CVR or Company name"
     :on-change #(reset! value (-> % .-target .-value))}]])

(defn header-menu []
  (let [search-text (r/atom "")]
    (fn []
      (search-company! @search-text)
      [:header
       [:p.site-name "Skattelisten"]
       [search-bar search-text]
       [:div.links]])))

(defn companies-list [companies]
  [:table
   [:thead
    [:tr
     [:th "CVR"]
     [:th "Name"]
     [:th "Type"]
     [:th "Year"]
     [:th "Taxable income"]
     [:th "Deficit"]
     [:th "Corporate Tax"]]]
   [:tbody
    (for [company companies]
      ^{:key (company :cvr)}
      [:tr
       [:td (company :cvr)]
       [:td (company :company_name)]
       [:td (company :company_type)]
       [:td (company :year)]
       [:td (company :taxable_income) " kr."]
       [:td (company :deficit) " kr."]
       [:td (company :corporate_tax) " kr."]])]])

(defn app []
  [:div
   [header-menu]
   [:div.body-container
    [companies-list @companies]]])

(defn mount-root []
  (d/render [app] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
