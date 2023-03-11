import java.util.Scanner;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class StopHits2 implements NativeKeyListener, Runnable {

    public static Scanner sc = new Scanner(System.in);
    public static String stop = "HitMe!";
    public String getStop() {
        return stop;
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        stop = NativeKeyEvent.getKeyText(e.getKeyCode());
        if (e.getKeyCode() == NativeKeyEvent.VC_ENTER) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException nativeHookException) {
                nativeHookException.printStackTrace();
            }
        }
    }
    
    @Override
    public void run() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        GlobalScreen.addNativeKeyListener(new StopHits2());
    }
    public static void main(String[] args) {
        StopHits2 hitMe = new StopHits2();
        Thread tr = new Thread(hitMe);
        tr.start();

        while (!hitMe.getStop().equals("Enter")) {
            System.out.print(hitMe.getStop());
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("\n\nThank You!");
    }

}