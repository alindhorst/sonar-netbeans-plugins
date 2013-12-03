/*
 * Copyright (C) 2013 alindhorst.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package de.alexanderlindhorst.sonar.pmd.plugin.options.gui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;

import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 *
 * @author alindhorst
 */
public class SonarPMDPluginConfigPane extends javax.swing.JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(SonarPMDPluginConfigPane.class);
    private final Set<ChangeListener> changeListeners = Sets.newHashSet();

    /**
     * Creates new form SonarPMDPluginConfigPane
     */
    public SonarPMDPluginConfigPane() {
        initComponents();
    }

    /* Event handling */
    public void addChangeListener(ChangeListener changeListener) {
        changeListeners.add(changeListener);
        changeListener.stateChanged(new ChangeEvent(this));
    }

    public void removeChangeListener(ChangeListener changeListener) {
        changeListeners.remove(changeListener);
    }

    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener changeListener : changeListeners) {
            changeListener.stateChanged(event);
        }
    }

    public void setConfigUrl(URL url) {
        urlField.setText(url == null ? null : url.toExternalForm());
    }

    public URL getConfigUrl() throws MalformedURLException {
        String givenUrl = urlField.getText();
        if (givenUrl == null || givenUrl.isEmpty()) {
            return null;
        }
        return new URL(urlField.getText());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
     * method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        urlField = new javax.swing.JTextField();
        javax.swing.JButton chooseButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SonarPMDPluginConfigPane.class, "SonarPMDPluginConfigPane.jLabel1.text")); // NOI18N
        add(jLabel1, new java.awt.GridBagConstraints());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.weighty = 2.0;
        add(urlField, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(chooseButton, org.openide.util.NbBundle.getMessage(SonarPMDPluginConfigPane.class, "SonarPMDPluginConfigPane.chooseButton.text")); // NOI18N
        chooseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        add(chooseButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void chooseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseButtonActionPerformed
        LOGGER.debug("Choosing config from file system");
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        int selectedOption = fileChooser.showOpenDialog(null);
        if (selectedOption == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                LOGGER.debug("selected config file is {}", selectedFile);
                setConfigUrl(Utilities.toURI(selectedFile).toURL());
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_chooseButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField urlField;
    // End of variables declaration//GEN-END:variables
}
