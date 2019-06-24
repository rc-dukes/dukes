package nl.vaneijndhoven.dukes.common;

/**
 * nicknames used for verticles
 *   Bo “Lost sheep"-> main character, driver, act first think later
 *   Luke “Lost sheep” -> main character, sidekick, planner
 *   Daisy “Bo Peep” -> sister of main characters, pretty, gathers intel
 *   Uncle Jesse “Shepherd"-> guardian of main characters, wise man
 *   Roscoe P Coltrane “Red Dog” -> comical sheriff, chasing, illegal speed traps, “hot pursuit"
 *   Flash “Velvet ears” -> Roscoes dog
 *   Boss “J.D.” Hogg “little fat buddy” -> villain, rich, fat, food lover “get them duke boys"
 *   Cooter Davenport “Crazy Cooter” -> Mechanic, supports main characters, "Breaker one, Breaker one, I might be crazy but I ain't dumb, Craaaazy Cooter comin' atcha, come on.”
 *   Enos Strate “dipstick" -> roscoes sidekick, loves daisy, helps the dukes
 *   Cletus Hogg -> roscoes sidekick, loves daisy,
 */
public enum Characters {

  BO("Lost sheep Bo"), LUKE("Lost sheep Luke"), DAISY("Bo Peep"), UNCLE_JESSE(
      "Shepherd"), ROSCOE("Red Dog"), FLASH("Velvet ears"), BOSS_HOGG(
          "Little fat buddy"), COOTER(
              "Crazy Cooter"), ENOS("Dipstick"), CLETUS("Cletus");

  private String callsign;

  Characters(String callsign) {
    this.callsign = callsign;
  }

  public String getCallsign() {
    return callsign;
  }
}
