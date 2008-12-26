import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import java.text.*;

class SettingWindow extends JFrame {
	DecimalFormat df = new DecimalFormat("#,###");
	NumberFormat nf = NumberFormat.getNumberInstance();
	NumberFormat inf = NumberFormat.getIntegerInstance();
	JFormattedTextField currentOutFactorField = new JFormattedTextField(nf);
    JFormattedTextField particleOutFactorField = new JFormattedTextField(nf);
    JFormattedTextField harmonicsField = new JFormattedTextField(inf);
    JFormattedTextField currentFactorField = new JFormattedTextField(nf);
    JFormattedTextField particleFactorField = new JFormattedTextField(nf);
    JFormattedTextField chargeField = new JFormattedTextField(inf);
    JFormattedTextField timming1Field = new JFormattedTextField(nf);
    JFormattedTextField timming2Field = new JFormattedTextField(nf);
	JButton import_but = new JButton("読み込み");
	JButton save_but = new JButton("保存");
	JButton saveas_but = new JButton("別名で保存...");
	File settingsSourceFile = null;
	JLabel settingsFileField = new JLabel();
	File settingsCurrentFile = new File("settings.txt");
	String filename = "settings.txt";
	
	double currentOutFactor = 1;
	double particleOutFactor = 1;
	int harmonics = 1;
	double currentFactor = 1;
	double particleFactor = 1;
	int charge = 1;
	float timming1 = 10;
	float timming2 = 600;
    
    private static SettingWindow instance = new SettingWindow("設定");
    
    private SettingWindow(String title) {
        super(title);

		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setBackground(Color.white);
		//getContentPane().setLayout(new GridBagLayout());
		getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel pathpanel = new JPanel(new FlowLayout());
		pathpanel.add(new JLabel("設定ファイル : "));
		pathpanel.add(settingsFileField);
		getContentPane().add(pathpanel);
		
		GridBagLayout gbl_fields = new GridBagLayout();
		JPanel fpanel = new JPanel(gbl_fields);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        String lblarray[] = {
            "電流 OUT 校正係数",
            "粒子数 OUT 校正係数",
            "Harmonics",
            "電流 校正係数",
            "粒子数 校正係数",
            "価数",
            "タイミング 1",
            "タイミング 2"
        };
        
        JTextField fieldarray[] = {
            currentOutFactorField,
            particleOutFactorField,
            harmonicsField,
            currentFactorField,
            particleFactorField,
            chargeField,
            timming1Field,
            timming2Field
        };
        TextFieldListener tlistner = new TextFieldListener();
        for (int i=0; i<lblarray.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.gridwidth = 1;
			fpanel.add(new JLabel(lblarray[i], Label.RIGHT), gbc);
            gbc.gridx = 1;
			gbc.gridwidth = 2;
            fieldarray[i].setHorizontalAlignment(JTextField.RIGHT);
			fieldarray[i].getDocument().addDocumentListener(tlistner);
			fpanel.add(fieldarray[i], gbc);
        }
		getContentPane().add(fpanel);
		/*
		gbc.gridx = 1;
		gbc.gridy++;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		*/
		JPanel buttonpanel = new JPanel();
		//buttonpanel.setBackground(Color.red);
		//buttonpanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		BoxLayout bxl = new BoxLayout(buttonpanel, BoxLayout.X_AXIS);
		buttonpanel.setLayout(bxl);
		//but.setPreferredSize(new Dimension(150, but.getPreferredSize().height));
		//but.setAlignmentX(Component.RIGHT_ALIGNMENT);
		//but.setAlignmentX(1.0f);
		//getContentPane().add(but, gbc);
		buttonpanel.add(import_but);
		import_but.addActionListener(new ReadAction());
		buttonpanel.add(save_but);
		save_but.addActionListener(new SaveAction());
		buttonpanel.add(saveas_but);
		saveas_but.addActionListener(new SaveAsAction());
		getContentPane().add(buttonpanel);
		readData();
		save_but.setEnabled(false);
    }
    
