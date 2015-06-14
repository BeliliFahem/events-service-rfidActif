package fr.lab.lissi.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import fr.lab.lissi.model.response.ResponseData;

public class DataFormatter {

	private static Gson gson = new Gson();

	public static String formatToJson(DataElement data) {

		List<DataElement> dataList = new ArrayList<DataElement>();
		dataList.add(data);

		ResponseData response = new ResponseData(null, dataList);

		return gson.toJson(response);
	}

	public static String formatDataToJson(List<DataElement> dataList) {

		ResponseData response = new ResponseData(null, dataList);

		return gson.toJson(response);
	}

	public static String formatToJson(List<String> allOffredData) {
		return gson.toJson(allOffredData);
	}

	public static String formatToJson(String string) {
		// TODO Auto-generated method stub
		return gson.toJson(string);
	}

}
