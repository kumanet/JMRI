package jmri.jmrit.operations.rollingstock.cars;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import jmri.InstanceManager;
import jmri.jmrit.operations.OperationsXml;
import jmri.jmrit.operations.rollingstock.RollingStockAttribute;
import jmri.jmrit.operations.rollingstock.RollingStockEditFrame;
import jmri.jmrit.operations.rollingstock.cars.tools.CarAttributeEditFrame;
import jmri.jmrit.operations.rollingstock.cars.tools.CarLoadEditFrame;
import jmri.jmrit.operations.setup.Setup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Frame for user edit of car
 *
 * @author Dan Boudreau Copyright (C) 2008, 2010, 2011, 2014, 2018
 */
public class CarEditFrame extends RollingStockEditFrame implements java.beans.PropertyChangeListener {

    protected static final ResourceBundle rb = ResourceBundle
            .getBundle("jmri.jmrit.operations.rollingstock.cars.JmritOperationsCarsBundle");

    CarManager carManager = InstanceManager.getDefault(CarManager.class);
    CarManagerXml managerXml = InstanceManager.getDefault(CarManagerXml.class);

    JCheckBox passengerCheckBox = new JCheckBox(Bundle.getMessage("Passenger"));
    JCheckBox cabooseCheckBox = new JCheckBox(Bundle.getMessage("Caboose"));
    JCheckBox fredCheckBox = new JCheckBox(Bundle.getMessage("Fred"));
    JCheckBox utilityCheckBox = new JCheckBox(Bundle.getMessage("Utility"));
    JCheckBox hazardousCheckBox = new JCheckBox(Bundle.getMessage("Hazardous"));

    CarLoadEditFrame carLoadEditFrame = null;