	class TextFieldAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			save_but.setEnabled(true);
		}
	}
	
	class TextFieldListener implements DocumentListener {
		public void changedUpdate(DocumentEvent de){ updates(de); }
		public void insertUpdate(DocumentEvent de){ updates(de); }
		public void removeUpdate(DocumentEvent de){ updates(de); }
		
		public void updates(DocumentEvent de) {
			save_but.setEnabled(true);
		}
	}
	
	public void setSettingsSource(File file) {
		settingsSourceFile = file;
		settingsFileField.setText(file.getAbsolutePath());
	}
	
	class SaveAsAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser filechooser = new JFileChooser();
			int selected = filechooser.showSaveDialog(((JComponent)e.getSource()).getTopLevelAncestor());
			if (selected == JFileChooser.APPROVE_OPTION){
				setSettingsSource(filechooser.getSelectedFile());
				saveData();
			}else if (selected == JFileChooser.CANCEL_OPTION){
				System.out.println("キャンセルされました");
			}else if (selected == JFileChooser.ERROR_OPTION){
				System.out.println("エラー又は取消しがありました");
			}
			save_but.setEnabled(false);
		}
	}
	
	class SaveAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			saveData();
			save_but.setEnabled(false);
		}
	}

	class ReadAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser filechooser = new JFileChooser();
			int selected = filechooser.showOpenDialog(((JComponent)e.getSource()).getTopLevelAncestor());
			if (selected == JFileChooser.APPROVE_OPTION){
				setSettingsSource(filechooser.getSelectedFile());
				readDataFromSettingsSource();
			}else if (selected == JFileChooser.CANCEL_OPTION){
				System.out.println("キャンセルされました");
			}else if (selected == JFileChooser.ERROR_OPTION){
				System.out.println("エラー又は取消しがありました");
			}
			save_but.setEnabled(false);
		}
	}
	
	public boolean readDataFromSettingsSource() {
		BufferedReader br;
		boolean result = false;
		try {
			br = new BufferedReader(new FileReader(settingsSourceFile));
		} catch (FileNotFoundException e) {
			System.out.println(e);
			return false;
		}
		result = writeToCurrentSettings(br);
		if (!result) return result;
		result = setDataWithReader(br);
		if (!result) return result;
		try {
			br.close();
		} catch (IOException e) {
			System.out.println(e);
			result = false;
		}
		return result;
	}
	
	public void saveDataToFile(File fout) {
		PrintStream ps;
		try {
			ps = new PrintStream(fout);
		} catch (FileNotFoundException e) {
			System.out.println(e);
			return;
		}
		ps.format("%f      %s\n", currentOutFactor, "# current out factor");
		ps.printf("%f      %s\n", particleOutFactor, "# particles out factor");
		ps.printf("%d      %s\n", harmonics, "# harmonics");
		ps.printf("%f      %s\n", currentFactor, "# current factor");
		ps.printf("%f      %s\n", particleFactor, "# particle factor");
		ps.printf("%d      %s\n", charge, "# charge");
		ps.printf("%f      %s\n", timming1, "# timming 1");
		ps.printf("%f      %s\n", timming2, "# timming 2");
		ps.close();
	}
	
	public void saveData() {
		saveDataToFile(settingsCurrentFile);
		if (settingsSourceFile != null) {
			saveDataToFile(settingsSourceFile);
		}
	}
	
    public static SettingWindow getInstance() {
        return instance;
    }
    
	private String parseLine(String aLine) {
		String numpart = aLine;
		int compos = aLine.indexOf("#");
		if (compos > -1) {
			numpart = aLine.substring(0, compos);
		}
		return numpart.trim();
	}
	
	abstract class ValueSetter {
		abstract void setValue(String aString);
	}
	
	public void setCurrentOutFactor(String aValue) {
		currentOutFactorField.setText(aValue);
		currentOutFactor = Double.parseDouble(aValue);
	}

	public void setParticleOutFactor(String aValue) {
		particleOutFactorField.setText(aValue);
		particleOutFactor = Double.parseDouble(aValue);
	}

	public void setHarmonics(String aValue) {
		harmonicsField.setText(aValue);
		harmonics = Integer.parseInt(aValue);
	}

	public void setParticleFactor(String aValue) {
		particleFactorField.setText(aValue);
		particleFactor = Double.parseDouble(aValue);
	}

	public void setCurrentFactor(String aValue) {
		currentFactorField.setText(aValue);
		currentFactor = Double.parseDouble(aValue);
	}

	public void setCharge(String aValue) {
		chargeField.setText(aValue);
		charge = Integer.parseInt(aValue);
	}

	public void setTimming1(String aValue) {
		timming1Field.setText(aValue);
		timming1 = Float.parseFloat(aValue);
	}

	public void setTimming2(String aValue) {
		timming2Field.setText(aValue);
		timming2 = Float.parseFloat(aValue);
	}
	
	private boolean setDataWithReader(BufferedReader reader) {
		ValueSetter setters[] = {
			new ValueSetter() {void setValue(String aString) {
			SettingWindow.this.setCurrentOutFactor(aString);}}, 
			new ValueSetter() {void setValue(String aString) {
			SettingWindow.this.setParticleOutFactor(aString);}},
			new ValueSetter() {void setValue(String aString) {
			SettingWindow.this.setHarmonics(aString);}},
			new ValueSetter() {void setValue(String aString) {
			SettingWindow.this.setCurrentFactor(aString);}},
			new ValueSetter() {void setValue(String aString) {
			SettingWindow.this.setParticleFactor(aString);}},
			new ValueSetter() {void setValue(String aString) {
			SettingWindow.this.setCharge(aString);}},
			new ValueSetter() {void setValue(String aString) {
			SettingWindow.this.setTimming1(aString);}},
			new ValueSetter() {void setValue(String aString) {
			SettingWindow.this.setTimming2(aString);}}
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
	
	private boolean writeToCurrentSettings(BufferedReader reader) {
		try {
			reader.mark(1024);
			BufferedWriter bw = new BufferedWriter(new FileWriter(settingsCurrentFile));
			String str;
			while (reader.ready()) {
				if ((str = reader.readLine()) == null) break;
				bw.write(str);
				bw.newLine();
			}
			bw.close();
			reader.reset();
		} catch (IOException e) {
			System.out.println(e);
			return false;
		}
		return true;
	}
	
	public boolean readData() {
		BufferedReader br;
		if (settingsCurrentFile.isFile() && settingsCurrentFile.canRead()) {
			try {
				br = new BufferedReader(new FileReader(settingsCurrentFile));
			} catch (FileNotFoundException e) {
				System.out.println(e);
				return false;
			}
		} else {
			InputStream fs=SettingWindow.class.getResourceAsStream("default-settings.txt");
			br = new BufferedReader(new InputStreamReader(fs));
			try {
				br.mark(1024);
				BufferedWriter bw = new BufferedWriter(new FileWriter(settingsCurrentFile));
				String str;
				while (br.ready()) {
					if ((str = br.readLine()) == null) break;
					bw.write(str);
					bw.newLine();
				}
				bw.close();
				br.reset();
			} catch (IOException e) {
				System.out.println(e);
				return false;
			}
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
	
    public boolean writeData() {
       return true;
    }
}