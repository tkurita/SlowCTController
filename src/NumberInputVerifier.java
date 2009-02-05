import javax.swing.*;

public class NumberInputVerifier extends InputVerifier{
	public boolean verify(JComponent c) {
		boolean verified = false;
		JTextField textField = (JTextField)c;
		try{
			Double.parseDouble(textField.getText());
			verified = true;
		}catch(NumberFormatException e) {
			UIManager.getLookAndFeel().provideErrorFeedback(c);
			//Toolkit.getDefaultToolkit().beep();
		}
		return verified;
	}
}