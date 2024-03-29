/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.TooManyListenersException;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;

import org.ascape.model.Scape;
import org.ascape.model.event.ControlEvent;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.event.ScapeListener;
import org.ascape.runtime.swing.DesktopEnvironment;
import org.ascape.util.sweep.SweepDimension;
import org.ascape.util.sweep.SweepGroup;
import org.ascape.util.sweep.Sweepable;
import org.ascape.util.swing.NumberOnlyField;
import org.ascape.view.nonvis.DataOutputView;
import org.ascape.view.nonvis.SweepControlView;

/**
 * WORK IN (SLOW) PROGRESS. A panel for controlling batch runs.
 * 
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/9/99 made changes to support other customizers
 * @history 1.2 6/2/99 first in
 * @since 1.2
 */
public class BatchView extends PanelView {

    /**
     * The sweep view.
     */
    private SweepControlView sweepView;

    /**
     * The data view.
     */
    private DataOutputView dataView;

    /**
     * The tool bar.
     */
    private JToolBar toolBar;

    //protected JLabel periodLabel;

    /**
     * The status label.
     */
    private JLabel statusLabel = new JLabel();

    /**
     * The frame list.
     */
    protected JComboBox frameList;

    /**
     * Scape control for opening a new scape.
     */
    private ScapeControlAction openAction;
    
    /**
     * The open button.
     */
    private JButton openButton;

    /**
     * Scape control for starting a scape.
     */
    private ScapeControlAction startStopAction;
    
    /**
     * The start stop button.
     */
    private JButton startStopButton;

    /**
     * Scape control for pausing a scape.
     */
    private ScapeControlAction pauseToggleAction;
    
    /**
     * The pause toggle button.
     */
    private JButton pauseToggleButton;

    /**
     * Scape control for starting a scape.
     */
    private ScapeControlAction settingsAction;
    
    /**
     * The settings button.
     */
    private JButton settingsButton;

    /**
     * Scape control for getting information about a scape.
     */
    private ScapeControlAction infoAction;
    
    /**
     * The info button.
     */
    private JButton infoButton;

    /**
     * Scape control for quiting the model.
     */
    private ScapeControlAction quitAction;
    
    /**
     * The quit button.
     */
    private JButton quitButton;

    /**
     * Scape control for creating a new time series graph.
     */
    protected ScapeControlAction addTSAction;
    
    /**
     * The add TS button.
     */
    protected JButton addTSButton;

    /**
     * Scape control for creating a new histogram.
     */
    protected ScapeControlAction addHistAction;
    
    /**
     * The add hist button.
     */
    protected JButton addHistButton;

    /**
     * Scape control for creating a new pie chart.
     */
    protected ScapeControlAction addPieAction;
    
    /**
     * The add pie button.
     */
    protected JButton addPieButton;

    /**
     * Does the scape appear to be running?.
     */
    private boolean scapeAppearsRunning = false;

    /**
     * Did the scape appear to be running last iteration?.
     */
    private boolean lastScapeAppearsRunning = false;

    /**
     * Does the scape appear to be paused?.
     */
    private boolean scapeAppearsPaused = false;

    /**
     * Did the scape appear to be paused last iteration?.
     */
    private boolean lastScapeAppearsPaused = false;

    /**
     * The sweep holder panel.
     */
    private JPanel sweepHolderPanel;

    /**
     * The sweep setup panel.
     */
    private JPanel sweepSetupPanel;

    /**
     * The sweep run panel.
     */
    JPanel sweepRunPanel;

    /**
     * The sweep model.
     */
    private AbstractTableModel sweepModel;

    /**
     * The run file candidate.
     */
    private File runFileCandidate;

    /**
     * The run file field.
     */
    private final JTextField runFileField = new JTextField();

    /**
     * The period file candidate.
     */
    private File periodFileCandidate;

    /**
     * The period file field.
     */
    private final JTextField periodFileField = new JTextField();

