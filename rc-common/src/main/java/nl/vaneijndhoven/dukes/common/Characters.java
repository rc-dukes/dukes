package nl.vaneijndhoven.dukes.common;

/**
 * nicknames used for DukesVerticles
 */
public enum Characters {

  BO("car","Processing Car control inputs","Lost sheep Bo","main character, driver, act first think later","https://en.wikipedia.org/wiki/Bo_Duke"), 
  LUKE("action","Proc Image analysis results into actions","Lost sheep Luke","main character, sidekick, planner","https://en.wikipedia.org/wiki/Luke_Duke"), 
  DAISY("detect","Detectors - e.g. Lane and StartLight", "Bo Peep","sister of main characters, pretty, gathers intel","https://en.wikipedia.org/wiki/Daisy_Duke"), 
  UNCLE_JESSE("app","JavaFX app to monitor the car","Shepherd"," guardian of main characters, wise man","https://en.wikipedia.org/wiki/The_Dukes_of_Hazzard#Jesse"), 
  ROSCOE("imageview","lane detection debug image web server","Red Dog","comical sheriff, chasing, illegal speed traps, “hot pursuit“","https://dukesofhazzard.fandom.com/wiki/Rosco_Purvis_Coltrane_(James_Best)"), 
  FLASH("watchdog","Heartbeat controller watchdog","Velvet ears","Roscoes dog","https://dukesofhazzard.fandom.com/wiki/Flash"), 
  BOSS_HOGG("webcontrol","Application to provide manual inputs","Little fat buddy","villain, rich, fat, food lover “get them duke boys“","https://dukesofhazzard.fandom.com/wiki/Boss_Hogg_(Sorrell_Booke)"), 
  COOTER("camera-matrix","CameraMatrix and PerspectiveShift","Crazy Cooter","Mechanic, supports main characters, ”Breaker one, Breaker one, I might be crazy but I ain't dumb, Craaaazy Cooter comin' atcha, come on.”","https://dukesofhazzard.fandom.com/wiki/Cooter_Davenport_(Ben_Jones)"), 
  ENOS("geometry","Dipstick","Geometry","roscoes sidekick, loves daisy, helps the dukes","https://dukesofhazzard.fandom.com/wiki/Enos_Strate_(Sonny_Shroyer)"), 
  CLETUS("roi","Region of Interest","Cletus","roscoes sidekick, loves daisy","https://dukesofhazzard.fandom.com/wiki/Cletus_Hogg");

  private String module;
  private String purpose;
  private String callsign;
  private String comment;
  private String url;

  Characters(String module,String purpose,String callsign,String comment, String url) {
    this.module=module;
    this.purpose=purpose;
    this.callsign = callsign;
    this.comment=comment;
    this.url=url;
  }

  public String getCallsign() {
    return callsign;
  }
}
