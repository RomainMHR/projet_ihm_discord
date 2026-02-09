package main.java.com.ubo.tp.message.ihm;

import java.io.File;

import javax.swing.UIManager;

import main.java.com.ubo.tp.message.core.DataManager;

/**
 * Classe principale l'application.
 *
 * @author S.Lucas
 */
public class MessageApp {
	/**
	 * Base de données.
	 */
	protected DataManager mDataManager;

	/**
	 * Vue principale de l'application.
	 */
	protected MessageAppMainView mMainView;

	/**
	 * Constructeur.
	 *
	 * @param dataManager
	 */
	public MessageApp(DataManager dataManager) {
		this.mDataManager = dataManager;
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
		this.mMainView = new MessageAppMainView();
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

			int result = fileChooser.showOpenDialog(this.mMainView.mFrame);
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
		this.mMainView.show();
	}
}
