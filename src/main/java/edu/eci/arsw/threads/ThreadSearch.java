package edu.eci.arsw.threads;

import edu.eci.arsw.blacklistvalidator.HostBlackListsValidator;
import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSearch extends Thread{

    AtomicInteger counts;
    AtomicBoolean notcompleted;
    private final String ipaddress;
    private HostBlacklistsDataSourceFacade blacklist;
    private int start;
    private int amount;
    private int alarm;

    public ThreadSearch(String ipaddress, HostBlacklistsDataSourceFacade blacklist, int start, int amount, AtomicInteger counts, AtomicBoolean notcompleted, int alarm){
        this.ipaddress = ipaddress;
        this.blacklist = blacklist;
        this.start = start;
        this.amount = amount;
        this.counts = counts;
        this.notcompleted = notcompleted;
        this.alarm = alarm;
    }

    public void run(){
        while(notcompleted.get()){
            search();
        }
    }

    public void search(){
        int control = start + amount;
        for (int i = start; i <= control; i++){
            if (blacklist.isInBlackListServer(i, ipaddress)) {
                if (counts.get() < alarm){
                    synchronized (counts) {
                        counts.getAndIncrement();
                    }
                } else{
                    notcompleted.set(false);
                }
            }
        }
    }
}
