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

import java.awt.*;

/**
 * Basic 2D body/entity class for JZBlock v0.5
 * 
 * Represents a fundamental game entity with position, velocity, dimensions,
 * and rendering properties. Serves as the foundation for all game objects
 * in the JZBlock framework.
 */
public class K2DBody 
{
    public int    x, y, width, height;
    public double vx, vy;
    public Color  defaultBodyColor;
    public String pathToTexture;    
}
