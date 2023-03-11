import java.util.Random;
import java.util.Scanner;

public class BrainTest implements Runnable{
    private String resultInput;
    public static Scanner sc = new Scanner(System.in);

    public static String  resultTest(int countCorrect){
        String str[]={"=> Baby brain","=> Normal brain", "=> Master brain"};
        return str[countCorrect<=4?0:(countCorrect<=7?1:2)];
    }
    public static int getRandom(){
        return new Random().nextInt(0,9);
    }
    public void setResultInput(String resultInput){
        this.resultInput=resultInput;
    }
    public String getResultInput(){
        return resultInput;
    }
    @Override
    public void run() {
        resultInput="";
        if(!sc.hasNextInt()){
            resultInput=sc.nextLine();
            resultInput="X";
            System.out.println("Not a number \nSo marked as incorrect");
        }else{
            resultInput=sc.nextLine();
        }
    }
        
    public static void main(String[] args) {
        int correct=0;
        int wrong=0;

        System.out.println("""
                \t\t=======================
                \t\t>>>>   Brain Test  <<<<
                \t\t=======================""");

        int result=0;
        while((correct + wrong) != 10){
            int A=getRandom();
            int B=getRandom();
            System.out.println();
            System.out.println(A+" + "+ B +" = ?");
            System.out.print("=> ");
            BrainTest brainTest = new BrainTest();
            brainTest.setResultInput(null);
            Thread t = new Thread(brainTest);
            t.start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            if(brainTest.getResultInput().equals("")){
                System.out.println("You finished "+ (correct + wrong) +" test" + ((correct + wrong) >1?"s":""));
                break;
            } else if(!brainTest.getResultInput().equals("X")) {
                result = Integer.valueOf(brainTest.getResultInput());
            }
    
            if((result == A + B) &&  !brainTest.getResultInput().equals("X")) correct++;
            else wrong++;
        }

        if(correct+wrong==10) System.out.println("Congrat!!! You have finished all the tests");
        System.out.print("\nResult: ");
        System.out.println("Correct: "+ correct);
        System.out.println("        Incorrect: "+ wrong);
        System.out.println("        "+ resultTest(correct));
    }
}