    /**
     * Instantiates a new batch view.
     */
    public BatchView() {
        sweepView = new SweepControlView();
        sweepView.setSweepGroup(new SweepGroup() {
            /**
             * 
             */
            private static final long serialVersionUID = -8973165433109317254L;

            public void addMember(Sweepable sweep) {
                super.addMember(sweep);
                sweepModel.fireTableDataChanged();
            }
        });
        //Make the data output view batch view aware
        dataView = new DataOutputView() {
            /**
             * 
             */
            private static final long serialVersionUID = -5100918563727511434L;

            public void setRunFile(File file) throws IOException {
                super.setRunFile(file);
                BatchView.this.runFileCandidate = file;
                runFileField.setText(file.getName());
            }
        };
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#build()
     */
    public void build() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(6, 6, 6, 6));
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(0, 0));
        add(contentPanel, BorderLayout.CENTER);

        sweepModel = new AbstractTableModel() {
            /**
             * 
             */
            private static final long serialVersionUID = 8292222004446498779L;

            public int getRowCount() {
                return sweepView.getSweepGroup().getUnlinkedSize();
            }

            public int getColumnCount() {
                return 4;
            }

            public void setValueAt(Object value, int row, int col) {
                SweepDimension dim = (SweepDimension) sweepView.getSweepGroup().getUnlinkedMember(row);
                if (col == 1) {
                    dim.setMinAsText((String) value);
                } else if (col == 2) {
                    dim.setMaxAsText((String) value);
                } else if (col == 3) {
                    dim.setIncrementAsText((String) value);
                } else {
                    throw new RuntimeException("Internal Error");
                }
            }

            public Object getValueAt(int row, int col) {
                SweepDimension dim = (SweepDimension) sweepView.getSweepGroup().getUnlinkedMember(row);
                if (col == 0) {
                    return dim.getLongName();
                } else if (col == 1) {
                    return dim.getMinAsText();
                } else if (col == 2) {
                    return dim.getMaxAsText();
                } else if (col == 3) {
                    return dim.getIncrementAsText();
                } else {
                    throw new RuntimeException("Internal Error");
                }
            }

            public boolean isCellEditable(int row, int col) {
                if (col == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            public String getColumnName(int col) {
                if (col == 0) {
                    return "Parameter";
                } else if (col == 1) {
                    return "Start";
                } else if (col == 2) {
                    return "End";
                } else if (col == 3) {
                    return "Increment";
                } else {
                    throw new RuntimeException("Internal Error");
                }
            }
        };

        GridBagLayout gbl = new GridBagLayout();
        contentPanel.setLayout(gbl);
        GridBagConstraints gbc = gbl.getConstraints(contentPanel);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.gridx = 0;
        gbc.ipadx = 6;
        gbc.ipady = 6;

        contentPanel.add(new JLabel("Sweep Settings"), gbc);

        sweepHolderPanel = new JPanel();
        sweepSetupPanel = new JPanel();
        GridBagLayout sgbl = new GridBagLayout();
        sweepSetupPanel.setLayout(sgbl);
        GridBagConstraints sgbc = sgbl.getConstraints(sweepSetupPanel);

        final JTable sweepTable = new JTable(sweepModel);
        sweepTable.setRowSelectionAllowed(false);
        sweepTable.setShowGrid(false);
        sweepTable.getColumnModel().getColumn(0).setMinWidth(120);
        sweepTable.getColumnModel().getColumn(1).setMaxWidth(80);
        sweepTable.getColumnModel().getColumn(2).setMaxWidth(80);
        sweepTable.getColumnModel().getColumn(3).setMaxWidth(60);
//Fix table behavior so that clicks edit immeadiatly and edits are live
        final DefaultCellEditor ed = (DefaultCellEditor) sweepTable.getDefaultEditor(Object.class);
        ed.setClickCountToStart(0);
        final JTextField f = (JTextField) (ed).getComponent();
        DocumentListener sweepListener = new DocumentListener() {
            public void anyUpdate() {
                int r = sweepTable.getEditingRow();
                int c = sweepTable.getEditingColumn();
                if ((r != -1) && (c != -1)) {
                    sweepTable.setValueAt(ed.getCellEditorValue(), r, c);
                }
            }

            public void changedUpdate(DocumentEvent e) {
                anyUpdate();
            }

            public void insertUpdate(DocumentEvent e) {
                anyUpdate();
            }

            public void removeUpdate(DocumentEvent e) {
                anyUpdate();
            }
        };
        f.getDocument().addDocumentListener(sweepListener);
        JScrollPane scrollPane = new JScrollPane(sweepTable);
        sgbc.gridwidth = GridBagConstraints.REMAINDER;
        sgbc.gridheight = 1;
        sgbc.fill = GridBagConstraints.BOTH;
        sgbc.weightx = 1.0;
        sgbc.weighty = 1.0;
        sgbc.gridx = 0;
        sgbc.gridy = 0;
        sweepSetupPanel.add(scrollPane, sgbc);

        JPanel runsPerPanel = new JPanel();
        runsPerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//agentIterationsPanel.setBorder(BorderFactory.createTitledBorder("Data Points"));
        final JTextField runsPerField = new NumberOnlyField(Integer.toString(sweepView.getSweepGroup().getRunsPer()), 5);
        runsPerField.setPreferredSize(new Dimension(40, runsPerField.getPreferredSize().height));
        runsPerPanel.add(new JLabel("Runs Per Sweep Value:"));
        runsPerPanel.add(runsPerField);
        runsPerField.getDocument().addDocumentListener(new DocumentListener() {
            public void updated() {
                try {
                    sweepView.getSweepGroup().setRunsPer(Integer.parseInt(runsPerField.getText()));
                } catch (NumberFormatException e) {
                }
            }

            public void changedUpdate(DocumentEvent e) {
                updated();
            };
            public void insertUpdate(DocumentEvent e) {
                updated();
            };
            public void removeUpdate(DocumentEvent e) {
                updated();
            };
        });
        sgbc.gridwidth = GridBagConstraints.REMAINDER;
        sgbc.gridy++;
        sgbc.fill = GridBagConstraints.HORIZONTAL;
        sgbc.weightx = 1.0;
        sgbc.weighty = 0.0;
        sweepSetupPanel.add(runsPerPanel, sgbc);

        JPanel startPeriodPanel = new JPanel();
        startPeriodPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        final JTextField startPeriodField = new NumberOnlyField(Integer.toString(scape.getStartPeriod()), 5);
//Need to put Stop period field up here so it will be accessible
//JPanel stopPeriodPanel = new JPanel();
//stopPeriodPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        final JTextField stopPeriodField = new NumberOnlyField(Integer.toString(scape.getStopPeriod()), 5);
        final JCheckBox stopPeriodNoneCheckbox = new JCheckBox("None");
        if (scape.getStopPeriod() == Integer.MAX_VALUE) {
            stopPeriodNoneCheckbox.setSelected(true);
            stopPeriodField.setText("");
            stopPeriodField.setEnabled(false);
        } else {
            stopPeriodNoneCheckbox.setSelected(false);
            stopPeriodField.setText(Integer.toString(scape.getStopPeriod()));
            stopPeriodField.setEnabled(true);
        }
        final DocumentListener periodListener = new DocumentListener() {
            public void updated() {
                try {
                    scape.setStartPeriod(Integer.parseInt(startPeriodField.getText()));
                    startPeriodField.setForeground(Color.black);
                } catch (NumberFormatException e) {
                } catch (org.ascape.model.space.SpatialTemporalException e) {
                    startPeriodField.setForeground(Color.red);
                }
                try {
                    if (stopPeriodNoneCheckbox.isSelected()) {
                        stopPeriodField.setText("");
                        stopPeriodField.setEnabled(false);
                        scape.setStopPeriod(Integer.MAX_VALUE);
                    } else {
                        stopPeriodField.setEnabled(true);
                        scape.setStopPeriod(Integer.parseInt(stopPeriodField.getText()));
                    }
                    stopPeriodField.setForeground(Color.black);
                } catch (NumberFormatException e) {
                } catch (org.ascape.model.space.SpatialTemporalException e) {
                    stopPeriodField.setForeground(Color.red);
                }
                try {
                    if (Integer.parseInt(startPeriodField.getText()) > Integer.parseInt(stopPeriodField.getText())) {
                        startPeriodField.setForeground(Color.red);
                        stopPeriodField.setForeground(Color.red);
                    }
                } catch (NumberFormatException e) {
                }
            }

            public void changedUpdate(DocumentEvent e) {
                updated();
            };
            public void insertUpdate(DocumentEvent e) {
                updated();
            };
            public void removeUpdate(DocumentEvent e) {
                updated();
            };
        };

        startPeriodPanel.add(new JLabel("Start Period:"));
        startPeriodPanel.add(startPeriodField);
        startPeriodField.getDocument().addDocumentListener(periodListener);
        sgbc.gridwidth = GridBagConstraints.REMAINDER;
        sgbc.gridy++;
        sgbc.fill = GridBagConstraints.HORIZONTAL;
        sgbc.weightx = 1.0;
        sgbc.weighty = 0.0;
        sweepSetupPanel.add(startPeriodPanel, sgbc);

        stopPeriodNoneCheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                periodListener.changedUpdate(null);
            }
        });
        startPeriodPanel.add(new JLabel("Stop Period:"));
        startPeriodPanel.add(stopPeriodField);
        startPeriodPanel.add(stopPeriodNoneCheckbox);
        stopPeriodField.getDocument().addDocumentListener(periodListener);
