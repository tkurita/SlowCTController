
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tkurita
 */
public class DspOutData {
    private long updatedTime = -1;
    private double bsCount;
    private double charge1;
    private double charge2;
    private double current2;
    private double current1;
    private double particles1;
    private double particles2;
    private double timming1;
    private double timming2;



    public void setTimming1(double t) {
        timming1 = t;
    }

    public double getTimming1() {
        return timming1;
    }

    public void setTimming2(double t) {
        timming2 = t;
    }

    public double getTimming2() {
        return timming2;
    }

    public void setTimmings(double t1, double t2) {
        setTimming1(t1);
        setTimming2(t2);
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setCurrent1(String aValue) {
        current1 = Double.parseDouble(aValue);
	}

    public double getCurrent1() {
        return current1;
    }

	public void setCurrent2(String aValue) {
        current2 = Double.parseDouble(aValue);
	}

    public double getCurrent2() {
        return current2;
    }

    public void setCharge1(String aValue) {
		charge1 = Double.parseDouble(aValue);
	}

    public double getCharge1() {
        return charge1;
    }

    public void setCharge2(String aValue) {
        charge2 = Double.parseDouble(aValue);
    }

    public double getCharge2() {
        return charge2;
    }

	public void setParticles1(String aValue) {
		particles1 = Double.parseDouble(aValue);
	}

    public double getParticles1() {
        return particles1;
    }

	public void setParticles2(String aValue) {
		particles2 = Double.parseDouble(aValue);
	}

    public double getParticles2() {
        return particles2;
    }
	public void setBSCount(String aValue) {
		bsCount = Double.parseDouble(aValue);
	}

    public double getBSCount() {
        return bsCount;
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

    public DspOutData() {
       
    }
    
	public boolean readData(File dspoutFile) {
        long t = dspoutFile.lastModified();
        if (updatedTime >= t) {
            return false;
        }

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
        updatedTime = t;
		return result;
	}
}
