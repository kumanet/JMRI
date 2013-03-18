// StatusPanel.java

package jmri.jmrit.beantable.beanedit;

import jmri.InstanceManager;
import jmri.NamedBean;
import jmri.Block;
import jmri.util.swing.JmriBeanComboBox;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Provides an edit panel for a block object
 *
 * @author	Kevin Dickerson Copyright (C) 2011
 * @version	$Revision: 19923 $
 */
public class BlockEditAction extends BeanEditAction {
    
    public static final ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrit.beantable.BeanTableBundle");
    
    private String noneText = Bundle.getMessage("BlockNone");
	private String gradualText = Bundle.getMessage("BlockGradual");
	private String tightText = Bundle.getMessage("BlockTight");
	private String severeText = Bundle.getMessage("BlockSevere");
	public String[] curveOptions = {noneText, gradualText, tightText, severeText};
    static final java.util.Vector<String> speedList = new java.util.Vector<String>();
    
    protected void createPanels(){
        super.createPanels();
        bei.add(sensor());
        bei.add(reporterDetails());
        bei.add(properties());
    }
    
    public String getBeanType() { return Bundle.getMessage("BeanNameBlock"); }
    public NamedBean getBySystemName(String name) { return InstanceManager.blockManagerInstance().getBySystemName(name);}
    public NamedBean getByUserName(String name) { return InstanceManager.blockManagerInstance().getByUserName(name);}
    
    JTextField userNameField = new JTextField(20);
    JmriBeanComboBox reporterField;
    JCheckBox useCurrent = new JCheckBox();
    JTextArea commentField = new JTextArea(3,30);
    JScrollPane commentFieldScroller = new JScrollPane(commentField);
    
