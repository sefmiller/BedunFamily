package com.gb.globalfamily.Model;
/**
 * Create an AidWorker Class which specializes User, storing all the details of the registered AidWorker.
 */
public class AidWorker extends User {

    private String dateOfBirth;
    private String LoginID;
    private String sector; private String workCountry; private String gender; private String aidOrganization;
    private String position; private String supervisor;

    /**
     * Aid Worker Constructor. initialises all the class members representing the aid worker details.
     * @param loginID loginId of the aid worker user, which identifies the user in the db.
     * @param nam name of the aid worker
     * @param dateob date of birth
     * @param gender gender
     * @param nat nationality
     * @param workCountry country currently working in
     * @param aidOrg Aid Organization working for
     * @param sector Sector worked in
     * @param position Position/job role of aid worker
     * @param supervisor Name of Supervisor
     */
    public AidWorker(String loginID, String nam, String dateob, String gender, String nat,
                     String workCountry, String aidOrg, String sector,
                     String position, String supervisor) {
        super(nam, nat);
        aidOrganization = aidOrg; this.gender = gender; this.workCountry = workCountry;
        this.LoginID = loginID; this.dateOfBirth = dateob;

        this.sector = sector; this.position = position; this.supervisor = supervisor;
    }

    //getters and setters
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getLoginID() {
        return LoginID;
    }
    public void setLoginID(String loginID) {
        LoginID = loginID;
    }
    public String getSector() {
        return sector;
    }
    public void setSector(String sector) {
        this.sector = sector;
    }
    public String getWorkCountry() {
        return workCountry;
    }
    public void setWorkCountry(String workCountry) {
        this.workCountry = workCountry;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }
    public String getSupervisor() {
        return supervisor;
    }
    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }
    public String getAidOrganization() {
        return aidOrganization;
    }
    public void setAidOrganization(String aidOrganization) {
        this.aidOrganization = aidOrganization;
    }

    //class members returned as a human readable string
    @Override
    public String toString() {
        return "AidWorker{" +
                "LoginID='" + LoginID + '\'' +
                ", sector='" + sector + '\'' +
                ", workCountry='" + workCountry + '\'' +
                ", gender='" + gender + '\'' +
                ", aidOrganization='" + aidOrganization + '\'' +
                ", position='" + position + '\'' +
                ", supervisor='" + supervisor + '\'' +
                " " + super.toString() +
                '}';
    }

}
