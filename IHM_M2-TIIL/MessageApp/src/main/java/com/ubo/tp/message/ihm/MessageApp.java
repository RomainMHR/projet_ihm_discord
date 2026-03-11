package main.java.com.ubo.tp.message.ihm;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.UIManager;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.login.LoginController;
import main.java.com.ubo.tp.message.ihm.login.LoginView;
import main.java.com.ubo.tp.message.ihm.login.RegisterController;
import main.java.com.ubo.tp.message.ihm.login.RegisterView;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.ihm.interfaces.IMessageApp;
import javax.swing.JOptionPane;

/**
 * Classe principale l'application.
 *
 * @author S.Lucas
 */
public class MessageApp implements ISessionObserver, IMessageApp {
	/**
	 * Base de données.
	 */
	protected DataManager mDataManager;

	/**
	 * Session.
	 */
	protected Session mSession;

	/**
	 * Vue principale de l'application.
	 */
	protected MessageAppMainView mMainView;

	/**
	 * Fenêtre pour le login/register.
	 */
	protected JFrame mLoginFrame;

	/**
	 * Constructeur.
	 *
	 * @param dataManager
	 */
	public MessageApp(DataManager dataManager) {
		this.mDataManager = dataManager;
		this.mSession = new Session();

		this.mSession.addObserver(this);
	}

	/**
	 * Initialisation de l'application.
	 */
	public void init() {
		// Init du look and feel de l'application
		this.initLookAndFeel();

		// Initialisation de l'IHM
		this.initGui();

		// Initialisation du répertoire d'échange
		this.initDirectory();
	}

	/**
	 * Initialisation du look and feel de l'application.
	 */
	protected void initLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialisation de l'interface graphique.
	 */
	protected void initGui() {
		ConsoleDatabaseObserver consoleObserver = new ConsoleDatabaseObserver();
		this.mDataManager.addObserver(consoleObserver);
	}

	/**
	 * Initialisation du répertoire d'échange (depuis la conf ou depuis un file
	 * chooser). <br/>
	 * <b>Le chemin doit obligatoirement avoir été saisi et être valide avant de
	 * pouvoir utiliser l'application</b>
	 */
	protected void initDirectory() {
		String exchangeDirectoryPath = null;
		String propertiesFilePath = "messageapp.properties";
		java.util.Properties properties = main.java.com.ubo.tp.message.common.PropertiesManager
				.loadProperties(propertiesFilePath);

		if (properties != null && properties
				.containsKey(main.java.com.ubo.tp.message.common.Constants.CONFIGURATION_KEY_EXCHANGE_DIRECTORY)) {
			exchangeDirectoryPath = properties
					.getProperty(main.java.com.ubo.tp.message.common.Constants.CONFIGURATION_KEY_EXCHANGE_DIRECTORY);
		}

		File exchangeDirectory = (exchangeDirectoryPath != null && !exchangeDirectoryPath.isEmpty())
				? new File(exchangeDirectoryPath)
				: null;

		if (!this.isValidExchangeDirectory(exchangeDirectory)) {
			javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
			fileChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setDialogTitle("Sélectionnez le répertoire d'échange");

			// Hack pour afficher le JFileChooser sans parent frame si mLoginFrame n'est pas
			// encore init
			JFrame parentFrame = (this.mMainView != null) ? this.mMainView.mFrame : null;

			int result = fileChooser.showOpenDialog(parentFrame);
			if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
				exchangeDirectory = fileChooser.getSelectedFile();
				exchangeDirectoryPath = exchangeDirectory.getAbsolutePath();

				// Sauvegarde de la configuration
				properties.setProperty(
						main.java.com.ubo.tp.message.common.Constants.CONFIGURATION_KEY_EXCHANGE_DIRECTORY,
						exchangeDirectoryPath);
				main.java.com.ubo.tp.message.common.PropertiesManager.writeProperties(properties, propertiesFilePath);
			} else {
				// L'utilisateur a annulé, on quitte l'application car le répertoire est
				// obligatoire
				System.exit(0);
			}
		}

