vertx.setPeriodic(1000, function() {
  vertx.eventBus().publish("events-feed", "server event - from JavaScript!");
});