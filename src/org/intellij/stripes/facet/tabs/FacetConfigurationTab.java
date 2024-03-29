/*
 * Copyright 2000-2007 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.intellij.stripes.facet.tabs;

import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.intellij.stripes.facet.StripesFacetConfiguration;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 11/11/2007 Time: 12:07:39 PM
 */
public class FacetConfigurationTab extends FacetEditorTab {
// ------------------------------ FIELDS ------------------------------

    private StripesFacetConfiguration configuration;
    private JPanel mainPanel;
    private JCheckBox changeIconsCheckBox;

// --------------------------- CONSTRUCTORS ---------------------------

    public FacetConfigurationTab(StripesFacetConfiguration configuration) {
        this.configuration = configuration;
        fillData();
    }

    private void fillData() {
        changeIconsCheckBox.setSelected(configuration.isChangeIcons());
    }

    // ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface Configurable ---------------------

    @Nls
    public String getDisplayName() {
        return "Plugin Configuration";
    }

// --------------------- Interface UnnamedConfigurable ---------------------


    public JComponent createComponent() {
        return mainPanel;
    }

    public boolean isModified() {
        return true;
    }

    public void apply() throws ConfigurationException {
        configuration.setChangeIcons(changeIconsCheckBox.isSelected());
    }

    public void reset() {

    }

    public void disposeUIResources() {

    }

// -------------------------- OTHER METHODS --------------------------

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        changeIconsCheckBox = new JCheckBox();
        changeIconsCheckBox.setSelected(true);
        changeIconsCheckBox.setText("Change Icons");
        changeIconsCheckBox.setToolTipText("Change Icons, Could Improve Performance in Very Large Proyects");
        mainPanel.add(changeIconsCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
