package fr.glog.aourir_infos.notifications;

public class Sender {
    public Data data;
    String to;

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }
}
