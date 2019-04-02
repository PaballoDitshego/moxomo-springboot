package za.co.moxomo.enums;

public enum AlertRoute {
	FCM("fcm"), SMS("sms");

	private String route;

	private AlertRoute(String route) {
		this.route = route;
	}

	public String getRoute() {
		return route;
	}

	@Override
	public String toString() {
		return getRoute();
	}

}
