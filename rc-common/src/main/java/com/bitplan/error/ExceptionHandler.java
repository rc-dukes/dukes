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
package com.bitplan.error;

/**
 * Exception Handler
 * @author wf
 *
 */
public interface ExceptionHandler {
  
  /**
   * handle the given Throwable with the given  hint
   * @param th - the throwable
   * @param hint - the hint to give to the user how to potentially fix this
   */
  public void handle(Throwable th, String hint);
  
  /**
   * handle the given Throwable with no hint
   * @param th - the throwable to handle
   */
  public void handle(Throwable th);
  
  /**
   * warn with the given message
   * @param msg - the message 
   */
  public void warn(String msg);
}