/*sgbc.gridwidth = GridBagConstraints.REMAINDER;
        sgbc.gridy++;
        sgbc.fill = GridBagConstraints.HORIZONTAL;
        sgbc.weightx = 1.0;
        sgbc.weighty = 0.0;
        sweepSetupPanel.add(stopPeriodPanel, sgbc);*/

        JPanel runFilePanel = new JPanel();
        runFilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        runFileCandidate = dataView.getRunFile();
        if (runFileCandidate != null) {
            runFileField.setText(runFileCandidate.getName());
        }
        runFileField.setColumns(15);
        runFileField.getDocument().addDocumentListener(new DocumentListener() {
            public void updated() {
                if (runFileCandidate != null) {
                    runFileCandidate = new File(runFileCandidate.getParent(), runFileField.getText());
                } else {
                    runFileCandidate = new File(runFileField.getText());
                }
            }

            public void changedUpdate(DocumentEvent e) {
                updated();
            };
            public void insertUpdate(DocumentEvent e) {
                updated();
            };
            public void removeUpdate(DocumentEvent e) {
                updated();
            };
        });
        JButton runFileButton = new JButton() {
            /**
             * 
             */
            private static final long serialVersionUID = 5465170239793211349L;

            public Dimension getPreferredSize() {
                int size = runFileField.getPreferredSize().height + 10;
                return new Dimension(size, size);
            }
        };
        runFileButton.addActionListener(new ScapeControlAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -130652294726718684L;

            public void actionPerformed(ActionEvent e) {
                System.out.println(runFileCandidate);
                JFileChooser fileChooser = new JFileChooser();
                //Will use parent file
                fileChooser.setCurrentDirectory(runFileCandidate);
                fileChooser.setSelectedFile(runFileCandidate);
                if (fileChooser.showSaveDialog(BatchView.this) == JFileChooser.APPROVE_OPTION) {
                    //This was neccessary because
                    System.out.println(fileChooser.getCurrentDirectory() + " " + fileChooser.getSelectedFile() + " " + fileChooser.getSelectedFile().getPath() + " " + fileChooser.getSelectedFile().getName());
                    try {
                        runFileCandidate = new File(fileChooser.getSelectedFile().getCanonicalPath());
                    } catch (IOException err) {
                        System.out.println(err);
                    }
                    runFileField.setText(runFileCandidate.getName());
                    System.out.println(runFileCandidate);
                }
            }
        });
        runFileButton.setToolTipText("Choose Run File");
        runFileButton.setIcon(DesktopEnvironment.getIcon("Folder"));
        JButton clearRunFileButton = new JButton() {
            /**
             * 
             */
            private static final long serialVersionUID = -2447525984460908398L;

            public Dimension getPreferredSize() {
                int size = runFileField.getPreferredSize().height + 10;
                return new Dimension(size, size);
            }
        };
        clearRunFileButton.addActionListener(new ScapeControlAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -6165518468762587609L;

            public void actionPerformed(ActionEvent e) {
                runFileCandidate = null;
                runFileField.setText("");
            }
        });
        clearRunFileButton.setToolTipText("Choose Run File");
        clearRunFileButton.setIcon(DesktopEnvironment.getIcon("DeleteSheet"));
        runFilePanel.add(new JLabel("Run File:"));
        runFilePanel.add(runFileField);
        runFilePanel.add(runFileButton);
        runFilePanel.add(clearRunFileButton);
        sgbc.gridwidth = GridBagConstraints.REMAINDER;
        sgbc.gridy++;
        sgbc.fill = GridBagConstraints.HORIZONTAL;
        sgbc.weightx = 1.0;
        sgbc.weighty = 0.0;
        sweepSetupPanel.add(runFilePanel, sgbc);


        JPanel periodFilePanel = new JPanel();
        periodFilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        periodFileCandidate = dataView.getRunFile();
        if (periodFileCandidate != null) {
            periodFileField.setText(periodFileCandidate.getName());
        }
        periodFileField.setColumns(15);
        periodFileField.getDocument().addDocumentListener(new DocumentListener() {
            public void updated() {
                if (periodFileCandidate != null) {
                    periodFileCandidate = new File(periodFileCandidate.getParent(), periodFileField.getText());
                } else {
                    periodFileCandidate = new File(periodFileField.getText());
                }
            }

            public void changedUpdate(DocumentEvent e) {
                updated();
            };
            public void insertUpdate(DocumentEvent e) {
                updated();
            };
            public void removeUpdate(DocumentEvent e) {
                updated();
            };
        });
        JButton periodFileButton = new JButton() {
            /**
             * 
             */
            private static final long serialVersionUID = 4409725700105528718L;

            public Dimension getPreferredSize() {
                int size = periodFileField.getPreferredSize().height + 10;
                return new Dimension(size, size);
            }
        };
        periodFileButton.addActionListener(new ScapeControlAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -547816975971961987L;

            public void actionPerformed(ActionEvent e) {
                System.out.println(periodFileCandidate);
                JFileChooser fileChooser = new JFileChooser();
                //Will use parent file
                fileChooser.setCurrentDirectory(periodFileCandidate);
                fileChooser.setSelectedFile(periodFileCandidate);
                if (fileChooser.showSaveDialog(BatchView.this) == JFileChooser.APPROVE_OPTION) {
                    //This was neccessary because
                    System.out.println(fileChooser.getCurrentDirectory() + " " + fileChooser.getSelectedFile() + " " + fileChooser.getSelectedFile().getPath() + " " + fileChooser.getSelectedFile().getName());
                    try {
                        periodFileCandidate = new File(fileChooser.getSelectedFile().getCanonicalPath());
                    } catch (IOException err) {
                        System.out.println(err);
                    }
                    periodFileField.setText(periodFileCandidate.getName());
                    System.out.println(periodFileCandidate);
                }
            }
        });
        periodFileButton.setToolTipText("Choose Run File");
        periodFileButton.setIcon(DesktopEnvironment.getIcon("Folder"));
        JButton clearPeriodFileButton = new JButton() {
            /**
             * 
             */
            private static final long serialVersionUID = -6180626721687218456L;

            public Dimension getPreferredSize() {
                int size = periodFileField.getPreferredSize().height + 10;
                return new Dimension(size, size);
            }
        };
        clearPeriodFileButton.addActionListener(new ScapeControlAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 1922019701775676799L;

            public void actionPerformed(ActionEvent e) {
                periodFileCandidate = null;
                periodFileField.setText("");
            }
        });
        clearPeriodFileButton.setToolTipText("Choose Run File");
        clearPeriodFileButton.setIcon(DesktopEnvironment.getIcon("DeleteSheet"));
        periodFilePanel.add(new JLabel("Run File:"));
        periodFilePanel.add(periodFileField);
        periodFilePanel.add(periodFileButton);
        periodFilePanel.add(clearRunFileButton);
        sgbc.gridwidth = GridBagConstraints.REMAINDER;
        sgbc.gridy++;
        sgbc.fill = GridBagConstraints.HORIZONTAL;
        sgbc.weightx = 1.0;
        sgbc.weighty = 0.0;
        sweepSetupPanel.add(periodFilePanel, sgbc);

        sweepHolderPanel.setLayout(new BorderLayout());
        sweepHolderPanel.add(BorderLayout.CENTER, sweepSetupPanel);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        contentPanel.add(sweepHolderPanel, gbc);

