package main.java.com.ubo.tp.message.ihm.interfaces;

import java.util.Set;
import main.java.com.ubo.tp.message.datamodel.User;

public interface IUserListView {
    void updateUserList(Set<User> users);

    /**
     * Désélectionne l'élément actuellement sélectionné dans la liste.
     */
    void clearSelection();
}
