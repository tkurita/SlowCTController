import javax.swing.*;

public class IntegerInputVerifier extends InputVerifier{
	public boolean verify(JComponent c) {
		boolean verified = false;
		JTextField textField = (JTextField)c;
		try{
			Integer.parseInt(textField.getText());
			verified = true;
		}catch(NumberFormatException e) {
			UIManager.getLookAndFeel().provideErrorFeedback(c);
			//Toolkit.getDefaultToolkit().beep();
		}
		return verified;
	}
}