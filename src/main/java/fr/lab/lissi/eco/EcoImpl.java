package fr.lab.lissi.eco;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.http.HTTPException;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.packet.Message;

import fr.lab.lissi.general.Constants;
import fr.lab.lissi.model.device.rfid.UniqueDataIdentifier;
import fr.lab.lissi.model.request.Contact.Protocol;
import fr.lab.lissi.model.request.Frequency.Condition;
import fr.lab.lissi.model.request.SubscriptionRequest;
import fr.lab.lissi.model.request.UnsubscriptionRequest;
import fr.lab.lissi.model.response.ResponseData;
import fr.lissi.lab.xmpp.XmppClient;

public class EcoImpl extends EcoAbstract<String> implements Observer {

	/**
	 * HashMap<String, Request> String Key obtained by :
	 * request.getUniqueRequestId() Request {@link SubscriptionRequest}
	 */
	private static HashMap<UniqueDataIdentifier, HashSet<SubscriptionRequest>> subscriptions;
	private XmppClient xmppClient;
	private Client httpClient;
	private Properties props;

	public EcoImpl() {
		if (subscriptions == null) {
			subscriptions = new HashMap<UniqueDataIdentifier, HashSet<SubscriptionRequest>>();
		}

		props = new Properties();
		try {
			props.load(new FileReader("config.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "FileNotFoundException", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "IOException", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

		initXmppClient(props.getProperty("xmppHost"), Integer.parseInt(props.getProperty("xmppPort")),
				props.getProperty("xmppServiceName"), props.getProperty("xmppEMail"), props.getProperty("xmppPassword"));
	}

	private void initXmppClient(String host, int port, String serviceName, String eMail, String password) {
		if (xmppClient != null && xmppClient.getConnection().isConnected())
			return;

		xmppClient = new XmppClient();
		boolean statusOp = xmppClient.connect(host, port, serviceName);
		System.out.println("INFO : connect to '" + host + "/" + port + "/" + serviceName + "' status->" + statusOp);

		statusOp = xmppClient.login(eMail, password);
		System.out.println("INFO : login with '" + eMail + "/" + password + "' status->" + statusOp);

		xmppClient.setPresence(true);

		xmppClient.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

		xmppClient.addPacketListnerAndPacketFilter();

		xmppClient.addRosterListner();
	}

	@Override
	public String doSubscription(SubscriptionRequest request) {
		System.out.println("INFO : subscriptions invocked by " + request.getSourceAddress() + " with : ");
		System.out.println(request);
		/*
		 * TODO The management of exception is to do correctly
		 */
		if (!subscriptions.isEmpty() && subscriptions.get(request.getUniqueDataIdentifier()) != null
				&& subscriptions.get(request.getUniqueDataIdentifier()).contains(request)) {
			System.err.println("ERROR : subscription already done.");
			displayAllSubscriptions();
			return "subscription already done";
		} else {
			// if there is not any subscription for
			// request.getUniqueDataIdentifier() => init
			if (subscriptions.get(request.getUniqueDataIdentifier()) == null) {
				subscriptions.put(request.getUniqueDataIdentifier(), new HashSet<SubscriptionRequest>());
			}

			subscriptions.get(request.getUniqueDataIdentifier()).add(request);
			displayAllSubscriptions();

			System.out.println("INFO : subscriptions done.");
			return "OK";
		}
	}

	@Override
	public String doUnsubscription(UnsubscriptionRequest request) {
		System.out.println("INFO : unsubscriptions invocked by " + request.getSourceAddress() + " with : ");
		System.out.println(request);
		/*
		 * TODO The management of exception is to do correctly
		 */
		HashSet<SubscriptionRequest> requestList = subscriptions.get(request.getUniqueDataIdentifier());
		if (subscriptions.isEmpty() || requestList == null || requestList.isEmpty()) {
			System.err.println("ERROR : Unsubscription impossible. There is any subscription.");
			displayAllSubscriptions();
			return "subscription doesn't exist. There is any subscription.";
		}

		for (SubscriptionRequest subscriptionRequest : requestList) {
			if (subscriptionRequest.getUniqueRequestId().equals(request.getUniqueRequestId())) {
				requestList.remove(subscriptionRequest);
				System.out.println("INFO : unsubscription done.");
				displayAllSubscriptions();
				return "unsubscription done";
			}
		}

		// if we exit the iterator for => subscription doesn't exist (because
		// there is return inside for)
		System.err.println("ERROR : Unsubscription impossible because the subscription doesn't exist.");
		displayAllSubscriptions();
		return "subscription doesn't exist";
	}

	/**
	 * Notify all requests depending on :<br>
	 * 0. the device id <br>
	 * 1. the condition <br>
	 * 2. the frequency <br>
	 * 3. Notify depending on protocol
	 */
	@Override
	public void update(Observable observable, Object object) {
		System.out.println((this.getClass().getSimpleName() + "/update invoqued").toUpperCase());

		ResponseData data = (ResponseData) object;

		// in config file
		data.setDeviceAliasName(props.getProperty("deviceAliasName"));
		data.setDeviceLocation(props.getProperty("deviceLocation"));
		data.setDeviceID(props.getProperty("deviceId"));

		HashSet<SubscriptionRequest> requests;

		// we confirm that we recover a proper event
		if (data.getUniqueDataIdentifier() instanceof UniqueDataIdentifier) {
			// get the request list of data.getUniqueDataIdentifier()
			requests = subscriptions.get(data.getUniqueDataIdentifier());
		} else {
			// TODO Internal error
			System.err.println("ERROR : " + data.getUniqueDataIdentifier() + " is not instance of "
					+ UniqueDataIdentifier.class);
			return;
		}

		// we verify that we have a request for this event
		if (requests == null || requests.isEmpty()) {
			System.out.println("INFO : No requests for " + data.getUniqueDataIdentifier());
			return;
		}

		// Notify all requests depending on : device id, condition, protocol and
		// frequency.
		for (SubscriptionRequest r : requests) {
			System.out.println("INFO : Treat request " + r);

			/*
			 * verify : 0. the device id 1. the condition 2. the frequency 3.
			 * Notify depending on protocol
			 */

			/*
			 * TODO 0. the device id If the data is provided by a device that is
			 * not requested by r => see the rest of requests If r.getDeviceId()
			 * requested is none => don't care about deviceId
			 */
			if (!r.getDeviceId().equalsIgnoreCase("none") && !data.getDeviceID().equalsIgnoreCase(r.getDeviceId())) {
				System.out.println("INFO : The events for the device " + data.getDeviceID()
						+ " has not regestred request.");
				continue;
			}

			/*
			 * 1. treat the condition : 
			 * if Condition.EachNewValue =>  any thing to do depending this condition 
			 * if Condition.EachNewDifferentValue => do not send the same value 
			 * TODO until this moment, we manage only with one data => index is 0 => r.getDataList().get(0)
			 */
			if (!(r.getFrequency().getCondition() instanceof Condition)) {
				System.err.println("ERROR : " + r.getFrequency().getCondition() + " is not a well formatted condition");
				continue;
			}

			float currentValue = Float.parseFloat(data.getDataList().get(0).getValue().trim());
			float lastSentValue = -1;

			// get last sent value if exist to compare
			if (r.getDataList() != null && !r.getDataList().isEmpty()) {
				// TODO until this moment, we manage only with one data => index
				// is 0 => r.getDataList().get(0)
				lastSentValue = Float.parseFloat(r.getDataList().get(0).getValue().trim());
			}

			// the following condition decides to jump the loop or continue to
			// let the rest of the operations
			if (r.getFrequency().getCondition() == Condition.EachNewDifferentValue) {
				// if Condition.EachNewDifferentValue => do not send the same
				// value
				if (currentValue == lastSentValue) {
					System.out
							.println("INFO : EachNewDifferentValue condition but the new value is not different from the old.");
					continue;
				}
				System.out.println("INFO : EachNewDifferentValue condition satisfied.");
			} else if (r.getFrequency().getCondition() == Condition.IfThresholdIsExceeded) {
				/*
				 * 1. treat the condition : 
				 * if Condition.IfThresholdIsExceeded and lastSentValue is not in [minThreshold, maxThreshold]
				 */

				if ((currentValue >= r.getFrequency().minThreshold && currentValue <= r.getFrequency().maxThreshold)) {
					System.out.println("INFO : not exceeded thresholds.");
					continue;
				}
				System.out.println("INFO : IfThresholdIsExceeded condition satisfied.");
			}

			// 2. treat the frequency
			try {
				System.out.println("INFO : watting for frequency");
				Thread.sleep(r.getFrequency().frequencyValue);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 3. Notify depending on protocol
			if (r.getContact().protocol == Protocol.XMPP) {

				System.out.println("INFO : Notify " + r.getSourceAddress() + "by XMPP...");
				xmppClient.sendMessage(r.getSourceAddress(), data.toString(), Message.Type.normal);
				// update with sent value to compare with current value
				r.setDataList(data.getDataList());
				
				// TODO notify with additional var if there is ?
				if (!props.getProperty("additionalVaribles").isEmpty()) {
					List<String> additionalVaribles = Arrays.asList(props.getProperty("additionalVaribles").trim().split(","));
					for (String userVar : additionalVaribles) {
						data.getDataList().get(0).setName(userVar);
						data.getDataList().get(0).setValue(props.getProperty(userVar).split(",")[0].trim());
						data.getDataList().get(0).setUnit(props.getProperty(userVar).split(",")[1].trim());
						data.getDataList().get(0).setType(props.getProperty(userVar).split(",")[2].trim());
						xmppClient.sendMessage(r.getSourceAddress(), data.toString(), Message.Type.normal);
					}
				}

				System.out.println("INFO : Notify " + r.getSourceAddress() + " OK");
			} else if (r.getContact().protocol == Protocol.HTTP) {
				System.out.println("INFO : Notify " + r.getSourceAddress() + "by HTTP...");
				
				// send requested data
				httpDoPost(r, data);
				// update with sent value to compare with current value
				r.setDataList(data.getDataList());
				
				// TODO notify with additional var if there is ?
				if (!props.getProperty("additionalVaribles").isEmpty()) {
					List<String> additionalVaribles = Arrays.asList(props.getProperty("additionalVaribles").trim().split(","));
					for (String userVar : additionalVaribles) {
						data.getDataList().get(0).setName(userVar);
						data.getDataList().get(0).setValue(props.getProperty(userVar).split(",")[0].trim());
						data.getDataList().get(0).setUnit(props.getProperty(userVar).split(",")[1].trim());
						data.getDataList().get(0).setType(props.getProperty(userVar).split(",")[2].trim());
						r.getContact().setResponseVaribaleName(userVar);
						httpDoPost(r, data);
					}
				}
			}
		}
	}
	
	
	private void httpDoPost(SubscriptionRequest r, ResponseData data) {
		if (httpClient == null)
			httpClient = ClientBuilder.newClient();
		WebTarget target = httpClient.target(r.getContact().address.trim());
		
		Form form = new Form();
		form.param(r.getContact().getResponseVaribaleName(), data.toString());

		// test if the server is connected

		try {
			Response resp = target.request(MediaType.APPLICATION_JSON).post(
					Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
			System.out.println(resp);
			if (resp.getStatusInfo().getStatusCode() == Response.Status.OK.getStatusCode()) {
				System.out.println("Notify " + r.getSourceAddress() + " OK");
				xmppClient.sendMessage(r.getSourceAddress(), resp.getStatusInfo().getStatusCode() + "",
						Message.Type.normal);
			} else {
				System.err.println("ERROR : Bad HTTP address contact : " + r.getContact().address);
				xmppClient.sendMessage(r.getSourceAddress(), "Bad HTTP address contact : "
						+ r.getContact().address, Message.Type.error);
			}
		} catch (HTTPException e) {
			System.err.println("ERROR : " + target.getUri()
					+ " is not accesible. Verify that the server is startd.  Cause details :");
			e.printStackTrace();
		}
	}

	private void displayAllSubscriptions() {
		for (UniqueDataIdentifier udi : subscriptions.keySet()) {
			System.out.println("-----> List<SubscriptionRequest> subscribed on '" + udi + "' : ");
			HashSet<SubscriptionRequest> requestList = subscriptions.get(udi);
			for (SubscriptionRequest subscriptionRequest : requestList) {
				System.out.println("->" + udi + "<->" + subscriptionRequest);
			}
		}
	}
}
