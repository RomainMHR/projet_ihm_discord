package main.java.com.ubo.tp.message.ihm;

import java.util.UUID;

import javax.swing.JOptionPane;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur pour l'écran d'inscription.
 */
public class RegisterController {

    protected DataManager mDataManager;
    protected MessageApp mMessageApp;
    protected RegisterView mView;

    public RegisterController(MessageApp messageApp, DataManager dataManager) {
        this.mMessageApp = messageApp;
        this.mDataManager = dataManager;
    }

    public void setView(RegisterView view) {
        this.mView = view;
    }

    public void register(String name, String tag, String password, String confirmPassword) {
        // SRS-MAP-USR-002 : Nom et Tag obligatoires
        if (name.isEmpty() || tag.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(mView, "Tous les champs sont obligatoires.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(mView, "Les mots de passe ne correspondent pas.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // SRS-MAP-USR-003 : Tag unique
        for (User user : mDataManager.getUsers()) {
            if (user.getUserTag().equals(tag)) {
                JOptionPane.showMessageDialog(mView, "Ce tag est déjà utilisé.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Création et sauvegarde de l'utilisateur
        User newUser = new User(UUID.randomUUID(), tag, password, name);
        mDataManager.sendUser(newUser);

        JOptionPane.showMessageDialog(mView, "Compte créé avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
        mMessageApp.showLoginView();
    }

    public void cancel() {
        mMessageApp.showLoginView();
    }
}
