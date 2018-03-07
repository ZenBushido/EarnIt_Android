package com.mobiledi.earnit.model;

/**
 * Created by adox on 20.01.2018..
 */

public class GoalV2Model
{
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    int id;

    public double getAmount() {
        return this.amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    double amount;

    public String getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    String createDate;

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    String name;

    public double getTally() {
        return this.tally;
    }
    public void setTally(double tally) {
        this.tally = tally;
    }
    double tally;

    public double getTallyPercent() {
        return this.tallyPercent;
    }
    public void setTallyPercent(double tallyPercent) {
        this.tallyPercent = tallyPercent;
    }
    double tallyPercent;

    public double getCash() {
        return this.cash;
    }
    public void setCash(double cash) {
        this.cash = cash;
    }
    double cash;

}
