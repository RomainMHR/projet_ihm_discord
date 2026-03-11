package main.java.com.ubo.tp.message.ihm.interfaces;

import main.java.com.ubo.tp.message.datamodel.User;

public interface IMessageApp {
    void showLoginView();

    void showRegisterView();

    void showMainView();

    void notifyLogin(User user);

    void notifyLogout();

    void showErrorMessage(String message);

    void showInformationMessage(String message);

    void triggerEasterEgg(String eggType);
}
