package playhub;

import java.util.ArrayList;
import java.util.List;

public abstract class Observable {

	private final List<SessionObserver> observers = new ArrayList<>();

	public void attach(SessionObserver observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public void detach(SessionObserver observer) {
		observers.remove(observer);
	}

	public void notify(String message) {
		for (SessionObserver observer : observers) {
			observer.update(this, message);
		}
	}

	public List<SessionObserver> getObservers() {
		return new ArrayList<>(observers);
	}

}
