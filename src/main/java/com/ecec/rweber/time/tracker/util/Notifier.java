package com.ecec.rweber.time.tracker.util;

import java.awt.TrayIcon.MessageType;

public interface Notifier {

	public void onMessage(String message, MessageType level);
}
