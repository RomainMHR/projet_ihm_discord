package main.java.com.ubo.tp.message.ihm.login;

import java.util.UUID;

import main.java.com.ubo.tp.message.ihm.interfaces.IMessageApp;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur pour l'écran d'inscription.
 */
public class RegisterController {

    protected DataManager mDataManager;
    protected IMessageApp mMessageApp;

    public RegisterController(IMessageApp messageApp, DataManager dataManager) {
        this.mMessageApp = messageApp;
        this.mDataManager = dataManager;
    }

    public void register(String name, String tag, String password, String confirmPassword) {
        // SRS-MAP-USR-002 : Nom et Tag obligatoires
        if (name.trim().isEmpty() || tag.trim().isEmpty() || password.trim().isEmpty()) {
            mMessageApp.showErrorMessage("Tous les champs sont obligatoires.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            mMessageApp.showErrorMessage("Les mots de passe ne correspondent pas.");
            return;
        }

        // SRS-MAP-USR-003 : Tag unique
        for (User user : mDataManager.getUsers()) {
            if (user.getUserTag().equals(tag)) {
                mMessageApp.showErrorMessage("Ce tag est déjà utilisé.");
                return;
            }
        }

        // Création et sauvegarde de l'utilisateur
        User newUser = new User(UUID.randomUUID(), tag, password, name);
        mDataManager.sendUser(newUser);

        mMessageApp.showInformationMessage("Compte créé avec succès !");
        mMessageApp.showLoginView();
    }

    public void cancel() {
        mMessageApp.showLoginView();
    }
}