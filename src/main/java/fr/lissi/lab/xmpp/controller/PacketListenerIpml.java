package fr.lissi.lab.xmpp.controller;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import com.google.gson.Gson;

import fr.lab.lissi.eco.EcoImpl;
import fr.lab.lissi.model.request.Method;
import fr.lab.lissi.model.request.Request;
import fr.lab.lissi.model.request.SubscriptionRequest;
import fr.lab.lissi.model.request.UnsubscriptionRequest;
import fr.lissi.lab.xmpp.XmppClient;

public class PacketListenerIpml implements PacketListener {

	XmppClient xmppClient;
	private EcoImpl eco;

	public PacketListenerIpml(XmppClient xmppClient) {
		this.xmppClient = xmppClient;
	}

	@Override
	public void processPacket(Packet packet) throws NotConnectedException {

		if (packet instanceof Message) {
			Message message = (Message) packet;
			String fromName = StringUtils.parseBareAddress(message.getFrom());
			System.out.println("PacketListenerIpml##processPacket/message.getType()->"
					+ message.getType());
			System.out.println("Got text [" + message.getBody() + "] from [" + fromName + "]");

			if (message.getBody() == null) {
				// TODO for testing with gmail/pidgin, i consider that the null message is not an
				// error
				xmppClient.sendMessage(fromName, "204", Message.Type.error);
				return;
			}

			// verify if the json structure is valid
			Gson gson = new Gson();
			Request request;
			try {
				request = gson.fromJson(message.getBody(), Request.class);
				request.setSourceAddress(fromName);
			} catch (com.google.gson.JsonSyntaxException e) {
				System.err.println("ERROR : " + e.getMessage());
				xmppClient.sendMessage(fromName, "400", Message.Type.error);
				return;
			}

			// TODO verify if all fields are OK(not null or empty and are correct) else bad
			// request
			if (!request.isValid()) {
				System.err.println("ERROR : " + "The body request is not well structured.");
				xmppClient.sendMessage(fromName, "400", Message.Type.error);
				return;
			}

			String response = "Unknown request method : " + request.getMethod();
			if (request.getMethod() == Method.SUBSCRIPTION) {
				SubscriptionRequest subscriptionRequest = gson.fromJson(message.getBody(),
						SubscriptionRequest.class);
				subscriptionRequest.setSourceAddress(fromName);
				if(!subscriptionRequest.isValid()) {
					System.err.println("ERROR : " + "The body request is not well structured.");
					xmppClient.sendMessage(fromName, "400", Message.Type.error);
					return;
				}
				if (eco == null)
					eco = new EcoImpl();
				response = eco.doSubscription(subscriptionRequest);
			}
			else if (request.getMethod() == Method.UNSUBSCRIPTION) {
				UnsubscriptionRequest unsubscriptionRequest = gson.fromJson(message.getBody(),
						UnsubscriptionRequest.class);
				unsubscriptionRequest.setSourceAddress(fromName);
				if(!unsubscriptionRequest.isValid()) {
					System.err.println("ERROR : " + "The body request is not well structured.");
					xmppClient.sendMessage(fromName, "400", Message.Type.error);
					return;
				}
				if (eco == null)
					eco = new EcoImpl();
				response = eco.doUnsubscription(unsubscriptionRequest);
			}
			
			if (response.equals("OK"))
				xmppClient.sendMessage(fromName, "200", Message.Type.normal);
			else
				xmppClient.sendMessage(fromName, response, Message.Type.error);

			// xmppClient.getConnection().disconnect();
		} else if (packet instanceof Presence) {
			Presence presence = (Presence) packet;
			System.out.println("PacketListenerIpml##processPacket/presence.getType()->"
					+ presence.getType());
		}

	}
}
