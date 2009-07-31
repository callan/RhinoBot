/*
 * SwingApplication.js - a translation into JavaScript of
 * SwingApplication.java, a java.sun.com Swing example.
 *
 * @author Roger E Critchlow, Jr.
 */

var swingNames = JavaImporter();

swingNames.importPackage(Packages.javax.swing);
swingNames.importPackage(Packages.java.awt);
swingNames.importPackage(Packages.java.awt.event);

function createComponents () 
{
	var crypt = new Crypto();

    with (swingNames) {
		
		var plainText  = new JTextField(15);
		var cipherText = new JTextField(15);
		var passPhrase = new JTextField(15);
		
		plainText.setFont(new Font("Courier New", 0, 12));
		cipherText.setFont(new Font("Courier New", 0, 12));
		passPhrase.setFont(new Font("Courier New", 0, 12));
		
		var encryptButton = new JButton("Encrypt");
		var decryptButton = new JButton("Decrypt");
		
		var plainLabel = new JLabel("Plaintext: ");
		var cipherLabel = new JLabel("Ciphertext: ");
		var passLabel = new JLabel("Passphrase: ");
		
		plainLabel.setLabelFor(plainText);
		cipherLabel.setLabelFor(cipherText);
		passLabel.setLabelFor(passPhrase);
		
		encryptButton.addActionListener( function() {
			// Use Crypto to create 
			
			var key = crypt.genKey(passPhrase.getText(), crypt.AES_KEYSIZE, false);
			
			var enc = crypt.AESEncrypt(plainText.getText(), key);
			
			cipherText.setText(crypt.Base64Encode(enc));
			
		} );
		
		decryptButton.addActionListener( function() {
			
			var key = crypt.genKey(passPhrase.getText(), crypt.AES_KEYSIZE, false);
			
			var dec = crypt.AESDecrypt(cipherText.getText(), key);
			
			plainText.setText(crypt.getString(dec));
		
		} );
		

        /*
         * An easy way to put space between a top-level container
         * and its contents is to put the contents in a JPanel
         * that has an "empty" border.
         */
        var pane = new JPanel();
        pane.border = BorderFactory.createEmptyBorder(30, //top
                                                      30, //left
                                                      10, //bottom
                                                      30); //right
        pane.setLayout(new GridLayout(7, 6));
        
        pane.add(plainLabel);
		pane.add(plainText);
		pane.add(passLabel);
		pane.add(passPhrase);
		pane.add(cipherLabel);
		pane.add(cipherText);
		pane.add(encryptButton);
		pane.add(decryptButton);

        return pane;
    }
}

function initSwing (nick, bot)
{
	bot.notice(nick, "Initializing Swing Interface... ");
	
	with (swingNames) {
		try {
		UIManager.
				setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (e) { }
	
		//Create the top-level container and add contents to it.
		var frame = new swingNames.JFrame("AES Encrypter");
		frame.getContentPane().add(createComponents(), BorderLayout.CENTER);
	
		// Pass JS function as implementation of WindowListener. It is allowed since 
		// all methods in WindowListener have the same signature. To distinguish 
		// between methods Rhino passes to JS function the name of corresponding 
		// method as the last argument  
		frame.addWindowListener(function(event, methodName) {
		if (methodName == "windowClosing") {     
				bot.notice(nick, "Window closed!");
				swingNames = null;
				frame	   = null;
		}
		});
	
		//Finish setting up the frame, and show it.
		frame.pack();
		frame.setVisible(true);
		
	}

}



