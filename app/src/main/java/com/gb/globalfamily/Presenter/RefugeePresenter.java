package com.gb.globalfamily.Presenter;

import android.content.Context;
import android.graphics.Color;

import com.gb.globalfamily.R;

/**
 * Presenter Class. checks for logical errors arising from user input of refugee details.
 * If error occurs e.g illegal characters or empty string, an appropriate error message is returned to the ui activity
 * and the error message colour set to red to indicate invalid input
 */
public class RefugeePresenter {
    private final Context context;
    private String placeOfBirthLabel; private String nationalityLabel; private String nameLabel;
    private String dateOfBirthLabel; private String nickNameLabel;
    private String ageLabel; private String tribeLabel; private String genderLabel; private String localAreaLabel; private String occupationLabel;
    private int placeOfBirthLabelColor; private int nationalityLabelColor; private int nameLabelColor;
    private int relationshipLabelColor;
    private int ageLabelColor; private int nickNameLabelColor;
    private int tribeLabelColor; private int genderLabelColor; private int localAreaLabelColor; private int occupationLabelColor;
    private String pobEmpty;  private String nameEmpty;
    private String nicknameEmpty; private String natEmpty; private String ageNull; private String tribeNull; private String genderNull;
    private String localAreaNull; private String occupationNull;
    private String relationshipLabel; private String relationshipNull;
    private String nameTooShort; private int errorColourCode;

//getters and setters
    public String getNameTooShort() {
        return nameTooShort;
    }
    public void setNameTooShort(String nameTooShort) {
        this.nameTooShort = nameTooShort;
    }
    public int getRelationshipLabelColor() {

        return relationshipLabelColor;
    }
    public void setRelationshipLabelColor(int relationshipLabelColor) {
        this.relationshipLabelColor = relationshipLabelColor;
    }
    public String getRelationshipLabel() {
        return relationshipLabel;
    }

    private void setRelationshipLabel(String relationshipLabel) {
        this.relationshipLabel = relationshipLabel;
    }
    public String getRelationshipNull() {
        return relationshipNull;
    }
    public void setRelationshipNull(String relationshipNull) {
        this.relationshipNull = relationshipNull;
    }
    public String getAgeLabel() {
        return ageLabel;
    }
    private void setAgeLabel(String ageLabel) {
        this.ageLabel = ageLabel;
    }
    public String getTribeLabel() {
        return tribeLabel;
    }
    private void setTribeLabel(String tribeLabel) {
        this.tribeLabel = tribeLabel;
    }
    public String getGenderLabel() {
        return genderLabel;
    }
    private void setGenderLabel(String genderLabel) {
        this.genderLabel = genderLabel;
    }
    public String getLocalAreaLabel() {
        return localAreaLabel;
    }
    private void setLocalAreaLabel(String localAreaLabel) {
        this.localAreaLabel = localAreaLabel;
    }
    public String getOccupationLabel() {
        return occupationLabel;
    }
    private void setOccupationLabel(String occupationLabel) {
        this.occupationLabel = occupationLabel;
    }
    public int getPlaceOfBirthLabelColor() {
        return placeOfBirthLabelColor;
    }
    public void setPlaceOfBirthLabelColor(int placeOfBirthLabelColor) {
        this.placeOfBirthLabelColor = placeOfBirthLabelColor;
    }
    public int getNationalityLabelColor() {
        return nationalityLabelColor;
    }
    public void setNationalityLabelColor(int nationalityLabelColor) {
        this.nationalityLabelColor = nationalityLabelColor;
    }
    public int getNameLabelColor() {
        return nameLabelColor;
    }
    public void setNameLabelColor(int nameLabelColor) {
        this.nameLabelColor = nameLabelColor;
    }
    public int getNickNameLabelColor() {
        return nickNameLabelColor;
    }
    public void setNickNameLabelColor(int nickNameLabelColor) {
        this.nickNameLabelColor = nickNameLabelColor;
    }
    public String getPlaceOfBirthLabel() {
        return placeOfBirthLabel;
    }
    private void setPlaceOfBirthLabel(String placeOfBirthLabel) {
        this.placeOfBirthLabel = placeOfBirthLabel;
    }
    public String getNationalityLabel() {
        return nationalityLabel;
    }
    private void setNationalityLabel(String nationalityLabel) {
        this.nationalityLabel = nationalityLabel;
    }
    public String getNameLabel() {
        return nameLabel;
    }
    private void setNameLabel(String nameLabel) {
        this.nameLabel = nameLabel;
    }
    public String getDateOfBirthLabel() {
        return dateOfBirthLabel;
    }
    public void setDateOfBirthLabel(String dateOfBirthLabel) {
        this.dateOfBirthLabel = dateOfBirthLabel;
    }
    public String getNickNameLabel() {
        return nickNameLabel;
    }
    private void setNickNameLabel(String nickNameLabel) {
        this.nickNameLabel = nickNameLabel;
    }
    public String getPobEmpty() {
        return pobEmpty;
    }
    public void setPobEmpty(String pobEmpty) {
        this.pobEmpty = pobEmpty;
    }
    public String getNameEmpty() {
        return nameEmpty;
    }
    public void setNameEmpty(String nameEmpty) {
        this.nameEmpty = nameEmpty;
    }
    public String getNicknameEmpty() {
        return nicknameEmpty;
    }
    public void setNicknameEmpty(String nicknameEmpty) {
        this.nicknameEmpty = nicknameEmpty;
    }
    public String getNatEmpty() {
        return natEmpty;
    }
    public void setNatEmpty(String natEmpty) {
        this.natEmpty = natEmpty;
    }
    public int getAgeLabelColor() {
        return ageLabelColor;
    }
    public void setAgeLabelColor(int ageLabelColor) {
        this.ageLabelColor = ageLabelColor;
    }
    public int getTribeLabelColor() {
        return tribeLabelColor;
    }
    public void setTribeLabelColor(int tribeLabelColor) {
        this.tribeLabelColor = tribeLabelColor;
    }
    public int getGenderLabelColor() {
        return genderLabelColor;
    }
    public void setGenderLabelColor(int genderLabelColor) {
        this.genderLabelColor = genderLabelColor;
    }
    public int getLocalAreaLabelColor() {
        return localAreaLabelColor;
    }
    public void setLocalAreaLabelColor(int localAreaLabelColor) {
        this.localAreaLabelColor = localAreaLabelColor;
    }
    public int getOccupationLabelColor() {
        return occupationLabelColor;
    }
    public void setOccupationLabelColor(int occupationLabelColor) {
        this.occupationLabelColor = occupationLabelColor;
    }
    public String getAgeNull() {
        return ageNull;
    }
    public void setAgeNull(String ageNull) {
        this.ageNull = ageNull;
    }
    public String getTribeNull() {
        return tribeNull;
    }
    public void setTribeNull(String tribeNull) {
        this.tribeNull = tribeNull;
    }
    public String getGenderNull() {
        return genderNull;
    }
    public void setGenderNull(String genderNull) {
        this.genderNull = genderNull;
    }
    public String getLocalAreaNull() {
        return localAreaNull;
    }
    public void setLocalAreaNull(String localAreaNull) {
        this.localAreaNull = localAreaNull;
    }
    public String getOccupationNull() {
        return occupationNull;
    }
    public void setOccupationNull(String occupationNull) {
        this.occupationNull = occupationNull;
    }
    public int getErrorColourCode() {
        return errorColourCode;
    }
    public void setErrorColourCode(int errorColourCode) {
        this.errorColourCode = errorColourCode;
    }

