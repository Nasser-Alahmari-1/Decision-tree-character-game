public class Person {
    public String name;
    public String gender;
    public boolean alive;
    public String ageGroup;
    public String famousFor;
    public String nationality;
    public String religion;
    public boolean royalty;

    public Person(String[] d) {
        this.name        = d[0].trim();
        this.gender      = d[1].trim().toLowerCase();
        this.alive       = d[2].trim().equalsIgnoreCase("yes");
        this.ageGroup    = d[3].trim().toLowerCase();
        this.famousFor   = d[4].trim().toLowerCase();
        this.nationality = d[5].trim().toLowerCase();
        this.religion    = d[6].trim().toLowerCase();
        this.royalty     = d[7].trim().equalsIgnoreCase("yes");
    }
}
