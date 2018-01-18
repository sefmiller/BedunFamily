package com.gb.globalfamily.Model;


/**
 * //Creates a Voicemail object which represents the details of the voicemail sent between users, held in storage
 * and referenced in database. see constructor.
 */
public  class Voicemail {
    private String senderId;  private String recieverId; private String reply; private String length; private String StorageKey; private String date;

    //getters and setters
    public String getSenderId() {
        return senderId;
    }

    public String getRecieverId() {
        return recieverId;
    }

    public void setRecieverId(String recieverId) {
        this.recieverId = recieverId;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getStorageKey() {
        return StorageKey;
    }

    public void setStorageKey(String storageKey) {
        StorageKey = storageKey;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    //class members returned as a human readable string
    @Override
    public String toString() {
        return "Voicemail{" +
                "senderId='" + senderId + '\'' +
                ", recieverId='" + recieverId + '\'' +
                ", reply='" + reply + '\'' +
                ", length='" + length + '\'' +
                ", StorageKey='" + StorageKey + '\'' +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Constructor. Initialises all class members represrenting the state of a voicemail
     * @param senderId the refugeeId of the sender, which identifies the refugee who sent the voicemail, in the database
     * @param recieverId the refugeeId of the reciever, which identifies the refugee who recieved the voicemail, in the database
     * @param reply if set to "Y" then voicemail is in response to previous voicemail. If set to "No" then voicemail
 *              is the first send-recieve of the communication
     * @param length base length/total time of the voice message
     * @param storageKey key which identifies where the voicemail is stored and can be retrieved from in the backend
     * @param date date voicemail recorded
     */
    public Voicemail(String senderId, String recieverId, String reply, String length, String storageKey, String date) {

        this.senderId = senderId;
        this.recieverId = recieverId;
        this.reply = reply;
        this.length = length;
        StorageKey = storageKey;
        this.date = date;

    }
}
