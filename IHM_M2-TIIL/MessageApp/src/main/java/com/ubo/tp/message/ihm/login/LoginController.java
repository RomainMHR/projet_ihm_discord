package main.java.com.ubo.tp.message.ihm.login;

import javax.swing.JOptionPane;

import main.java.com.ubo.tp.message.ihm.MessageApp;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur pour l'écran de connexion.
 */
public class LoginController {

    protected DataManager mDataManager;
    protected ISession mSession;
    protected MessageApp mMessageApp;
    protected LoginView mView;

    public LoginController(MessageApp messageApp, DataManager dataManager, ISession session) {
        this.mMessageApp = messageApp;
        this.mDataManager = dataManager;
        this.mSession = session;
    }

    public void setView(LoginView view) {
        this.mView = view;
    }

    public void login(String tag, String password) {
        if (tag.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(mView, "Veuillez remplir tous les champs.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Recherche de l'utilisateur
        User foundUser = null;
        for (User user : mDataManager.getUsers()) {
            if (user.getUserTag().equals(tag)) {
                foundUser = user;
                break;
            }
        }

        if (foundUser != null && foundUser.getUserPassword().equals(password)) {
            // SRS-MAP-USR-004 : Connexion
            mSession.connect(foundUser);
            JOptionPane.showMessageDialog(mView, "Connexion réussie ! Bienvenue " + foundUser.getName(), "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            mMessageApp.showMainView();
        } else {
            JOptionPane.showMessageDialog(mView, "Identifiant ou mot de passe incorrect.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void goToRegister() {
        mMessageApp.showRegisterView();
    }
}
