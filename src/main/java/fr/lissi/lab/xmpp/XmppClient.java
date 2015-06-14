package fr.lissi.lab.xmpp;

import java.io.IOException;
import java.util.Collection;

import javax.security.sasl.SaslException;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import fr.lissi.lab.xmpp.controller.PacketFilterImpl;
import fr.lissi.lab.xmpp.controller.PacketListenerIpml;
import fr.lissi.lab.xmpp.controller.RosterListnerImpl;

/**
 * 
 * @author Belili Fahem
 *
 */
public class XmppClient {

	private XMPPTCPConnection connection;

	/**
	 * Connecting to XMPP Server
	 * 
	 * @param host
	 *            Example :
	 * @param port
	 *            Example :
	 * @param serviceName
	 *            Example :
	 * @return true if
	 */
	public boolean connect(String host, int port, String serviceName) {
		// Create the configuration for this new connection_
		ConnectionConfiguration config = new ConnectionConfiguration(host, port, serviceName);

		connection = new XMPPTCPConnection(config);
		// Connect to the server_
		try {
			connection.connect();
			return true;
		} catch (SmackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public boolean login(String userName, String password) {
		// Log into the server_
		try {
			connection.login(userName, password);
			return true;
		} catch (SaslException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (SmackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public void setPresence(boolean presenceState) {
		// Create a new presence. Pass in false to indicate we're unavailable.

		Presence presence = new Presence(Presence.Type.available);
		presence.setStatus("I’m available");
		if (!presenceState) {
			presence.setType(Presence.Type.unavailable);
			presence.setStatus("I’m unavailable");
		}
		try {
			connection.sendPacket(presence);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Every user in a roster is represented by a RosterEntry.
	 * 
	 * The Roster class does the following :
	 * 
	 * Keeps track of the availability (presence) of other users Allows users to be organized into
	 * groups such as "Friends" and "Co-workers" Finds all roster entries and groups they belong to
	 * Retrieves the presence status of each user.
	 */
	public Collection<RosterEntry> getAllRooster() {
		// Get all rosters
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		return entries;
	}

	public boolean addBuddy(String email, String nickname, String group) {
		String[] groups = { group };
		Roster roster = connection.getRoster();

		if (roster.getEntry(email) == null) { // if already exist
			try {
				roster.createEntry(email, nickname, groups);
			} catch (NotLoggedInException | NoResponseException | XMPPErrorException
					| NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}

	public boolean removeBuddy(String email) {
		Roster roster = connection.getRoster();
		RosterEntry entry = roster.getEntry(email);
		if (entry == null) {
			System.err.println("Byddy '" + email + "' doesn't exist.");
			return false;
		}
		try {
			roster.removeEntry(entry);
			return true;
		} catch (NotLoggedInException | NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public void removeAllBuddies() {
		// TODO verify if there is another way to avoid the for iteration
		Roster roster = connection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();
		for (RosterEntry entry : entries) {
			removeBuddy(entry.getUser());
		}
	}

	public XMPPTCPConnection getConnection() {
		return connection;
	}

	public void printAllBuddiesInConsole() {
		System.out.println("List of buddies for " + connection.getUser() + " :");
		Collection<RosterEntry> entries = getAllRooster();
		for (RosterEntry entry : entries) {
			// example: get presence, type, mode, status
			Presence entryPresence = getConnection().getRoster().getPresence(entry.getUser());
			Presence.Type userType = entryPresence.getType();
			Presence.Mode mode = entryPresence.getMode();
			String status = entryPresence.getStatus();

			String group = "GroupUndefined";
			if (entry.getGroups().toArray().length > 0) {
				group = ((RosterGroup) entry.getGroups().toArray()[0]).getName();
			}
			System.out.println(entry.getUser() + ", Group:" + group + " / " + entryPresence);
		}
		System.out.println("\n\n");
	}

	public boolean isBuddyConnected(String eMail) {
		RosterEntry entry = connection.getRoster().getEntry(eMail);
		if (entry == null) {
			// TODO indicate the classe and the méthode and the line of this error or use LOG
			System.err.println("ERROR : The user " + eMail
					+ " is not in your buddies list. You cannot verify the presence.");
			return false;
		}
		Presence presence = connection.getRoster().getPresence(entry.getUser());
		return presence.isAway();
	}

	public void sendMessage(String toEmail, String msgBody, Message.Type type) {
		RosterEntry entry = connection.getRoster().getEntry(toEmail);
		if (entry == null) {
			// TODO indicate the classe and the méthode and the line of this error or use LOG
			System.out.println("ERROR : The user " + toEmail
					+ " is not in your buddies list. You cannot send him a message.");
			return;
		}
		Message msg = new Message(toEmail, type);
		msg.setBody(msgBody);
		System.out.println("sendMessage '"+msgBody+"' for "+toEmail);
		try {
			connection.sendPacket(msg);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addMessageListener(){
	}

	/**
	 * Roster.SubscriptionMode.manual
	 */
	public void setSubscriptionMode(Roster.SubscriptionMode mode) {
		connection.getRoster().setSubscriptionMode(mode);
	}

	public void addPacketListnerAndPacketFilter() {
		connection.addPacketListener(new PacketListenerIpml(this), new PacketFilterImpl());
	}

	public void addRosterListner() {
		connection.getRoster().addRosterListener(new RosterListnerImpl());
	}
	
}
