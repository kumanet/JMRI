package jmri.jmrit.display;

import jmri.*;
import jmri.jmrit.catalog.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.sun.java.util.collections.*;

/**
 * Provides a simple editor for adding jmri.jmrit.display items
 * to a captive JFrame
 * <P>GUI is structured as a band of common parameters across the
 * top, then a series of things you can add.
 * <P>
 * All created objects are put specific levels depending on their
 * type (higher levels are in front):
 * <UL>
 * <LI>BKG background
 * <LI>ICONS icons and other drawing symbols
 * <LI>LABELS text labels
 * <LI>TURNOUTS turnouts and other variable track items
 * <LI>SENSORS sensors and other independently modified objects
 * </UL>
 * <P>
 * The "contents" List keeps track of all the objects added to the target
 * frame for later manipulation.
 * <P>
 * If you close the Editor window, the target is left alone and
 * the editor window is just hidden, not disposed.
 * If you close the target, the editor and target are removed,
 * and dispose is run. To make this logic work, the PanelEditor
 * is descended from a JFrame, not a JPanel.  That way it
 * can control its own visibility.
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * @author Bob Jacobsen
 * @version $Revision: 1.23 $
 */

public class PanelEditor extends JFrame {

    final public static Integer BKG       = new Integer(1);
    final public static Integer ICONS     = new Integer(3);
    final public static Integer LABELS    = new Integer(5);
    final public static Integer SECURITY  = new Integer(6);
    final public static Integer TURNOUTS  = new Integer(7);
    final public static Integer SIGNALS   = new Integer(9);
    final public static Integer SENSORS   = new Integer(10);

    JTextField nextX = new JTextField("20",4);
    JTextField nextY = new JTextField("30",4);

    JButton labelAdd = new JButton("Add text:");
    JTextField nextLabel = new JTextField(10);

    JButton iconAdd = new JButton("Add icon:");
    JButton pickIcon = new JButton("pick");
    NamedIcon labelIcon = null;

    JButton turnoutAddR = new JButton("Add turnout:");
    JTextField nextTurnoutR = new JTextField(5);
    JButton closedIconButtonR;
    NamedIcon closedIconR;
    JButton thrownIconButtonR;
    NamedIcon thrownIconR;
    NamedIcon inconsistentR;
    NamedIcon unknownR;

    JButton turnoutAddL = new JButton("Add turnout:");
    JTextField nextTurnoutL = new JTextField(5);
    JButton closedIconButtonL;
    NamedIcon closedIconL;
    JButton thrownIconButtonL;
    NamedIcon thrownIconL;

    JButton sensorAdd = new JButton("Add sensor:");
    JTextField nextSensor = new JTextField(5);
    JButton activeIconButton;
    NamedIcon activeIcon;
    JButton inactiveIconButton;
    NamedIcon inactiveIcon;

    JButton signalAdd = new JButton("Add signal:");
    JTextField nextSignalSE = new JTextField(5);
    JTextField nextSignalHead = new JTextField(2);

    JButton backgroundAddButton = new JButton("Pick background image...");

    public CatalogPane catalog = new CatalogPane();

    public PanelEditor() { this("Panel Editor");}