//Status and period labels
/*periodLabel = new JLabel() {
            public Dimension getPreferredSize() {return new Dimension(100, super.getPreferredSize().height);}
        };
        periodLabel.setHorizontalAlignment(SwingConstants.CENTER);
		if (!scape.isPaused()) {
            periodLabel.setForeground(Color.black);
        }
        else {
            periodLabel.setForeground(Color.gray);
        }
        periodLabel.setOpaque(true);
        //periodLabel.setBackground(Color.white);
        periodLabel.setBorder(new CompoundBorder(new EmptyBorder(2,2,2,2), new ShadowBorder(ShadowBorder.LOWERED)));
        //Pad it initially
        periodLabel.setText(scape.getPeriodDescription());

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        gbc.weighty = 0.0;
        gbc.gridy++;
        contentPanel.add(periodLabel, gbc);
        statusLabel = new JLabel() {
            public Dimension getPreferredSize() {return new Dimension(60, super.getPreferredSize().height);};
        };
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		if (!scape.isPaused()) {
            statusLabel.setForeground(Color.black);
        }
        else {
            statusLabel.setForeground(Color.gray);
        }
        statusLabel.setOpaque(true);
        //statusLabel.setBackground(Color.white);
        statusLabel.setBorder(new CompoundBorder(new EmptyBorder(2,2,2,2), new ShadowBorder(ShadowBorder.LOWERED)));
        //Pad it initially
        statusLabel.setText("Running");
        gbc.gridx++;
        contentPanel.add(statusLabel, gbc);*/