    /**
     * default constructor
     */
    public RefugeePresenter(Context context) {
        this.context = context;
        errorColourCode = Color.RED;
        nicknameEmpty = context.getResources().getString(R.string.nickname_empty);
        nameEmpty = context.getResources().getString(R.string.name_empty);
        pobEmpty = context.getResources().getString(R.string.pob_empty);
        natEmpty = context.getResources().getString(R.string.nationality_empty);
        ageNull = context.getResources().getString(R.string.age_null);
        tribeNull = context.getResources().getString(R.string.tribe_null);
        genderNull = context.getResources().getString(R.string.gender_null);
        localAreaNull = context.getResources().getString(R.string.local_null);
        occupationNull = context.getResources().getString(R.string.occupation_null);
        nameTooShort = context.getResources().getString(R.string.name_too_short);
        relationshipNull = context.getResources().getString(R.string.relationship_null);
    }

    /**
     * @param pob place of birth
     * @param refNat refugee nationality
     * @param refName refugee name
     * @param age age range
     * @param nickyname nickname
     * @param tribe tribe
     * @param gender gender
     * @param local local area
     * @param occupation occupation
     * @return checks if each parameter, representing the refugee's state (e.g. name, nationality, age range) is valid.
     * if all of the checks succeed then returns true. if a check fails then the method returns false. the label colour for that
     * attribute (eg nameLabelColour) is set to red and the label string is set to indicate the error. this is fed back to the
     * ui activity which then either displays the error(s) identified or acknowledges success and takes further action.
     */
    public boolean init(String pob, String refNat, String refName, String age,
                        String nickyname, String tribe, String gender, String local, String occupation){
        boolean checksSucceed = true;
        nationalityLabelColor = Color.WHITE;
        nameLabelColor = Color.WHITE;
        placeOfBirthLabelColor = Color.WHITE;
        ageLabelColor = Color.WHITE;
        nickNameLabelColor = Color.WHITE;
        localAreaLabelColor = Color.WHITE;
        occupationLabelColor = Color.WHITE;
        genderLabelColor = Color.WHITE;
        tribeLabelColor = Color.WHITE;
        genderLabelColor = Color.WHITE;
        placeOfBirthLabel = context.getResources().getString(R.string.place_of_birth);
        nationalityLabel = context.getResources().getString(R.string.nationality);
        nameLabel = context.getResources().getString(R.string.full_name);
        ageLabel = context.getResources().getString(R.string.age_label);
        genderLabel = context.getResources().getString(R.string.gender_label);
        nickNameLabel = context.getResources().getString(R.string.nickname);
        tribeLabel = context.getResources().getString(R.string.tribe_label);
        localAreaLabel = context.getResources().getString(R.string.local_area_label);
        occupationLabel = context.getResources().getString(R.string.occupation_label);

        if (! checkName(refName)){
            checksSucceed = false;
            nameLabelColor = errorColourCode;
        }
        if (! checkNationality(refNat)){
            checksSucceed = false;
            nationalityLabelColor = errorColourCode;

        }
        if (! checkPlaceOfBirth(pob)){
            checksSucceed = false;
            placeOfBirthLabelColor = errorColourCode;

        }
        if (! checkAge(age)){
            checksSucceed = false;
            ageLabelColor = errorColourCode;

        }


        if (! checkNickName(nickyname)) {
            checksSucceed = false;
            nickNameLabelColor = errorColourCode;

        }
        if (! checkLocalArea(local)) {
            checksSucceed = false;
            localAreaLabelColor = errorColourCode;

        }
        if (! checkGender(gender)) {
            checksSucceed = false;
            genderLabelColor = errorColourCode;

        }
        if (! checkOccupation(occupation)) {
            checksSucceed = false;
            occupationLabelColor = errorColourCode;

        }
        if (! checkTribe(tribe)) {
            checksSucceed = false;
            tribeLabelColor = errorColourCode;

        }
        System.out.println(checksSucceed);
        return checksSucceed;
    }