    public PanelEditor(String name) {
        super(name);
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        // common items
        JPanel common = new JPanel();
        common.setLayout(new FlowLayout());
        common.add(new JLabel(" x:"));
        common.add(nextX);
        common.add(new JLabel(" y:"));
        common.add(nextY);
        this.getContentPane().add(common);

        this.getContentPane().add(new JSeparator(javax.swing.SwingConstants.HORIZONTAL));

        // add a background image
        {
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());
            panel.add(backgroundAddButton);
            panel.add(labelAdd);
            backgroundAddButton.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        addBackground();
                    }
                }
            );
            this.getContentPane().add(panel);
        }

        // add a text label
        {
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());
            panel.add(labelAdd);
            panel.add(nextLabel);
            labelAdd.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        addLabel();
                    }
                }
            );
            this.getContentPane().add(panel);
        }

        this.getContentPane().add(new JSeparator(javax.swing.SwingConstants.HORIZONTAL));

        // add an icon label
        {
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());
            panel.add(iconAdd);
            panel.add(pickIcon);
            pickIcon.setToolTipText("Click to select new icon");
            pickIcon.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        pickLabelIcon();
                    }
                }
            );
            iconAdd.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        addIcon();
                    }
                }
            );
            this.getContentPane().add(panel);

        }

        this.getContentPane().add(new JSeparator(javax.swing.SwingConstants.HORIZONTAL));

        // Add a turnout indicator for right-bound
        {
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());
            panel.add(turnoutAddR);
            TurnoutIcon to = new TurnoutIcon();

            inconsistentR = new NamedIcon("resources/icons/smallschematics/tracksegments/os-righthand-west-error.gif",
                            "resources/icons/smallschematics/tracksegments/os-righthand-west-error.gif");
            unknownR = new NamedIcon("resources/icons/smallschematics/tracksegments/os-righthand-west-unknown.gif",
                            "resources/icons/smallschematics/tracksegments/os-righthand-west-unknown.gif");

            closedIconR = new NamedIcon("resources/icons/smallschematics/tracksegments/os-righthand-west-closed.gif",
                            "resources/icons/smallschematics/tracksegments/os-righthand-west-closed.gif");
            closedIconButtonR = new JButton(closedIconR);
            closedIconButtonR.setToolTipText("Icon for turnout closed. Click to select new icon");

            thrownIconR = new NamedIcon("resources/icons/smallschematics/tracksegments/os-righthand-west-thrown.gif",
                            "resources/icons/smallschematics/tracksegments/os-righthand-west-thrown.gif");
            thrownIconButtonR = new JButton(thrownIconR);
            thrownIconButtonR.setToolTipText("Icon for turnout thrown. Click to select new icon");

            panel.add(nextTurnoutR);
            panel.add(closedIconButtonR);
            closedIconButtonR.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        pickClosedIconR();
                    }
                }
            );
            panel.add(thrownIconButtonR);
            thrownIconButtonR.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        pickThrownIconR();
                    }
                }
            );
            turnoutAddR.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        addTurnoutR();
                    }
                }
            );
            this.getContentPane().add(panel);
        }

        // Add a turnout indicator for left-hand
        {
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());
            panel.add(turnoutAddL);
            TurnoutIcon to = new TurnoutIcon();

            NamedIcon inconsistentL = new NamedIcon("resources/icons/smallschematics/tracksegments/os-lefthand-east-error.gif",
                            "resources/icons/smallschematics/tracksegments/os-lefthand-east-error.gif");
            NamedIcon unknownL = new NamedIcon("resources/icons/smallschematics/tracksegments/os-lefthand-east-unknown.gif",
                            "resources/icons/smallschematics/tracksegments/os-lefthand-east-unknown.gif");

            closedIconL = new NamedIcon("resources/icons/smallschematics/tracksegments/os-lefthand-east-closed.gif",
                            "resources/icons/smallschematics/tracksegments/os-lefthand-east-closed.gif");
            closedIconButtonL = new JButton(closedIconL);
            closedIconButtonL.setToolTipText("Icon for turnout closed. Click to select new icon");

            thrownIconL = new NamedIcon("resources/icons/smallschematics/tracksegments/os-lefthand-east-thrown.gif",
                            "resources/icons/smallschematics/tracksegments/os-lefthand-east-thrown.gif");
            thrownIconButtonL = new JButton(thrownIconL);
            thrownIconButtonL.setToolTipText("Icon for turnout thrown. Click to select new icon");

            panel.add(nextTurnoutL);
            panel.add(closedIconButtonL);
            closedIconButtonL.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        pickClosedIconL();
                    }
                }
            );
            panel.add(thrownIconButtonL);
            thrownIconButtonL.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        pickThrownIconL();
                    }
                }
            );
            turnoutAddL.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        addTurnoutL();
                    }
                }
            );
            this.getContentPane().add(panel);
        }

        // Add a sensor indicator
        {
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());

            SensorIcon to = new SensorIcon();
            activeIcon = to.getActiveIcon();
            inactiveIcon = to.getInactiveIcon();
            activeIconButton = new JButton(activeIcon);
            activeIconButton.setToolTipText("Icon for sensor active. Click to select new icon");
            inactiveIconButton = new JButton(inactiveIcon);
            inactiveIconButton.setToolTipText("Icon for sensor inactive. Click to select new icon");

            panel.add(sensorAdd);
            panel.add(nextSensor);
            panel.add(activeIconButton);
            activeIconButton.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        pickActiveIcon();
                    }
                }
            );
            panel.add(inactiveIconButton);
            inactiveIconButton.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        pickInactiveIcon();
                    }
                }
            );
            sensorAdd.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        addSensor();
                    }
                }
            );
            this.getContentPane().add(panel);
        }

        // add a signal head
        {
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());

            panel.add(signalAdd);
            panel.add(nextSignalSE);
            panel.add(new JLabel("head:"));
            panel.add(nextSignalHead);
            signalAdd.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent a) {
                        addSignalHead();
                    }
                }
            );
            this.getContentPane().add(panel);
        }

        // allow for selection of icons
        this.getContentPane().add(catalog);

        // register the result for later configuration
        InstanceManager.configureManagerInstance().register(this);

        // move it off the panel's position
        setLocation(250,0);

    }  // end ctor

    /**
     * Select the icon for an icon label
     */
    void pickLabelIcon() {
        labelIcon = pickIcon();
        pickIcon.setIcon(labelIcon);
        pickIcon.setText(null);  // remove original "pick" label
    }

    /**
     * Select the icon for "thrown" right-bound
     */
    void pickThrownIconR() {
        thrownIconR = pickIcon();
        thrownIconButtonR.setIcon(thrownIconR);
    }

    /**
     * Select the image for "closed" right-bound
     */
    void pickClosedIconR() {
        closedIconR = pickIcon();
        closedIconButtonR.setIcon(closedIconR);
    }

    /**
     * Select the icon for "thrown" left-bound
     */
    void pickThrownIconL() {
        thrownIconL = pickIcon();
        thrownIconButtonL.setIcon(thrownIconL);
    }

    /**
     * Select the image for "closed" left-bound
     */
    void pickClosedIconL() {
        closedIconL = pickIcon();
        closedIconButtonL.setIcon(closedIconL);
    }

    /**
     * Select the icon for "active"
     */
    void pickActiveIcon() {
        activeIcon = pickIcon();
        activeIconButton.setIcon(activeIcon);
    }

    /**
     * Select the image for "inactive"
     */
    void pickInactiveIcon() {
        inactiveIcon = pickIcon();
        inactiveIconButton.setIcon(inactiveIcon);
    }

    /**
     * Select and create image for use in some icon
     */
    NamedIcon pickIcon() {
        return catalog.getSelectedIcon();
    }

    /**
     * Button pushed, add a background image (do this early!)
     */
    void addBackground() {
        JFileChooser inputFileChooser = new JFileChooser(" ");
        int retVal = inputFileChooser.showOpenDialog(this);
        if (retVal != JFileChooser.APPROVE_OPTION) return;  // give up if no file selected
        log.debug("Open image file: "+inputFileChooser.getSelectedFile().getPath());
        NamedIcon icon = new NamedIcon(inputFileChooser.getSelectedFile().getPath(),
                                        inputFileChooser.getSelectedFile().getPath());
        JLabel l = new PositionableLabel(icon);
        l.setSize(icon.getIconWidth(), icon.getIconHeight());
        putBackground(l);
    }

    public void putBackground(JLabel l) {
        target.add(l, BKG);
        contents.add(l);
        backgroundAddButton.setEnabled(false);   // theres only one
        target.revalidate();
    }

    /**
     * Add a turnout indicator to the target
     */
    void addTurnoutR() {
        TurnoutIcon l = new TurnoutIcon();
        if (closedIconR!=null) l.setClosedIcon(new NamedIcon(closedIconR));
        if (thrownIconR!=null) l.setThrownIcon(new NamedIcon(thrownIconR));
        if (inconsistentR!=null) l.setInconsistentIcon(new NamedIcon(inconsistentR));
        if (unknownR!=null) l.setUnknownIcon(new NamedIcon(unknownR));

        l.setTurnout(null, nextTurnoutR.getText());

        log.debug("turnout height, width: "+l.getHeight()+" "+l.getWidth());
        setNextLocation(l);
        putTurnout(l);
    }
    void addTurnoutL() {
        TurnoutIcon l = new TurnoutIcon();
        if (closedIconL!=null) l.setClosedIcon(new NamedIcon(closedIconL));
        if (thrownIconL!=null) l.setThrownIcon(new NamedIcon(thrownIconL));
        l.setTurnout(null, nextTurnoutL.getText());

        setNextLocation(l);
        putTurnout(l);
    }
    public void putTurnout(TurnoutIcon l) {
        l.invalidate();
        target.add(l, TURNOUTS);
        contents.add(l);
        // reshow the panel
        target.validate();
    }

    /**
     * Add a sensor indicator to the target
     */
    void addSensor() {
        SensorIcon l = new SensorIcon();
        if (activeIcon!=null) l.setActiveIcon(new NamedIcon(activeIcon));
        if (inactiveIcon!=null) l.setInactiveIcon(new NamedIcon(inactiveIcon));
        l.setSensor(null, nextSensor.getText());
        setNextLocation(l);
        putSensor(l);
    }
    public void putSensor(SensorIcon l) {
        l.invalidate();
        target.add(l, SENSORS);
        contents.add(l);
        // reshow the panel
        target.validate();
    }

    /**
     * Add a signal head to the target
     */
    void addSignalHead() {
        SignalHeadIcon l = new SignalHeadIcon();
        l.setSignalHead(nextSignalSE.getText(), Integer.parseInt(nextSignalHead.getText()));
        setNextLocation(l);
        putSignal(l);
    }
    public void putSignal(SignalHeadIcon l) {
        l.invalidate();
        target.add(l, SIGNALS);
        contents.add(l);
        // reshow the panel
        target.validate();
    }

    /**
     * Add a label to the target
     */
    void addLabel() {
        JComponent l = new PositionableLabel(nextLabel.getText());
        setNextLocation(l);
        putLabel(l);
    }
    public void putLabel(JComponent l) {
        l.invalidate();
        target.add(l, LABELS);
        contents.add(l);
        target.validate();
    }

    /**
     * Add an icon to the target
     */
    void addIcon() {
        PositionableLabel l = new PositionableLabel(new NamedIcon(labelIcon) );
        setNextLocation(l);
        putIcon(l);
    }
    public void putIcon(PositionableLabel l) {
        l.invalidate();
        target.add(l, ICONS);
        contents.add(l);
        // reshow the panel
        target.validate();
    }

    /**
     * Set an objects location and size as it is created.
     * Size comes from the preferredSize; location comes
     * from the fields where the user can spec it.
     */
    void setNextLocation(JComponent obj) {
        int x = Integer.parseInt(nextX.getText());
        int y = Integer.parseInt(nextY.getText());
        //obj.setLocation(x,y);
        obj.setBounds(x,y,obj.getWidth(),obj.getHeight());
    }

    /**
     * Set the JLayeredPane containing the objects to be edited.
     */
    public void setTarget(JLayeredPane f) {
        target = f;
    }
    public JLayeredPane getTarget() { return target;}
    public JLayeredPane target;

    /**
     * Get the frame containing the results (not the editor)
     */
    public JFrame getFrame() { return frame; }
    public void setFrame(JFrame f) {
        frame = f;
        // handle target window closes
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				targetWindowClosing(e);
			}
		});
    }

    JFrame frame;

    public ArrayList contents = new ArrayList();

    /**
     * Clean up when its time to make it all go away
     */
    public void dispose() {
        // register the result for later configuration
        InstanceManager.configureManagerInstance().deregister(this);

        // clean up local links to push GC
        contents.clear();
        target = null;
        frame = null;

        // clean up GUI aspects
        this.removeAll();
        super.dispose();
    }

    /**
     * The target window has been requested to close, so clean up
     */
    void targetWindowClosing(java.awt.event.WindowEvent e) {
        frame.setVisible(false);  // removes the target window
        this.setVisible(false);        // doesn't remove the editor!

        dispose();
    }

    // initialize logging
    static org.apache.log4j.Category log = org.apache.log4j.Category.getInstance(PanelEditor.class.getName());
}