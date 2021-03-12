package task5;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.*;

public class DollFactory {
    List<Doll> dolls = new ArrayList<>();
    List<Doll> badDolls = new ArrayList<>();

    private CyclicBarrier stageA, stageB, stageC;

    private void execution(int dollsNumber) throws InterruptedException {
        stageA = new CyclicBarrier(dollsNumber);
        stageB = new CyclicBarrier(dollsNumber);
        stageC = new CyclicBarrier(dollsNumber + 1);

        dolls = new ArrayList<>(dollsNumber);
        badDolls = new ArrayList<>(dollsNumber);

        for (int i = 0; i < dollsNumber; i++) {
            Process task = new Process(i);
            Thread thread = new Thread(task);
            thread.start();
        }

        try {
            stageC.await();
            System.out.println("Packaging process D ");
            System.out.println("Number of accepted dolls is " + dolls.size());
            for (Doll d : dolls) {
                System.out.println("Doll " + d.id + " quality: " + d.getQualityScore() + " | painted value: "
                        + d.isPainted + " | imperfection: " + d.imperfect);
            }
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DollFactory dcb = new DollFactory();
        dcb.execution(30);
    }

    class Process implements Runnable {

        int id;

        public Process(int id) {
            this.id = id;
        }

        public void run() {
            Doll d;
            try {
                d = assembly(); // StageA
                System.out.println("Created doll " + d.id);
                stageA.await(); // Wait until all dolls have finished stageA before painting

                painting(d); // StageB
                System.out.println("Painted doll " + d.id);
                stageB.await(); // Wait until all dolls are painted

                qualityControl(d); // stageC
                System.out.println("Quality controlled doll " + d.id);
                stageC.await(); // Wait until all dolls are quality controlled

            } catch (InterruptedException e) {

            } catch (BrokenBarrierException e) {

            }

        }

        void painting(Doll d) {
            d.setPainted(true);
        }

        Doll assembly() {
            Random r = new Random();
            return new Doll(id, r.nextInt(4) + 7);
        }

        void qualityControl(Doll d) {
            if (d.getQualityScore() >= 9) {
                d.hasImperfections(false);
                dolls.add(d); // List of dolls that got approved
            } else {
                badDolls.add(d); // List of dolls failing the check
            }
        }
    }

    class Doll {
        int id;
        int qualityScoreMachine;
        boolean imperfect, isPainted;

        public Doll(int id, int qualityScoreMachine) {
            this.id = id;
            this.qualityScoreMachine = qualityScoreMachine;
            this.imperfect = true; // Init imperfect before check to be true
            this.isPainted = false; // Init to unpainted before getting paint

        }

        public int getId() {
            return id;
        }

        public int getQualityScore() {
            return qualityScoreMachine;
        }

        public void hasImperfections(boolean imperfect) {
            this.imperfect = imperfect;
        }

        public void setPainted(boolean isPainted) {
            this.isPainted = true;
        }

    }
}