/*
         *Toolbar
         */
        toolBar = new JToolBar();
        ToolTipManager.sharedInstance().setInitialDelay(50);
        startStopAction = new ScapeControlAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 2573638775694459860L;

            public void actionPerformed(ActionEvent e) {
                if (!scapeAppearsRunning) {
                    try {
                        dataView.setRunFile(periodFileCandidate);
                        scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_START));
                    } catch (IOException e2) {
                        JOptionPane.showMessageDialog(null, "An IO Exception occurred. Check File Name: " + e2, "File Exception", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
//Start restart sequence by stopping and setting restart flag
                    scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_STOP));
                }
            }
        };
        startStopButton = toolBar.add(startStopAction);
        startStopButton.setToolTipText("Start Model");
        startStopButton.setIcon(DesktopEnvironment.getIcon("ClockGo"));

        pauseToggleAction = new ScapeControlAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -2678577358841435312L;

            public void actionPerformed(ActionEvent e) {
                if (!scapeAppearsPaused) {
                    scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_PAUSE));
                } else {
                    scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_RESUME));
                }
            }
        };
        pauseToggleButton = toolBar.add(pauseToggleAction);
        pauseToggleButton.setToolTipText("Pause Model");
        pauseToggleButton.setIcon(DesktopEnvironment.getIcon("RedFlag"));

        toolBar.addSeparator();

        openAction = new ScapeControlAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -7621049533313261247L;

            public void actionPerformed(ActionEvent e) {
                if (scape != null) {
                    scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_OPEN));
                }
            }
        };
        openButton = toolBar.add(openAction);
        openButton.setToolTipText("Open New Model");
        openButton.setIcon(DesktopEnvironment.getIcon("OpenArrow"));

        quitAction = new ScapeControlAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 4084017518199443791L;

            public void actionPerformed(ActionEvent e) {
                scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_QUIT));
            }
        };
        quitButton = toolBar.add(quitAction);
        quitButton.setToolTipText("Quit");
        quitButton.setIcon(DesktopEnvironment.getIcon("Delete"));

        toolBar.addSeparator();

        settingsAction = new ScapeControlAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -3257310071059420569L;

            public void actionPerformed(ActionEvent e) {
                getScape().addView((ScapeListener) (getScape().getUIEnvironment().getCustomizer()));
            }
        };
        settingsButton = toolBar.add(settingsAction);
        settingsButton.setToolTipText("Settings");
        settingsButton.setIcon(DesktopEnvironment.getIcon("World"));

        infoAction = new ScapeControlAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 3247427874416911674L;

            public void actionPerformed(ActionEvent e) {
                DesktopEnvironment.displayAboutDialog(scape);
            }
        };
        infoButton = toolBar.add(infoAction);
        infoButton.setToolTipText("About");
        infoButton.setIcon(DesktopEnvironment.getIcon("Inform"));

        toolBar.addSeparator();

