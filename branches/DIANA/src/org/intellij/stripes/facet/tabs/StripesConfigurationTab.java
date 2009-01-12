/*
 * Copyright 2000-2009 JetBrains s.r.o.
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
import com.intellij.javaee.model.xml.ParamValue;
import com.intellij.javaee.model.xml.web.Filter;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.PackageChooser;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.peer.PeerFactory;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.ListUtil;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.Function;
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
import java.util.Arrays;

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
    private JList actionResolverPackagesList;
    private JButton addButton;
    private JButton removeButton;

// --------------------------- CONSTRUCTORS ---------------------------

    public StripesConfigurationTab(final FacetEditorContext editorContext, final StripesFacetConfiguration configuration, FacetValidatorsManager validatorsManager) {
        $$$setupUI$$$();
        validator = FacetEditorsFactory.getInstance().createLibrariesValidator(StripesConstants.STRIPES_LIBRARY_INFO,
                new FacetLibrariesValidatorDescription(StripesConstants.STRIPES),
                editorContext,
                validatorsManager);
        validatorsManager.registerValidator(validator);
        this.editorContext = editorContext;
        this.configuration = configuration;

        messagePanel.setLayout(new VerticalFlowLayout());
        messagePanel.add(StripesUtil.createLink("More about Stripes", "http://www.stripesframework.org"), 1);

        addLoggingCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                log4jComboBox.setEnabled(addLoggingCheckBox.isSelected());
            }
        });

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PackageChooser chooser = PeerFactory.getInstance().getUIHelper().createPackageChooser("Package(s) containing ActionBeans", editorContext.getProject());
                chooser.setCrossClosesWindow(false);
                chooser.show();

                DefaultListModel model = (DefaultListModel) actionResolverPackagesList.getModel();
                for (PsiPackage psiPackage : chooser.getSelectedPackages()) {
                    model.addElement(psiPackage.getQualifiedName());
                }
            }
        });
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ListUtil.removeSelectedItems(actionResolverPackagesList);
            }
        });

        fillData();

    }

    public void onTabEntering() {
        if (actionResolverPackagesList.getModel().getSize() == 0) {
            addButton.doClick();
        }
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

        Filter f = StripesSupportUtil.findStripesFilter(((StripesFacet) editorContext.getFacet()).getWebFacet().getRoot());
        if (null != f) {
            for (ParamValue paramValue : f.getInitParams()) {
                if (StripesConstants.ACTION_RESOLVER_PACKAGES.equals(paramValue.getParamName().getStringValue())) {
                    for (String pkg : StringUtil.split(paramValue.getParamValue().getStringValue(), ",")) {
                        ((DefaultListModel) actionResolverPackagesList.getModel()).addElement(pkg);
                    }
                    break;
                }
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
        configuration.setActionResolverPackages(
                StringUtil.join(Arrays.asList(((DefaultListModel) actionResolverPackagesList.getModel()).toArray()), new Function<Object, String>() {
                    public String fun(Object o) {
                        return o.toString();
                    }
                }, ","));

        final StripesFacet stripesFacet = (StripesFacet) editorContext.getFacet();
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
        log4jComboBox = new JComboBox();
        log4jComboBox.setEnabled(false);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("log4j.properties");
        defaultComboBoxModel1.addElement("log4j.xml");
        log4jComboBox.setModel(defaultComboBoxModel1);
        log4jComboBox.setToolTipText("Choose between log4j.properties or log4j.xaml");
        mainPanel.add(log4jComboBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(190, 23), null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        messagePanel = new JPanel();
        messagePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        mainPanel.add(messagePanel, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Stripes is a presentation framework for building web applications using the latest Java technologies.");
        messagePanel.add(label1);
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(5, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 2, new Insets(0, 2, 2, 2), -1, -1));
        mainPanel.add(panel1, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, new Dimension(346, 66), null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder("ActionResolver.Packages"));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        actionResolverPackagesList = new JList();
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        actionResolverPackagesList.setModel(defaultListModel1);
        scrollPane1.setViewportView(actionResolverPackagesList);
        addButton = new JButton();
        addButton.setText("Add");
        panel1.add(addButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, new Dimension(66, 25), null, 0, false));
        removeButton = new JButton();
        removeButton.setText("Remove");
        panel1.add(removeButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setFont(new Font(label2.getFont().getName(), Font.ITALIC, 9));
        label2.setText("/Spring web context must be configured/");
        mainPanel.add(label2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