    EditBeanItem reporterDetails(){
        EditBeanItem reporter = new EditBeanItem();
        reporter.setName(Bundle.getMessage("BeanNameReporter"));
        ArrayList<Item> items = new ArrayList<Item>();
        
        reporterField = new JmriBeanComboBox(InstanceManager.reporterManagerInstance(), ((Block)bean).getReporter(), JmriBeanComboBox.DISPLAYNAME);
        reporterField.setFirstItemBlank(true);

        items.add(new Item(reporterField, Bundle.getMessage("BeanNameReporter"), Bundle.getMessage("BlockReporterText")));

        reporterField.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if(reporterField.getSelectedBean()!=null)
                    useCurrent.setEnabled(true);
                else
                    useCurrent.setEnabled(false);
            }
        });
        

        items.add(new Item(useCurrent, Bundle.getMessage("BlockReporterCurrent"), Bundle.getMessage("BlockUseCurrentText")));

        if(reporterField.getSelectedBean()==null){
            useCurrent.setEnabled(false);
        }
        
        reporter.setResetItem(new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                reporterField.setSelectedBean(((Block)bean).getReporter());
                useCurrent.setSelected(((Block)bean).isReportingCurrent());
            }
        });
        addToPanel(reporter, items);
        return reporter;
    }
    
    JTextField lengthField = new JTextField(20);
    JComboBox curvatureField = new JComboBox(curveOptions);
    JCheckBox permissiveField = new JCheckBox();
    JComboBox speedField;
    

    String defaultBlockSpeedText;
    
    EditBeanItem properties(){
    
        ArrayList<Item> items = new ArrayList<Item>();
        
        defaultBlockSpeedText = (Bundle.getMessage("UseGlobal") + " " + jmri.InstanceManager.blockManagerInstance().getDefaultSpeed());
        speedList.add(defaultBlockSpeedText);
        java.util.Vector<String> _speedMap = jmri.implementation.SignalSpeedMap.getMap().getValidSpeedNames();
        for(int i = 0; i<_speedMap.size(); i++){
            if (!speedList.contains(_speedMap.get(i))){
                speedList.add(_speedMap.get(i));
            }
        }
        EditBeanItem basic = new EditBeanItem();
        basic.setName(Bundle.getMessage("Properties"));

        items.add(new Item(null, null, Bundle.getMessage("BlockPropertiesText")));
        items.add(new Item(lengthField, Bundle.getMessage("BlockLengthColName"), Bundle.getMessage("BlockLengthText")));
        
        lengthField.addKeyListener( new KeyListener() {
            public void keyPressed(KeyEvent keyEvent) {
            }
            public void keyReleased(KeyEvent keyEvent) {
                String text = lengthField.getText();
                if (!validateNumericalInput(text)){
                    String msg = java.text.MessageFormat.format(Bundle.getMessage("ShouldBeNumber"), new Object[] { rb.getString("BlockLengthColName") });
                    jmri.InstanceManager.getDefault(jmri.UserPreferencesManager.class).showInfoMessage(rb.getString("ErrorTitle"), msg, "Block Details", "length", false, false);
                }
            }
            public void keyTyped(KeyEvent keyEvent) {
            }
        });
        
        JRadioButton inch = new JRadioButton(Bundle.getMessage("LengthInches"));
        JRadioButton cm = new JRadioButton(Bundle.getMessage("LengthCentimeters"));
        ButtonGroup rg = new ButtonGroup();
        rg.add(inch);
        rg.add(cm);
        
        JPanel p = new JPanel();
        p.add(inch);
        p.add(cm);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        items.add(new Item(p, Bundle.getMessage("BlockLengthUnits"), Bundle.getMessage("BlockLengthUnitsText")));
        items.add(new Item(curvatureField, Bundle.getMessage("BlockCurveColName"), ""));
        items.add(new Item(speedField = new JComboBox(speedList), Bundle.getMessage("BlockSpeedColName"), Bundle.getMessage("BlockMaxSpeedText")));
        items.add(new Item(permissiveField, Bundle.getMessage("BlockPermColName"), Bundle.getMessage("BlockPermissiveText")));

        permissiveField.setSelected(((Block)bean).getPermissiveWorking());

        basic.setSaveItem(new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                Block blk = (Block) bean;
                String cName = (String)curvatureField.getSelectedItem();
                if (cName.equals(noneText)) blk.setCurvature(Block.NONE);
                else if (cName.equals(gradualText)) blk.setCurvature(Block.GRADUAL);
                else if (cName.equals(tightText)) blk.setCurvature(Block.TIGHT);
                else if (cName.equals(severeText)) blk.setCurvature(Block.SEVERE);
                
                String speed = (String)speedField.getSelectedItem();
                try {
                    blk.setBlockSpeed(speed);
                } catch (jmri.JmriException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage() + "\n" + speed);
                    return;
                }
                if (!speedList.contains(speed) && !speed.contains(Bundle.getMessage("UseGlobal"))){
                    speedList.add(speed);
                }
            }
        });
        basic.setResetItem(new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                Block blk = (Block) bean;
                lengthField.setText(""+((Block)bean).getLengthMm());

                if (blk.getCurvature()==Block.NONE) curvatureField.setSelectedItem(0);
                else if (blk.getCurvature()==Block.GRADUAL) curvatureField.setSelectedItem(gradualText);
                else if (blk.getCurvature()==Block.TIGHT) curvatureField.setSelectedItem(tightText);
                else if (blk.getCurvature()==Block.SEVERE) curvatureField.setSelectedItem(severeText);
                
                String speed = blk.getBlockSpeed();
                if(!speedList.contains(speed)){
                    speedList.add(speed);
                }

                speedField.setEditable(true);
                speedField.setSelectedItem(speed);

                permissiveField.setSelected(((Block)bean).getPermissiveWorking());
            }
        });
        addToPanel(basic, items);
        return basic;
    }
    
    JmriBeanComboBox sensorField;
    JTextField sensorDebounceInactiveField = new JTextField(5);
    JTextField sensorDebounceActiveField = new JTextField(5);
    JCheckBox sensorDebounceGlobalCheck = new JCheckBox();//"Use global Debounce"/*rb.getString("OccupancySensorUseGlobal")*/);
    
    EditBeanItem sensor() {
    
        ArrayList<Item> items = new ArrayList<Item>();
        EditBeanItem basic = new EditBeanItem();
        basic.setName(Bundle.getMessage("BeanNameSensor"));

        sensorField = new JmriBeanComboBox(InstanceManager.sensorManagerInstance(), ((Block)bean).getSensor(), JmriBeanComboBox.DISPLAYNAME);
        sensorField.setFirstItemBlank(true);
        items.add(new Item(sensorField, Bundle.getMessage("BeanNameSensor"), Bundle.getMessage("BlockAssignSensorText")));
        
        sensorField.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if(sensorField.getSelectedBean()==null){
                    sensorDebounceGlobalCheck.setEnabled(false);
                    sensorDebounceInactiveField.setEnabled(false);
                    sensorDebounceActiveField.setEnabled(false);
                } else {
                    sensorDebounceGlobalCheck.setEnabled(true);
                    sensorDebounceInactiveField.setEnabled(true);
                    sensorDebounceActiveField.setEnabled(true);
                }
            }
        });

        items.add(new Item(null, null, Bundle.getMessage("SensorDebounceText")));
        items.add(new Item(sensorDebounceGlobalCheck,Bundle.getMessage("SensorDebounceUseGlobalText"), null));
        items.add(new Item(sensorDebounceInactiveField,Bundle.getMessage("SensorInActiveDebounce"), Bundle.getMessage("SensorInActiveDebounceText")));
        items.add(new Item(sensorDebounceActiveField,Bundle.getMessage("SensorActiveDebounce"), Bundle.getMessage("SensorActiveDebounceText")));
        
        basic.setSaveItem(new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                Block blk = (Block) bean;
                jmri.jmrit.display.layoutEditor.LayoutBlock lBlk = InstanceManager.getDefault(jmri.jmrit.display.layoutEditor.LayoutBlockManager.class).getLayoutBlock(blk);
                //If the block is related to a layoutblock then set the sensor details there and allow that to propergate the changes down.
                if(lBlk!=null)
                    lBlk.validateSensor(sensorField.getSelectedDisplayName(), null);
                else
                    blk.setSensor(sensorField.getSelectedDisplayName());
            }
        });
        basic.setResetItem(new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                Block blk = (Block)bean;
                //From basic details
                sensorField.setSelectedBean(blk.getSensor());
                if(sensorField.getSelectedBean()==null){
                    sensorDebounceGlobalCheck.setEnabled(false);
                    sensorDebounceInactiveField.setEnabled(false);
                    sensorDebounceActiveField.setEnabled(false);
                }
            }
        });
        addToPanel(basic, items);
        return basic;
    }

}