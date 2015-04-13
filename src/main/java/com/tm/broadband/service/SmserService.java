package com.tm.broadband.service;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.tm.broadband.sms.Smser;

@Service
public class SmserService implements Smser {

	private TaskExecutor taskExecutor;

	@Autowired
	public SmserService(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public SmserService() {
	}

	@Override
	public void sendSMSBySynchronizationMode(String cellphone,
			String content) {
		try {
			@SuppressWarnings({ "deprecation", "resource" })
			HttpClient client = new DefaultHttpClient();
			HttpGet pxpayRequest = new HttpGet(
					"http://101.98.13.138/goip/en/dosend.php?USERNAME=root&PASSWORD="
							+ URLEncoder.encode("hitech12345^", "UTF-8")
							+ "&smsprovider=1&smsnum="
							+ cellphone
							+ "&method=2&Memo="
							+ URLEncoder.encode(content,
									"UTF-8"));
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			client.execute(pxpayRequest, responseHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void sendSMSByAsynchronousMode(final String cellphone, final String content) {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				sendSMSBySynchronizationMode(cellphone, content);
			}
		});
	}

}
