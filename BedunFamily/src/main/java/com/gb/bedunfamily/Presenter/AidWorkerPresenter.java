package com.gb.bedunfamily.Presenter;

import android.content.Context;
import android.graphics.Color;

import com.gb.bedunfamily.R;

import java.util.Calendar;

/**
 * Presenter Class. checks for logical errors arising from user input of aid worker details.
 * If error occurs e.g illegal characters or empty string, an appropriate error message is returned to the ui activity
 * and the error message colour set to red to indicate invalid input
 */
public class AidWorkerPresenter {

    private final Context context;
    private  String nameEmpty; private String dobNotInPast; private String dobtooYoung; private String natEmpty;
    private int errorColourCode; private String positionEmpty; private String sectorEmpty; private String workCountryEmpty;
    private String genderEmpty; private String supervisorEmpty; private String nameTooShort; private String aidOrgEmpty;
    private String aidOrgLabel; private String nationalityLabel; private String nameLabel; private String dateOfBirthLabel;
    private int aidOrgLabelColor; private int nationalityLabelColor; private int nameLabelColor;
    private String genderLabel; private String sectorLabel; private String workCountryLabel;
    private String positionLabel; private String supervisorLabel;
    private int genderLabelColor; private int sectorLabelColor; private int workCountryLabelColor;
    private int positionLabelColor; private int supervisorLabelColor; private int dateOfBirthLabelColor;

//getters and setters
    public int getErrorColourCode() {
        return errorColourCode;
    }
    public void setErrorColourCode(int errorColourCode) {
        this.errorColourCode = errorColourCode;
    }
    public String getPositionEmpty() {
        return positionEmpty;
    }
    public void setPositionEmpty(String positionEmpty) {
        this.positionEmpty = positionEmpty;
    }
    public String getSectorEmpty() {
        return sectorEmpty;
    }
    public void setSectorEmpty(String sectorEmpty) {
        this.sectorEmpty = sectorEmpty;
    }
    public String getWorkCountryEmpty() {
        return workCountryEmpty;
    }
    public void setWorkCountryEmpty(String workCountryEmpty) {
        this.workCountryEmpty = workCountryEmpty;
    }
    public String getGenderEmpty() {
        return genderEmpty;
    }
    public void setGenderEmpty(String genderEmpty) {
        this.genderEmpty = genderEmpty;
    }
    public String getSupervisorEmpty() {
        return supervisorEmpty;
    }
    public void setSupervisorEmpty(String supervisorEmpty) {
        this.supervisorEmpty = supervisorEmpty;
    }
    public String getGenderLabel() {
        return genderLabel;
    }
    public String getSectorLabel() {
        return sectorLabel;
    }
    private void setSectorLabel(String sectorLabel) {
        this.sectorLabel = sectorLabel;
    }
    public String getWorkCountryLabel() {
        return workCountryLabel;
    }
    private void setWorkCountryLabel(String workCountryLabel) {
        this.workCountryLabel = workCountryLabel;
    }
    public String getPositionLabel() {
        return positionLabel;
    }
    private void setPositionLabel(String positionLabel) {
        this.positionLabel = positionLabel;
    }
    public String getSupervisorLabel() {
        return supervisorLabel;
    }
    private void setSupervisorLabel(String supervisorLabel) {
        this.supervisorLabel = supervisorLabel;
    }
    public int getGenderLabelColor() {
        return genderLabelColor;
    }
    public void setGenderLabelColor(int genderLabelColor) {
        this.genderLabelColor = genderLabelColor;
    }
    public int getSectorLabelColor() {
        return sectorLabelColor;
    }
    public void setSectorLabelColor(int sectorLabelColor) {
        this.sectorLabelColor = sectorLabelColor;
    }
    public int getWorkCountryLabelColor() {
        return workCountryLabelColor;
    }
    public void setWorkCountryLabelColor(int workCountryLabelColor) {
        this.workCountryLabelColor = workCountryLabelColor;
    }
    public int getPositionLabelColor() {
        return positionLabelColor;
    }
    public void setPositionLabelColor(int positionLabelColor) {
        this.positionLabelColor = positionLabelColor;
    }
    public int getSupervisorLabelColor() {
        return supervisorLabelColor;
    }
    public void setSupervisorLabelColor(int supervisorLabelColor) {
        this.supervisorLabelColor = supervisorLabelColor;
    }
    public String getAidOrgLabel() {
        return aidOrgLabel;
    }
    private void setAidOrgLabel(String aidOrgLabel) {
        this.aidOrgLabel = aidOrgLabel;
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

    private void setDateOfBirthLabel(String dateOfBirthLabel) {
        this.dateOfBirthLabel = dateOfBirthLabel;
    }
    public int getAidOrgLabelColor() {
        return aidOrgLabelColor;
    }
    public void setAidOrgLabelColor(int aidOrgLabelColor) {
        this.aidOrgLabelColor = aidOrgLabelColor;
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
    public int getDateOfBirthLabelColor() {
        return dateOfBirthLabelColor;
    }
    public void setDateOfBirthLabelColor(int dateOfBirthLabelColor) {
        this.dateOfBirthLabelColor = dateOfBirthLabelColor;
    }
    public String getNameEmpty() {
        return nameEmpty;
    }
    public void setNameEmpty(String nameEmpty) {
        this.nameEmpty = nameEmpty;
    }
    public String getDobNotInPast() {
        return dobNotInPast;
    }
    public void setDobNotInPast(String dobNotInPast) {
        this.dobNotInPast = dobNotInPast;
    }
    public String getDobtooYoung() {
        return dobtooYoung;
    }
    public void setDobtooYoung(String dobtooYoung) {
        this.dobtooYoung = dobtooYoung;
    }
    public String getNatEmpty() {
        return natEmpty;
    }
    public void setNatEmpty(String natEmpty) {
        this.natEmpty = natEmpty;
    }
    public String getAidOrgEmpty() {
        return aidOrgEmpty;
    }
    public void setAidOrgEmpty(String aidOrgEmpty) {
        this.aidOrgEmpty = aidOrgEmpty;
    }
    public String getNameTooShort() {
        return nameTooShort;
    }
    public void setNameTooShort(String nameTooShort) {
        this.nameTooShort = nameTooShort;
    }
    private void setGenderLabel(String genderLabel) {
        this.genderLabel = genderLabel;
    }

    //default constructor
    public AidWorkerPresenter(Context context) {
        this.context = context;
        dobNotInPast = context.getResources().getString(R.string.dob_past);
        dobtooYoung = context.getResources().getString(R.string.dob_sixteen_or_over);
        nameEmpty = context.getResources().getString(R.string.name_empty);
        natEmpty = context.getResources().getString(R.string.nationality_empty);
        positionEmpty = context.getResources().getString(R.string.position_empty);
        sectorEmpty = context.getResources().getString(R.string.sector_empty);
        workCountryEmpty = context.getResources().getString(R.string.work_country_empty);
        genderEmpty = context.getResources().getString(R.string.gender_empty);
        aidOrgEmpty = context.getResources().getString(R.string.aid_org_empty);
        supervisorEmpty = context.getResources().getString(R.string.supervisor_empty);
        nameTooShort = context.getResources().getString(R.string.name_too_short);
        errorColourCode = Color.RED;

    }


    /**
     * @param aidOrg aid organization
     * @param aidNat Nationality
     * @param aidName Name
     * @param dat Date Of Birth
     * @param sector Sector
     * @param gender Gender
     * @param aidWorkerPosition Position
     * @param supervisor Supervisor
     * @param work_country Work Country
     * @return checks if each parameter, representing the aid worker's state (e.g. name, nationality, dateofbirth) is valid.
     * if all of the checks succeed then returns true. if a check fails then the method returns false. the label colour for that
     * attribute (eg nameLabelColour) is set to red and the label string is set to indicate the error. this is fed back to the
     * ui activity which then either displays the error(s) identified or acknowledges success and takes further action.
     */
    public boolean init(String aidOrg, String aidNat, String aidName, Calendar dat,
                        String sector, String gender, String aidWorkerPosition, String supervisor, String work_country){
        boolean checksSucceed = true;

        nameLabel = context.getResources().getString(R.string.full_name);
        aidOrgLabel = context.getResources().getString(R.string.aidOrg);
        nationalityLabel = context.getResources().getString(R.string.nationality);
        dateOfBirthLabel = context.getResources().getString(R.string.date_of_birth);
        genderLabel = context.getResources().getString(R.string.gender_label);
        sectorLabel = context.getResources().getString(R.string.sector_label);
        workCountryLabel = context.getResources().getString(R.string.work_country_label);
        positionLabel = context.getResources().getString(R.string.position_label);
        supervisorLabel  = context.getResources().getString(R.string.supervisor_label);
        nationalityLabelColor = Color.WHITE;
        nameLabelColor = Color.WHITE;
        aidOrgLabelColor = Color.WHITE;
        dateOfBirthLabelColor = Color.WHITE;
        sectorLabelColor = Color.WHITE;
        genderLabelColor = Color.WHITE;
        positionLabelColor = Color.WHITE;
        supervisorLabelColor = Color.WHITE;
        workCountryLabelColor = Color.WHITE;

        if (! checkName(aidName)){
            checksSucceed = false;
            nameLabelColor = errorColourCode;
        }
        if (! checkNationality(aidNat)){
            checksSucceed = false;
            nationalityLabelColor = errorColourCode;

        }
        if (! checkAidOrg(aidOrg)){
            checksSucceed = false;
            aidOrgLabelColor = errorColourCode;

        }
        if (! checkDateOfBirth(dat)){
            checksSucceed = false;
            dateOfBirthLabelColor = errorColourCode;

        }

        if (! checkSector(sector)){
            checksSucceed = false;
            sectorLabelColor = errorColourCode;
        }
        if (! checkGender(gender)){
            checksSucceed = false;
            genderLabelColor = errorColourCode;

        }
        if (! checkPosition(aidWorkerPosition)){
            checksSucceed = false;
            positionLabelColor = errorColourCode;

        }
        if (! checkSupervisor(supervisor)){
            checksSucceed = false;
            supervisorLabelColor = errorColourCode;

        }
        if (! checkWorkCountry(work_country)) {
            checksSucceed = false;
            workCountryLabelColor = errorColourCode;
        }
        return checksSucceed;
    }


    /**
     * checks if sector is not empty
     * @param sec sector
     * @return false if sector string is empty and set sector label to error message.
     */
    private boolean checkSector(String sec){
        boolean result = true;
        if (sec.length() < 1){
            result = false;
            this.setSectorLabel(sectorEmpty);

        }
        System.out.println(sectorEmpty);

        return result;
    }

    /**
     * checks if gender is not empty
     * @param gen gender
     * @return false if gender string is empty and set gender label to error message.
     */
    private boolean checkGender(String gen){
        boolean result = true;
        if (gen.length() < 1){
            result = false;
            this.setGenderLabel(genderEmpty);
        }
        System.out.println(gen);

        return result;
    }

    /**
     * checks if position is not empty.
     * @param pos position
     * @return false if position string is empty and set position label to error message.
     */
    private boolean checkPosition(String pos){
        boolean result = true;
        if (pos.length() < 1){
            result = false;
            this.setPositionLabel(positionEmpty);
        }
        System.out.println(pos);
        return result;
    }

    /**
     * checks if Work Country is not empty.
     * @param wor  Country working in
     * @return false if wor string is empty and set Work Country label to error message.
     */
    private boolean checkWorkCountry(String wor){
        boolean result = true;
        if (wor.length() < 1){
            result = false;
            this.setWorkCountryLabel(workCountryEmpty);
        }
        System.out.println(wor);
        return result;
    }

    /**
     * checks if Supervisor is not empty.
     * @param sup  Supervisor
     * @return false if supervisor string is empty and set supervisor label to error message.
     */
    private boolean checkSupervisor(String sup){
        boolean result = true;

        if (sup.length() < 1){
            result = false;
            this.setSupervisorLabel(supervisorEmpty);
        }
        System.out.println(sup);
        return result;
    }

    /**
     * checks if Name is not empty and is at least 6 characters
     * @param nam  Name
     * @return false if Name string is empty  or less then 6 charecters. set name label to error message reflecting identified error
     */
    private boolean checkName(String nam){
        boolean result = true;

        if (nam.length() < 1){
            result = false;
            this.setNameLabel(nameEmpty);
        }
        if (nam.length() < 6){
            result = false;
            this.setNameLabel(nameTooShort);
        }
        return result;
    }

    /**
     * checks if Nationality is not empty.
     * @param nation Nationality
     * @return false if nationality string is empty and set nationality label to error message.
     */
    private boolean checkNationality(String nation){
        boolean result = true;
        if (nation.length() < 1){
            result = false;
            this.setNationalityLabel(natEmpty);
        }
        System.out.println(nation);
        return result;
    }

    /**
     * checks if Aid Organization is not empty.
     * @param org  Aid Organization
     * @return false if Aid Organization string is empty and set Aid Organization label to error message.
     */
    private boolean checkAidOrg(String org){
        boolean result = true;
        if (org.length() < 1){
            result = false;
            this.setAidOrgLabel(aidOrgEmpty);

        }
        return result;
    }

    /**
     * checks if Aid Organization is not in the future and user is at least 16 years old
     * @param d  date of birth
     * @return false if Aid Organization is in the future or user younger then 16 years old and set Date of Birth label
     * to error message.
     */
    private boolean checkDateOfBirth(Calendar d){
        boolean result = true;
        if (d.after(Calendar.getInstance())){
            result = false;
            this.setDateOfBirthLabel(dobNotInPast);

        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -16);
        if (d.after(cal)){
            result = false;
            this.setDateOfBirthLabel(dobtooYoung);

        }
        return result;
    }

}
