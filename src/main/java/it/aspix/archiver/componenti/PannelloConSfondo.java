/****************************************************************************
 * Copyright 2011 studio Aspix 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 ***************************************************************************/
package it.aspix.archiver.componenti;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/****************************************************************************
 * @author Edoardo Panfili, studio Aspix
 ***************************************************************************/
public class PannelloConSfondo extends JPanel{
    
    private static final long serialVersionUID = 1L;
    
    ImageIcon ii;
    
    public PannelloConSfondo(ImageIcon ii){
        this.ii = ii;
    }
    
    public void update(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(ii.getImage(),0,0,this);
        //super.update(g);
    }
    
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(ii.getImage(),0,0,this);
        super.paint(g);
    }
    
}
