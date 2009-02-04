import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;
import java.text.*;

class MonitorWindow extends Frame implements ActionListener{
    TextField dummyField = new TextField("");
    TextField chargeField1 = new TextField("charge 1");
    TextField chargeField2 = new TextField("charge 2");
    TextField particlesField1 = new TextField("particle 1");
    TextField particlesField2 = new TextField("particle 2");
    TextField currentField1 = new TextField("current 1");
    TextField currentField2 = new TextField("current 2");
    TextField bsCountField = new TextField("BS [count]");
	TextField bsnAField = new TextField("BS [nA]");
    TextField bsnCField = new TextField("BS [nC]");
    TextField accEfficiency = new TextField("acc efficiency");
    TextField extEfficiency = new TextField("ext efficiency");
    
	double current1;
	double current2;
	double charge1;
	double charge2;
	double particles1;
	double particles2;
	double bsCount;
	double bsnA;
	double bsnC;

    CheckboxGroup bsScaleCheckboxGroup = new CheckboxGroup();
    Checkbox bsScaleChboxes[] = {new Checkbox("100 pC/count"), 
                                 new Checkbox("1 pC/count")};
	File dspoutFile = new File("dspout.txt");
	
	private Timer timer = null;
	
    private Panel[] panelsForTimmingData() {
        GridBagLayout gridbags[] = {new GridBagLayout(), 
                                    new GridBagLayout()};
        GridBagConstraints constraints = new GridBagConstraints();
        Panel panelarray[] = {new Panel(gridbags[0]), 
                              new Panel(gridbags[1])};
        String lblarray[][] = {{"タイミング 1", "電流 [mA]", "電荷量 [nC]", "粒子数 [nA]"}, 
                            {"タイミング 2", "電流 [mA]", "電荷量 [nC]", "粒子数 [nA]"}};
        TextField tfieldarray[][] = {{dummyField,
                                    currentField1,
                                    chargeField1,
                                    particlesField1},
                                   {dummyField,
                                    currentField2,
                                    chargeField2,
                                    particlesField2}};
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        for (int j=0; j < 2; j++) {
            for (int i=0; i < lblarray[0].length; i++) {
                constraints.gridy = i;
                constraints.gridx = 0;
                panelarray[j].add(new Label(lblarray[j][i], Label.RIGHT), constraints);
                constraints.gridx = 1;
                panelarray[j].add(tfieldarray[j][i], constraints);
                tfieldarray[j][i].setEditable(false);
            }
        }
        return panelarray;
    }
    
    private Panel bsPanel() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        GridBagLayout gb3 = new GridBagLayout();
        Panel bspanel = new Panel(gb3);
        constraints.gridx = 0;
        Label bslbl = new Label("BS");
        constraints.gridy = 0;
        bspanel.add(bslbl, constraints);
		
		TextField tfields[] = {bsCountField, bsnAField, bsnCField};
		String unitlabels[] = {"[Count]", "[nA]", "[nC]"};
		for (int i = 0; i < tfields.length; i++) {
			constraints.gridy = i+1;
			constraints.gridx = 0;
			bspanel.add(tfields[i], constraints);
			constraints.gridx = 1;
			bspanel.add(new Label(unitlabels[i], Label.LEFT), constraints);
		}

