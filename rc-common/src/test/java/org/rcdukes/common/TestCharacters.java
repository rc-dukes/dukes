package org.rcdukes.common;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.lang3.text.WordUtils;
import org.junit.Test;
import org.rcdukes.common.Characters;

/**
 * test Characters
 * @author wf
 *
 */
public class TestCharacters {

  public String spc(int numberOfSpaces) {
    return String.format(String.format("%%%ds", numberOfSpaces), "");
  }
  
  public String capitalize(String name) {
    // return name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
    return WordUtils.capitalizeFully(name.replace("_"," ")).replace(" ","");
  }
  
  public void output(String format,Object ...args) {
    System.out.println(String.format(format, args));
  }
  @Test
  public void testWikiMarkup() {
    Characters[] chars = Characters.values();
    Arrays.sort(chars, Comparator.comparing(Characters::getModule));

    for (Characters c : chars) {
      String name=c.module.replace("-","");
      String uml="https://rc-dukes.github.io/dukes/dukes/apidocs/nl/vaneijndhoven/dukes/%s/package-summary.html";
      output("  %-14s[",name);
      output("  %s  label=\" \"",spc(14));
      output("  %s  image=\"%s\"",spc(14),capitalize(c.name())+".png");
      output("  %s  fontcolor=white",spc(14));
      output("  %s  URL=\"%s\"",spc(14),c.getUrl());
      output("  %s]",spc(14));
      output("  %-14s[",name+"label");
      output("  %s  shape=\"note\"",spc(14));
      output("  %s  label=\"%s\\n%s\"",spc(14),c.module,c.name());
      output("  %s  URL=\""+uml+"\"",spc(14),c.module);
      output("  %s]",spc(14));
      output("  dukes--%slabel--%s",name,name);
    }
  }
}
