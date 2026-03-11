package main.java.com.ubo.tp.message.core.session;

import java.util.ArrayList;
import java.util.List;

import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Session de l'application.
 *
 * @author S.Lucas
 */
public class Session implements ISession {

	/**
	 * Utilisateur connecté
	 */
	protected User mConnectedUser;

	/**
	 * Liste des observateurs de la session.
	 */
	protected List<ISessionObserver> mObservers = new ArrayList<>();

	@Override
	public void addObserver(ISessionObserver observer) {
		this.mObservers.add(observer);
	}

	@Override
	public void removeObserver(ISessionObserver observer) {
		this.mObservers.remove(observer);
	}

	@Override
	public User getConnectedUser() {
		return mConnectedUser;
	}

	@Override
	public void connect(User connectedUser) {
		this.mConnectedUser = connectedUser;

		List<ISessionObserver> copyOfObservers = new ArrayList<>(mObservers);
		for (ISessionObserver observer : copyOfObservers) {
			observer.notifyLogin(connectedUser);
		}
	}

	@Override
	public void disconnect() {
		this.mConnectedUser = null;

		List<ISessionObserver> copyOfObservers = new ArrayList<>(mObservers);
		for (ISessionObserver observer : copyOfObservers) {
			observer.notifyLogout();
		}
	}
}
