package edu.eci.arsw.threads;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadController extends Thread{

    private AtomicInteger counts = new AtomicInteger();
    private AtomicBoolean notcompleted = new AtomicBoolean(true);
    private String ipaddress;
    private HostBlacklistsDataSourceFacade skds;
    private final int BLACK_LIST_ALARM_COUNT;
    int N;

    public ThreadController(int N, String ipaddress, HostBlacklistsDataSourceFacade skds, int BLACK_LIST_ALARM_COUNT){
        this.ipaddress = ipaddress;
        this.skds = skds;
        this.N = N;
        this.BLACK_LIST_ALARM_COUNT = BLACK_LIST_ALARM_COUNT;
    }

    public void run(){
        while (notcompleted.get()){
            createThreads();
        }
    }

    public void createThreads(){
        int start = 0;
        int amount  = skds.getRegisteredServersCount() / N;
        for (int i = 0; i < N; i++){
            ThreadSearch t = new ThreadSearch(ipaddress, skds, start, amount, counts, notcompleted, BLACK_LIST_ALARM_COUNT);
            start+= amount;
            t.start();
            if (counts.get() >= BLACK_LIST_ALARM_COUNT){
                if (counts.get()>=BLACK_LIST_ALARM_COUNT){
                    skds.reportAsNotTrustworthy(ipaddress);
                }
                else{
                    skds.reportAsTrustworthy(ipaddress);
                }
                notcompleted.set(false);
            }
        }
    }
}
