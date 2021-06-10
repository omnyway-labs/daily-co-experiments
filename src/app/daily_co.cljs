(ns app.daily-co
  (:require
   ;;   ["@daily-co/daily-js/dist/daily-iframe-esm.js" :default DailyIframe])
   ["@daily-co/daily-js" :default DailyIframe])
  )

(js/console.log "DailyIframe: " DailyIframe)
;; (js/console.log "createFrame: " createFrame)
(defonce call-frame (atom nil))

(defn create-call-frame [call-wrapper {:keys [show-event
                                              loaded
                                              started-camera
                                              camera-error
                                              joining-meeting
                                              joined-meeting
                                              left-meeting]}]

  (js/console.log "Top create-call-frame call-wrapper: " call-wrapper)
  (let [cframe (.createFrame DailyIframe  call-wrapper
                             #js {:showLeaveButton true
                                  :showFullscreenButton true
                                  })]
    (reset! call-frame cframe)
    (->
     cframe
     (.on "loaded" loaded)
     (.on "started-camera" started-camera)
     (.on "camera-error" camera-error)
     (.on "joining-meeting" joining-meeting)
     (.on "joined-meeting" joined-meeting)
     (.on "left-meeting" left-meeting)
     (.on "loading", show-event)
     (.on "participant-joined", show-event)
     (.on "participant-updated", show-event)
     (.on "participant-left", show-event)
     (.on "recording-started", show-event)
     (.on "recording-stopped", show-event)
     (.on "recording-stats", show-event)
     (.on "recording-error", show-event)
     (.on "recording-upload-completed", show-event)
     (.on "app-message", show-event)
     (.on "input-event", show-event)
     (.on "error", show-event)
     (.on "live-streaming-error", show-event)
     (.on "live-streaming-started", show-event)
     (.on "live-streaming-stopped", show-event);
     )))

(defn join-room
  "Join the room using the supplied room url
  `room-url` - daily.co room url"
  [{:keys [room-url meeting-token]}]
  (js/console.log "JOIN-ROOM room-url: " room-url " meeting-token: " meeting-token)
  (let [base-props {:url room-url
                    :showLeaveButton true}
        props (if meeting-token
                (merge base-props {:token meeting-token})
                base-props)]
    (js/console.log "!!!!!"::join-room (clj->js props))
    (try
      (.join ^js/DailyIframe.callFrame @call-frame (clj->js props))
      (catch
          js/Object e (.error js/console e)))))

(defn leave
  "Leaves the meeting. If there is no meeting, this method does nothing."
  []
  (try
    (when @call-frame
      (.leave ^js/DailyIframe.callFrame @call-frame)
      (.destroy ^js/DailyIframe.callFrame @call-frame))
    (reset! call-frame nil)
    (catch
        js/Object e (.error js/console "LEAVE CATCH e: " e))))

(defn start-live-streaming [rtmp-full-url]
  (js/console.log ::start-live-streaming "rtmp-full-url: " rtmp-full-url)
  (.startLiveStreaming ^js/DailyIframe.callFrame @call-frame #js {:rtmpUrl rtmp-full-url}))

(defn stop-live-streaming []
  (js/console.log ::stop-live-streaming)
  (.stopLiveStreaming ^js/DailyIframe.callFrame @call-frame))
;;
;; Event listener callbacks and helpers
;;