		this.initDirectory(exchangeDirectoryPath);
	}

	/**
	 * Indique si le fichier donné est valide pour servir de répertoire d'échange
	 *
	 * @param directory , Répertoire à tester.
	 */
	protected boolean isValidExchangeDirectory(File directory) {
		// Valide si répertoire disponible en lecture et écriture
		return directory != null && directory.exists() && directory.isDirectory() && directory.canRead()
				&& directory.canWrite();
	}

	/**
	 * Initialisation du répertoire d'échange.
	 *
	 * @param directoryPath
	 */
	protected void initDirectory(String directoryPath) {
		mDataManager.setExchangeDirectory(directoryPath);
	}

	public void show() {
		// Au démarrage, on affiche la vue de login
		this.showLoginView();
	}

	public void showLoginView() {
		if (mLoginFrame == null) {
			mLoginFrame = new JFrame("Connexion");
			mLoginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mLoginFrame.setSize(400, 300);
			mLoginFrame.setLocationRelativeTo(null);
		}

		LoginController controller = new LoginController(this, mDataManager, mSession);
		LoginView loginView = new LoginView(controller);
		mLoginFrame.setContentPane(loginView);
		mLoginFrame.revalidate();
		mLoginFrame.repaint();
		mLoginFrame.setVisible(true);
	}

	public void showRegisterView() {
		if (mLoginFrame == null) {
			mLoginFrame = new JFrame("Inscription");
			mLoginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mLoginFrame.setSize(400, 400); // Un peu plus grand pour l'inscription
			mLoginFrame.setLocationRelativeTo(null);
		}

		RegisterController controller = new RegisterController(this, mDataManager);
		RegisterView registerView = new RegisterView(controller);
		mLoginFrame.setContentPane(registerView);
		mLoginFrame.revalidate();
		mLoginFrame.repaint();
		mLoginFrame.setVisible(true);
	}

	public void showMainView() {
		if (mLoginFrame != null) {
			mLoginFrame.dispose();
			mLoginFrame = null;
		}

		if (mMainView == null) {
			this.mMainView = new MessageAppMainView(this, mSession, mDataManager);
		}
		this.mMainView.show();
	}

	@Override
	public void notifyLogout() {
		System.out.println("Utilsateur déconnecté");
		if (mMainView != null) {
			mMainView.mFrame.dispose();
			mMainView = null;
		}
		this.showLoginView();
	}

	@Override
	public void notifyLogin(User user) {
		this.showMainView();
	}

	@Override
	public void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(mLoginFrame != null ? mLoginFrame : (mMainView != null ? mMainView.mFrame : null),
				message, "Erreur", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void showInformationMessage(String message) {
		javax.swing.JFrame parent = (mLoginFrame != null) ? mLoginFrame : (mMainView != null ? mMainView.mFrame : null);

		final javax.swing.JWindow toast = new javax.swing.JWindow(parent);
		toast.setBackground(new java.awt.Color(0, 0, 0, 0)); // Fond transparent

		javax.swing.JPanel panel = new javax.swing.JPanel() {
			@Override
			protected void paintComponent(java.awt.Graphics g) {
				java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
				g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
						java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new java.awt.Color(0, 0, 0, 200)); // Noir avec alpha (~80%)
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		panel.setOpaque(false);
		panel.setLayout(new java.awt.BorderLayout());
		panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 20, 15, 20));

		javax.swing.JLabel label = new javax.swing.JLabel(message);
		label.setForeground(java.awt.Color.WHITE);
		label.setFont(label.getFont().deriveFont(java.awt.Font.BOLD, 14f));
		panel.add(label, java.awt.BorderLayout.CENTER);

		toast.add(panel);
		toast.pack();

		// Positionnement (en bas, centré par rapport à la fenêtre parente ou l'écran)
		if (parent != null && parent.isVisible()) {
			java.awt.Point parentLoc = parent.getLocationOnScreen();
			java.awt.Dimension parentSize = parent.getSize();
			int x = parentLoc.x + (parentSize.width - toast.getWidth()) / 2;
			int y = parentLoc.y + parentSize.height - toast.getHeight() - 50;
			toast.setLocation(x, y);
		} else {
			java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			int x = (screenSize.width - toast.getWidth()) / 2;
			int y = screenSize.height - toast.getHeight() - 100;
			toast.setLocation(x, y);
		}

		// Animation d'opacité
		toast.setOpacity(0.0f);
		toast.setVisible(true);

		// Timer pour l'apparition en fondu
		javax.swing.Timer fadeInTimer = new javax.swing.Timer(20, null);
		fadeInTimer.addActionListener(new java.awt.event.ActionListener() {
			float opacity = 0.0f;

			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				opacity += 0.05f;
				if (opacity >= 1.0f) {
					opacity = 1.0f;
					fadeInTimer.stop();
				}
				toast.setOpacity(opacity);
			}
		});
		fadeInTimer.start();

		// Timer pour planifier la disparition après 3 secondes
		javax.swing.Timer delayTimer = new javax.swing.Timer(3000, new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				// Timer pour la disparition en fondu
				javax.swing.Timer fadeOutTimer = new javax.swing.Timer(20, null);
				fadeOutTimer.addActionListener(new java.awt.event.ActionListener() {
					float opacity = 1.0f;

					@Override
					public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
						opacity -= 0.05f;
						if (opacity <= 0.0f) {
							opacity = 0.0f;
							fadeOutTimer.stop();
							toast.dispose();
						} else {
							toast.setOpacity(opacity);
						}
					}
				});
				fadeOutTimer.start();
			}
		});
		delayTimer.setRepeats(false);
		delayTimer.start();
	}

	@Override
	public void triggerEasterEgg(String eggType) {
		JFrame frame = (mMainView != null) ? mMainView.mFrame : mLoginFrame;
		if (frame == null) return;

		switch (eggType) {
			case "earthquake":
				java.awt.Point originalLocation = frame.getLocation();
				javax.swing.Timer timer = new javax.swing.Timer(50, new java.awt.event.ActionListener() {
					int count = 0;
					@Override
					public void actionPerformed(java.awt.event.ActionEvent e) {
						if (count >= 40) { // 2 secondes
							frame.setLocation(originalLocation);
							((javax.swing.Timer)e.getSource()).stop();
						} else {
							int xOffset = (int)(Math.random() * 20 - 10);
							int yOffset = (int)(Math.random() * 20 - 10);
							frame.setLocation(originalLocation.x + xOffset, originalLocation.y + yOffset);
							count++;
						}
					}
				});
				timer.start();
				break;
			case "party":
				showInformationMessage("🎉 PARTY TIME! 🎉");
				break;
			case "flip":
				showInformationMessage("🙃 FLIP! 🙃");
				break;
		}
	}
}
