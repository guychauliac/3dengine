package chabernac.opengl;

import java.util.ArrayList;
import java.util.List;

import chabernac.control.ISynchronizedEventManager;
import chabernac.control.iSynchronizedEvent;

public class SynchronizedEventManagerAdapter implements ISynchronizedEventManager {
	private final List<iSynchronizedEvent> listeners = new ArrayList<>();

	@Override
	public void addSyncronizedEventListener(iSynchronizedEvent anEventListener) {
		listeners.add(anEventListener);
	}

	@Override
	public void removeSyncronizedEventListener(iSynchronizedEvent anEventListener) {
		listeners.remove(anEventListener);
	}

	public void fireEvent(long aCounter) {
		for (iSynchronizedEvent listener : listeners) {
			listener.executeEvent(aCounter);
		}
	}

}
