package fr.lab.lissi.eco;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import fr.lab.lissi.general.Constants;
import fr.lab.lissi.model.request.SubscriptionRequest;
import fr.lab.lissi.model.request.UnsubscriptionRequest;

public abstract class EcoAbstract<T> implements ECO<String> {

	@Override
	public List<String> getAllOffredEvents() {
		Properties myProperties = new Properties();
		try {
			myProperties.load(new FileInputStream(Constants.APP_ROOT_PATH + Constants.CONFIG_FILE_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Arrays.asList(myProperties.getProperty("offredEventList").trim().replaceAll(" ", "")
				.split(","));
	}


	public abstract T doSubscription(SubscriptionRequest request);


	public abstract String doUnsubscription(UnsubscriptionRequest request);

}