    /**
     * @param pob place of birth
     * @param refNat refugee nationality
     * @param refName refugee name
     * @param age age range
     * @param nickyname nickname
     * @param tribe tribe
     * @param gender gender
     * @param local local area
     * @param occupation occupation
     * @return checks if each parameter, representing the refugee's state (e.g. name, nationality, age range) is valid.
     * if all of the checks succeed then returns true. if a check fails then the method returns false. the label colour for that
     * attribute (eg nameLabelColour) is set to red and the label string is set to indicate the error. this is fed back to the
     * ui activity which then either displays the error(s) identified or acknowledges success and takes further action.
     */
    public boolean init(String pob, String refNat, String refName, String age,
                        String nickyname, String tribe, String gender, String local, String occupation, String rel){
        boolean checksSucceed = true;
        nationalityLabelColor = Color.WHITE;
        nameLabelColor = Color.WHITE;
        placeOfBirthLabelColor = Color.WHITE;
        ageLabelColor = Color.WHITE;
        nickNameLabelColor = Color.WHITE;
        localAreaLabelColor = Color.WHITE;
        occupationLabelColor = Color.WHITE;
        genderLabelColor = Color.WHITE;
        tribeLabelColor = Color.WHITE;
        genderLabelColor = Color.WHITE;
        relationshipLabelColor = Color.WHITE;
        placeOfBirthLabel = context.getResources().getString(R.string.place_of_birth);
        nationalityLabel = context.getResources().getString(R.string.nationality);
        nameLabel = context.getResources().getString(R.string.full_name);
        ageLabel = context.getResources().getString(R.string.age_label);
        genderLabel = context.getResources().getString(R.string.gender_label);
        nickNameLabel = context.getResources().getString(R.string.nickname);
        tribeLabel = context.getResources().getString(R.string.tribe_label);
        relationshipLabel = context.getResources().getString(R.string.relationship_label);
        localAreaLabel = context.getResources().getString(R.string.local_area_label);
        occupationLabel = context.getResources().getString(R.string.occupation_label);
        relationshipLabel = context.getResources().getString(R.string.relationship_label);


        if (! checkName(refName)){
            checksSucceed = false;
            nameLabelColor = errorColourCode;
        }
        if (! checkNationality(refNat)){
            checksSucceed = false;
            nationalityLabelColor = errorColourCode;

        }
        if (! checkPlaceOfBirth(pob)){
            checksSucceed = false;
            placeOfBirthLabelColor = errorColourCode;

        }
        if (! checkAge(age)){
            checksSucceed = false;
            ageLabelColor = errorColourCode;

        }
        if (! checkNickName(nickyname)) {
            checksSucceed = false;
            nickNameLabelColor = errorColourCode;

        }
        if (! checkLocalArea(local)) {
            checksSucceed = false;
            localAreaLabelColor = errorColourCode;

        }
        if (! checkGender(gender)) {
            checksSucceed = false;
            genderLabelColor = errorColourCode;

        }
        if (! checkOccupation(occupation)) {
            checksSucceed = false;
            occupationLabelColor = errorColourCode;

        }
        if (! checkTribe(tribe)) {
            checksSucceed = false;
            tribeLabelColor = errorColourCode;

        }
        if (! checkRelationship(rel)) {
            checksSucceed = false;
            relationshipLabelColor = errorColourCode;

        }
        return checksSucceed;
    }


