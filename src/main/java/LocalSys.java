/*
 * Copyright 2024 JZBlock Project
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

package main.java;

/**
 * Local system utilities for JZBlock v0.5
 * 
 * Provides cross-platform system information and utilities including
 * operating system detection and directory path management.
 * Supports Windows, Linux, and other Java-compatible platforms.
 */
public class LocalSys 
{
    public static LocalSys o = new LocalSys();

    public String curr_dir()
    {
        return System.getProperty("user.dir");
    }

    public String os_tag()
    {
        String name = System.getProperty("os.name").toLowerCase();
        if (name.contains("win"))
            return ".windows";
        else if (name.contains("nux"))
            return ".linux";
        
            return "";
    }
}
