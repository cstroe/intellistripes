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

import com.intellij.facet.Facet;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetEditorsFactory;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.facet.ui.libraries.FacetLibrariesValidator;
import com.intellij.facet.ui.libraries.FacetLibrariesValidatorDescription;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.intellij.stripes.facet.StripesFacet;
import org.intellij.stripes.facet.StripesFacetConfiguration;
import org.intellij.stripes.support.StripesSupportUtil;
import org.intellij.stripes.util.StripesConstants;
import org.intellij.stripes.util.StripesUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA. User: Mario Arias Date: 2/07/2007 Time: 11:22:05 PM
 */
public class StripesConfigurationTab extends FacetEditorTab {
// ------------------------------ FIELDS ------------------------------

    private FacetEditorContext editorContext;
    private StripesFacetConfiguration configuration;
    private FacetLibrariesValidator validator;


    private JPanel mainPanel;
    private JCheckBox addSpringIntegrationCheckBox;
    private JCheckBox addLoggingCheckBox;
    private JCheckBox addStripesResourcesCheckBox;
    private JComboBox log4jComboBox;
    private JPanel messagePanel;
    private JCheckBox configureActionResolverUrlFiltersCheckBox;
    private JTextField actionResolverUrlFiltersTextField;

// --------------------------- CONSTRUCTORS ---------------------------

    public StripesConfigurationTab(FacetEditorContext editorContext, final StripesFacetConfiguration configuration, FacetValidatorsManager validatorsManager) {
        validator = FacetEditorsFactory.getInstance().createLibrariesValidator(StripesConstants.STRIPES_LIBRARY_INFO,
                new FacetLibrariesValidatorDescription(StripesConstants.STRIPES),
                editorContext,
                validatorsManager);
        validatorsManager.registerValidator(validator);
        this.editorContext = editorContext;
        this.configuration = configuration;
        messagePanel.setLayout(new VerticalFlowLayout());
        messagePanel.add(StripesUtil.createLink("More about Stripes", "http://mc4j.org/confluence/display/stripes/"), 1);
        addLoggingCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                log4jComboBox.setEnabled(addLoggingCheckBox.isSelected());
            }
        });
        configureActionResolverUrlFiltersCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                actionResolverUrlFiltersTextField.setEnabled(configureActionResolverUrlFiltersCheckBox.isSelected());
            }
        });
        fillData();
    }

    private void fillData() {
        addSpringIntegrationCheckBox.setSelected(configuration.isSpringIntegration());
        addLoggingCheckBox.setSelected(configuration.isLogging());
        addStripesResourcesCheckBox.setSelected(configuration.isStripesResources());
        if (configuration.getLog4jFile() == null || configuration.getLog4jFile().equalsIgnoreCase("")) {
            log4jComboBox.setSelectedItem(StripesConstants.LOG4J_PROPERTIES);
        } else {
            log4jComboBox.setSelectedItem(configuration.getLog4jFile());
        }
        log4jComboBox.setEnabled(addLoggingCheckBox.isSelected());
        configureActionResolverUrlFiltersCheckBox.setSelected(configuration.isActionResolverUrlFilters());
        actionResolverUrlFiltersTextField.setEnabled(configuration.isActionResolverUrlFilters());
        if (configuration.getUrlFiltersValue() != null) {
            if (!(configuration.getUrlFiltersValue().length() == 0)) {
                actionResolverUrlFiltersTextField.setText(configuration.getUrlFiltersValue());
            } else {
                actionResolverUrlFiltersTextField.setText("WEB-INF/classes");
            }
        }
    }

    // ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface Configurable ---------------------

    @Nls
    public String getDisplayName() {
        return "Stripes Configuration";
    }

    @Override
    @Nullable
    public Icon getIcon() {
        return StripesConstants.STRIPES_ICON;
    }

// --------------------- Interface UnnamedConfigurable ---------------------


    public JComponent createComponent() {
        return mainPanel;
    }

    public boolean isModified() {
        return true;
    }

    public void apply() throws ConfigurationException {
        configuration.setSpringIntegration(addSpringIntegrationCheckBox.isSelected());
        configuration.setLogging(addLoggingCheckBox.isSelected());
        configuration.setStripesResources(addStripesResourcesCheckBox.isSelected());
        configuration.setLog4jFile(log4jComboBox.getSelectedItem().toString());
        configuration.setActionResolverUrlFilters(configureActionResolverUrlFiltersCheckBox.isSelected());
        configuration.setUrlFiltersValue(actionResolverUrlFiltersTextField.getText());
        final StripesFacet stripesFacet = (StripesFacet) editorContext.getFacet();
        assert stripesFacet != null;
        new WriteCommandAction.Simple(editorContext.getProject(), stripesFacet.getWebXmlPsiFile()) {
            @Override
            protected void run() throws Throwable {
                StripesSupportUtil.addSupport(stripesFacet);
            }
        }.execute();
    }

    public void reset() {

    }

    public void disposeUIResources() {

    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    public void onFacetInitialized(@NotNull Facet facet) {
        validator.onFacetInitialized(facet);
    }

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
        mainPanel.setLayout(new GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        addSpringIntegrationCheckBox = new JCheckBox();
        addSpringIntegrationCheckBox.setText("Add Spring Integration");
        addSpringIntegrationCheckBox.setToolTipText("Add necesary configuration in web.xml to enable Spring Integration");
        mainPanel.add(addSpringIntegrationCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addLoggingCheckBox = new JCheckBox();
        addLoggingCheckBox.setText("Add Logging");
        addLoggingCheckBox.setToolTipText("Add commons-logging.properties and log4j.properties or xml");
        mainPanel.add(addLoggingCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addStripesResourcesCheckBox = new JCheckBox();
        addStripesResourcesCheckBox.setText("Add Stripes Resources");
        addStripesResourcesCheckBox.setToolTipText("Add StripesResources.properties ");
        mainPanel.add(addStripesResourcesCheckBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        log4jComboBox = new JComboBox();
        log4jComboBox.setEnabled(false);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("log4j.properties");
        defaultComboBoxModel1.addElement("log4j.xml");
        log4jComboBox.setModel(defaultComboBoxModel1);
        log4jComboBox.setToolTipText("Choose between log4j.properties or log4j.xaml");
        mainPanel.add(log4jComboBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        mainPanel.add(spacer3, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        messagePanel = new JPanel();
        messagePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        mainPanel.add(messagePanel, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Stripes is a presentation framework for building web applications using the latest Java technologies.");
        messagePanel.add(label1);
        configureActionResolverUrlFiltersCheckBox = new JCheckBox();
        configureActionResolverUrlFiltersCheckBox.setEnabled(true);
        configureActionResolverUrlFiltersCheckBox.setText("Configure ActionResolver UrlFilters");
        configureActionResolverUrlFiltersCheckBox.setToolTipText("Add ActionResolver.UrlFilter to Stripes Filter");
        mainPanel.add(configureActionResolverUrlFiltersCheckBox, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        actionResolverUrlFiltersTextField = new JTextField();
        actionResolverUrlFiltersTextField.setEnabled(false);
        actionResolverUrlFiltersTextField.setText("WEB-INF/classes");
        mainPanel.add(actionResolverUrlFiltersTextField, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
