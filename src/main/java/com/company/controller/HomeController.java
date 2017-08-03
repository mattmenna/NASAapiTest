package com.company.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.jws.WebParam;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

@Controller
public class HomeController {

    @RequestMapping(value = {"/", "getnasajson"}, method = RequestMethod.GET)

    public ModelAndView helloWorld(Model model) {

        String explanation = "";
        try {

            // the HTTPClient Interface represents the contract for the HTTP Request execution
            HttpClient http = HttpClientBuilder.create().build();

            //HTTPHost holds the variables needed for the connection
            // default port for http is 80
            // default port for https is 443
            HttpHost host = new HttpHost("api.nasa.gov", 443, "https");

            // HttpGet retrieves the info identified by the request URI (in the form of an entity)
            HttpGet getPage = new HttpGet("/planetary/apod?api_key=BBkhbjLuOJ3xpZ9NEBIpEvin0SvjGzB5NUN6Wx8k&date=2017-08-02");

            // execute the http request and pull the response
            HttpResponse resp = http.execute(host, getPage);

            String jsonString = EntityUtils.toString(resp.getEntity());

            // assign the returned result to a json object
            JSONObject json = new JSONObject(jsonString);

            explanation = json.get("explanation").toString();

            // this is for me as a developer to identify that my API is working
            System.out.println("Response code: " + resp.getStatusLine().getStatusCode());

            //String to hold data for our loop once we return the json array

            String text = "";

            //create a json array to hold the data in the "text" array node
            //also think of this as the json array has an array of text nested inside of the data object

            JSONArray ar = json.getJSONObject("explanation").getJSONArray("text");

            //loop through json array
            for (int i = 0; i < ar.length(); i++) {
                text+=("<h6>" + ar.getString(i) + "</h6>");
            }

          model.addAttribute("jsonArray", text);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new
                ModelAndView("welcome", "message", explanation);

    }

    @RequestMapping(value = {"getweatherxml"}, method = RequestMethod.GET)
    public String getXML(Model model){

        String prodCenter = "";
        try {

            // the HTTPClient Interface represents the contract for the HTTP Request execution
            HttpClient http = HttpClientBuilder.create().build();

            //HTTPHost holds the variables needed for the connection
            // default port for http is 80
            // default port for https is 443
            HttpHost host = new HttpHost("forecast.weather.gov", 80, "http");

            // HttpGet retrieves the info identified by the request URI (in the form of an entity)
            HttpGet getPage = new HttpGet("/MapClick.php?lat=42.331427&lon=-83.045754&FcstType=xml");

            // execute the http request and pull the response
            HttpResponse resp = http.execute(host, getPage);

            String xmlString = EntityUtils.toString(resp.getEntity());

            //factory is enabling our app to obtain a parser for the XML DOM
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            DocumentBuilder db = factory.newDocumentBuilder();

            InputSource inStream = new InputSource();

            inStream.setCharacterStream(new StringReader(xmlString));

            Document doc = db.parse(inStream);

            String result = "";
            String weatherForecast = "";

            NodeList nL = doc.getElementsByTagName("text");

            for (int i = 0; i <nL.getLength(); i++) {
                // cast nodelist as an element of the DOM -- this is used for XML Processing
                org.w3c.dom.Element nameElement = (org.w3c.dom.Element) nL.item(i);
                weatherForecast = nameElement.getFirstChild().getNodeValue().trim();
                result += "<h6>" + weatherForecast + "<h6>";
            }

            model.addAttribute("xmlPageData", result);

            // this is for me as a developer to identify that my API is working
            System.out.println("Response code: " + resp.getStatusLine().getStatusCode());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return "xmlData";
    }

}