        bsScaleChboxes[0].setCheckboxGroup(bsScaleCheckboxGroup);
        bsScaleChboxes[1].setCheckboxGroup(bsScaleCheckboxGroup);
        constraints.gridx = 0;
        constraints.gridy++;
        bspanel.add(bsScaleChboxes[0], constraints);
        constraints.gridx = 1;
        bspanel.add(bsScaleChboxes[1], constraints);
        return bspanel;
    }
    
    private Panel efficiencyPanel() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        Panel p = new Panel(gbl);
        Label lbl;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        lbl = (Label)p.add(new Label("加速効率", Label.RIGHT));
        gbl.setConstraints(lbl, gbc);
        
        gbc.gridx = 1;
		p.add(accEfficiency, gbc);
		
        gbc.gridx = 2;
        gbc.insets = new Insets(10, 0, 10, 30);
        lbl = (Label)p.add(new Label("[%]", Label.LEFT));
        gbl.setConstraints(lbl, gbc);
		
        gbc.gridx = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        lbl = (Label)p.add(new Label("出射効率", Label.RIGHT));
        gbl.setConstraints(lbl, gbc);
        
        gbc.gridx = 4;
		p.add(extEfficiency, gbc);
		
        lbl = (Label)p.add(new Label("[%]", Label.LEFT));
        gbc.gridx = 5;
        gbc.insets = new Insets(10, 0, 10, 30);
        gbl.setConstraints(lbl, gbc);
        return p;
    }
    
	abstract class ValueSetter {
		abstract void setValue(String aString);
	}

    public void setBSScaleMode(int mode) {
        bsScaleCheckboxGroup.setSelectedCheckbox(bsScaleChboxes[mode]);
    }

    private double getBSScale() {
        double result = 0.001; //1 [pC]
        if (bsScaleCheckboxGroup.getSelectedCheckbox().equals(bsScaleChboxes[0])) {
            result = 0.1; // 100 [pC]
        }
        return result;
    }
    
	public void setCurrent1(String aValue) {
		currentField1.setText(aValue);
		current1 = Double.parseDouble(aValue);
	}

	public void setCurrent2(String aValue) {
		currentField2.setText(aValue);
		current2 = Double.parseDouble(aValue);
	}

    public void setCharge1(String aValue) {
		chargeField1.setText(aValue);
		charge1 = Double.parseDouble(aValue);
	}

    public void setCharge2(String aValue) {
		chargeField2.setText(aValue);
		charge2 = Double.parseDouble(aValue);
    }

	public void setParticles1(String aValue) {
		particlesField1.setText(aValue);
		particles1 = Double.parseDouble(aValue);
	}

	public void setParticles2(String aValue) {
		particlesField2.setText(aValue);
		particles2 = Double.parseDouble(aValue);
	}

	public void setBSCount(String aValue) {
		bsCountField.setText(aValue);
		bsCount = Double.parseDouble(aValue);
        bsnC = bsCount*getBSScale();
        bsnCField.setText(Double.toString(bsnC));
        bsnA = bsnC/2;
        bsnAField.setText(Double.toString(bsnA));
	}
	
	private String parseLine(String aLine) {
		String numpart = aLine;
		int compos = aLine.indexOf("#");
		if (compos > -1) {
			numpart = aLine.substring(0, compos);
		}
		return numpart.trim();
	}
	
	private boolean setDataWithReader(BufferedReader reader) {
		ValueSetter setters[] = {
			new ValueSetter() {void setValue(String aString) {
								setCurrent1(aString);}}, 
			new ValueSetter() {void setValue(String aString) {
								setCharge1(aString);}},
            new ValueSetter() {void setValue(String aString) {
								setParticles1(aString);}},
			new ValueSetter() {void setValue(String aString) {
								setCurrent2(aString);}},
			new ValueSetter() {void setValue(String aString) {
								setCharge2(aString);}},
            new ValueSetter() {void setValue(String aString) {
								setParticles2(aString);}},
			new ValueSetter() {void setValue(String aString) {
								setBSCount(aString);}}
		};
		
		try{
			String str;
			for (int i = 0; i<setters.length; i++) {
				if ((str = reader.readLine()) == null) break;
				setters[i].setValue(parseLine(str));
			}
			reader.close();		
		} catch(IOException e) {
            System.out.println(e);
			return false;
		}
		return true;		
	}
	
	public boolean readData() {
		BufferedReader br;
		if (dspoutFile.canRead()) {
			try {
				br = new BufferedReader(new FileReader(dspoutFile));
			} catch (FileNotFoundException e) {
				System.out.println(e);
				return false;
			}
		} else {
			return false;
		}
		boolean result = setDataWithReader(br);
		try {
			br.close();
		} catch (IOException e) {
			System.out.println(e);
			result = false;
		}
		return result;
	}
	
	class DSPOutWatcher extends TimerTask {
		long lastUpdatedData = 0;
		public void run() {
			if (dspoutFile.isFile()) {
				long data = dspoutFile.lastModified();
				if (data < lastUpdatedData) {
					return;
				}
				lastUpdatedData = data;
			} else {
				return;
			}
			readData();
		}
	}
	
	private void setupTimerTask() {
		timer = new Timer(true);
		timer.schedule(new DSPOutWatcher(), 0, 1000);
	}
	
    public MonitorWindow(String title) {
        super(title);
        dummyField.setVisible(false);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                //e.getWindow().setVisible(false);
				e.getWindow().dispose();
            }
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}});
		
        setSize(700, 350);
        setBackground(Color.white);
        setLayout(new GridBagLayout());
        GridBagConstraints main_gbc = new GridBagConstraints();
        main_gbc.gridx = 0;
        main_gbc.gridy = 0;
        main_gbc.gridwidth = 1;
        main_gbc.gridheight = 5;
        main_gbc.insets = new Insets(20, 20, 10, 10);
        Panel tpanels[] = panelsForTimmingData();
        for (int i=0; i<tpanels.length; i++) {
            main_gbc.gridx = i;
            add(tpanels[i], main_gbc);
        }
        
        main_gbc.gridx = tpanels.length;
        Panel bsp = bsPanel();
        add(bsp, main_gbc);

        Panel effp = efficiencyPanel();
        main_gbc.gridx = 0;
        main_gbc.gridy = 6;
        main_gbc.gridwidth = 3;
        main_gbc.gridheight = 1;
        add(effp, main_gbc);
        
        main_gbc.gridx = 2;
        main_gbc.gridy = 7;
        main_gbc.gridwidth = 1;
        main_gbc.gridheight = 1;
        Button but = new Button("設定");
		add(but, main_gbc);
        but.addActionListener(this);
        
        MenuBar mb = new MenuBar();
        Menu emenu = mb.add(new Menu("編集"));
        MenuItem smi = emenu.add(new MenuItem("設定"));
        smi.addActionListener(this);
        setMenuBar(mb);
		setupTimerTask();
    }
    /*
    public Frame showSettingWindow() {
        SettingWindow win = SettingWindow.getInstance(this);
        win.setVisible(true);
        return win;
    }
     */

    public void actionPerformed(ActionEvent e) {
        //if (e.getActionCommand() == "設定")  showSettingWindow();
    }

}
