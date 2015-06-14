package fr.lissi.lab.xmpp.controller;

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

public class PacketFilterImpl implements PacketFilter {

	@Override
	public boolean accept(Packet packet) {
		if (packet instanceof Presence) {
			Presence presence = (Presence) packet;
			System.out.println("PacketFilterImpl##accept/presence.getType()->"
					+ presence.getType());
		}
		return true;
	}

}
