package com.stkj.pperty.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * 
 * The use Httpclient-4.4.1.jar;
 * 
 * @author luoyh
 * @date Jun 26, 2015
 */
public abstract class HttpUtils {
	private final static String URL_ENCODING = "UTF-8";
	
	public static List<Header> getHttpPostHeader() {
		// 定义Header对象列表
		List<Header> headers = new ArrayList<Header>();
		// 添加Header类型
		headers.add(new BasicHeader("Content-type", "application/json;charset=utf-8"));
		return headers;
	}
	public static String doGet(String url, Map<String, Object> params) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		StringBuilder sb = new StringBuilder();
		sb.append(url).append("?");
		if (null != params) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				sb.append(entry.getKey()).append("=");
				if (null == entry.getValue())
					sb.append("");
				else {
					try {
						sb.append(URLEncoder.encode(entry.getValue().toString(), URL_ENCODING));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				sb.append("&");
			}
			sb.append("_=").append(System.currentTimeMillis());
		}
		HttpGet httpGet = new HttpGet(sb.toString());
		String ret = null;
		try {
			response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			ret = EntityUtils.toString(entity, URL_ENCODING);
			// closed
			EntityUtils.consume(entity);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != response)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	/**
	 * Http Get request
	 * 
	 * @param url
	 * @return
	 */
	public static String doGet(String url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = null;
		String ret = null;
		try {
			response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			ret = EntityUtils.toString(entity, URL_ENCODING);
			EntityUtils.consume(entity);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != response)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	/**
	 * HttpPost Request and upload File.
	 * 
	 * @param url
	 * @param params
	 * @param files
	 * @return
	 */
	public static String doPostFile(String url, Map<String, Object> params, Map<String, File[]> files) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response = null;
		MultipartEntityBuilder mEntity = MultipartEntityBuilder.create();
		if (null != params) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				mEntity.addTextBody(entry.getKey(), null == entry.getValue() ? "" : entry.getValue().toString());
			}
		}
		if (null != files) {
			for (Map.Entry<String, File[]> entry : files.entrySet()) {
				File[] fs = entry.getValue();
				String fname = entry.getKey();
				for (File f : fs) {
					mEntity.addPart(fname, new FileBody(f));
				}
			}
		}
		String ret = null;
		try {
			httpPost.setEntity(mEntity.build());
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			ret = EntityUtils.toString(entity, URL_ENCODING);
			EntityUtils.consume(entity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != response)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("HttpClient doPost return::" + ret);
		return ret;
	}

	public static String doPostEntity(String url, String stringEntity, List<Header> headers) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		if (null != headers) {
			for (Header header : headers) {
				httpPost.addHeader(header);
			}
		}
		CloseableHttpResponse response = null;
		String ret = null;
		try {
			httpPost.setEntity(new StringEntity(stringEntity, URL_ENCODING));
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			ret = EntityUtils.toString(entity, URL_ENCODING);
			EntityUtils.consume(entity);
		} catch (UnsupportedEncodingException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} finally {
			try {
				if (null != response)
					response.close();
			} catch (IOException e) {
			}
		}
		return ret;
	}

	public static String doPostEntity(String url, String stringEntity) {
		return doPostEntity(url, stringEntity, null);
	}

	public static String doPostText(String url, Map<String, Object> params) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		CloseableHttpResponse response = null;
		if (null != params) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(),
						null == entry.getValue() ? "" : entry.getValue().toString()));
			}
		}
		String ret = null;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, URL_ENCODING));
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			ret = EntityUtils.toString(entity, URL_ENCODING);
			EntityUtils.consume(entity);
		} catch (UnsupportedEncodingException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} finally {
			try {
				if (null != response)
					response.close();
			} catch (IOException e) {
			}
		}
		return ret;
	}
}
