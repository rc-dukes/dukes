/**
 *
 * This file is part of the https://github.com/BITPlan/com.bitplan.gui open source project
 *
 * Copyright 2017 BITPlan GmbH https://github.com/BITPlan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *  You may obtain a copy of the License at
 *
 *  http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rcdukes.error;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * simple ErrorHandler
 * @author wf
 *
 */
public class ErrorHandler implements ExceptionHandler {
  private static final Logger LOG=LoggerFactory.getLogger(ErrorHandler.class);

  @Override
  public void handle(Throwable th, String msg) {
    if (msg==null) {
      msg="";
    } else {
      msg="("+msg+")";
    }
    LOG.warn("Error " + th.getClass().getName()+msg+":"+ th.getMessage());
    StringWriter sw = new StringWriter();
    th.printStackTrace(new PrintWriter(sw));
    LOG.warn("Stacktrace: " + sw.toString());
  }

  @Override
  public void warn(String msg) {
    LOG.warn(msg);
  }

  @Override
  public void handle(Throwable th) {
    handle(th,null);
  }
  private static ErrorHandler instance;
  private ErrorHandler() {
    
  }
  
  /**
   * singleton access
   * @return the singleton
   */
  public static ErrorHandler getInstance() {
    if (instance==null)
      instance=new ErrorHandler();
    return instance;
  }
}