    public CarEditFrame() {
        super(Bundle.getMessage("TitleCarAdd"));
    }

    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "Checks for null")
    @Override
    public void initComponents() {

        groupComboBox = InstanceManager.getDefault(CarManager.class).getKernelComboBox();

        super.initComponents();

        addButton.setText(Bundle.getMessage("TitleCarAdd"));

        // type options for cars
        addItem(pTypeOptions, passengerCheckBox, 0, 1);
        addItem(pTypeOptions, cabooseCheckBox, 1, 1);
        addItem(pTypeOptions, fredCheckBox, 2, 1);
        addItem(pTypeOptions, utilityCheckBox, 3, 1);
        addItem(pTypeOptions, hazardousCheckBox, 4, 1);

        // default check box selections
        autoWeightCheckBox.setSelected(true);
        passengerCheckBox.setSelected(false);
        cabooseCheckBox.setSelected(false);
        fredCheckBox.setSelected(false);
        hazardousCheckBox.setSelected(false);

        // load tool tips
        weightTextField.setToolTipText(Bundle.getMessage("TipCarWeightOz"));
        weightTonsTextField.setToolTipText(Bundle.getMessage("TipCarWeightTons"));
        autoWeightCheckBox.setToolTipText(Bundle.getMessage("TipCarAutoCalculate"));
        autoTrackCheckBox.setToolTipText(Bundle.getMessage("rsTipAutoTrack"));
        passengerCheckBox.setToolTipText(Bundle.getMessage("TipCarPassenger"));
        cabooseCheckBox.setToolTipText(Bundle.getMessage("TipCarCaboose"));
        fredCheckBox.setToolTipText(Bundle.getMessage("TipCarFred"));
        utilityCheckBox.setToolTipText(Bundle.getMessage("TipCarUtility"));
        hazardousCheckBox.setToolTipText(Bundle.getMessage("TipCarHazardous"));
        blockingTextField.setToolTipText(Bundle.getMessage("TipPassengerCarBlocking"));
        fillWeightButton.setToolTipText(Bundle.getMessage("TipCalculateCarWeight"));
        builtTextField.setToolTipText(Bundle.getMessage("TipBuildDate"));
        valueTextField.setToolTipText(Bundle.getMessage("TipValue"));

        deleteButton.setToolTipText(Bundle.getMessage("TipDeleteButton"));
        addButton.setToolTipText(Bundle.getMessage("TipAddButton"));
        saveButton.setToolTipText(Bundle.getMessage("TipSaveButton"));

        pGroup.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("Kernel")));

        // setup check boxes
        addCheckBoxAction(cabooseCheckBox);
        addCheckBoxAction(fredCheckBox);
        addCheckBoxAction(passengerCheckBox);
        addCheckBoxAction(autoTrackCheckBox);
        autoTrackCheckBox.setEnabled(false);
        //
        addHelpMenu("package.jmri.jmrit.operations.Operations_CarsEdit", true); // NOI18N
        carManager.addPropertyChangeListener(this);
    }

    @Override
    protected ResourceBundle getRb() {
        return rb;
    }

    @Override
    protected RollingStockAttribute getTypeManager() {
        return InstanceManager.getDefault(CarTypes.class);
    }

    @Override
    protected RollingStockAttribute getLengthManager() {
        return InstanceManager.getDefault(CarLengths.class);
    }

    public void load(Car car) {
        super.load(car);

        passengerCheckBox.setSelected(car.isPassenger());
        cabooseCheckBox.setSelected(car.isCaboose());
        utilityCheckBox.setSelected(car.isUtility());
        fredCheckBox.setSelected(car.hasFred());
        hazardousCheckBox.setSelected(car.isHazardous());

        pBlocking.setVisible(car.isPassenger() || car.getKernel() != null);

        if (!InstanceManager.getDefault(CarLoads.class).containsName(car.getTypeName(), car.getLoadName())) {
            if (JOptionPane.showConfirmDialog(this, MessageFormat.format(Bundle.getMessage("loadNameNotExist"),
                    new Object[]{car.getLoadName()}), Bundle.getMessage("addLoad"),
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                InstanceManager.getDefault(CarLoads.class).addName(car.getTypeName(), car.getLoadName());
            }
        }
        // listen for changes in car load
        car.addPropertyChangeListener(this);
        InstanceManager.getDefault(CarLoads.class).updateComboBox(car.getTypeName(), loadComboBox);
        loadComboBox.setSelectedItem(car.getLoadName());

        // only cars have color attribute
        if (!InstanceManager.getDefault(CarColors.class).containsName(car.getColor())) {
            if (JOptionPane.showConfirmDialog(this, MessageFormat.format(Bundle.getMessage("colorNameNotExist"),
                    new Object[]{car.getColor()}), Bundle.getMessage("carAddColor"),
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                InstanceManager.getDefault(CarColors.class).addName(car.getColor());
            }
        }
        colorComboBox.setSelectedItem(car.getColor());

        groupComboBox.setSelectedItem(car.getKernelName());

        setTitle(Bundle.getMessage("TitleCarEdit"));
    }

    // combo boxes
    @Override
    public void comboBoxActionPerformed(java.awt.event.ActionEvent ae) {
        if (ae.getSource() == typeComboBox && typeComboBox.getSelectedItem() != null) {
            log.debug("Type comboBox sees change, update car loads");
            InstanceManager.getDefault(CarLoads.class).updateComboBox((String) typeComboBox.getSelectedItem(),
                    loadComboBox);
            // turn off auto for location tracks
            autoTrackCheckBox.setSelected(false);
            autoTrackCheckBox.setEnabled(false);
            updateTrackLocationBox();
        }
        if (ae.getSource() == lengthComboBox && autoWeightCheckBox.isSelected()) {
            calculateWeight();
        }
        super.comboBoxActionPerformed(ae);
    }

    @Override
    public void checkBoxActionPerformed(java.awt.event.ActionEvent ae) {
        //        JCheckBox b = (JCheckBox) ae.getSource();
        //        log.debug("checkbox change {}", b.getText());
        if (ae.getSource() == cabooseCheckBox && cabooseCheckBox.isSelected()) {
            fredCheckBox.setSelected(false);
        }
        if (ae.getSource() == fredCheckBox && fredCheckBox.isSelected()) {
            cabooseCheckBox.setSelected(false);
        }
        if (ae.getSource() == autoTrackCheckBox) {
            updateTrackLocationBox();
        }
        if (ae.getSource() == passengerCheckBox) {
            pBlocking.setVisible(passengerCheckBox.isSelected() || (_rs != null && ((Car) _rs).getKernel() != null));
        }
    }

    // Save, Delete, Add, Clear, Calculate, Edit Load buttons
    @Override
    public void buttonActionPerformed(java.awt.event.ActionEvent ae) {
        if (ae.getSource() == saveButton) {
            // log.debug("car save button pressed");
            if (!check((Car) _rs)) {
                return;
            }
            save(IS_SAVE);
            // save car file
            OperationsXml.save();
            if (Setup.isCloseWindowOnSaveEnabled()) {
                dispose();
            }
        }
        if (ae.getSource() == deleteButton) {
            log.debug("car delete button activated");
            // disable delete and save buttons
            deleteButton.setEnabled(false);
            saveButton.setEnabled(false);
            if (_rs != null) {
                _rs.removePropertyChangeListener(this);
            }
            Car car = carManager.getByRoadAndNumber((String) roadComboBox.getSelectedItem(), roadNumberTextField
                    .getText());
            if (car != null) {
                carManager.deregister(car);
            }
            _rs = null;
            // save car file
            OperationsXml.save();
        }
        if (ae.getSource() == addButton) {
            if (!check(null)) {
                return;
            }

            // enable delete and save buttons
            deleteButton.setEnabled(true);
            saveButton.setEnabled(true);

            save(!IS_SAVE);
            // save car file
            OperationsXml.save();
        }
        if (ae.getSource() == clearRoadNumberButton) {
            roadNumberTextField.setText("");
            roadNumberTextField.requestFocus();
        }

        if (ae.getSource() == fillWeightButton) {
            calculateWeight();
        }
        if (ae.getSource() == editLoadButton) {
            if (carLoadEditFrame != null) {
                carLoadEditFrame.dispose();
            }
            carLoadEditFrame = new CarLoadEditFrame();
            carLoadEditFrame.setLocationRelativeTo(this);
            carLoadEditFrame.initComponents((String) typeComboBox.getSelectedItem(),
                    (String) loadComboBox.getSelectedItem());
        }
    }

    protected boolean check(Car car) {
        // check to see if car with road and number already exists
        Car existingCar = carManager.getByRoadAndNumber((String) roadComboBox.getSelectedItem(), roadNumberTextField
                .getText());
        if (existingCar != null) {
            // new car?
            if (car == null) {
                JOptionPane.showMessageDialog(this, Bundle.getMessage("carRoadExists"), Bundle
                        .getMessage("carCanNotAdd"), JOptionPane.ERROR_MESSAGE);
                return false;
            }
            // old car with new road or number?
            if (!existingCar.getId().equals(car.getId())) {
                JOptionPane.showMessageDialog(this, Bundle.getMessage("carRoadExists"), Bundle
                        .getMessage("carCanNotUpdate"), JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        // check car's weight has proper format
        try {
            Number number = NumberFormat.getNumberInstance().parse(weightTextField.getText());
            log.debug("Car weight in oz: {}", number);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, Bundle.getMessage("carWeightFormat"), Bundle
                    .getMessage("carActualWeight"), JOptionPane.ERROR_MESSAGE);
            return false;
        }
 
        return super.check(car);
    }

    private void calculateWeight() {
        if (lengthComboBox.getSelectedItem() != null) {
            String length = (String) lengthComboBox.getSelectedItem();
            try {
                double carLength = Double.parseDouble(length) * 12 / Setup.getScaleRatio();
                double carWeight = (Setup.getInitalWeight() + carLength * Setup.getAddWeight()) / 1000;
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(1);
                weightTextField.setText((nf.format(carWeight))); // car weight in ounces.
                int tons = (int) (carWeight * Setup.getScaleTonRatio());
                // adjust weight for caboose
                if (cabooseCheckBox.isSelected() || passengerCheckBox.isSelected()) {
                    tons = (int) (Double.parseDouble(length) * .9); // .9 tons/foot
                }
                weightTonsTextField.setText(Integer.toString(tons));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, Bundle.getMessage("carLengthMustBe"), Bundle
                        .getMessage("carWeigthCanNot"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    protected void save(boolean isSave) {
        if (roadComboBox.getSelectedItem() == null) {
            return;
        }

        super.save(carManager, isSave);

        if (colorComboBox.getSelectedItem() != null) {
            _rs.setColor((String) colorComboBox.getSelectedItem());
        }

        // ask if all cars of this type should be passenger
        Car car = (Car) _rs;
        if (isSave && car.isPassenger() ^ passengerCheckBox.isSelected()) {
            if (JOptionPane.showConfirmDialog(this, MessageFormat.format(passengerCheckBox.isSelected() ? Bundle
                    .getMessage("carModifyTypePassenger") : Bundle.getMessage("carRemoveTypePassenger"),
                    new Object[]{car.getTypeName()}),
                    MessageFormat.format(Bundle.getMessage("carModifyAllType"),
                            new Object[]{car.getTypeName()}),
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                // go through the entire list and change the passenger setting
                // for all cars of this type
                for (Car c : carManager.getList()) {
                    if (c.getTypeName().equals(car.getTypeName())) {
                        c.setPassenger(passengerCheckBox.isSelected());
                    }
                }
            }
        }
        car.setPassenger(passengerCheckBox.isSelected());
        int blocking = 0;
        try {
            blocking = Integer.parseInt(blockingTextField.getText());
            // only allow numbers between 0 and 100
            if (blocking < 0 || blocking > 100) {
                blocking = 0;
            }
        } catch (Exception e) {
            log.warn("Blocking must be a number between 0 and 100");
        }
        // ask if blocking order should be the same
        if (isSave && car.getKernel() == null && passengerCheckBox.isSelected() && car.getBlocking() != blocking) {
            if (JOptionPane.showConfirmDialog(this, MessageFormat.format(Bundle.getMessage("carChangeBlocking"),
                    new Object[]{blocking, car.getTypeName()}),
                    MessageFormat.format(Bundle
                            .getMessage("carModifyAllType"), new Object[]{car.getTypeName()}),
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                // go through the entire list and change the passenger setting
                // for all cars of this type
                for (Car c : carManager.getList()) {
                    if (c.isPassenger() && c.getTypeName().equals(car.getTypeName())) {
                        c.setBlocking(blocking);
                    }
                }
            }
        }
        car.setBlocking(blocking);
        // ask if all cars of this type should be caboose
        if (isSave && car.isCaboose() ^ cabooseCheckBox.isSelected()) {
            if (JOptionPane.showConfirmDialog(this, MessageFormat.format(cabooseCheckBox.isSelected() ? Bundle
                    .getMessage("carModifyTypeCaboose") : Bundle.getMessage("carRemoveTypeCaboose"),
                    new Object[]{car.getTypeName()}),
                    MessageFormat.format(Bundle.getMessage("carModifyAllType"),
                            new Object[]{car.getTypeName()}),
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                // go through the entire list and change the caboose setting for all cars of this type
                for (Car c : carManager.getList()) {
                    if (c.getTypeName().equals(car.getTypeName())) {
                        c.setCaboose(cabooseCheckBox.isSelected());
                    }
                }
            }
        }
        car.setCaboose(cabooseCheckBox.isSelected());
        // ask if all cars of this type should be utility
        if (isSave && car.isUtility() ^ utilityCheckBox.isSelected()) {
            if (JOptionPane.showConfirmDialog(this, MessageFormat.format(utilityCheckBox.isSelected() ? Bundle
                    .getMessage("carModifyTypeUtility") : Bundle.getMessage("carRemoveTypeUtility"),
                    new Object[]{car.getTypeName()}),
                    MessageFormat.format(Bundle.getMessage("carModifyAllType"),
                            new Object[]{car.getTypeName()}),
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                // go through the entire list and change the utility for all cars of this type
                for (Car c : carManager.getList()) {
                    if (c.getTypeName().equals(car.getTypeName())) {
                        c.setUtility(utilityCheckBox.isSelected());
                    }
                }
            }
        }
        car.setUtility(utilityCheckBox.isSelected());
        // ask if all cars of this type should be hazardous
        if (isSave && car.isHazardous() ^ hazardousCheckBox.isSelected()) {
            if (JOptionPane.showConfirmDialog(this, MessageFormat.format(hazardousCheckBox.isSelected() ? Bundle
                    .getMessage("carModifyTypeHazardous") : Bundle.getMessage("carRemoveTypeHazardous"),
                    new Object[]{car.getTypeName()}),
                    MessageFormat.format(Bundle.getMessage("carModifyAllType"),
                            new Object[]{car.getTypeName()}),
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                // go through the entire list and change the hazardous setting for all cars of this type
                for (Car c : carManager.getList()) {
                    if (c.getTypeName().equals(car.getTypeName())) {
                        c.setHazardous(hazardousCheckBox.isSelected());
                    }
                }
            }
        }
        car.setHazardous(hazardousCheckBox.isSelected());
        car.setFred(fredCheckBox.isSelected());
        car.setBuilt(builtTextField.getText());
        if (ownerComboBox.getSelectedItem() != null) {
            car.setOwner((String) ownerComboBox.getSelectedItem());
        }
        if (groupComboBox.getSelectedItem() != null) {
            if (groupComboBox.getSelectedItem().equals(CarManager.NONE)) {
                car.setKernel(null);
            } else if (!car.getKernelName().equals(groupComboBox.getSelectedItem())) {
                car.setKernel(carManager.getKernelByName((String) groupComboBox.getSelectedItem()));
                // if car has FRED or caboose make lead
                if (car.hasFred() || car.isCaboose()) {
                    car.getKernel().setLead(car);
                }
                car.setBlocking(car.getKernel().getSize());
            }
        }
        if (loadComboBox.getSelectedItem() != null && !car.getLoadName().equals(loadComboBox.getSelectedItem())) {
            car.setLoadName((String) loadComboBox.getSelectedItem());
            // check to see if car is part of kernel, and ask if all the other cars in the kernel should be changed
            if (car.getKernel() != null) {
                List<Car> cars = car.getKernel().getCars();
                if (cars.size() > 1) {
                    if (JOptionPane.showConfirmDialog(this, MessageFormat.format(Bundle.getMessage("carInKernelLoad"),
                            new Object[]{car.toString(), car.getLoadName()}),
                            MessageFormat.format(Bundle.getMessage("carPartKernel"),
                                    new Object[]{car.getKernelName()}),
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        // go through the entire list and change the loads for all cars
                        for (Car c : cars) {
                            if (InstanceManager.getDefault(CarLoads.class).containsName(c.getTypeName(),
                                    car.getLoadName())) {
                                c.setLoadName(car.getLoadName());
                            }
                        }
                    }
                }
            }
        }

        // update blocking
        pBlocking.setVisible(passengerCheckBox.isSelected() || car.getKernel() != null);
        blockingTextField.setText(Integer.toString(car.getBlocking()));

        // is this car part of a kernel?
        if (car.getKernel() != null) {
            List<Car> cars = car.getKernel().getCars();
            if (cars.size() > 1) {
                for (Car kcar : cars) {
                    if (kcar != car) {
                        if (kcar.getLocation() != car.getLocation() || kcar.getTrack() != car.getTrack()) {
                            int results = JOptionPane.showConfirmDialog(this, MessageFormat.format(Bundle
                                    .getMessage("carInKernelLocation"),
                                    new Object[]{car.toString(), car.getLocationName(), car.getTrackName()}),
                                    MessageFormat
                                            .format(Bundle.getMessage("carPartKernel"),
                                                    new Object[]{car.getKernelName()}),
                                    JOptionPane.YES_NO_OPTION);
                            if (results == JOptionPane.NO_OPTION) {
                                break; // done
                            }
                            // change the location for all cars in kernel
                            for (Car kcar2 : cars) {
                                if (kcar2 != car) {
                                    setLocationAndTrack(kcar2);
                                }
                            }
                            break; // done
                        }
                    }
                }
            }
        }
    }

    CarAttributeEditFrame carAttributeEditFrame;

    // edit buttons only one frame active at a time
    @Override
    public void buttonEditActionPerformed(java.awt.event.ActionEvent ae) {
        if (carAttributeEditFrame != null) {
            carAttributeEditFrame.dispose();
        }
        carAttributeEditFrame = new CarAttributeEditFrame();
        carAttributeEditFrame.setLocationRelativeTo(this);
        carAttributeEditFrame.addPropertyChangeListener(this);

        if (ae.getSource() == editRoadButton) {
            carAttributeEditFrame.initComponents(CarAttributeEditFrame.ROAD, (String) roadComboBox.getSelectedItem());
        }
        if (ae.getSource() == editTypeButton) {
            carAttributeEditFrame.initComponents(CarAttributeEditFrame.TYPE, (String) typeComboBox.getSelectedItem());
        }
        if (ae.getSource() == editColorButton) {
            carAttributeEditFrame.initComponents(CarAttributeEditFrame.COLOR, (String) colorComboBox.getSelectedItem());
        }
        if (ae.getSource() == editLengthButton) {
            carAttributeEditFrame.initComponents(CarAttributeEditFrame.LENGTH,
                    (String) lengthComboBox.getSelectedItem());
        }
        if (ae.getSource() == editOwnerButton) {
            carAttributeEditFrame.initComponents(CarAttributeEditFrame.OWNER, (String) ownerComboBox.getSelectedItem());
        }
        if (ae.getSource() == editGroupButton) {
            carAttributeEditFrame.initComponents(CarAttributeEditFrame.KERNEL,
                    (String) groupComboBox.getSelectedItem());
        }
    }

    @Override
    public void dispose() {
        removePropertyChangeListeners();
        super.dispose();
    }

    private void removePropertyChangeListeners() {
        carManager.removePropertyChangeListener(this);
    }

    @Override
    public void propertyChange(java.beans.PropertyChangeEvent e) {
        //        if (Control.SHOW_PROPERTY) {
        log.debug("Property change: ({}) old: ({}) new: ({})", e.getPropertyName(), e.getOldValue(), e
                .getNewValue());
        //        }
        super.propertyChange(e);

        if (e.getPropertyName().equals(CarLengths.CARLENGTHS_CHANGED_PROPERTY)) {
            InstanceManager.getDefault(CarLengths.class).updateComboBox(lengthComboBox);
            if (_rs != null) {
                lengthComboBox.setSelectedItem(_rs.getLength());
            }
        }
        if (e.getPropertyName().equals(CarManager.KERNEL_LISTLENGTH_CHANGED_PROPERTY) ||
                e.getPropertyName().equals(Car.KERNEL_NAME_CHANGED_PROPERTY)) {
            carManager.updateKernelComboBox(groupComboBox);
            if (_rs != null) {
                groupComboBox.setSelectedItem(((Car) _rs).getKernelName());
            }
        }
        if (e.getPropertyName().equals(CarLoads.LOAD_CHANGED_PROPERTY)) {
            InstanceManager.getDefault(CarLoads.class).updateComboBox((String) typeComboBox.getSelectedItem(),
                    loadComboBox);
        }
        if (e.getPropertyName().equals(Car.LOAD_CHANGED_PROPERTY) ||
                e.getPropertyName().equals(CarLoads.LOAD_CHANGED_PROPERTY)) {
            if (_rs != null) {
                loadComboBox.setSelectedItem(((Car) _rs).getLoadName());
            }
        }
        if (e.getPropertyName().equals(CarAttributeEditFrame.DISPOSE)) {
            carAttributeEditFrame = null;
        }
    }

    private final static Logger log = LoggerFactory.getLogger(CarEditFrame.class);
}
