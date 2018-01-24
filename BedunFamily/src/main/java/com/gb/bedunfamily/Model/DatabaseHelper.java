package com.gb.bedunfamily.Model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.gb.bedunfamily.AppHelper;
import com.gb.bedunfamily.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;


/**
 * // Utility Class. Reads and writes user date to/from the database. All crud operations are encapsulated in php scripts
 * accesed by url requests.
 */
public class DatabaseHelper {
    private String username;
    private String myJSONString;
    private String aidWorkerId;
    private HashMap<Integer, Refugee> missingFamLinkedToCurrentRef = new HashMap<>();
    private String currentSentRefId;
    private Refugee currentSentRefugee;
    private ArrayList<Refugee> searchResults;
    private HashMap<Integer, Refugee> currentSentRefugeeMap = new HashMap<>();
    private HashMap<Integer, Refugee> currentRecievedRefugeeMap = new HashMap<>();
    private String currentVoicemailId;
    private Voicemail currentVoicemail;
    private HashMap<Integer, Voicemail> currentVoicemailMap = new HashMap<>();
    private Refugee currentRefugee;
    private Refugee currentFamMember;
    private String miss_id;
    private String loginId;
    private String connectedUserLoginId;
    private String ConnectedLoginId;
    private String currentUserType;
    private String missingPersonId;
    private String userTypeId;
    private JSONArray userLoginJsonArray;
    private AidWorker aidWorker;
    private HashMap<Integer, ArrayList<String>> ulMap = new HashMap<>();
    private JSONArray userJsonArray;
    private JSONArray userJsonArr;
    private HashMap<Integer, Refugee> refMap = new HashMap<>();
    private final HashMap<Integer, Refugee> missingFamMap = new HashMap<>();
    private String refId;
    private Refugee searchableRefugee;
    private JSONArray userTypeJsonArray;
    private HashMap<Integer, ArrayList<String>> userTypeMap = new HashMap<>();
    private static final String JSON_ARRAY = "result";
    private static final String ID = "LoginId";
    private static final String name = "username";
    private String loginUri;
    private String currentRecievedRefId;
    private Refugee currentRecievedRefugee;
    private Refugee foundRefugee;
    private final HashMap<Integer, ArrayList<String>> removedFromSearchMap = new HashMap<>();
    private HashMap<Integer, Refugee> matchedRefugees = new HashMap<>();
    private String currentMatchedRefId;
    private HashMap<Integer, ArrayList<String>> contactOrgMap = new HashMap<>();
    private MainActivity mainActivity;
    private boolean hasSearched;

    //getters & setters
    public HashMap<Integer, Refugee> getCurrentRecievedRefugeeMap() {
        return currentRecievedRefugeeMap;
    }

    public void setFoundRefugee(Refugee foundRefugee) {
        this.foundRefugee = foundRefugee;
    }

    public HashMap<Integer, ArrayList<String>> getContactOrgMap() {
        return contactOrgMap;
    }

    public void setContactOrgMap(HashMap<Integer, ArrayList<String>> contactOrgMap) {
        this.contactOrgMap = contactOrgMap;
    }

    public void setCurrentRecievedRefugeeMap(HashMap<Integer, Refugee> currentRecievedRefugeeMap) {
        this.currentRecievedRefugeeMap = currentRecievedRefugeeMap;
    }

    public String getCurrentRecievedRefId() {
        return currentRecievedRefId;
    }

    public void setCurrentRecievedRefId(String currentRecievedRefId) {
        this.currentRecievedRefId = currentRecievedRefId;
    }

    public Refugee getCurrentRecievedRefugee() {
        return currentRecievedRefugee;
    }

    public void setCurrentRecievedRefugee(Refugee currentRecievedRefugee) {
        this.currentRecievedRefugee = currentRecievedRefugee;
    }

    public Refugee getSearchableRefugee() {
        return searchableRefugee;
    }

    public void setSearchableRefugee(Refugee searchableRefugee) {
        this.searchableRefugee = searchableRefugee;
    }
    public Refugee getFoundRefugee() {
        return foundRefugee;
    }
    public void setMiss_id(String miss_id) {
        this.miss_id = miss_id;
    }

    public String getMiss_id() {

        return miss_id;
    }
    public String getCurrentMatchedRefId() {
        return currentMatchedRefId;
    }

    public void setCurrentMatchedRefId(String currentMatchedRefId) {
        this.currentMatchedRefId = currentMatchedRefId;
    }
    public HashMap<Integer, Refugee> getMissingFamLinkedToCurrentRef() {
        return missingFamLinkedToCurrentRef;
    }

