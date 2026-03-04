package main.java.com.ubo.tp.message.ihm.login;

import main.java.com.ubo.tp.message.ihm.interfaces.IMessageApp;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISession;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur pour l'écran de connexion.
 */
public class LoginController {

    protected DataManager mDataManager;
    protected ISession mSession;
    protected IMessageApp mMessageApp;

    public LoginController(IMessageApp messageApp, DataManager dataManager, ISession session) {
        this.mMessageApp = messageApp;
        this.mDataManager = dataManager;
        this.mSession = session;
    }

    public void login(String tag, String password) {
        if (tag.isEmpty() || password.isEmpty()) {
            mMessageApp.showErrorMessage("Veuillez remplir tous les champs.");
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
            mMessageApp.showInformationMessage("Connexion réussie ! Bienvenue " + foundUser.getName());
            mMessageApp.notifyLogin(foundUser);
        } else {
            mMessageApp.showErrorMessage("Identifiant ou mot de passe incorrect.");
        }
    }

    public void goToRegister() {
        mMessageApp.showRegisterView();
    }

}