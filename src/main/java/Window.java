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
import javax.swing.*;

/**
 * Cross-platform window management system for JZBlock v0.5
 * 
 * Provides a singleton window manager that handles JFrame creation,
 * configuration, and display across different operating systems.
 * Features configurable dimensions, always-on-top behavior, and
 * centered positioning.
 */
public class Window 
    extends JPanel
{
    public static Window o = new Window();

    private JFrame frame;


    public Window()
    {
        init_Window(0,0,"");
    }
    public Window(int width, int height, String title)
    {
        init_Window(width, height, title);
    }
    public void init_Window(int width, int height, String title)
    {
        frame = new JFrame();
        this.setLayout(null);
        this.setPreferredSize(
            new Dimension(width, height)
        );
        frame.add(this);
        frame.pack();
        frame.setTitle(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);
    }
}
