/*
 * AcousticRuler
 * Copyright 2023 Tim Teulings
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.framstag.acousticrules;

import picocli.CommandLine;

public class VersionProvider implements CommandLine.IVersionProvider {
  @Override
  public String[] getVersion() throws Exception {
    var title = getClass().getPackage().getImplementationTitle();
    var version = getClass().getPackage().getImplementationVersion();

    if (title == null) {
      title = "AcousticRules";
    }

    if (version == null) {
      version = "x.x (cannot extract version from MANIFEST!)";
    }

    var versionArray = new String[1];

    versionArray[0] = String.format("%s %s", title, version);

    return versionArray;
  }
}