    public void setMissingFamLinkedToCurrentRef(HashMap<Integer, Refugee> missingFamLinkedToCurrentRef) {
        this.missingFamLinkedToCurrentRef = missingFamLinkedToCurrentRef;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public void setCurrentRefugee(Refugee currentRefugee) {

        this.currentRefugee = currentRefugee;
    }

    public Refugee getCurrentRefugee() {
        return currentRefugee;
    }

    public Refugee getCurrentFamMember() {
        return currentFamMember;
    }

    public void setCurrentFamMember(Refugee currentFamMember) {
        this.currentFamMember = currentFamMember;
    }

    public HashMap<Integer, Refugee> getMissingFamMap() {
        return missingFamMap;

    }
    public String getConnectedUserLoginId() {
        return connectedUserLoginId;
    }

    public void setConnectedUserLoginId(String connectedUserLoginId) {
        this.connectedUserLoginId = connectedUserLoginId;
    }
    private String getUserTypeId() {
        return userTypeId;
    }
    public String getCurrentUserType() {
        return currentUserType;
    }
    public HashMap<Integer, ArrayList<String>> getUserTypeMap() {
        return userTypeMap;
    }
    public void setUserTypeMap(HashMap<Integer, ArrayList<String>> userTypeMap) {
        this.userTypeMap = userTypeMap;
    }
    public DatabaseHelper(String username) {
        this.username = username;
    }
    public HashMap<Integer, ArrayList<String>> getUlMap() {
        return ulMap;
    }
    public void setUlMap(HashMap<Integer, ArrayList<String>> ulMap) {
        this.ulMap = ulMap;
    }
    public AidWorker getAidWorker() {
        return aidWorker;
    }
    public void setAidWorker(AidWorker aidWorker) {
        this.aidWorker = aidWorker;
    }
    public HashMap<Integer, Refugee> getRefMap() {
        return refMap;
    }
    public void setRefMap(HashMap<Integer, Refugee> refMap) {
        this.refMap = refMap;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getMyJSONString() {
        return myJSONString;
    }
    public void setMyJSONString(String myJSONString) {
        this.myJSONString = myJSONString;
    }
    public String getAidWorkerId() {
        return aidWorkerId;
    }
    public void setAidWorkerId(String aidWorkerId) {
        this.aidWorkerId = aidWorkerId;
    }
    public String getCurrentSentRefId() {
        return currentSentRefId;
    }
    public void setCurrentSentRefId(String currentSentRefId) {
        this.currentSentRefId = currentSentRefId;
    }
    public Refugee getCurrentSentRefugee() {
        return currentSentRefugee;
    }
    public void setCurrentSentRefugee(Refugee currentSentRefugee) {
        this.currentSentRefugee = currentSentRefugee;
    }
    public HashMap<Integer, Refugee> getCurrentSentRefugeeMap() {
        return currentSentRefugeeMap;
    }
    public void setCurrentSentRefugeeMap(HashMap<Integer, Refugee> currentSentRefugeeMap) {
        this.currentSentRefugeeMap = currentSentRefugeeMap;
    }
    public String getCurrentVoicemailId() {
        return currentVoicemailId;
    }
    public void setCurrentVoicemailId(String currentVoicemailId) {
        this.currentVoicemailId = currentVoicemailId;
    }
    public Voicemail getCurrentVoicemail() {
        return currentVoicemail;
    }
    public void setCurrentVoicemail(Voicemail currentVoicemail) {
        this.currentVoicemail = currentVoicemail;
    }
    public HashMap<Integer, Voicemail> getCurrentVoicemailMap() {
        return currentVoicemailMap;
    }
    public void setCurrentVoicemailMap(HashMap<Integer, Voicemail> currentVoicemailMap) {
        this.currentVoicemailMap = currentVoicemailMap;
    }
    public String getConnectedLoginId() {
        return ConnectedLoginId;
    }
    public void setConnectedLoginId(String connectedLoginId) {
        ConnectedLoginId = connectedLoginId;
    }
    public void setCurrentUserType(String currentUserType) {
        this.currentUserType = currentUserType;
    }
    public String getMissingPersonId() {
        return missingPersonId;
    }
    public void setMissingPersonId(String missingPersonId) {
        this.missingPersonId = missingPersonId;
    }
    public void setMissingFamMap(HashMap<Integer, Refugee> missingFamMap) {
        missingFamMap = missingFamMap;
    }
    public static String getJsonArray() {
        return JSON_ARRAY;
    }
    public static String getID() {
        return ID;
    }
    public static String getName() {
        return name;
    }
    public String getLoginUri() {
        return loginUri;
    }
    public void setLoginUri(String loginUri) {
        this.loginUri = loginUri;
    }
    public JSONArray getUserTypeJsonArray() {
        return userTypeJsonArray;
    }
    public void setUserTypeJsonArray(JSONArray userTypeJsonArray) {
        this.userTypeJsonArray = userTypeJsonArray;
    }
    public HashMap<Integer, Refugee> getMatchedRefugees() {
        return matchedRefugees;
    }

    public void setUserTypeId(String userTypeId) {
        this.userTypeId = userTypeId;
    }
    public JSONArray getUserLoginJsonArray() {
        return userLoginJsonArray;
    }
    public void setUserLoginJsonArray(JSONArray userLoginJsonArray) {
        this.userLoginJsonArray = userLoginJsonArray;
    }
    public JSONArray getUserJsonArray() {
        return userJsonArray;
    }
    public void setUserJsonArray(JSONArray userJsonArray) {
        this.userJsonArray = userJsonArray;
    }
    public JSONArray getUserJsonArr() {
        return userJsonArr;
    }
    public void setUserJsonArr(JSONArray userJsonArr) {
        this.userJsonArr = userJsonArr;
    }
    public String getLoginId() {
        return loginId;
    }
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
    public String getRefId() {
        return refId;
    }
    public String getUsername() {
        return username;
    }
    public ArrayList<Refugee> getSearchResults() {
        return searchResults;
    }
    public HashMap<Integer, ArrayList<String>> getRemovedFromSearchMap() {
        return removedFromSearchMap;
    }
    //default constructor
    public DatabaseHelper(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    //constructor with username set to user phone number
    public DatabaseHelper(String loginId, String username) {
        this.username = username;
        this.loginId = loginId;
    }
    /**
     * Asynchronous task is ran upon method being called. In background thread, sets POST connection to url parameter.
     * encodes username (phone number) and writes with buffered writer
     * php script inserts username into database login table as a new entry, assigned to a unique identifier.
     * url connection in wrapped in try/catch block which catches malformed url errors and input/output errors
     * with the buffered writer/reader
     *
     */
    public void setloginDb() {
        class SetLogin extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                String uname = username;
                String text = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();
                    String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(uname, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    int statusCode = con.getResponseCode();
                    if (statusCode == 200) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null)
                            sb.append(line).append("\n");
                        text = sb.toString();
                        bufferedWriter.close();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(s);
            }
        }
        SetLogin sl = new SetLogin();
        sl.execute("http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/adduser_login.php");
    }

    /**
     * Asynchronous task is ran upon method being called. In background thread, sets POST connection to url
     * encodes storage key, length, senderId (id referencing the refugee sending the voicemail), recieverId (id referencing the refugee receiving the voicemail),
     * reply (either 1 or 0. 1 is if the voicemail is in response to recieved voicemail. 0 is if voicemail is the first instance of contact between the refugees)
     * and writes with buffered writer
     * php script inserts attributes into database voicemail table as a new entry, assigned to a unique identifier.
     * url connection in wrapped in try/catch block which catches malformed url errors and input/output errors
     * with the buffered writer/reader
     *
     * @param key  key used to identify file resource in database
     * @param time total length/time of voice recording
     */
    public void setVoicemailDb(final String key, final Long time, final int reply) {
        class SetVoicemail extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {

                String uri = "http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/add_voicemail.php";
                String length = time.toString();
                String rep = Integer.toString(reply);
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                Calendar c = Calendar.getInstance();
                String date = sdf.format(c.getTime());
                String text = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();

                    String data = URLEncoder.encode("length", "UTF-8") + "=" + URLEncoder.encode(length, "UTF-8") + "&" +
                            URLEncoder.encode("StorageKey", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8") + "&" +
                            URLEncoder.encode("SenderId", "UTF-8") + "=" + URLEncoder.encode(getRefId(), "UTF-8") + "&" +
                            URLEncoder.encode("RecieverId", "UTF-8") + "=" + URLEncoder.encode(getSearchableRefugee().getRefugeeId(), "UTF-8") + "&" +
                            URLEncoder.encode("Date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8") + "&" +
                            URLEncoder.encode("reply", "UTF-8") + "=" + URLEncoder.encode(rep, "UTF-8");

                    System.out.println(refId);
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    int statusCode = con.getResponseCode();
                    if (statusCode == 200) {

                        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null)
                            sb.append(line).append("\n");

                        text = sb.toString();
                        bufferedWriter.close();
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                currentVoicemailMap = new HashMap<>();
                currentVoicemailId = getSearchableRefugee().getRefugeeId();
                int id = Integer.parseInt(currentVoicemailId);
                currentVoicemail = new Voicemail(getRefId(), currentVoicemailId, rep, length, key, date);
                currentVoicemailMap.put(id, currentVoicemail);
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(s);

            }
        }
        SetVoicemail sV = new SetVoicemail();
        sV.execute();
    }

    /**
     * Asynchronous task is ran upon method being called. In background thread, appends username/phone number to URL
     * php script is accessed by opening url connection.
     * script gets login entry containing the username and returns in JSON format
     * buffered reader appends json to String. A json array is created with the string containing the json array fed in as the argument
     * <p>
     * first try catch block, catches exception in url connection and bufferedReader reading output to string.
     * second try catch block catches json exception from creating json array.
     * <p>
     * For loop retrieves json objects representing User Logins. As each username is unique, this will always return 1 json object.
     * getString() methods called on json object to store username and loginID into strings
     * username added to array. array added to HashMap ulMap, with the key set to the unique loginID.
     * <p>
     * third try catch block catches json exception from looping through json array and extracting json object.
     *
     */
    public void getUserLogin() {

        class GetJSON extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                URL url = null;
                String uri = params[0];

                BufferedReader bufferedReader;
                try {

                    loginUri = uri + username;
                    url = new URL(loginUri);

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    myJSONString = sb.toString().trim();

                    try {
                        userLoginJsonArray = new JSONArray(myJSONString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    return null;
                }
                try {
                    for (int i = 0; i < userLoginJsonArray.length(); i++) {
                        JSONObject jsonObject = userLoginJsonArray.getJSONObject(i);
                        String uname = jsonObject.getString(name);
                        loginId = jsonObject.getString(ID);
                        System.out.println(loginId + "FUCK");
                        int id = Integer.parseInt(loginId);
                        ArrayList<String> tempArray = new ArrayList<>();
                        tempArray.add(uname);
                        ulMap.put(id, tempArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return myJSONString;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(loginUri);
                System.out.println(loginId);

            }
        }
        GetJSON gj = new GetJSON();
        gj.execute("http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/get_user_login.php?username=");
    }

    /**
     * Asynchronous task is ran upon method being called. In background thread, sets POST connection to url of the php script
     * buffered writer posts loginID to the script
     * php script inserts loginID into database UserType table as a new entry assigned to a unique identifier. User Type is set to Refugee
     * url connection in wrapped in try/catch block which catches malformed url errors and input/output errors
     * with the buffered writer/reader
     *
     */
    public void setRefugeeType() {
        class SetUserType extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String logId = loginId;

                String text = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();
                    String data = URLEncoder.encode("loginId", "UTF-8") + "=" + URLEncoder.encode(logId, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    int statusCode = con.getResponseCode();
                    if (statusCode == 200) {

                        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null)
                            sb.append(line).append("\n");

                        text = sb.toString();
                        bufferedWriter.close();
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(s);
            }
        }
        SetUserType sut = new SetUserType();
        sut.execute("http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/add_user_refugee.php");
    }

    /**
     * Asynchronous task is ran upon method being called. In background thread, sets POST connection to url of the php script
     * buffered writer posts loginID to the script
     * php script inserts loginID into database UserType table as a new entry assigned to a unique identifier. User Type is set to AidWorker
     * url connection in wrapped in try/catch block which catches malformed url errors and input/output errors
     * with the buffered writer/reader
     *
     */
    public void setAidWorkerType() {
        class SetUserType extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                System.out.println(uri);
                String logId = loginId;
                System.out.println(uri);

                String text = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();

                    String data = URLEncoder.encode("loginId", "UTF-8") + "=" + URLEncoder.encode(logId, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    int statusCode = con.getResponseCode();
                    if (statusCode == 200) {

                        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null)
                            sb.append(line).append("\n");

                        text = sb.toString();
                        bufferedWriter.close();
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(s);
            }
        }
        SetUserType sut = new SetUserType();
        sut.execute("http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/add_user_aidworker.php");
    }

    /**
     * Asynchronous task is ran upon method being called. In background thread, appends username/phone number to URL
     * php script is accessed by opening url connection.
     * script gets User Type entry containing related to the loginID and returns in JSON format
     * buffered reader appends json to String. A json array is created with the string containing the json array fed in as the argument
     * <p>
     * first try catch block, catches exception in url connection and bufferedReader reading output to string.
     * second try catch block catches json exception from creating json array.
     * <p>
     * For loop retrieves json objects representing UserType. As each UserType is unique, this will always return 1 json object.
     * getString() methods called on json object to store UserTypeId, loginID and UserType (Refugee or AidWorker)  into strings
     * loginID and UserType added to array. array added to HashMap with the key set to the unique UserTypeId.
     * <p>
     * third try catch block catches json exception from looping through json array and extracting json object.
     *
     */
    public void getUserType() {
        class GetJSON extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                BufferedReader bufferedReader;
                try {
                    loginUri = uri + loginId;
                    System.out.println(loginUri);
                    URL url = new URL(loginUri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    myJSONString = sb.toString().trim();

                    try {
                        userTypeJsonArray = new JSONArray(myJSONString);
                        System.out.println(userTypeJsonArray.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    return null;
                }
                try {
                    for (int i = 0; i < userTypeJsonArray.length(); i++) {
                        JSONObject jsonObject = userTypeJsonArray.getJSONObject(i);
                        userTypeId = jsonObject.getString("UserId");
                        currentUserType = jsonObject.getString("UserType");
                        int id = Integer.parseInt(userTypeId);
                        ArrayList<String> tempArray = new ArrayList<>();
                        tempArray.add(currentUserType);
                        tempArray.add(loginId);
                        userTypeMap.put(id, tempArray);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return myJSONString;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(loginUri);
                System.out.println(userTypeId);

            }
        }
        GetJSON gj = new GetJSON();
        gj.execute("http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/get_user_type.php?LoginId=");
    }

    /**
     * Asynchronous task is ran upon method being called. In background thread, sets POST connection to url of the php script
     * URL encoder encodes aid worker details(UserId, Name, DateOfBirth AidOrg, Nationality, Gender, CountryOfWork, Sector,
     * Supervisor and Position).
     * buffered writer posts encoded  aid worker attributes to the script.
     * php script inserts aid worker attributes into database AidWorker table as a new entry assigned to a unique identifier.
     * url connection in wrapped in try/catch block which catches malformed url errors and input/output errors
     * with the buffered writer/reader
     *
     * @param a aid Worker being registered
     */
    public void setAidWorkerDb(final AidWorker a) {
        class SetAidW extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                aidWorker = a;
                String uri = params[0];
                System.out.println(uri);
                String userId = userTypeId;
                System.out.println(uri);

                String text = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();

                    String data = URLEncoder.encode("UserId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                            URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getName(), "UTF-8") + "&" +
                            URLEncoder.encode("DateOfBirth", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getDateOfBirth(), "UTF-8") + "&" +
                            URLEncoder.encode("AidOrg", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getAidOrganization(), "UTF-8") + "&" +
                            URLEncoder.encode("Nationality", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getNationality(), "UTF-8") + "&" +
                            URLEncoder.encode("Gender", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getGender(), "UTF-8") + "&" +
                            URLEncoder.encode("CountryOfWork", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getWorkCountry(), "UTF-8") + "&" +
                            URLEncoder.encode("Sector", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getSector(), "UTF-8") + "&" +
                            URLEncoder.encode("Supervisor", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getSupervisor(), "UTF-8") + "&" +
                            URLEncoder.encode("Position", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getPosition(), "UTF-8");

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    int statusCode = con.getResponseCode();
                    if (statusCode == 200) {

                        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null)
                            sb.append(line).append("\n");

                        text = sb.toString();
                        bufferedWriter.close();
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(s);
            }
        }
        SetAidW saw = new SetAidW();
        saw.execute("http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/add_aid_worker_details.php");
    }

    /**
     * Asynchronous task is ran upon method being called. In background thread, sets POST connection to url of the php script
     * URL encoder encodes refugee details(UserId, Name, AgeRange, Nickname, Nationality, Gender, PlaceOfBirth, Tribe,
     * LocalArea, Occupation).
     * buffered writer posts encoded refugee attributes to the script.
     * php script inserts refugee attributes into database Refugee table as a new entry assigned to a unique identifier.
     * url connection is wrapped in try/catch block which catches malformed url errors and input/output errors
     * with the buffered writer/reader
     *
     * @param u url of php script
     * @param r Refugee being registered
     */
    public void setRefugeeDb(final String u, final Refugee r) {
        class SetRefDb extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                currentRefugee = r;
                String uri = params[0];
                System.out.println(currentRefugee);
                String userId = userTypeId;
                System.out.println(uri);

                String text = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();

                    String data = URLEncoder.encode("UserId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                            URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getName(), "UTF-8") + "&" +
                            URLEncoder.encode("AgeRange", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getAgeGroup(), "UTF-8") + "&" +
                            URLEncoder.encode("Nickname", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getNickname(), "UTF-8") + "&" +
                            URLEncoder.encode("Nationality", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getNationality(), "UTF-8") + "&" +
                            URLEncoder.encode("PlaceOfBirth", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getPlaceOfBirth(), "UTF-8") + "&" +
                            URLEncoder.encode("Tribe", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getTribe(), "UTF-8") + "&" +
                            URLEncoder.encode("Gender", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getGen(), "UTF-8") + "&" +
                            URLEncoder.encode("LocalArea", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getLoc(), "UTF-8") + "&" +
                            URLEncoder.encode("Occupation", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getOcc(), "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    int statusCode = con.getResponseCode();
                    if (statusCode == 200) {
                        //
                        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String result = "";
                        String line = "";

                        while ((line = reader.readLine()) != null)
                            result += line;
                        bufferedWriter.close();
                        System.out.println(result);
                        JSONObject responseJSON = null;
                        try {
                            responseJSON = new JSONObject(result);
                            refId = responseJSON.getString("ref_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        int id = Integer.parseInt(refId);
                        refMap.put(id, currentRefugee);

                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(s);
                if (mainActivity != null){
                    android.support.v4.app.Fragment frag = new MainActivity.HomeFragment();
                    mainActivity.loadFragment(frag);
                }
            }
        }
        SetRefDb srd = new SetRefDb();
        srd.execute(u);

    }

    /**
     * Asynchronous task is ran upon method being called. In background thread, php script is accessed by opening url connection.
     * The RefugeeID of the current refugee is posted to the script.
     * script gets each voicemail where the SenderID == the posted refugee id and returns in JSON format
     * buffered reader appends json to String. A json array is created with the string containing the json array fed in as the argument
     * <p>
     * first try catch block, catches exception in url connection, buffered writer posting refugeeID
     * and bufferedReader reading output to string.
     * second try catch block catches json exception from creating json array.
     * <p>
     * For loop retrieves json objects representing Voicemails.
     * For each object:
     * getString() methods called on json object to store senderID, VoicemailId, recieverID, reply, length and key
     * voicemail object instatiated from local attributes.
     * voicemail added to HashMap with the key set to the unique VoicemailId.
     * <p>
     * third try catch block catches json exception from looping through json array and extracting json objects.
     *
     * UPDATE: json read also returns removefromsearch map. This is retrieved so the voicemail always returns is always live. If the other user has declared the
     * voicemail is a match or not a match, the voicemail adapter can determine whether the user can continue with the conversation.
     */
    public void getSentVoicemailsForRef() {
        class GetVoicemailSentBox extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                URL url;
                String uri = "http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/get_voicemail_sent.php";
                currentVoicemailMap = new HashMap<>();


                try {

                    url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();

                    String data =
                            URLEncoder.encode("SenderId", "UTF-8") + "=" + URLEncoder.encode(getRefId(), "UTF-8");
                    System.out.println(getRefId());
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    BufferedReader bufferedReader;
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json).append("\n");
                    }
                    myJSONString = sb.toString().trim();
                    System.out.println(myJSONString);

                    try {
                        userJsonArray = new JSONArray(myJSONString);
                        System.out.println(userJsonArray.toString() + "tesstting");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    return null;
                }
                try {
                    userJsonArr = userJsonArray.getJSONArray(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray jsonArray = userJsonArr.getJSONArray(0);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String senderId = jsonObject.getString("SenderId");
                        String reply = jsonObject.getString("Reply");
                        String key = jsonObject.getString("StorageKey");
                        String length = jsonObject.getString("length");
                        String date = jsonObject.getString("Date");

                        currentVoicemailId = jsonObject.getString("RecieverId");
                        int id = Integer.parseInt(currentVoicemailId);
                        currentVoicemail = new Voicemail(senderId, currentVoicemailId, reply, length, key, date);
                        currentVoicemailMap.put(id, currentVoicemail);
                        System.out.println(currentVoicemailMap.keySet());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray jArray = userJsonArr.getJSONArray(1);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jsonObject = jArray.getJSONObject(i);
                        currentSentRefId = jsonObject.getString("RefugeeId");
                        String name = jsonObject.getString("Name");
                        String ageRange = jsonObject.getString("AgeRange");
                        String nickname = jsonObject.getString("Nickname");
                        String nationality = jsonObject.getString("Nationality");
                        String placeOfBirth = jsonObject.getString("PlaceOfBirth");
                        String tribe = jsonObject.getString("Tribe");
                        String gender = jsonObject.getString("Gender");
                        String localArea = jsonObject.getString("LocalArea");
                        String occupation = jsonObject.getString("Occupation");
                        int id = Integer.parseInt(currentSentRefId);
                        currentSentRefugee = new Refugee(name, ageRange, nationality, nickname, placeOfBirth, tribe, gender, localArea, occupation);
                        currentSentRefugeeMap.put(id, currentSentRefugee);
                        System.out.println(currentSentRefugee.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray jArray = userJsonArr.getJSONArray(2);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jsonObject = jArray.getJSONObject(i);
                        String refToBeRemovedId = jsonObject.getString("RefToBeRemovedId");
                        String refId = jsonObject.getString("RefId");
                        String isAMatch = jsonObject.getString("IsAMatch");
                        String idString = refId+refToBeRemovedId;
                        int id = Integer.parseInt(idString);
                        ArrayList<String> tempArray;
                        tempArray = new ArrayList<>();
                        tempArray.add(refId);
                        tempArray.add(refToBeRemovedId);
                        tempArray.add(isAMatch);
                        removedFromSearchMap.put(id, tempArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return myJSONString;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }
        GetVoicemailSentBox gvsb = new GetVoicemailSentBox();
        gvsb.execute();
    }

    /**
     * Asynchronous task is ran upon method being called. In background thread, php script is accessed by opening url connection.
     * The RefugeeID of the current refugee is posted to the script.
     * script gets each voicemail where the SenderID == the posted refugee id and returns in JSON format
     * buffered reader appends json to String. A json array is created with the string containing the json array fed in as the argument
     * <p>
     * first try catch block, catches exception in url connection, buffered writer posting refugeeID
     * and bufferedReader reading output to string.
     * second try catch block catches json exception from creating json array.
     * <p>
     * For loop retrieves json objects representing Voicemails.
     * For each object:
     * getString() methods called on json object to store senderID, VoicemailId, recieverID, reply, length and key
     * voicemail object instatiated from local attributes.
     * voicemail added to HashMap with the key set to the unique VoicemailId.
     * <p>
     * third try catch block catches json exception from looping through json array and extracting json objects.
     * UPDATE: json read also returns removefromsearch map. This is retrieved so the voicemail always returns is always live. If the other user has declared the
     * voicemail is a match or not a match, the voicemail adapter can determine whether the user can continue with the conversation.
     */
    public void getRecievedVoicemailsForRef() {
        class GetInboxDb extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                URL url;
                String uri = "http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/get_voicemail_recieved.php";
                currentVoicemailMap = new HashMap<>();
                try {

                    url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();

                    String data =
                            URLEncoder.encode("RecieverId", "UTF-8") + "=" + URLEncoder.encode(getRefId(), "UTF-8");
                    System.out.println(getRefId());
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    BufferedReader bufferedReader;
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json).append("\n");
                    }
                    myJSONString = sb.toString().trim();
                    System.out.println(myJSONString);

                    try {
                        userJsonArray = new JSONArray(myJSONString);
                        System.out.println(userJsonArray.toString() + "tesstting");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    return null;
                }
                try {
                    userJsonArr = userJsonArray.getJSONArray(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray jsonArray = userJsonArr.getJSONArray(0);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String recieverId = jsonObject.getString("RecieverId");
                        String reply = jsonObject.getString("Reply");
                        String key = jsonObject.getString("StorageKey");
                        String length = jsonObject.getString("length");
                        String date = jsonObject.getString("Date");

                        currentVoicemailId = jsonObject.getString("SenderId");
                        int id = Integer.parseInt(currentVoicemailId);
                        currentVoicemail = new Voicemail(currentVoicemailId, recieverId, reply, length, key, date);
                        currentVoicemailMap.put(id, currentVoicemail);
                        System.out.println(currentVoicemailMap);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray jArray = userJsonArr.getJSONArray(1);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jsonObject = jArray.getJSONObject(i);
                        currentRecievedRefId = jsonObject.getString("RefugeeId");
                        String name = jsonObject.getString("Name");
                        String ageRange = jsonObject.getString("AgeRange");
                        String nickname = jsonObject.getString("Nickname");
                        String nationality = jsonObject.getString("Nationality");
                        String placeOfBirth = jsonObject.getString("PlaceOfBirth");
                        String tribe = jsonObject.getString("Tribe");
                        String gender = jsonObject.getString("Gender");
                        String localArea = jsonObject.getString("LocalArea");
                        String occupation = jsonObject.getString("Occupation");
                        int id = Integer.parseInt(currentRecievedRefId);
                        currentRecievedRefugee = new Refugee(currentRecievedRefId, name, ageRange, nationality, nickname, placeOfBirth, tribe,
                                gender, localArea, occupation, null, null);
                        currentRecievedRefugeeMap.put(id, currentRecievedRefugee);
                        System.out.println(currentRecievedRefugee.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray jArray = userJsonArr.getJSONArray(2);
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jsonObject = jArray.getJSONObject(i);
                        String refToBeRemovedId = jsonObject.getString("RefToBeRemovedId");
                        String refId = jsonObject.getString("RefId");
                        String isAMatch = jsonObject.getString("IsAMatch");
                        String idString = refId+refToBeRemovedId;
                        int id = Integer.parseInt(idString);
                        ArrayList<String> tempArray;
                        tempArray = new ArrayList<>();
                        tempArray.add(refId);
                        tempArray.add(refToBeRemovedId);
                        tempArray.add(isAMatch);
                        removedFromSearchMap.put(id, tempArray);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return myJSONString;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }
        GetInboxDb gid = new GetInboxDb();
        gid.execute();
    }

    /**
     * Asynchronous task is ran upon method being called. In background thread, php script is accessed by opening url connection.
     * The RefugeeID of the current refugee is posted to the script.
     * Script gets each Match where the RefugeeID == the current refugee id. Returns each refugee matched to the current refugee and adds to a hashmap of matched refugees.
     * returns in JSON format
     * buffered reader appends json to String. A json array is created with the string containing the json array fed in as the argument
     * <p>
     * first try catch block, catches exception in url connection, buffered writer posting refugeeID
     * and bufferedReader reading output to string.
     * second try catch block catches json exception from creating json array.
     * <p>
     * For loop retrieves json objects representing Refugees
     * For each object:
     * getString() methods called on json object to store Refugee attributes.
     * Refugee object instatiated from local attributes.
     * Refugee object added to MatchedRefugees Hashmap
     * <p>
     * third try catch block catches json exception from looping through json array and extracting json objects.
     * UPDATE: json read also returns removefromsearch map. This is retrieved so the voicemail always returns is always live. If the other user has declared the
     * voicemail is a match or not a match, the voicemail adapter can determine whether the user can continue with the conversation.
     */
    public void loadMatchesDb() {
        class LoadMatches extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                URL url;
                String uri = "http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/load_matches.php";
                String refId = getRefId();
                matchedRefugees = new HashMap<>();
                try {

                    url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();

                    String data =
                            URLEncoder.encode("RefId", "UTF-8") + "=" + URLEncoder.encode(refId, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    BufferedReader bufferedReader;
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json).append("\n");
                    }
                    myJSONString = sb.toString().trim();
                    System.out.println(myJSONString);

                    try {
                        userJsonArray = new JSONArray(myJSONString);
                        System.out.println(userJsonArray.toString() + "tesstting");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    return null;
                }
                
                try {
                    System.out.println(userJsonArray.length());
                    for (int i = 0; i < userJsonArray.length(); i++) {
                        JSONObject jsonObject = userJsonArray.getJSONObject(i);
                        currentMatchedRefId = jsonObject.getString("RefugeeId");
                        String name = jsonObject.getString("Name");
                        String ageRange = jsonObject.getString("AgeRange");
                        String nickname = jsonObject.getString("Nickname");
                        String nationality = jsonObject.getString("Nationality");
                        String placeOfBirth = jsonObject.getString("PlaceOfBirth");
                        String tribe = jsonObject.getString("Tribe");
                        String gender = jsonObject.getString("Gender");
                        String localArea = jsonObject.getString("LocalArea");
                        String occupation = jsonObject.getString("Occupation");
                        int id = Integer.parseInt(currentMatchedRefId);
                        Refugee aRef = new Refugee(currentMatchedRefId, name, ageRange, nationality, nickname, placeOfBirth, tribe,
                                gender, localArea, occupation, null, null);
                        matchedRefugees.put(id, aRef);
                        System.out.println(aRef.toString());
                    }
                }   catch (JSONException e) {
                    e.printStackTrace();
                }
                return myJSONString;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }
        LoadMatches lm = new LoadMatches();
        lm.execute();
    }

    /**
     * Asynchronous task is ran upon method being called. In background thread, sets POST connection to url of the php script
     * URL encoder encodes missing person details(RefugeeId, Name, AgeRange, Nickname, Nationality, Gender, PlaceOfBirth, Tribe,
     * LocalArea, Occupation, Relationship). RefugeeId links MissingPerson to the Refugee who wishes to find them.
     * Buffered writer posts encoded missing person attributes to the script.
     * php script inserts missing person attributes into database MissingPerson table as a new entry assigned to a unique identifier.
     * url connection is wrapped in try/catch block which catches malformed url errors and input/output errors
     * with the buffered writer/reader
     *
     * @param u url of php script
     * @param r Refugee being registered as Missing Person
     */
    public void setMissingPersonDb(final String u, final Refugee r) {
        class SetMissPDb extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                currentFamMember = r;
                String uri = params[0];
                System.out.println(uri);
                String tempRefId = refId;
                System.out.println(uri);

                String text = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    OutputStream os = con.getOutputStream();

                    String data = URLEncoder.encode("RefugeeId", "UTF-8") + "=" + URLEncoder.encode(tempRefId, "UTF-8") + "&" +
                            URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getName(), "UTF-8") + "&" +
                            URLEncoder.encode("AgeRange", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getAgeGroup(), "UTF-8") + "&" +
                            URLEncoder.encode("Nickname", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getNickname(), "UTF-8") + "&" +
                            URLEncoder.encode("Nationality", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getNationality(), "UTF-8") + "&" +
                            URLEncoder.encode("PlaceOfBirth", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getPlaceOfBirth(), "UTF-8") + "&" +
                            URLEncoder.encode("Tribe", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getTribe(), "UTF-8") + "&" +
                            URLEncoder.encode("Gender", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getGen(), "UTF-8") + "&" +
                            URLEncoder.encode("LocalArea", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getLoc(), "UTF-8") + "&" +
                            URLEncoder.encode("Occupation", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getOcc(), "UTF-8") + "&" +
                            URLEncoder.encode("DateCreated", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getDateCreated(), "UTF-8") + "&" +
                            URLEncoder.encode("Relationship", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getRelationship(), "UTF-8");


                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    int statusCode = con.getResponseCode();
                    System.out.println(con.getResponseMessage());
                    if (statusCode == 200) {

                        //
                        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String result = "";
                        String line = "";

                        while ((line = reader.readLine()) != null)
                            result += line;
                        bufferedWriter.close();
                        System.out.println(result);
                        JSONObject responseJSON = null;
                        try {
                            responseJSON = new JSONObject(result);
                            miss_id = responseJSON.getString("miss_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        int id = Integer.parseInt(miss_id);


                        missingFamMap.put(id, currentFamMember);
                        missingFamLinkedToCurrentRef.put(id, currentFamMember);
                        System.out.println(currentFamMember + "test");

                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                android.support.v4.app.Fragment frag = new MainActivity.HomeFragment();
                mainActivity.loadFragment(frag);
            }
        }
        SetMissPDb smp = new SetMissPDb();
        smp.execute(u);

    }

    /**
     * Asynchronous task is executed upon method being called. In background thread,
     * the current family member is set to @param refugee.
     * php script is accessed by opening url connection.
     * URL encoder encodes missing person details(MissingPersonId, Name, AgeRange, Nickname, Nationality, Gender, PlaceOfBirth, Tribe,
     * LocalArea, Occupation, Relationship). .
     * Buffered writer posts encodes missing person attributes to the script.
     * script gets the Missing person where the MissingPersonID == the posted MissingPersonId and updates so the fields
     * reflect the posted attributes
     * buffered reader appends json to String to provide response code from the php script
     * Update missingFamLinkedToCurrentRef HashMap so: current family member value held under key MissingPersonId is updated to
     * reflect changes made to Missing Person/Family Member in the database.
     * Update missingFamMap HashMap so: current family member value held under key MissingPersonId is updated to
     * reflect changes made to Missing Person/Family Member in the database.
     * <p>
     * first try catch block, catches exception in url connection, buffered writer posting attributes
     * and bufferedReader reading output to string.
     *
     * @param u       URL of php script
     * @param refugee Missing Family Member Refugee object
     */
    public void changeFamilyMemberDb(final String u, final Refugee refugee) {
        class changeFamDb extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                currentFamMember = refugee;
                String uri = params[0];
                System.out.println(uri);
                String famId = miss_id;
                System.out.println(uri);

                String text = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();

                    String data = URLEncoder.encode("MissingPersonId", "UTF-8") + "=" + URLEncoder.encode(famId, "UTF-8") + "&" +
                            URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getName(), "UTF-8") + "&" +
                            URLEncoder.encode("AgeRange", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getAgeGroup(), "UTF-8") + "&" +
                            URLEncoder.encode("Nickname", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getNickname(), "UTF-8") + "&" +
                            URLEncoder.encode("Nationality", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getNationality(), "UTF-8") + "&" +
                            URLEncoder.encode("PlaceOfBirth", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getPlaceOfBirth(), "UTF-8") + "&" +
                            URLEncoder.encode("Tribe", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getTribe(), "UTF-8") + "&" +
                            URLEncoder.encode("Gender", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getGen(), "UTF-8") + "&" +
                            URLEncoder.encode("LocalArea", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getLoc(), "UTF-8") + "&" +
                            URLEncoder.encode("Occupation", "UTF-8") + "=" + URLEncoder.encode(currentFamMember.getLoc(), "UTF-8");

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    int statusCode = con.getResponseCode();
                    if (statusCode == 200) {

                        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null)
                            sb.append(line).append("\n");

                        text = sb.toString();
                        bufferedWriter.close();
                        missingFamLinkedToCurrentRef.put(Integer.parseInt(miss_id), currentFamMember);
                        missingFamMap.put(Integer.parseInt(miss_id), currentFamMember);


                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(currentFamMember);
            }
        }
        changeFamDb cfd = new changeFamDb();
        cfd.execute(u);
    }

    /**
     * Asynchronous task is executed upon method being called. In background thread,
     * the current AidWorker is set to @param AidWorker.
     * php script is accessed by opening url connection.
     * URL encoder encodes AidWorker details(AidWorkerId, Name, DateOfBirth, AidOrg, Nationality, Gender, CountryOfWork, Sector,
     * Supervisor, Position).
     * Buffered writer posts encoded AidWorker attributes to the script.
     * script gets the AidWorker where the AidWorkerID == the posted AidWorkerID and updates so the fields
     * reflect the posted attributes
     * buffered reader appends json to String to provide response code from the php script
     * <p>
     * first try catch block, catches exception in url connection, buffered writer posting attributes
     * and bufferedReader reading output to string.
     *
     * @param u         URL of php script
     * @param aidworker AidWorker object
     */
    public void changeAidWorkerDb(final String u, final AidWorker aidworker) {
        class changeAWDb extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                aidWorker = aidworker;
                String uri = params[0];
                System.out.println(uri);
                String awId = aidWorkerId;
                System.out.println(uri);

                String text = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();

                    String data = URLEncoder.encode("AidWorkerId", "UTF-8") + "=" + URLEncoder.encode(awId, "UTF-8") + "&" +
                            URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getName(), "UTF-8") + "&" +
                            URLEncoder.encode("DateOfBirth", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getDateOfBirth(), "UTF-8") + "&" +
                            URLEncoder.encode("AidOrg", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getAidOrganization(), "UTF-8") + "&" +
                            URLEncoder.encode("Nationality", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getNationality(), "UTF-8") + "&" +
                            URLEncoder.encode("Gender", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getGender(), "UTF-8") + "&" +
                            URLEncoder.encode("CountryOfWork", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getWorkCountry(), "UTF-8") + "&" +
                            URLEncoder.encode("Sector", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getSector(), "UTF-8") + "&" +
                            URLEncoder.encode("Supervisor", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getSupervisor(), "UTF-8") + "&" +
                            URLEncoder.encode("Position", "UTF-8") + "=" + URLEncoder.encode(aidWorker.getPosition(), "UTF-8");

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    int statusCode = con.getResponseCode();
                    if (statusCode == 200) {
                        bufferedWriter.close();


                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(aidworker);
            }
        }
        changeAWDb caw = new changeAWDb();
        caw.execute(u);
    }

    /**
     * Asynchronous task is executed upon method being called. In background thread,
     * the current Refugee is set to @param refugee.
     * php script is accessed by opening url connection.
     * URL encoder encodes Refugee details(RefugeeId, Name, AgeRange, Nickname, Nationality, Gender, PlaceOfBirth, Tribe,
     * LocalArea, Occupation).
     * Buffered writer posts encoded Refugee attributes to the script.
     * script gets the Refugee where the RefugeeID == the posted RefugeeID and updates so the fields
     * reflect the posted attributes
     * buffered reader appends json to String to provide response code from the php script
     * Update refMap HashMap so: current Refugee value held under key RefugeeId is updated to
     * reflect changes made to Current Refugee in the database.
     * <p>
     * first try catch block, catches exception in url connection, buffered writer posting attributes
     * and bufferedReader reading output to string.
     *
     * @param u       URL of php script
     * @param refugee Refugee object
     */
    public void changeRefugeeDb(final String u, final Refugee refugee) {
        class changeRefDb extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                currentRefugee = refugee;
                String uri = params[0];
                System.out.println(uri);
                String refugeeId = refId;
                System.out.println(uri);

                String text = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();

                    String data = URLEncoder.encode("RefugeeId", "UTF-8") + "=" + URLEncoder.encode(refugeeId, "UTF-8") + "&" +
                            URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getName(), "UTF-8") + "&" +
                            URLEncoder.encode("AgeRange", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getAgeGroup(), "UTF-8") + "&" +
                            URLEncoder.encode("Nickname", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getNickname(), "UTF-8") + "&" +
                            URLEncoder.encode("Nationality", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getNationality(), "UTF-8") + "&" +
                            URLEncoder.encode("PlaceOfBirth", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getPlaceOfBirth(), "UTF-8") + "&" +
                            URLEncoder.encode("Tribe", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getTribe(), "UTF-8") + "&" +
                            URLEncoder.encode("Gender", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getGen(), "UTF-8") + "&" +
                            URLEncoder.encode("LocalArea", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getLoc(), "UTF-8") + "&" +
                            URLEncoder.encode("Occupation", "UTF-8") + "=" + URLEncoder.encode(currentRefugee.getLoc(), "UTF-8");

                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    int statusCode = con.getResponseCode();
                    if (statusCode == 200) {

                        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null)
                            sb.append(line).append("\n");

                        text = sb.toString();
                        bufferedWriter.close();
                        refMap.put(Integer.parseInt(refId), currentRefugee);
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(currentRefugee);
            }
        }
        changeRefDb crd = new changeRefDb();
        crd.execute(u);
    }

    /**
     * Called after user first logs in to retirieve all user data from backend. Asynchronous task is ran upon method being called. In background thread, appends username/phone number to URL
     * php script is accessed by opening url connection.
     * script gets login entry containing the username and returns in JSON format
     * buffered reader appends json to String. A json array, userJsonArray, is created with the string containing the json array fed in as the argument
     * a further json array, userJsonArr, is created. This json array is nested inside userJsonArray. each array entry in userJsonArr
     * holds a table entry (e.g UserType, Login, Missing Family Member)
     * first try catch block, catches exception in url connection and bufferedReader reading output to string.
     * second and third try catch block catches json exception from creating json arrays
     * <p>
     * For loop iterates through each array nested inside userJsonArray. Switch statement checks on each value of i from the for loop
     * and handles the array data for each case:
     * case 0: Retrieves user login information. getString() methods called on json object to store username and loginID into strings
     * username added to array. array added to HashMap ulMap, with the key set to the unique loginID.
     * case 1: Retrieves user type information. userTypeID, UserType and loginID stored into strings.
     * currentUserType and loginID added to array. array added to HashMap userTypeMap, with the key set to the unique userTypeId.
     * case 2: if the currentUserType set in case 1 == 'Refugee', then a refugee object (currentRefugee) is created from the json array
     * and added to the hashMap, refMap
     * else if the currentUserType set in case 1 == 'AidWorker' then an aidWorker object (aidWorker) is created from the json
     * array.
     * case 3: if the currentUserType set in case 1 == 'Refugee', then a for loop iterates through the json array to find every Missing
     * Family member linked to currentRefugee (the user)
     * each family member is added to the hashMap, missingFamLinkedToCurrentRef, with the key set to the unique MissingPersonId.
     * else if the currentUserType set in case 1 == 'AidWorker' then a for loop iterates through the json array
     * to return each refugee object (currentRefugee) linked to the AidWorker. each refugee is added to the hashMap, refMap.
     * case 4: a for loop iterates through the json array to find every Missing
     * Family member. This has the constraint that the MissingPerson object must be linked to a Refugee object which is linked to the
     * AidWorker object representing the user. This is handled in the php script so only MissingPerson objects which satisfy this constraint
     * are added
     * each family member is added to the hashMap, missingPersonMap, with the key set to the unique MissingPersonId.
     * third try-catch block catches json exception from looping through the json arrays.
     *
     */
    public void getTables() {
        class GetTabs extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                URL url;
                String uri = params[0];
                BufferedReader bufferedReader;
                CognitoUser cognitoUser = AppHelper.getPool().getCurrentUser();
                username = cognitoUser.getUserId();
                System.out.println(username);
                loginUri = uri + username;
                try {
                    url = new URL(loginUri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    myJSONString = sb.toString().trim();

                    try {
                        userJsonArray = new JSONArray(myJSONString);
                        System.out.println(userJsonArray.toString() + "a");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        userJsonArr = userJsonArray.getJSONArray(0);
                        System.out.println(userJsonArr.toString() + "b");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    return null;
                }
                try {
                    for (int i = 0; i < userJsonArr.length(); i++) {
                        JSONArray jsonArr = userJsonArr.getJSONArray(i);
                        if (jsonArr.length() != 0) {
                            JSONObject jsonObject = jsonArr.getJSONObject(0);
                            switch (i) {
                                case 0:
                                    loginId = jsonObject.getString("LoginId");
                                    String usern = jsonObject.getString("username");
                                    int logId = Integer.parseInt(loginId);
                                    ArrayList<String> temptingArray = new ArrayList<>();
                                    temptingArray.add(usern);
                                    ulMap.put(logId, temptingArray);
                                    System.out.println(ulMap.toString());
                                    break;
                                case 1:
                                    userTypeId = jsonObject.getString("UserId");
                                    currentUserType = jsonObject.getString("UserType");
                                    loginId = jsonObject.getString("LoginId");
                                    int uId = Integer.parseInt(userTypeId);
                                    ArrayList<String> temptArray = new ArrayList<>();
                                    temptArray.add(currentUserType);
                                    temptArray.add(loginId);
                                    System.out.println(temptArray.toString() + "TEST");
                                    userTypeMap.put(uId, temptArray);
                                    System.out.println(currentUserType + "TESTING");
                                    break;
                                case 2:
                                    System.out.println("Length3: " + jsonArr.length());
                                    if (currentUserType.equals("Refugee")) {
                                        refId = jsonObject.getString("RefugeeId");
                                        String name = jsonObject.getString("Name");
                                        String ageRange = jsonObject.getString("AgeRange");
                                        String nickname = jsonObject.getString("Nickname");
                                        String nationality = jsonObject.getString("Nationality");
                                        String placeOfBirth = jsonObject.getString("PlaceOfBirth");
                                        String tribe = jsonObject.getString("Tribe");
                                        String gender = jsonObject.getString("Gender");
                                        String localArea = jsonObject.getString("LocalArea");
                                        String occupation = jsonObject.getString("Occupation");
                                        String userId = jsonObject.getString("UserId");
                                        int id = Integer.parseInt(refId);
                                        currentRefugee = new Refugee(name, ageRange, nationality, nickname, placeOfBirth, tribe, gender, localArea, occupation);
                                        refMap.put(id, currentRefugee);
                                        System.out.println("I am Ref" + currentRefugee.getName());
                                    } else {

                                        aidWorkerId = jsonObject.getString("AidWorkerId");
                                        String name = jsonObject.getString("Name");
                                        String dateOfBirth = jsonObject.getString("DateOfBirth");
                                        String gender = jsonObject.getString("Gender");
                                        String nationality = jsonObject.getString("Nationality");
                                        String countryOfWork = jsonObject.getString("CountryOfWork");
                                        String aidOrganization = jsonObject.getString("AidOrg");
                                        String sector = jsonObject.getString("Sector");
                                        String pos = jsonObject.getString("Position");
                                        String supervisor = jsonObject.getString("Supervisor");
                                        int id = Integer.parseInt(aidWorkerId);
                                        //String loginID, String nam, String dateob, String gender, String nat,
                                        //String workCountry, String aidOrg, String sector,
                                        //      String position, String supervisor)
                                        aidWorker = new AidWorker(loginId, name, dateOfBirth, gender, nationality, countryOfWork, aidOrganization,
                                                sector, pos, supervisor);
                                        System.out.println(getAidWorker() + "test");

                                    }
                                    break;
                                case 3:
                                    System.out.println("Length3: " + jsonArr.length());
                                    if (currentUserType.equals("Refugee")) {
                                        for (int x = 0; x < jsonArr.length(); x++) {
                                            System.out.println(i + "Ref");
                                            JSONObject jsonObj = jsonArr.getJSONObject(x);
                                            miss_id = jsonObj.getString("MissingPersonId");
                                            String mpName = jsonObj.getString("Name");
                                            String mpAgeRange = jsonObj.getString("AgeRange");
                                            String mpNickname = jsonObj.getString("Nickname");
                                            String mpNationality = jsonObj.getString("Nationality");
                                            String mpPlaceOfBirth = jsonObj.getString("PlaceOfBirth");
                                            String mpTribe = jsonObj.getString("Tribe");
                                            String mpGender = jsonObj.getString("Gender");
                                            String mpRelationship = jsonObj.getString("Relationship");
                                            String mpLocalArea = jsonObj.getString("LocalArea");
                                            String mpOccupation = jsonObj.getString("Occupation");
                                            String mpDateCreated = jsonObj.getString("DateCreated");
                                            String refugeeId = jsonObj.getString("RefugeeId");
                                            int missId = Integer.parseInt(miss_id);

                                            currentFamMember = new Refugee(refugeeId, mpName, mpAgeRange, mpNationality, mpNickname, mpPlaceOfBirth, mpTribe,
                                                    mpGender, mpLocalArea, mpOccupation, mpRelationship, mpDateCreated);
                                            missingFamLinkedToCurrentRef.put(missId, currentFamMember);
                                        }
                                    } else {
                                        //refugee map
                                        for (int z = 0; z < jsonArr.length(); z++) {
                                            JSONObject jsonObj = jsonArr.getJSONObject(z);
                                            refId = jsonObj.getString("RefugeeId");
                                            String name = jsonObj.getString("Name");
                                            String ageRange = jsonObj.getString("AgeRange");
                                            String nickname = jsonObj.getString("Nickname");
                                            String nationality = jsonObj.getString("Nationality");
                                            String placeOfBirth = jsonObj.getString("PlaceOfBirth");
                                            String tribe = jsonObj.getString("Tribe");
                                            String gender = jsonObj.getString("Gender");
                                            String localArea = jsonObj.getString("LocalArea");
                                            String occupation = jsonObj.getString("Occupation");
                                            //String userId = jsonObject.getString("UserId");
                                            int id = Integer.parseInt(refId);
                                            currentRefugee = new Refugee(name, ageRange, nationality, nickname, placeOfBirth, tribe, gender, localArea, occupation);
                                            refMap.put(id, currentRefugee);
                                            System.out.println(currentRefugee.toString());

                                        }

                                    }
                                    break;
                                case 4:
                                    if (currentUserType.equals("Refugee")) {
                                        for (int z = 0; z < jsonArr.length(); z++) {
                                            JSONObject jsonObj = jsonArr.getJSONObject(z);
                                            String refToBeRemovedId = jsonObj.getString("RefToBeRemovedId");
                                            String refId = jsonObj.getString("RefId");
                                            String isAMatch = jsonObj.getString("IsAMatch");
                                            String idString = refId+refToBeRemovedId;
                                            int id = Integer.parseInt(idString);
                                            ArrayList<String> tempArray;
                                            tempArray = new ArrayList<>();
                                            tempArray.add(refId);
                                            tempArray.add(refToBeRemovedId);
                                            tempArray.add(isAMatch);
                                            removedFromSearchMap.put(id, tempArray);
                                        }
                                    } else {
                                        System.out.println("Length4: " + jsonArr.length());
                                        for (int y = 0; y < jsonArr.length(); y++) {
                                            JSONObject jsonObj = jsonArr.getJSONObject(y);
                                            miss_id = jsonObj.getString("MissingPersonId");
                                            String mpName = jsonObj.getString("Name");
                                            String mpAgeRange = jsonObj.getString("AgeRange");
                                            String mpNickname = jsonObj.getString("Nickname");
                                            String mpNationality = jsonObj.getString("Nationality");
                                            String mpPlaceOfBirth = jsonObj.getString("PlaceOfBirth");
                                            String mpTribe = jsonObj.getString("Tribe");
                                            String mpGender = jsonObj.getString("Gender");
                                            String mpRelationship = jsonObj.getString("Relationship");
                                            String mpLocalArea = jsonObj.getString("LocalArea");
                                            String mpOccupation = jsonObj.getString("Occupation");
                                            String mpDateCreated = jsonObj.getString("DateCreated");

                                            String refugeeId = jsonObj.getString("RefugeeId");
                                            int missId = Integer.parseInt(miss_id);

                                            currentFamMember = new Refugee(refugeeId, mpName, mpAgeRange, mpNationality, mpNickname, mpPlaceOfBirth, mpTribe,
                                                    mpGender, mpLocalArea, mpOccupation, mpRelationship, mpDateCreated);
                                            System.out.println("yo" + currentFamMember);
                                            missingFamMap.put(missId, currentFamMember);

                                            HashMap<Integer, Refugee> tempMap;
                                            tempMap = new HashMap<>();
                                            for (Integer misId : getMissingFamMap().keySet()) {
                                                Refugee aRef = getMissingFamMap().get(misId);
                                                if (aRef.getRefugeeId().equals(getRefId())) {
                                                    tempMap.put(missId, aRef);
                                                }
                                            }
                                            setMissingFamLinkedToCurrentRef(tempMap);
                                        }
                                        System.out.println("TEST" + missingFamMap.toString());
                                    }
                                    break;
                                case 5:
                                    if (currentUserType.equals("Refugee")) {
                                        for (int z = 0; z < jsonArr.length(); z++) {
                                            JSONObject jsonObj = jsonArr.getJSONObject(z);
                                            String contactOrgId = jsonObj.getString("ContactOrgId");
                                            String foundRefId = jsonObj.getString("FoundRefId");
                                            String refId = jsonObj.getString("RefId");
                                            String location = jsonObj.getString("Location");
                                            String phoneNumber = jsonObj.getString("PhoneNumber");
                                            String missingPersonId = jsonObj.getString("MissingPersonId");
                                            int id = Integer.parseInt(contactOrgId);
                                            ArrayList<String> tempArray;
                                            tempArray = new ArrayList<>();
                                            tempArray.add(refId);
                                            tempArray.add(foundRefId);
                                            tempArray.add(location);
                                            tempArray.add(phoneNumber);
                                            tempArray.add(missingPersonId);
                                            contactOrgMap.put(id, tempArray);
                                            System.out.println(contactOrgMap);
                                        }
                                    } else {
                                        for (int z = 0; z < jsonArr.length(); z++) {
                                            JSONObject jsonObj = jsonArr.getJSONObject(z);
                                            String refToBeRemovedId = jsonObj.getString("RefToBeRemovedId");
                                            String refId = jsonObj.getString("RefId");
                                            String isAMatch = jsonObj.getString("IsAMatch");
                                            String idString = refId+refToBeRemovedId;
                                            int id = Integer.parseInt(idString);
                                            ArrayList<String> tempArray;
                                            tempArray = new ArrayList<>();
                                            tempArray.add(refId);
                                            tempArray.add(refToBeRemovedId);
                                            tempArray.add(isAMatch);
                                            removedFromSearchMap.put(id, tempArray);
                                        }
                                    }
                                    break;
                                case 6:
                                    for (int z = 0; z < jsonArr.length(); z++) {
                                        JSONObject jsonObj = jsonArr.getJSONObject(z);
                                        String contactOrgId = jsonObj.getString("ContactOrgId");
                                        String foundRefId = jsonObj.getString("FoundRefId");
                                        String refId = jsonObj.getString("RefId");
                                        String location = jsonObj.getString("Location");
                                        String phoneNumber = jsonObj.getString("PhoneNumber");
                                        String missingPersonId = jsonObj.getString("MissingPersonId");
                                        int id = Integer.parseInt(contactOrgId);
                                        ArrayList<String> tempArray;
                                        tempArray = new ArrayList<>();
                                        tempArray.add(refId);
                                        tempArray.add(foundRefId);
                                        tempArray.add(location);
                                        tempArray.add(phoneNumber);
                                        tempArray.add(missingPersonId);
                                        contactOrgMap.put(id, tempArray);
                                        System.out.println(contactOrgMap);
                                    }
                                    break;
                            }
                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                return myJSONString;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                mainActivity.init();
            }
        }
        GetTabs gts = new GetTabs();
        gts.execute("http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/get_all_tables.php?username=");
    }

    /**
     * Asynchronous task is ran upon method being called. In background thread, php script is accessed by opening url connection.
     * The UserId and RefugeeID of the current refugee is posted to the script.
     * script gets each refugee who is not contained in the removefromsearch database entries linked to the current refugee by
     * a foreign key constraint. Also removes all refugees from the search who are owned by the user. So for a Refugee user type
     * this would be the registered user. For an aid worker, it would remove all refugees created by the aid worker.
     *
     * buffered reader appends json to String. A json array is created with the string containing the json array fed in as the argument
     * <p>
     * first try-catch block, catches exception in url connection, buffered writer posting refugeeID
     * and bufferedReader reading output to string.
     * second try-catch block catches json exception from creating json array.
     * <p>
     * For loop retrieves json objects representing Refugees
     * For each object:
     * getString() methods called on json object to store attributes of a refugee
     * refugee object instatiated from local attributes.
     * refugee added to  searchResults HashMap.
     * <p>
     * third try catch block catches json exception from looping through json array and extracting json objects.
     */
    public void searchForRef() {
        class SearchForRefugees extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                URL url;
                String uri = "http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/search_refugees.php";
                searchResults = new ArrayList<>();
                hasSearched = false;
                try {

                    url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();

                    String data =
                            URLEncoder.encode("UserId", "UTF-8") + "=" + URLEncoder.encode(userTypeId, "UTF-8") + "&" +
                                    URLEncoder.encode("RefId", "UTF-8") + "=" + URLEncoder.encode(getRefId(), "UTF-8");
                    System.out.println(getUserTypeId());
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    BufferedReader bufferedReader;
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json).append("\n");
                    }
                    myJSONString = sb.toString().trim();
                    System.out.println(myJSONString);

                    try {
                        userJsonArray = new JSONArray(myJSONString);
                        System.out.println(userJsonArray.toString() + "a");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    return null;
                }
                try {
                    for (int i = 0; i < userJsonArray.length(); i++) {
                        JSONObject jsonObject = userJsonArray.getJSONObject(i);
                        String aRefId = jsonObject.getString("RefugeeId");
                        String name = jsonObject.getString("Name");
                        String ageRange = jsonObject.getString("AgeRange");
                        String nickname = jsonObject.getString("Nickname");
                        String nationality = jsonObject.getString("Nationality");
                        String placeOfBirth = jsonObject.getString("PlaceOfBirth");
                        String tribe = jsonObject.getString("Tribe");
                        String gender = jsonObject.getString("Gender");
                        String localArea = jsonObject.getString("LocalArea");
                        String occupation = jsonObject.getString("Occupation");
                        Refugee aRef = new Refugee(aRefId, name, ageRange, nationality, nickname, placeOfBirth,
                                tribe, gender, localArea, occupation, null, null);
                        searchResults.add(aRef);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(searchResults + "test");

                return myJSONString;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                mainActivity.loadFrag();
            }
        }

        SearchForRefugees sfr = new SearchForRefugees();
        sfr.execute();
    }


    /**
     * Asynchronous task is ran upon method being called. In background thread, php script is accessed by opening url connection.
     * The RefugeeID of the current refugee is posted to the script along with the id of the matched refugee, the id of the missing person,
     * the contact number of the current refugee and a description of the current location.
     *
     * If the user is contacting an aid organisation because they cannot find their family member then matched refugee is set to null
     * If the user is contacting an aid organisation because they cannot found their family member then missingpersonid is set to null
     *
     * URL encoder encodes details
     * buffered writer posts encoded attributes to the script.
     * php script inserts attributes into database removefromSearch table as a new entry assigned to a unique identifier.
     * url connection is wrapped in try/catch block which catches malformed url errors and input/output errors
     * with the buffered writer/reader
     */
    public void setContactOrgDb(final String location, final String phone, final String contactType) {
        class setContactOrg extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = "http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/add_contact_org.php";
                String refId = getRefId();
                String foundRefId;
                String miss_person_id;
                if (contactType == "NoMatch") {
                    miss_person_id = miss_id;
                    foundRefId = "";
                } else {
                    foundRefId = getCurrentMatchedRefId();
                    miss_person_id = "";
                }
                String text = null;
                String contId = "";
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();
                    String data = URLEncoder.encode("RefId", "UTF-8") + "=" + URLEncoder.encode(refId, "UTF-8") + "&" +
                            URLEncoder.encode("FoundRefId", "UTF-8") + "=" + URLEncoder.encode(foundRefId, "UTF-8") + "&" +
                            URLEncoder.encode("Location", "UTF-8") + "=" + URLEncoder.encode(location, "UTF-8") + "&" +
                            URLEncoder.encode("PhoneNumber", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&" +
                            URLEncoder.encode("MissingPersonId", "UTF-8") + "=" + URLEncoder.encode(miss_person_id, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    int statusCode = con.getResponseCode();
                    if (statusCode == 200) {

                        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null)
                            sb.append(line).append("\n");

                        text = sb.toString();
                        System.out.println(text);
                        bufferedWriter.close();
                        try {
                            JSONObject responseJSON = new JSONObject(text);
                            contId = responseJSON.getString("contact_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        int id = Integer.parseInt(contId);
                        ArrayList<String> tempArray = new ArrayList<>();
                        //contact org retrieve in php
                        tempArray.add(refId);
                        tempArray.add(foundRefId);
                        tempArray.add(location);
                        tempArray.add(phone);
                        tempArray.add(miss_person_id);
                        contactOrgMap.put(id, tempArray);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(s);
            }
        }
        setContactOrg sco = new setContactOrg();
        sco.execute();
    }

    /**
     * Asynchronous task is ran upon method being called. In background thread, php script is accessed by opening url connection.
     * The RefugeeID of the current refugee is posted to the script along with the id of the refugee to be removed from search results,
     * the userid and the isamatch variable which ise set to 'Match'
     * Script checks if an existing entry exists for the refugee id and refugee to be removed. If so, UPDATE is ran on the entry
     * with isAMatch altered to 'match'. If no entry exists in the table, a new row is created.
     *
     * URL encoder encodes details
     * buffered writer posts encoded attributes to the script.
     * php script inserts attributes into database removefromSearch table as a new entry assigned to a unique identifier.
     * url connection is wrapped in try/catch block which catches malformed url errors and input/output errors
     * with the buffered writer/reader
     */
    public void matchDb() {
        class Match extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = "http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/rem_from_search.php";
                String refToBeRemovedId = getSearchableRefugee().getRefugeeId();
                String userId = getUserTypeId();
                String refId = getRefId();
                String isAMatch = "Match";
                String text = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();
                    String data = URLEncoder.encode("RefId", "UTF-8") + "=" + URLEncoder.encode(refId, "UTF-8") + "&" +
                            URLEncoder.encode("UserId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                            URLEncoder.encode("RefToBeRemovedId", "UTF-8") + "=" + URLEncoder.encode(refToBeRemovedId, "UTF-8") + "&" +
                            URLEncoder.encode("IsAMatch", "UTF-8") + "=" + URLEncoder.encode(isAMatch, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    int statusCode = con.getResponseCode();
                    if (statusCode == 200) {

                        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null)
                            sb.append(line).append("\n");
                        text = sb.toString();
                        System.out.println(text);
                        bufferedWriter.close();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String idString = refId + refToBeRemovedId;
                int id = Integer.parseInt(idString);
                ArrayList<String> tempArray = new ArrayList<>();
                tempArray.add(refId);
                tempArray.add(refToBeRemovedId);
                tempArray.add(isAMatch);
                tempArray.add(userId);
                removedFromSearchMap.put(id, tempArray);
                System.out.println("removedFromSearchMap" + removedFromSearchMap);
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(s);
            }
        }
        Match match = new Match();
        match.execute();
    }

    /**
     * Asynchronous task is ran upon method being called. In background thread, php script is accessed by opening url connection.
     * The RefugeeID of the current refugee is posted to the script along with the id of the refugee to be removed from search results,
     * the userid and the isamatch variable which is set to 'Ongoing'
     * Script checks if an existing entry exists for the refugee id and refugee to be removed. If so, UPDATE is ran on the entry
     * with isAMatch altered to 'match'. If no entry exists in the table, a new row is created.
     *
     * The database stores two entries for removefromsearch. the link is bidirectional.
     * this is so when a change is made to removefromsearch such as matching the refugees, each user is notified.
     * When a refugee rejects a voicemail to a refugee this method is called.
     * When a refugee rejects a search result as not being matched, this method is called.
     * URL encoder encodes details
     * buffered writer posts encoded attributes to the script.
     * php script inserts attributes into database removefromSearch table as a new entry assigned to a unique identifier.
     * url connection is wrapped in try/catch block which catches malformed url errors and input/output errors
     * with the buffered writer/reader
     */
    public void noMatchDb() {
        class Match extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = "http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/rem_from_search.php";
                String refToBeRemovedId = getSearchableRefugee().getRefugeeId();
                String refId = getRefId();
                String userId = getUserTypeId();
                String isAMatch = "NoMatch";
                System.out.println(refToBeRemovedId + refId + userId + isAMatch);
                String text = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();
                    String data = URLEncoder.encode("RefId", "UTF-8") + "=" + URLEncoder.encode(refId, "UTF-8") + "&" +
                            URLEncoder.encode("UserId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                            URLEncoder.encode("RefToBeRemovedId", "UTF-8") + "=" + URLEncoder.encode(refToBeRemovedId, "UTF-8") + "&" +
                            URLEncoder.encode("IsAMatch", "UTF-8") + "=" + URLEncoder.encode(isAMatch, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    int statusCode = con.getResponseCode();
                    if (statusCode == 200) {

                        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null)
                            sb.append(line).append("\n");
                        text = sb.toString();
                        System.out.println(text);
                        bufferedWriter.close();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String idString = refId + refToBeRemovedId;
                int id = Integer.parseInt(idString);
                ArrayList<String> tempArray = new ArrayList<>();
                tempArray.add(refId);
                tempArray.add(refToBeRemovedId);
                tempArray.add(isAMatch);
                tempArray.add(userId);
                removedFromSearchMap.put(id, tempArray);
                System.out.println("removedFromSearchMap" + removedFromSearchMap);
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(s);
            }
        }
        Match match = new Match();
        match.execute();
    }

    /**
     * Asynchronous task is ran upon method being called. In background thread, php script is accessed by opening url connection.
     * The RefugeeID of the current refugee is posted to the script along with the id of the refugee to be removed from search results,
     * the userid and the isamatch variable which is set to 'Match'
     * Script checks if an existing entry exists for the refugee id and refugee to be removed. If so, UPDATE is ran on the entry
     * with isAMatch altered to 'match'. If no entry exists in the table, a new row is created.
     *
     * The database stores two entries for removefromsearch. the link is bidirectional.
     * this is so when a change is made to removefromsearch such as matching the refugees, each user is notified.
     * difference between this and matchdb is that only the entry for the current refugee is set to match rather then both.
     * When a refugee accepts a recieived voicemail in their inbox they are matched, which is when this method is called.
     * In order for a three way handshake to be complete, the original sneder must then accept the returned voicemail from the matched
     * refugee. At this point matchDb is called and both refugees are matched.
     *
     * URL encoder encodes details
     * buffered writer posts encoded attributes to the script.
     * php script inserts attributes into database removefromSearch table as a new entry assigned to a unique identifier.
     * url connection is wrapped in try/catch block which catches malformed url errors and input/output errors
     * with the buffered writer/reader
     */
    public void setOneWayMatchDb() {
        class OneWayMatchDb extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = "http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/one_way_match.php";
                String refToBeRemovedId = getSearchableRefugee().getRefugeeId();
                String refId = getRefId();
                String userId = getUserTypeId();
                String isAMatch = "Match";
                String text = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();
                    String data = URLEncoder.encode("RefId", "UTF-8") + "=" + URLEncoder.encode(refId, "UTF-8") + "&" +
                            URLEncoder.encode("UserId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                            URLEncoder.encode("RefToBeRemovedId", "UTF-8") + "=" + URLEncoder.encode(refToBeRemovedId, "UTF-8") + "&" +
                            URLEncoder.encode("IsAMatch", "UTF-8") + "=" + URLEncoder.encode(isAMatch, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    int statusCode = con.getResponseCode();
                    if (statusCode == 200) {

                        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null)
                            sb.append(line).append("\n");
                        text = sb.toString();
                        System.out.println(text);
                        bufferedWriter.close();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String idString = refId + refToBeRemovedId;
                int id = Integer.parseInt(idString);
                ArrayList<String> tempArray = new ArrayList<>();
                tempArray.add(refId);
                tempArray.add(refToBeRemovedId);
                tempArray.add(isAMatch);
                tempArray.add(userId);
                removedFromSearchMap.put(id, tempArray);
                System.out.println("removedFromSearchMap" + removedFromSearchMap);
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(s);
                android.support.v4.app.Fragment frag = new MainActivity.VoiceFinalFragment();
                mainActivity.loadFragment(frag);
            }
        }
        OneWayMatchDb owmd = new OneWayMatchDb();
        owmd.execute();
    }

    /**
     * Asynchronous task is ran upon method being called. In background thread, php script is accessed by opening url connection.
     * The RefugeeID of the current refugee is posted to the script along with the id of the refugee to be removed from search results,
     * the userid and the isamatch variable which is set to 'Ongoing'
     * Script checks if an existing entry exists for the refugee id and refugee to be removed. If so, UPDATE is ran on the entry
     * with isAMatch altered to 'match'. If no entry exists in the table, a new row is created.
     *
     * The database stores two entries for removefromsearch. the link is bidirectional.
     * this is so when a change is made to removefromsearch such as matching the refugees, each user is notified.
     * When a refugee first sends a  voicemail to a refugee they have found by searchingm this method is called.
     * the status of the removefromsearch entries will later change to noMatch or match depending on the success of the conversation,
     *
     * URL encoder encodes details
     * buffered writer posts encoded attributes to the script.
     * php script inserts attributes into database removefromSearch table as a new entry assigned to a unique identifier.
     * url connection is wrapped in try/catch block which catches malformed url errors and input/output errors
     * with the buffered writer/reader
     */
    public void ongoingMatchDb() {
        class Match extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                String uri = "http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/ongoing_match.php";
                String refToBeRemovedId = getSearchableRefugee().getRefugeeId();
                String refId = getRefId();
                String userId = getUserTypeId();
                String isAMatch = "Ongoing";
                String text = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();
                    String data = URLEncoder.encode("RefId", "UTF-8") + "=" + URLEncoder.encode(refId, "UTF-8") + "&" +
                            URLEncoder.encode("UserId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                            URLEncoder.encode("RefToBeRemovedId", "UTF-8") + "=" + URLEncoder.encode(refToBeRemovedId, "UTF-8") + "&" +
                            URLEncoder.encode("IsAMatch", "UTF-8") + "=" + URLEncoder.encode(isAMatch, "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    int statusCode = con.getResponseCode();
                    if (statusCode == 200) {

                        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null)
                            sb.append(line).append("\n");
                        text = sb.toString();
                        System.out.println(text);
                        bufferedWriter.close();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String idString = refId + refToBeRemovedId;
                int id = Integer.parseInt(idString);
                ArrayList<String> tempArray = new ArrayList<>();
                tempArray.add(refId);
                tempArray.add(refToBeRemovedId);
                tempArray.add(isAMatch);
                tempArray.add(userId);
                removedFromSearchMap.put(id, tempArray);
                System.out.println("removedFromSearchMap" + removedFromSearchMap);
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                System.out.println(s);
                android.support.v4.app.Fragment frag = new MainActivity.VoiceFinalFragment();
                mainActivity.loadFragment(frag);
            }
        }
        Match match = new Match();
        match.execute();
    }
}