//Looks cool, but uneccesary and will cause problems for this application..
        toolBar.setFloatable(false);
        gbc.gridy++;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(toolBar, gbc);

        setPreferredSize(new Dimension(340, 500));
        revalidate();
        repaint();
//validate();
/*addTSAction = new ScapeControlAction() {
            public void actionPerformed(ActionEvent e) {
                newChart(ChartView.TIME_SERIES);
            }
        };
        addTSButton = toolBar.add(addTSAction);
        addTSButton.setToolTipText("New Time Series");
        addTSButton.setIcon(UserEnvironment.getIcon("LineGraph"));

        addHistAction = new ScapeControlAction() {
            public void actionPerformed(ActionEvent e) {
                newChart(ChartView.HISTOGRAM);
            }
        };
        addHistButton = toolBar.add(addHistAction);
        addHistButton.setToolTipText("New Histogram");
        addHistButton.setIcon(UserEnvironment.getIcon("BarGraph"));

        addPieAction = new ScapeControlAction() {
            public void actionPerformed(ActionEvent e) {
                newChart(ChartView.PIE);
            }
        };
        addPieButton = toolBar.add(addPieAction);
        addPieButton.setToolTipText("New Pie Chart");
        addPieButton.setIcon(UserEnvironment.getIcon("PieGraph"));*/
    }

    /**
     * Update the components. Ensures that the state of all buttons matchhes the
     * state of the observed UserEnvironment.
     */
    public synchronized void updateScapeGraphics() {
        //periodLabel.setText(getScape().getPeriodDescription());
        scapeAppearsRunning = getScape().isRunning();
        scapeAppearsPaused = getScape().isPaused();
        if (scapeAppearsRunning && !lastScapeAppearsRunning) {
            startStopButton.setToolTipText("Stop Model");
            startStopButton.setIcon(DesktopEnvironment.getIcon("ClockStop"));
            pauseToggleAction.setEnabled(true);
            statusLabel.setText("Running");
        }
        if (!scapeAppearsRunning && lastScapeAppearsRunning) {
            startStopButton.setToolTipText("Start Model");
            startStopButton.setIcon(DesktopEnvironment.getIcon("ClockGo"));
            pauseToggleAction.setEnabled(false);
            statusLabel.setText("Stopped");
        }
        if (scapeAppearsPaused && !lastScapeAppearsPaused) {
            pauseToggleButton.setToolTipText("Resume Model");
            pauseToggleButton.setIcon(DesktopEnvironment.getIcon("GreenFlag"));
            statusLabel.setText("Paused");
        }
        if (!scapeAppearsPaused && lastScapeAppearsPaused) {
            pauseToggleButton.setToolTipText("Pause Model");
            pauseToggleButton.setIcon(DesktopEnvironment.getIcon("RedFlag"));
            statusLabel.setText("Running");
        }
        //Trap always to catch the case where the step button breifly sets the label to black
        if (scapeAppearsPaused) {
            //periodLabel.setForeground(Color.gray);
            statusLabel.setForeground(Color.gray);
            statusLabel.setText("Paused");
        }
        if ((!scapeAppearsPaused && lastScapeAppearsPaused) || (!scapeAppearsRunning && lastScapeAppearsRunning)) {
            //periodLabel.setForeground(Color.black);
            statusLabel.setForeground(Color.black);
        }
        lastScapeAppearsRunning = scapeAppearsRunning;
        lastScapeAppearsPaused = scapeAppearsPaused;
    }

    /**
     * Gets the sweep view.
     * 
     * @return the sweep view
     */
    public SweepControlView getSweepView() {
        return sweepView;
    }

    /**
     * Gets the data view.
     * 
     * @return the data view
     */
    public DataOutputView getDataView() {
        return dataView;
    }

    /**
     * Notifies the view that the scape has added it.
     * 
     * @param scapeEvent
     *            the scape added notification event
     * @throws TooManyListenersException
     *             the too many listeners exception
     * @exception TooManyListenersException
     *                on attempt to add this listener to another scape when one
     *                has already been assigned
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        super.scapeAdded(scapeEvent);
        //Frame case out for 1.1. applet
        if (getViewFrame() != null) {
            getViewFrame().setTitle("Batch Settings");
        }
        ((Scape) scapeEvent.getSource()).setStartOnOpen(false);
        ((Scape) scapeEvent.getSource()).addView(sweepView);
        ((Scape) scapeEvent.getSource()).addView(dataView);
        //build();
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#toString()
     */
    public String toString() {
        return "Batch View";
    }
}

/**
 * A class representing actions that generate scape control events.
 */
abstract class ScapeControlAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = -3576701771298860547L;

    public ScapeControlAction() {
        super("", null);
    }
}

