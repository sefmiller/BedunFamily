package com.gb.globalfamily.Model;


/**
 * //Specializes User. Creates a refugee object. The state of the refugee attributes represents a refugee details.
 * A refugee can either be a user or a person under the duty of care of an aid worker.
 */
public class Refugee extends User {
    private String dateCreated; private String refugeeId;
    private String nickname; private String placeOfBirth; private String tribe; private String ageGroup; private String gen; private String loc; private String occ;

    //getters and setters
    public String getRelationship() {
        return relationship;
    }
    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
    public String getName() {
        return name;
    }
    public String getNationality() {
        return nationality;
    }
    public String getNickname() {
        return nickname;
    }
    public String getPlaceOfBirth() {
        return placeOfBirth;
    }
    public String getTribe() {return tribe;}
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }
    public void setTribe(String tribe) {
        this.tribe = tribe;
    }
    public String getLoginID() {
        return loginID;
    }
    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }
    public String getAgeGroup() {
        return ageGroup;
    }
    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }
    public String getGen() {
        return gen;
    }
    public void setGen(String gen) {
        this.gen = gen;
    }
    public String getLoc() {
        return loc;
    }
    public void setLoc(String loc) {
        this.loc = loc;
    }
    public String getOcc() {
        return occ;
    }
    public void setOcc(String occ) {
        this.occ = occ;
    }
    private String relationship;
    private String loginID;
    public void setRefugeeId(String refugeeId) {
        this.refugeeId = refugeeId;
    }
    public String getRefugeeId() {
        return refugeeId;
    }

    //class members returned as a human readable string
    @Override
    public String toString() {
        return "Refugee{" +
                "RefugeeId" +refugeeId +
                "nickname='" + nickname + '\'' +
                ", placeOfBirth='" + placeOfBirth + '\'' +
                ", tribe='" + tribe + '\'' +
                " " + super.toString() +
                ", Age=" + ageGroup +
                ", Gender=" + gen +
                ", Location=" + loc +
                ", Occupation=" + occ +
                '}';
    }

    public String getDateCreated() {
        return dateCreated;
    }

    /**
     * Constructor used to create a refugee object representing a missing family member
     * @param refId RefugeeId of refugee who is missing family member. RefugeeId identifies refugee in db.
     * @param nam Name
     * @param age age range (eg 16-19)
     * @param nat nationality
     * @param nick nickname
     * @param pob place of birth (eg city, town)
     * @param tri tribe
     * @param gender gender
     * @param local local area where they were born
     * @param occupation occupation of the refugee
     * @param relation relationship to the refugee who is either a user or under the duty of care of an aid worker
     */

    public Refugee(String refId, String nam, String age, String nat,
                   String nick, String pob, String tri, String gender, String local, String occupation, String relation, String dateCreated) {
        super(nam, nat);
        nickname = nick;
        refugeeId = refId;
        loc = local;
        placeOfBirth = pob;
        tribe = tri;
        occ = occupation;
        gen = gender;
        ageGroup = age;
        relationship = relation;
        this.dateCreated = dateCreated;
    }

    /**
     * Constructor used to create refugee object who is also the registered user
     * @param login loginId of refugee who is missing family member. linked to backend login
     * @param nam Name
     * @param age age range (eg 16-19)
     * @param nat nationality
     * @param nick nickname
     * @param pob place of birth (eg city, town)
     * @param tri tribe
     * @param gender gender
     * @param local local area where they were born
     * @param occupation occupation of the refugee
     */
    public Refugee(String login, String nam, String age, String nat,
                   String nick, String pob, String tri, String gender, String local, String occupation) {
        super(nam, nat);
        nickname = nick;
        loginID = login;
        loc = local;
        placeOfBirth = pob;
        tribe = tri;
        occ = occupation;
        gen = gender;
        ageGroup = age;
    }


    /**
     * Constructor used to create and modify refugee object linked to aid worker
     * @param nam Name
     * @param age age range (eg 16-19)
     * @param nat nationality
     * @param nick nickname
     * @param pob place of birth (eg city, town)
     * @param tri tribe
     * @param gender gender
     * @param local local area where they were born
     * @param occupation occupation of the refugee
     */
    public Refugee(String nam, String age, String nat,
                   String nick, String pob, String tri, String gender, String local, String occupation) {
        super(nam, nat);
        nickname = nick;
        loc = local;
        placeOfBirth = pob;
        tribe = tri;
        occ = occupation;
        gen = gender;
        ageGroup = age;
    }


}
