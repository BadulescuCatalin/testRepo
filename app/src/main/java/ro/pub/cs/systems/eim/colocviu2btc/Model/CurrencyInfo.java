package ro.pub.cs.systems.eim.colocviu2btc.Model;

public class CurrencyInfo {
    private String code;
    private String rate;
    private String description;
    private String rate_float;

    public CurrencyInfo(String code, String rate, String description, String rate_float) {
        this.code = code;
        this.rate = rate;
        this.description = description;
        this.rate_float = rate_float;
    }

    public String getCode() {
        return code;
    }

    public String getRate() {
        return rate;
    }

    public String getDescription() {
        return description;
    }

    public String getRateFloat() {
        return rate_float;
    }

    public String toString() {
        return "CurrencyInfo{" +
                "code='" + code + '\'' +
                ", rate='" + rate + '\'' +
                ", description='" + description + '\'' +
                ", rate_float='" + rate_float + '\'' +
                '}';
    }

}