    /**
     * checks if Name is not empty and is at least 6 characters
     * @param nam  Name
     * @return false if Name string is empty  or less then 6 characters. set name label to error message reflecting identified error
     */
    private boolean checkName(String nam){
        boolean result = true;
                if (nam.length() < 1){
            result = false;
            this.setNameLabel(nameEmpty); }
        if (nam.length() < 6) {
            result = false;
            this.setNameLabel(nameTooShort);
        }
        System.out.println(result + nam);

        return result;
    }

    /**
     * checks if Nationality is not empty.
     * @param nation Nationality
     * @return false if nationality string is empty and set nationality label to error message.
     */
    private  boolean checkNationality(String nation){
        boolean result = true;
        if (nation.length() < 1){
            result = false;
            this.setNationalityLabel(natEmpty);
            System.out.println(result + nation);

        }
        System.out.println(result + nation);
        return result;
    }

    /**
     * checks if Place Of Birth is not empty.
     * @param pOb Place Of Birth
     * @return false if Place Of Birth string is empty and set Place Of Birth label to error message.
     */
    private  boolean checkPlaceOfBirth(String pOb){
        boolean result = true;
        if (pOb.length() < 1){
            result = false;
            this.setPlaceOfBirthLabel(pobEmpty);
        }
        System.out.println(result + pOb);
        return result;
    }

    /**
     * checks if NickName is not empty.
     * @param nam NickName
     * @return false if Nickname string is empty and set NickName label to error message.
     */
    private boolean checkNickName(String nam){
        boolean result = true;

        if (nam.length() < 1){
            result = false;
            this.setNickNameLabel(nicknameEmpty);
        }
        System.out.println(result + nam);
        return result;
    }

    /**
     * checks if Local Area is not empty.
     * @param loc Local Area
     * @return false if Local Area string is empty and set Local Area label to error message.
     */
    private boolean checkLocalArea(String loc){
        boolean result = true;

        if (loc.length() < 1){
            result = false;
            this.setLocalAreaLabel(localAreaNull);
        }
        System.out.println(result + loc);
        return result;
    }

    /**
     * checks if Gender is not empty.
     * @param gen Gender
     * @return false if Gender string is empty and set Gender label to error message.
     */
    private boolean checkGender(String gen){
        boolean result = true;

        if (gen.length() < 1){
            result = false;
            this.setGenderLabel(genderNull);
            System.out.println(this.getGenderLabel());
        }
        System.out.println(result + gen);
        return result;
    }

    /**
     * checks if Relationship is not empty.
     * @param rel Relationship
     * @return false if Relationship string is empty and set Relationship label to error message.
     */
    private boolean checkRelationship(String rel){
        boolean result = true;

        if (rel.length() < 1){
            result = false;
            this.setRelationshipLabel(relationshipNull);
            System.out.println(this.getRelationshipLabel());
        }
        System.out.println(result + rel);
        return result;
    }

    /**
     * checks if Occupation is not empty.
     * @param occ Occupation
     * @return false if Occupation string is empty and set Occupation label to error message.
     */
    private boolean checkOccupation(String occ){
        boolean result = true;

        if (occ.length() < 1){
            result = false;
            this.setOccupationLabel(occupationNull);
            System.out.println(this.getOccupationLabel());
        }
        System.out.println(result + occ);
        return result;
    }

    /**
     * checks if Tribe is not empty.
     * @param tri Tribe
     * @return false if Tribe string is empty and set Tribe label to error message.
     */
    private boolean checkTribe(String tri){
        boolean result = true;

        if (tri.length() < 1){
            result = false;
            this.setTribeLabel(tribeNull);
        }
        System.out.println(result + tri);
        return result;
    }

    /**
     * checks if Age is not empty.
     * @param age Age Range
     * @return false if Age string is empty and set Age label to error message.
     */
    private boolean checkAge(String age){
        boolean result = true;

        if (age.length() < 1){
            result = false;
            this.setAgeLabel(ageNull);
            System.out.println(this.getAgeLabel());
        }
        System.out.println(result + age);
        return result;
    }
}

