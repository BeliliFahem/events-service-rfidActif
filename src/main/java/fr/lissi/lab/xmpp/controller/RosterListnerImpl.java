package fr.lissi.lab.xmpp.controller;

import java.util.Collection;

import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

public class RosterListnerImpl implements RosterListener {

	@Override
	public void presenceChanged(Presence presence) {
		// TODO Auto-generated method stub
		System.out.println("RosterListnerImpl##Presence changed: " + presence.getFrom() + " " + presence);
	}

	@Override
	public void entriesUpdated(Collection<String> entries) {
//		System.out.println("entries Updated from : ");
//		for (String s : entries) {
//			System.out.println(s);
//		}
	}

	@Override
	public void entriesDeleted(Collection<String> entries) {
		// TODO Auto-generated method stub
		System.out.println("entries Deleted From : ");
		for (String s : entries) {
			System.out.println(s);
		}
	}

	@Override
	public void entriesAdded(Collection<String> entries) {
		// TODO Auto-generated method stub
		System.out.println("entries Added From : ");
		for (String s : entries) {
			System.out.println(s);
		}
	}

}
